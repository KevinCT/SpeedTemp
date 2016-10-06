package com.zweigbergk.speedswede.database;

import android.util.Log;

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
import com.zweigbergk.speedswede.util.ProductLock;
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
    public static final String USER_TO_CHAT = "user_chat";
    public static final String MESSAGES = "messages";
    public static final String TIMESTAMP = "timeStamp";
    public static final String FIRST_USER = "firstUser";
    public static final String SECOND_USER = "secondUser";
    public static final String NAME = "name";


    private DatabaseReference mRoot;

    private Map<String, MessageListener> messageListeners;
    private ChatListener chatsListener;

    private UserToChatListener mUserToChatListener;

    public void initialize() {
        mRoot = FirebaseDatabase.getInstance().getReference();

        messageListeners = new HashMap<>();
       // initializeChatsListener();
        initializeUserToChatListener();

        tryQuery();
    }

    private void tryQuery() {
        Log.d(TAG, "Start");

        mRoot.child(CHATS).orderByChild(FIRST_USER).equalTo(DbUserHandler.INSTANCE.getActiveUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "First user: " + dataSnapshot.getRef().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRoot.child(CHATS).orderByChild(SECOND_USER).equalTo(DbUserHandler.INSTANCE.getActiveUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Second user: " + dataSnapshot.getRef().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    enum ChatAttribute {
        FIRST_USER, SECOND_USER;

        public String getDbKey() {
            switch(this) {
                case FIRST_USER:
                    return DbChatHandler.FIRST_USER;
                case SECOND_USER:
                    return DbChatHandler.SECOND_USER;
                default:
                    return null;
            }
        }
    }

    public void setChatAttribute(Chat chat, ChatAttribute attribute, Object value) {
        String key = attribute.getDbKey();

        mRoot.child(CHATS).child(chat.getId()).child(key).setValue(value);
    }

    public void removeActiveUserFromChat(Chat chat) {
        if(DbUserHandler.INSTANCE.getActiveUserId() == chat.getFirstUser().getUid()) {
            setChatAttribute(chat, ChatAttribute.FIRST_USER, null);
            chat.setFirstUser(null);
        }
        else if(DbUserHandler.INSTANCE.getActiveUserId() == chat.getSecondUser().getUid()) {
            setChatAttribute(chat, ChatAttribute.SECOND_USER, null);
            chat.setSecondUser(null);
        }
    }

    private void initializeUserToChatListener() {
        mUserToChatListener = new UserToChatListener();
        DatabaseReference ref = mRoot.child(USER_TO_CHAT)
                .child(DbUserHandler.INSTANCE.getActiveUserId());

        ref.addChildEventListener(mUserToChatListener);
        ref.keepSynced(true);
    }

    // TODO Remove chats listener? We shouldn't have a listener that observes EVERY chat, right?
    private void initializeChatsListener() {
        chatsListener = new ChatListener();
        DatabaseReference ref = mRoot.child(CHATS);
        ref.addChildEventListener(chatsListener);
        ref.keepSynced(true);
    }

    private List<Chat> getChatListBlueprint(Map<ProductLock, Object> items) {
        return (List) items.get(ProductLock.CHAT_LIST);
    }

    public void getChatsByActiveUser(Client<List<Chat>> client) {
        ProductBuilder<List<Chat>> chatBuilder =
                new ProductBuilder<>(this::getChatListBlueprint, ProductLock.CHAT_LIST);

        List<Chat> chats = new ArrayList<>();
        chatBuilder.addItem(ProductLock.CHAT_LIST, chats);
        chatBuilder.addClient(client);


        Log.d(TAG, "getChatsByActiveUser");
        String uid = DbUserHandler.INSTANCE.getActiveUserId();
        DatabaseReference ref = mRoot.child(USER_TO_CHAT).child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getChatsByActiveUser:onDataChange");
                long chatCount = dataSnapshot.getChildrenCount();

                //Require (make sure) that we have gotten all chats before returning the object
                // from the chatBuilder
                chatBuilder.requireState(ProductLock.CHAT_LIST, object -> ((List) object).size() == chatCount);

                for (DataSnapshot idSnapshot : dataSnapshot.getChildren()) {
                    DbChatHandler.INSTANCE.convertToChatById(idSnapshot.getKey(), chats::add);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ChatListCompiler loadChats(DataSnapshot chatIdsSnapshot) {
        ChatListCompiler asyncList = new ChatListCompiler();
        asyncList.run(chatIdsSnapshot);

        return asyncList;
    }

    public void postMessageToChat(Chat chat, Message message) {
        DatabaseReference ref = mRoot.child(CHATS).child(chat.getId());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean chatExists = dataSnapshot.exists();
                if (chatExists) {
                    Log.d(TAG, "Chat wasn't null, posting message!");
                    mRoot.child(CHATS).child(chat.getId()).child(MESSAGES).push().setValue(message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void pushChat(Chat chat) {
        mRoot.child(CHATS).child(chat.getId()).setValue(chat);
        mRoot.child(USER_TO_CHAT).child(DbUserHandler.INSTANCE.getActiveUserId()).child(chat.getId()).setValue(1);
    }

    public void getUserChats(User user) {
        mRoot.child(USER_TO_CHAT).child(user.getUid()).addChildEventListener(mUserToChatListener);
    }

    public void addUserChatsClient(Client<List<Chat>> client) {
//        mUserToChatListener.addClient(client);
    }

    public void getChatWithId(String id, Client<Chat> client) {
        mRoot.child(CHATS).child(id).addListenerForSingleValueEvent(
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
        chatBuilder.attachLocks(ProductLock.ID, ProductLock.NAME, ProductLock.TIMESTAMP, ProductLock.MESSAGE_LIST,
                ProductLock.FIRST_USER, ProductLock.SECOND_USER);

        chatBuilder.addClient(client);

        String chatId = snapshot.getKey();
        String name = (String) snapshot.child(NAME).getValue();

        long chatTimestamp = (long) snapshot.child(TIMESTAMP).getValue();

        Iterable<DataSnapshot> messageSnapshots = snapshot.child(MESSAGES).getChildren();
        List<Message> messageList = asMessageList(messageSnapshots);

        String firstUserId = ChatFactory.getUserId(snapshot.child(FIRST_USER));
        String secondUserId = ChatFactory.getUserId(snapshot.child(SECOND_USER));

        chatBuilder.addItem(ProductLock.ID, chatId);
        chatBuilder.addItem(ProductLock.NAME, name);
        chatBuilder.addItem(ProductLock.TIMESTAMP, chatTimestamp);
        chatBuilder.addItem(ProductLock.MESSAGE_LIST, messageList);
        DbUserHandler.INSTANCE.getUserById(firstUserId, user -> chatBuilder.addItem(ProductLock.FIRST_USER, user));
        DbUserHandler.INSTANCE.getUserById(secondUserId, user -> chatBuilder.addItem(ProductLock.SECOND_USER, user));
    }

    public void delete(DatabaseReference ref) {
        ref.removeValue();
    }

    public void convertToChatById(String id, Client<Chat> client) {
        DatabaseReference ref = mRoot.child(CHATS).child(id).getRef();
        Log.d(TAG, String.format("convertToChatById for ID: [%s]\nref: %s", id, ref.toString()));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "Inside ValueEventListener, onDataChange. Snapshot: " + dataSnapshot.getValue());
                    convertToChat(dataSnapshot, client::supply);
                } else {
                    DatabaseReference invalidRef = mRoot.child(USER_TO_CHAT).child(id);
                    delete(invalidRef);
                }
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
        DatabaseReference ref = mRoot.child(CHATS).child(chat.getId()).child(MESSAGES);
        ref.addChildEventListener(messageListener);

        //Add it to the listener-list so that we can attach clients to it
        messageListeners.put(chat.getId(), messageListener);
    }


}
