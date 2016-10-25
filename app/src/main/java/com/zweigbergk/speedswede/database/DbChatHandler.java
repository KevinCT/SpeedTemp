package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.database.eventListener.MessageListener;
import com.zweigbergk.speedswede.database.eventListener.ChatListener;
import com.zweigbergk.speedswede.util.async.FirebasePromise;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.Collections;
import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.collection.MapExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.async.PromiseNeed;

import static com.zweigbergk.speedswede.Constants.CHATS;
import static com.zweigbergk.speedswede.Constants.FIRST_USER;
import static com.zweigbergk.speedswede.Constants.MESSAGES;
import static com.zweigbergk.speedswede.Constants.SECOND_USER;


class DbChatHandler extends DbTopLevelHandler {
    private static DbChatHandler INSTANCE;

    private static final String TAG = DbChatHandler.class.getSimpleName().toUpperCase();

    private DatabaseReference mRoot = FirebaseDatabase.getInstance().getReference();

    private MapExtension<String, MessageListener> messageListeners = new HashMapExtension<>();

    private ChatListener mChatListener;

    private DbChatHandler() {

    }

    public static synchronized DbChatHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DbChatHandler();
        }

        return INSTANCE;
    }

    Statement exists(Chat chat) {
        return hasReference(mRoot.child(CHATS).child(chat.getId()));
    }

    private void registerChatsListener() {
        mChatListener = new ChatListener();

        String uid = DbUserHandler.getInstance().getActiveUserId();
        Log.d(TAG, "Active user ID: " + uid);

        Query firstUserRef = mRoot.child(CHATS).orderByChild("firstUser/uid").equalTo(uid);
        firstUserRef.keepSynced(true);
        firstUserRef.addChildEventListener(mChatListener);

        Query secondUserRef = mRoot.child(CHATS).orderByChild("secondUser/uid").equalTo(uid);
        secondUserRef.keepSynced(true);
        secondUserRef.addChildEventListener(mChatListener);
    }

    ChatListener getChatListener() {
        if (mChatListener == null) {
            registerChatsListener();
        }

        return mChatListener;
    }

    void setChatAttribute(Chat chat, ChatReference.ChatAttribute attribute, Object value) {
        String key = attribute.getDbKey();

        mRoot.child(CHATS).child(chat.getId()).child(key).setValue(value);
    }

    /**
     * Should <u>not</u> be used explicitly. Use a {@link ChatReference} instead.
     * */
    void postMessageToChat(Chat chat, Message message) {
        mRoot.child(CHATS).child(chat.getId()).child(Constants.MESSAGES).push().setValue(message);
    }

    /**
     * Should <u>not</u> be used explicitly. Use DatabaseHandler.getReference(user).push instead.
     * */
    void pushChat(Chat chat) {
        Log.d(TAG, "Push chat: " + chat.getName());
        mRoot.child(CHATS).child(chat.getId()).setValue(chat);
    }

    void delete(DatabaseReference ref) {
        ref.removeValue();
    }

    void addMessageClient(Chat chat, Client<DataChange<Message>> client) {
        if (hasMessageListenerForChat(chat)) {
            //If the listener is already there, we must explicitly pass every existing message
            // to our new client
            DatabaseHandler.getReference(chat).pullMessages().then(
                    list -> list.foreach(message -> client.supply(DataChange.added(message))));

        } else {
            createMessageListenerForChat(chat);
        }

        messageListeners.get(chat.getId()).bind(client);
    }

    Promise<ListExtension<Message>> pullMessages(Chat chat) {
        Promise<ListExtension<Message>> messagesPromised = Promise.create();
        messagesPromised.requires(PromiseNeed.SNAPSHOT);
        messagesPromised.setResultForm(items -> {
            ListExtension<Message> result = new ArrayListExtension<>();
           DataSnapshot messageSnapshot = items.getSnapshot(PromiseNeed.SNAPSHOT);
            for (DataSnapshot snapshot : messageSnapshot.getChildren()) {
                result.add(snapshot.getValue(Message.class));
            }

            return result;
        });

        FirebasePromise snapshotPromise = new FirebasePromise(databasePath(CHATS, chat.getId(), MESSAGES));
        return snapshotPromise.thenPromise(PromiseNeed.SNAPSHOT, messagesPromised);
    }

    private boolean hasMessageListenerForChat(Chat chat) {
        return messageListeners.containsKey(chat.getId());
    }

    void removeMessageClient(Chat chat, Client<DataChange<Message>> client) {
        if (!messageListeners.containsKey(chat.getId())) {
            Log.e(TAG, String.format("WARNING: Tried removing client: [%s] from chat with id: [%s]," +
                    " but the client was not attached to the message listener.",
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

    Statement hasUsers(Chat chat) {
        Statement statement = new Statement();
          hasReference(mRoot.child(CHATS).child(chat.getId()).child(FIRST_USER)).then(
                    firstExists -> {
                        hasReference(mRoot.child(CHATS).child(chat.getId()).child(SECOND_USER)).then(
                                secondExists -> {
                                    statement.setReturnValue(firstExists && secondExists);
                                }
                        );
                 }
         );
        return statement;
    }

    void deleteChat(Chat chat) {
        delete(mRoot.child(CHATS).child(chat.getId()));
    }


}
