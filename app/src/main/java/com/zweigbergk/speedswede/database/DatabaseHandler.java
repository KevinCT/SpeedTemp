package com.zweigbergk.speedswede.database;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.eventListener.ChatListener;
import com.zweigbergk.speedswede.database.eventListener.DataQuery;
import com.zweigbergk.speedswede.database.eventListener.MessageListener;
import com.zweigbergk.speedswede.database.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.util.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DatabaseHandler {
    INSTANCE;

    public static final String TAG = DatabaseHandler.class.getSimpleName().toUpperCase();

    private Map<String, MessageListener> messageListeners;

    DatabaseHandler() {
        messageListeners = new HashMap<>();
    }

    public static final String MESSAGES = "messages";
    public static final String CHATS = "chats";
    public static final String POOL = "pool";
    public static final String USER = "users";
    public static final String USER_NAME = "displayName";
    public static final String UID = "uid";
    public static final String BANS = "bans";
    public static final String STRIKES = "strikes";
    private User mLoggedInUser;
   // private HashMap<String,List<User>> banMap;
    private Banner mBanner = new Banner();

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    private DatabaseReference fetchChats() {
        return mDatabaseReference.child(CHATS);
    }

    public static User convertToUser(DataSnapshot snapshot) {
        User user = new UserProfile(snapshot.child("displayName").getValue().toString(),
                snapshot.child("uid").getValue().toString());
        return user;
    }

    public void addChatMessageClient(Chat chat, Client<DataChange<Message>> client) {
        if (!hasMessageListenerForChat(chat)) {
            createMessageListenerForChat(chat);
        }

        messageListeners.get(chat.getId()).addClient(client);
    }

    public Chat convertToChat(DataSnapshot snapshot) {
        Log.d(TAG, snapshot.toString());
        if (snapshot.getValue() == null)
            return null;

        //TODO: is key also its id????
        String id = snapshot.getKey();
        long timeStamp = (long) snapshot.child("timeStamp").getValue();
        User firstUser = new UserProfile("user1", "user1");
        User secondUser = new UserProfile("user2", "user2");
        DataSnapshot messagesSnapshot = snapshot.child("conversation");

        List<Message> messages = new ArrayList<>();
        for (DataSnapshot messageSnapshot : messagesSnapshot.getChildren()) {
            Message message = messageSnapshot.getValue(Message.class);
            messages.add(message);
        }
        Chat chat = new Chat(id, timeStamp, messages, firstUser, secondUser);
        return chat;
    }

    public void getChatWithId(String id, Client<Chat> client) {
        mDatabaseReference.child(CHATS).child(id).addListenerForSingleValueEvent(
                new DataQuery(snapshot -> client.supply(convertToChat(snapshot))));
    }

    private boolean hasMessageListenerForChat(Chat chat) {
        return messageListeners.containsKey(chat.getId());
    }

    private void createMessageListenerForChat(Chat chat) {
        MessageListener messageListener = new MessageListener(Collections.emptySet());

        //Connect our listener to the chat in our database
        DatabaseReference ref = mDatabaseReference.child(CHATS).child(chat.getId()).child(MESSAGES);
        ref.addChildEventListener(messageListener);

        //Add it to the listener-list so that we can attach clients to it
        messageListeners.put(chat.getId(), messageListener);
    }

    public void removeChatMessageClient(Chat chat, Client<DataChange<Message>> client) {
        if (!messageListeners.containsKey(chat.getId())) {
            Log.e(TAG, String.format("WARNING: Tried removing client: [%s] from chat with id: [%s]", client.toString(), chat.getId()));
            return;
        }

        messageListeners.get(chat.getId()).removeClient(client);
    }

    public void registerChatListener(Client<DataChange<Chat>> client) {
        DatabaseReference chatReference = fetchChats();
        chatReference.keepSynced(true);

        chatReference.addChildEventListener(new ChatListener(client));
    }

    // TODO: Implement fetchMatchingPool and registerPoolListener instead of getMatchingPool /Andreas
    private DatabaseReference fetchMatchingPool() {
        return mDatabaseReference.child(POOL);
    }

    public void registerPoolListener(Client<DataChange<User>> client) {
        DatabaseReference poolReference = fetchMatchingPool();
        poolReference.keepSynced(true);

        poolReference.addChildEventListener(new UserPoolListener(client));
    }

    public void getUserById(String uid, Client<User> client) {
        User cachedUser = DataCache.INSTANCE.getCachedUserById(uid);
        if (cachedUser != null) {
            client.supply(cachedUser);
            return;
        }

        mDatabaseReference.child(USER).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = convertToUser(dataSnapshot);
                DataCache.INSTANCE.cache(user);

                client.supply(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void addUserToPool(User user) {
        mDatabaseReference.child(POOL).child(user.getUid()).setValue(user);
    }

    public void addUser() {
        mDatabaseReference.child(USER).child(getActiveUserId()).setValue(getLoggedInUser());
    }


    public void removeUserFromPool(User user) {
        mDatabaseReference.child(POOL).child(user.getUid()).setValue(null);
    }

    public String getActiveUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String uid = profile.getUid();
                if (uid != null)
                    return uid;
            }
        }

        return null;
    }

    public User getLoggedInUser() {
        if (mLoggedInUser == null) {
            String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String uid = getActiveUserId();
            mLoggedInUser = new UserProfile(name, uid);
        }

        return mLoggedInUser;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String generateId() {
        return mDatabaseReference.push().getKey();
    }

    public void postMessageToChat(Chat chat, Message message) {
        getChatWithId(chat.getId(), chatInDatabase -> {
            if (chatInDatabase != null) {
                mDatabaseReference.child(CHATS).child(chat.getId()).child(MESSAGES).push().setValue(message);
            }
        });
    }

    public void pushChat(Chat chat) {
        mDatabaseReference.child(CHATS).child(chat.getId()).setValue(chat);
    }

    public void sendObject(String child, Object object ){
        /*getChatWithId(chatId, chat -> {
            Banner banner = getBans(getActiveUserId());
            banner.addBan(getActiveUserId(), chat.getFirstUser().getUid(), chat.getSecondUser().getUid());
            mDatabaseReference.child(BANS).child(getActiveUserId()).setValue(banner);
            //mDatabaseReference.child("Global"+BANS).push().setValue(strangerID);
        });*/
        mDatabaseReference.child(BANS).child(getActiveUserId()).setValue(object);
    }

    public Banner getBans(String uID){
        mDatabaseReference.child(BANS).child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBanner = dataSnapshot.getValue(Banner.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBanner = new Banner();

            }

        });
        return mBanner;

    }
    
}
