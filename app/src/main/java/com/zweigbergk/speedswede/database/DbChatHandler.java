package com.zweigbergk.speedswede.database;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
import com.zweigbergk.speedswede.core.Pair;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.eventListener.MessageListener;
import com.zweigbergk.speedswede.database.eventListener.ChatListener;
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.KeyValuePair;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.PreferenceValue;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.Statement;
import com.zweigbergk.speedswede.util.ProductLock;
import com.zweigbergk.speedswede.util.methodwrapper.StateRequirement;

import static com.zweigbergk.speedswede.Constants.CHATS;
import static com.zweigbergk.speedswede.Constants.FIRST_USER;
import static com.zweigbergk.speedswede.Constants.MESSAGES;
import static com.zweigbergk.speedswede.Constants.SECOND_USER;
import static com.zweigbergk.speedswede.core.User.Preference;
import static com.zweigbergk.speedswede.util.Lists.EntryMapping;


public enum DbChatHandler {
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
     * Should <u>not</u> be used explicitly. Use a {@link ChatReference} instead.
     * */
    void postMessageToChat(Chat chat, Message message) {
        mRoot.child(CHATS).child(chat.getId()).child(Constants.MESSAGES).push().setValue(message);
    }

    /**
     * Should <u>not</u> be used explicitly. Use DatabaseHandler.get(user).push instead.
     * */
    void pushChat(Chat chat) {
        Log.d(TAG, chat.getName());

        DatabaseReference ref = mRoot.child(CHATS).child(chat.getId());

        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    pushPreferences(chat);
                    Log.d(TAG, "In listener where pushPreferences is called");
                    ref.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mRoot.child(CHATS).child(chat.getId()).addValueEventListener(listener);
        mRoot.child(CHATS).child(chat.getId()).setValue(chat);
    }

    /**
     * Remove user preferences in a chat so that it can be pushed.
     * @return the stripped preferences. Index 0 holds first user preferences,
     * index 1 holds second user preferences.
     */
    private Pair<Map<Preference, PreferenceValue>> stripPreferences(Chat chat) {
        UserProfile firstUser = (UserProfile) chat.getFirstUser();
        UserProfile secondUser = (UserProfile) chat.getSecondUser();
        Map<Preference, PreferenceValue> firstMap = firstUser.getPreferences();
        Map<Preference, PreferenceValue> secondMap = secondUser.getPreferences();

        chat.setFirstUser(firstUser.withPreferences(null));

        return new Pair<>(firstMap, secondMap);
    }

    /**
     * Push the preferences of the chat's users into the chat
     */
    private void pushPreferences(Chat chat) {
        String firstUid = chat.getFirstUser().getUid();
        String secondUid = chat.getSecondUser().getUid();

        Map<Preference, PreferenceValue> firstUserPrefs = chat.getFirstUser().getPreferences();
        Map<Preference, PreferenceValue> secondUserPrefs = chat.getSecondUser().getPreferences();

        Map<String, String> pojoMap = Lists.map(firstUserPrefs, createPojoEntry);

        mRoot.child(CHATS).child(chat.getId()).child(FIRST_USER).child(Constants.PREFERENCES).setValue(pojoMap);

        pojoMap = Lists.map(secondUserPrefs, createPojoEntry);
        mRoot.child(CHATS).child(chat.getId()).child(SECOND_USER).child(Constants.PREFERENCES).setValue(pojoMap);
    }

    private static final EntryMapping<String, String> createPojoEntry = mapEntry -> {
        String prefAsString = parseToReadable((Preference) mapEntry.getKey());

        PreferenceValue prefValue = (PreferenceValue) mapEntry.getValue();
        String prefValueAsString = parseToReadable(prefValue);

        return new KeyValuePair<>(prefAsString, prefValueAsString);
    };

    private static String parseToReadable(PreferenceValue prefValue) {
        if (prefValue == null || prefValue.getValue() == null) {
            return null;
        }

        String value = prefValue.getValue().toString();

        return value;
    }

    private static String parseToReadable(Preference preference) {
        if (preference == null) {
            return "";
        }

        String name = preference.toString().toLowerCase();
        StringBuilder prettyName = new StringBuilder();

        for (int i = 0; i < name.length(); ++i) {
            if (name.charAt(i) == '_') {
                prettyName.append(Character.toUpperCase(name.charAt(++i)));
                continue;
            }

            prettyName.append(name.charAt(i));
        }

        return prettyName.toString();
    }

    ProductBuilder<Chat> createChatFrom(DataSnapshot snapshot) {
        return ChatFactory.serializeChat(snapshot);
    }

    void delete(DatabaseReference ref) {
        ref.removeValue();
    }

    public void addMesageClient(Chat chat, Client<DataChange<Message>> client) {
        if (!hasMessageListenerForChat(chat)) {
            createMessageListenerForChat(chat);
        }

        messageListeners.get(chat.getId()).bind(client);
    }



    ProductBuilder<List<Message>> pullMessages(Chat chat) {
        final ProductBuilder<List<Message>> builder = ProductBuilder.shell();
        builder.attachLocks(ProductLock.MESSAGE_LIST);

        List<Message> messages = new ArrayList<>();

        final MessageListener listener = new MessageListener();
        Client<DataChange<Message>> client = change -> {
            Message message = change.getItem();
            messages.add(message);
            builder.updateState();
            Log.d(TAG, "Adding message: " + message.getText());
        };

        listener.setIdentifier("pullMessageListener");


        listener.bind(client);

        mRoot.child(CHATS).child(chat.getId()).child(MESSAGES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long messageCount = dataSnapshot.getChildrenCount();
                StateRequirement<List> hasAllMessages = list -> list.size() == messageCount;

                builder.setBlueprint(items -> items.getList(ProductLock.MESSAGE_LIST));


                builder.requireState(ProductLock.MESSAGE_LIST, hasAllMessages);
                builder.addItem(ProductLock.MESSAGE_LIST, messages);



                mRoot.child(CHATS).child(chat.getId()).child(MESSAGES).addChildEventListener(
                        listener);

                builder.addExecutable(() -> {
                    mRoot.child(CHATS).child(chat.getId())
                        .child(MESSAGES).removeEventListener(listener);
                    Log.d(TAG, "MessageBuilder finished.");
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return builder;
    }

    boolean hasMessageListenerForChat(Chat chat) {
        return messageListeners.containsKey(chat.getId());
    }

    public void removeMessageClient(Chat chat, Client<DataChange<Message>> client) {
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

    public void deleteChat(Chat chat) {
        delete(mRoot.child(CHATS).child(chat.getId()));
    }

}
