package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;

import com.zweigbergk.speedswede.database.eventListener.DataQuery;
import com.zweigbergk.speedswede.database.eventListener.FirebaseDataListener;
import com.zweigbergk.speedswede.database.eventListener.MessageListener;
import com.zweigbergk.speedswede.database.eventListener.WellBehavedChatListener;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.ProductLock;
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DbChatHandler {
    INSTANCE;

    public enum Node { CHATS }

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

    private WellBehavedChatListener mChatsListener;

    public void initialize() {
        mRoot = FirebaseDatabase.getInstance().getReference();

        messageListeners = new HashMap<>();
        registerListener(Node.CHATS);
    }

    private void registerListener(Node type) {
        switch (type) {
            case CHATS:
                registerChatsListener();
        }
    }

    private void registerChatsListener() {
        mChatsListener = new WellBehavedChatListener();

        String uid = DbUserHandler.INSTANCE.getLoggedInUserId();
        mRoot.child(CHATS).orderByChild("firstUser/uid").equalTo(uid).
                addChildEventListener(mChatsListener);

        mRoot.child(CHATS).orderByChild("secondUser/uid").equalTo(uid).
                addChildEventListener(mChatsListener);
    }

    public FirebaseDataListener<Chat> onChatChanged() {
        return mChatsListener;
    }

    public DatabaseReference getDbRoot() {
        return mRoot;
    }

    public void setChatAttribute(Chat chat, ChatAttribute attribute, Object value) {
        String key = attribute.getDbKey();

        mRoot.child(CHATS).child(chat.getId()).child(key).setValue(value);
    }

    public void removeActiveUserFromChat(Chat chat) {
        if(DbUserHandler.INSTANCE.getLoggedInUserId() == chat.getFirstUser().getUid()) {
            setChatAttribute(chat, ChatAttribute.FIRST_USER, null);
            chat.setFirstUser(null);
        }
        else if(DbUserHandler.INSTANCE.getLoggedInUserId() == chat.getSecondUser().getUid()) {
            setChatAttribute(chat, ChatAttribute.SECOND_USER, null);
            chat.setSecondUser(null);
        }
    }

    private List<Chat> getChatListBlueprint(Map<ProductLock, Object> items) {
        List<Chat> chats = new ArrayList<>();
        List list = (List) items.get(ProductLock.CHAT_LIST);
        Lists.addAll(list, chats);

        return chats;
    }

    public void getChatsByActiveUser(Client<List<Chat>> client) {
        ProductBuilder<List<Chat>> chatBuilder =
                new ProductBuilder<>(this::getChatListBlueprint, ProductLock.CHAT_LIST);

        List<Chat> chats = new ArrayList<>();
        chatBuilder.addItem(ProductLock.CHAT_LIST, chats);
        chatBuilder.thenNotify(client);


        Log.d(TAG, "getChatsByActiveUser");
        String uid = DbUserHandler.INSTANCE.getLoggedInUserId();
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
    }

    public void getChatWithId(String id, Client<Chat> client) {
        mRoot.child(CHATS).child(id).addListenerForSingleValueEvent(
                new DataQuery(snapshot -> ChatFactory.createChatFrom(snapshot, client::supply)));
    }

    public ProductBuilder<Chat> createChatFrom(DataSnapshot snapshot) {
        return ChatFactory.serializeChat(snapshot);
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
                    createChatFrom(dataSnapshot).thenPassTo(client);
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

    private void createMessageListenerForChat(Chat chat) {
        MessageListener messageListener = new MessageListener(Collections.emptySet());

        //Connect our listener to the chat in our database
        DatabaseReference ref = mRoot.child(CHATS).child(chat.getId()).child(MESSAGES);
        ref.addChildEventListener(messageListener);

        //Add it to the listener-list so that we can attach clients to it
        messageListeners.put(chat.getId(), messageListener);
    }


}
