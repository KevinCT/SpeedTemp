package com.zweigbergk.speedswede.database;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.eventListener.MessageListener;
import com.zweigbergk.speedswede.database.eventListener.WellBehavedChatListener;
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;

import static com.zweigbergk.speedswede.Constants.CHATS;
import static com.zweigbergk.speedswede.Constants.USERS;

enum DbChatHandler {
    INSTANCE;

    public static final String TAG = DbChatHandler.class.getSimpleName().toUpperCase();

    private DatabaseReference mRoot;

    private Map<String, MessageListener> messageListeners;

    private WellBehavedChatListener mChatListener;

    public static DbChatHandler getInstance() {
        return INSTANCE;
    }

    void initialize() {
        mRoot = FirebaseDatabase.getInstance().getReference();

        messageListeners = new HashMap<>();
    }

    void registerChatsListener() {
        mChatListener = new WellBehavedChatListener();

        String uid = DbUserHandler.INSTANCE.getActiveUserId();

        Query firstUserRef = mRoot.child(CHATS).orderByChild("firstUser/uid").equalTo(uid);
        firstUserRef.keepSynced(true);
        firstUserRef.addChildEventListener(mChatListener);

        Query secondUserRef = mRoot.child(CHATS).orderByChild("secondUser/uid").equalTo(uid);
        secondUserRef.keepSynced(true);
        secondUserRef.addChildEventListener(mChatListener);
    }

    WellBehavedChatListener getChatListener() {
        return mChatListener;
    }

    void setChatAttribute(Chat chat, ChatManipulator.ChatAttribute attribute, Object value) {
        String key = attribute.getDbKey();

        mRoot.child(CHATS).child(chat.getId()).child(key).setValue(value);
    }

    void removeActiveUserFromChat(Chat chat) {
        User activeUser = DbUserHandler.INSTANCE.getActiveUser();
        DatabaseHandler.get(chat).removeUser(activeUser);
    }

    /**
     * Should <u>not</u> be used explicitly. Use a {@link ChatManipulator} instead.
     * */
    void postMessageToChat(Chat chat, Message message) {
        mRoot.child(CHATS).child(chat.getId()).child(Constants.MESSAGES).push().setValue(message);
    }

    void pushChat(Chat chat) {
        mRoot.child(CHATS).child(chat.getId()).setValue(chat);
    }

    ProductBuilder<Chat> createChatFrom(DataSnapshot snapshot) {
        return ChatFactory.serializeChat(snapshot);
    }

    void delete(DatabaseReference ref) {
        ref.removeValue();
    }

    void convertToChatById(String id, Client<Chat> client) {
        DatabaseReference ref = mRoot.child(CHATS).child(id).getRef();
        Log.d(TAG, String.format("convertToChatById for ID: [%s]\nref: %s", id, ref.toString()));
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    Log.d(TAG, "Inside ValueEventListener, onDataChange. Snapshot: " + dataSnapshot.getValue());
                    createChatFrom(dataSnapshot).thenPassTo(client);
                } else {
                    DatabaseReference invalidRef = mRoot.child(Constants.USER_TO_CHAT).child(id);
                    delete(invalidRef);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    void addMesageClient(Chat chat, Client<DataChange<Message>> client) {
        if (!hasMessageListenerForChat(chat)) {
            createMessageListenerForChat(chat);
        }

        messageListeners.get(chat.getId()).bind(client);
    }

    boolean hasMessageListenerForChat(Chat chat) {
        return messageListeners.containsKey(chat.getId());
    }

    void removeMessageClient(Chat chat, Client<DataChange<Message>> client) {
        if (!messageListeners.containsKey(chat.getId())) {
            Log.e(TAG, String.format("WARNING: Tried removing client: [%s] from chat with id: [%s]," +
                    " but the client was not attached to the message listener.",
                    client.toString(), chat.getId()));
            return;
        }

        messageListeners.get(chat.getId()).unbind(client);
    }

    private void createMessageListenerForChat(Chat chat) {
        MessageListener messageListener = new MessageListener(Collections.emptySet());

        //Connect our listener to the chat in our database
        DatabaseReference ref = mRoot.child(CHATS).child(chat.getId()).child(Constants.MESSAGES);
        ref.addChildEventListener(messageListener);

        //Add it to the listener-list so that we can attach clients to it
        messageListeners.put(chat.getId(), messageListener);
    }


}
