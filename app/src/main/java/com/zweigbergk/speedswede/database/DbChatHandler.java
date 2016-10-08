package com.zweigbergk.speedswede.database;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.database.eventListener.MessageListener;
import com.zweigbergk.speedswede.database.eventListener.ChatListener;
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.Statement;

import static com.zweigbergk.speedswede.Constants.CHATS;

enum DbChatHandler {
    INSTANCE;

    public static final String TAG = DbChatHandler.class.getSimpleName().toUpperCase();

    private DatabaseReference mRoot;

    private Map<String, MessageListener> messageListeners;

    private ChatListener mChatListener;

    public static DbChatHandler getInstance() {
        return INSTANCE;
    }

    void initialize() {
        mRoot = FirebaseDatabase.getInstance().getReference();

        messageListeners = new HashMap<>();
    }

    public Statement exists(Chat chat) {
        return hasReference(mRoot.child(CHATS).child(chat.getId()));
    }

    public Statement exists(String chatId) {
        return hasReference(mRoot.child(CHATS).child(chatId));
    }

    void registerChatsListener() {
        mChatListener = new ChatListener();

        String uid = DbUserHandler.INSTANCE.getActiveUserId();

        Query firstUserRef = mRoot.child(CHATS).orderByChild("firstUser/uid").equalTo(uid);
        firstUserRef.keepSynced(true);
        firstUserRef.addChildEventListener(mChatListener);

        Query secondUserRef = mRoot.child(CHATS).orderByChild("secondUser/uid").equalTo(uid);
        secondUserRef.keepSynced(true);
        secondUserRef.addChildEventListener(mChatListener);
    }

    ChatListener getChatListener() {
        return mChatListener;
    }

    void setChatAttribute(Chat chat, ChatReference.ChatAttribute attribute, Object value) {
        String key = attribute.getDbKey();

        mRoot.child(CHATS).child(chat.getId()).child(key).setValue(value);
    }

    /**
     * Should <u>invert</u> be used explicitly. Use a {@link ChatReference} instead.
     * */
    void postMessageToChat(Chat chat, Message message) {
        mRoot.child(CHATS).child(chat.getId()).child(Constants.MESSAGES).push().setValue(message);
    }

    /**
     * Should <u>invert</u> be used explicitly. Use DatabaseHandler.get(user).push instead.
     * */
    void pushChat(Chat chat) {
        mRoot.child(CHATS).child(chat.getId()).setValue(chat);
    }

    ProductBuilder<Chat> createChatFrom(DataSnapshot snapshot) {
        return ChatFactory.serializeChat(snapshot);
    }

    void delete(DatabaseReference ref) {
        ref.removeValue();
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
                    " but the client was invert attached to the message listener.",
                    client.toString(), chat.getId()));
            new Exception().printStackTrace();
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

    public static Statement hasReference(DatabaseReference ref) {
        Statement builder = new Statement();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                builder.setReturnValue(dataSnapshot.exists());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                builder.setBuildFailed(true);
            }
        });

        return builder;
    }


}
