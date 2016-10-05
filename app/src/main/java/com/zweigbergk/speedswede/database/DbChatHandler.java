package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.eventListener.ChatListener;
import com.zweigbergk.speedswede.database.eventListener.DataQuery;
import com.zweigbergk.speedswede.database.eventListener.MessageListener;
import com.zweigbergk.speedswede.database.eventListener.UserToChatListener;
import com.zweigbergk.speedswede.util.BuilderKey;
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.ProductBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DbChatHandler {
    INSTANCE;

    public static final String TAG = DbChatHandler.class.getSimpleName().toUpperCase();

    public static final String CHATS = "chats";
    public static final String USER_CHAT = "user_chat";
    public static final String MESSAGES = "messages";
    public static final String TIMESTAMP = "timeStamp";
    public static final String FIRST_USER = "firstUser";
    public static final String SECOND_USER = "secondUser";


    private DatabaseReference mDatabaseReference;

    private Map<String, MessageListener> messageListeners;
    private ChatListener chatsListener;

    private UserToChatListener mUserToChatListener;

    public void initialize() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        messageListeners = new HashMap<>();
        initializeChatsListener();
        initializeUserToChatListener();
    }

    public void removeUserFromChat(Chat chat, User user) {
        if(DbUserHandler.INSTANCE.getActiveUserId() == chat.getFirstUser().getUid()) {
            chat.setFirstUser(null);
        }
        else if(DbUserHandler.INSTANCE.getActiveUserId() == chat.getSecondUser().getUid()) {
            chat.setSecondUser(null);
        }
    }

    private void initializeUserToChatListener() {
        mUserToChatListener = new UserToChatListener();
        DatabaseReference ref = mDatabaseReference.child(USER_CHAT)
                .child(DbUserHandler.INSTANCE.getActiveUserId());

        ref.addChildEventListener(mUserToChatListener);
        ref.keepSynced(true);
    }

    private void initializeChatsListener() {
        chatsListener = new ChatListener();
        DatabaseReference ref = mDatabaseReference.child(CHATS);
        ref.addChildEventListener(chatsListener);
        ref.keepSynced(true);
    }

    public void getActiveUserChats(Client<List<Chat>> client) {
        List<Chat> result = new ArrayList<>();

        String uid = DbUserHandler.INSTANCE.getActiveUserId();
        DatabaseReference ref = mDatabaseReference.child(USER_CHAT).child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                    convertToChatById(idSnapshot.getKey(), result::add);
                }
                Log.d(TAG, String.format("Returning from getActiveUserChats with %d entries.", result.size()));
                client.supply(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void postMessageToChat(Chat chat, Message message) {
        DatabaseReference ref = mDatabaseReference.child(CHATS).child(chat.getId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean chatExists = dataSnapshot.exists();
                if (chatExists) {
                    Log.d(TAG, "Chat wasn't null, posting message!");
                    mDatabaseReference.child(CHATS).child(chat.getId()).child(MESSAGES).push().setValue(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pushChat(Chat chat) {
        mDatabaseReference.child(CHATS).child(chat.getId()).setValue(chat);
        mDatabaseReference.child(USER_CHAT).child(DbUserHandler.INSTANCE.getActiveUserId()).child(chat.getId()).setValue(1);
    }

    public void getUserChats(User user) {
        mDatabaseReference.child(USER_CHAT).child(user.getUid()).addChildEventListener(mUserToChatListener);
    }

    public void addUserChatsClient(Client<List<Chat>> client) {
//        mUserToChatListener.addClient(client);
    }

    public void getChatWithId(String id, Client<Chat> client) {
        mDatabaseReference.child(CHATS).child(id).addListenerForSingleValueEvent(
                new DataQuery(snapshot -> convertToChat(snapshot, client::supply)));
    }

    /** Supplies a Client with a Chat created from a DataSnapshot. Returns null if the snapshot
     * points to nothing. */
    public void convertToChat(DataSnapshot snapshot, Client<Chat> client) {
        if (snapshot.getValue() == null) {
            Log.e(TAG, String.format(
                    "WARNING! Tried to convert non-existing reference to a chat. (Reference: %s)",
                    snapshot.getRef().toString()));
            client.supply(null);
            return;
        }

        ProductBuilder<Chat> chatBuilder = new ProductBuilder<>(ChatFactory::getReconstructionBlueprint);
        chatBuilder.require(BuilderKey.ID, BuilderKey.NAME, BuilderKey.TIMESTAMP, BuilderKey.MESSAGE_LIST,
                BuilderKey.FIRST_USER, BuilderKey.SECOND_USER);

        chatBuilder.addClient(client);

        String chatId = snapshot.getKey();

        long chatTimestamp = (long) snapshot.child(TIMESTAMP).getValue();

        Iterable<DataSnapshot> messageSnapshots = snapshot.child(MESSAGES).getChildren();
        List<Message> messageList = asMessageList(messageSnapshots);

        String firstUserId = ChatFactory.getUserId(snapshot.child(FIRST_USER));
        String secondUserId = ChatFactory.getUserId(snapshot.child(SECOND_USER));

        chatBuilder.append(BuilderKey.ID, chatId);
        chatBuilder.append(BuilderKey.TIMESTAMP, chatTimestamp);
        chatBuilder.append(BuilderKey.MESSAGE_LIST, messageList);
        DbUserHandler.INSTANCE.getUserById(firstUserId, user -> chatBuilder.append(BuilderKey.FIRST_USER, user));
        DbUserHandler.INSTANCE.getUserById(secondUserId, user -> chatBuilder.append(BuilderKey.SECOND_USER, user));
    }

    public void convertToChatById(String id, Client<Chat> client) {
        DatabaseReference ref = mDatabaseReference.child(CHATS).child(id).getRef();
        Log.d(TAG, String.format("convertToChatById for ID: [%s]\nref: %s", id, ref.toString()));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Inside ValueEventListener, onDataChange. Snapshot: " + dataSnapshot.getValue());
                convertToChat(dataSnapshot, client::supply);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private List<Message> asMessageList(Iterable<DataSnapshot> snapshot) {
        List<Message> messages = new ArrayList<>();
        Lists.forEach(snapshot, messageSnapshot -> {
            Message message = messageSnapshot.getValue(Message.class);
            messages.add(message);
        });

        return messages;
    }

    public void addMesageClient(Chat chat, Client<DataChange<Message>> client) {
        if (!hasMessageListenerForChat(chat)) {
            createMessageListenerForChat(chat);
        }

        messageListeners.get(chat.getId()).addClient(client);
    }

    private boolean hasMessageListenerForChat(Chat chat) {
        return messageListeners.containsKey(chat.getId());
    }

    public void removeMessageClient(Chat chat, Client<DataChange<Message>> client) {
        if (!messageListeners.containsKey(chat.getId())) {
            Log.e(TAG, String.format("WARNING: Tried removing client: [%s] from chat with id: [%s]," +
                    " but the client was not attached to the message listener.",
                    client.toString(), chat.getId()));
            return;
        }

        messageListeners.get(chat.getId()).removeClient(client);
    }

    public void addChatListClient(Client<DataChange<Chat>> client) {
        chatsListener.addClient(client);
    }

    public void removeChatListClient(Client<DataChange<Chat>> client) {
        chatsListener.removeClient(client);
    }

    public void addUserToChatClient(Client<DataChange<Chat>> client) {
        mUserToChatListener.addClient(client);
    }

    public void removeUserToChatClient(Client<DataChange<Chat>> client) {
        mUserToChatListener.removeClient(client);
    }

    private void createMessageListenerForChat(Chat chat) {
        MessageListener messageListener = new MessageListener(Collections.emptySet());

        //Connect our listener to the chat in our database
        DatabaseReference ref = mDatabaseReference.child(CHATS).child(chat.getId()).child(MESSAGES);
        ref.addChildEventListener(messageListener);

        //Add it to the listener-list so that we can attach clients to it
        messageListeners.put(chat.getId(), messageListener);
    }


}
