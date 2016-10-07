package com.zweigbergk.speedswede.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.ProductBuilder.ItemMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ChatFactory {

    public static final String TAG = ChatFactory.class.getSimpleName().toUpperCase();

    public final static String USER_1_NAME = "Sir";
    public final static String USER_2_NAME = "Lord";
    public final static String USER_3_NAME = "Igor";

    public final static String USER_1_ID = "uid_user1";
    public final static String USER_2_ID = "uid_user2";
    public final static String USER_3_ID = "uid_user3";

    public static User mockUser(String name, String uid) {
        return new UserProfile(name, uid);
    }

    public static void createChat(Collection<Client<Chat>> clients) {
        String activeUserId = DatabaseHandler.getInstance().getActiveUserId();

        ProductBuilder<Chat> builder = new ProductBuilder<>(newChatBlueprint);
        builder.attachLocks(ProductLock.FIRST_USER,
                ProductLock.SECOND_USER);

        Lists.forEach(clients, builder::thenNotify);

        //Append active user
        DatabaseHandler.users().pull(activeUserId).then(user -> builder.addItem(ProductLock.FIRST_USER, user));

        //Append test user
        DatabaseHandler.users().pull(Constants.TEST_USER_UID).then(user -> builder.addItem(ProductLock.SECOND_USER, user));
    }

    /** Supplies a Client with a Chat created from a DataSnapshot. Returns null if the snapshot
     * points to nothing. */
    public static void createChatFrom(DataSnapshot snapshot, Client<Chat> client) {
        if (snapshot.getValue() == null) {
            Log.e(TAG, String.format(
                    "WARNING! Tried to convert non-existing reference to a chat. (Reference: %s)",
                    snapshot.getRef().toString()));
            client.supply(null);
            return;
        }

        serializeChat(snapshot).thenNotify(client);
    }

    public static ProductBuilder<Chat> serializeChat(DataSnapshot snapshot) {
        ProductBuilder<Chat> chatBuilder = new ProductBuilder<>(ChatFactory::getReconstructionBlueprint);

        chatBuilder.attachLocks(ProductLock.ID, ProductLock.NAME, ProductLock.TIMESTAMP, ProductLock.MESSAGE_LIST,
                ProductLock.FIRST_USER, ProductLock.SECOND_USER);

        String chatId = snapshot.getKey();
        String name = (String) snapshot.child(Constants.NAME).getValue();

        long chatTimestamp = (long) snapshot.child(Constants.TIMESTAMP).getValue();

        Iterable<DataSnapshot> messageSnapshots = snapshot.child(Constants.MESSAGES).getChildren();
        List<Message> messageList = asMessageList(messageSnapshots);

        String firstUserId = ChatFactory.getUserId(snapshot.child(Constants.FIRST_USER));
        String secondUserId = ChatFactory.getUserId(snapshot.child(Constants.SECOND_USER));

        chatBuilder.addItem(ProductLock.ID, chatId);
        chatBuilder.addItem(ProductLock.NAME, name);
        chatBuilder.addItem(ProductLock.TIMESTAMP, chatTimestamp);
        chatBuilder.addItem(ProductLock.MESSAGE_LIST, messageList);

        DatabaseHandler.users().pull(firstUserId).then(user -> chatBuilder.addItem(ProductLock.FIRST_USER, user));
        DatabaseHandler.users().pull(secondUserId).then(user -> chatBuilder.addItem(ProductLock.SECOND_USER, user));

        return chatBuilder;
    }

    private static List<Message> asMessageList(Iterable<DataSnapshot> snapshot) {
        List<Message> messages = new ArrayList<>();
        Lists.forEach(snapshot, messageSnapshot -> {
            Message message = messageSnapshot.getValue(Message.class);
            messages.add(message);
        });

        return messages;
    }

    private static ProductBuilder.Blueprint<Chat> newChatBlueprint = items -> {
        User user1 = items.getUser(ProductLock.FIRST_USER);
        User user2 = items.getUser(ProductLock.SECOND_USER);

        return new Chat(user1, user2);
    };

    private static Chat getReconstructionBlueprint(ItemMap items) {
        User user1 = items.getUser(ProductLock.FIRST_USER);
        User user2 = items.getUser(ProductLock.SECOND_USER);

        long timestamp = items.getLong(ProductLock.TIMESTAMP);
        String id = items.getString(ProductLock.ID);
        String name = items.getString(ProductLock.NAME);

        List<Message> messages = new ArrayList<>();
        List list = items.getList(ProductLock.MESSAGE_LIST);
        Lists.addAll(messages, list);

        return new Chat(id, name, timestamp, messages, user1, user2);
    }

    private static String getUserId(DataSnapshot snapshot) {
        Object value = snapshot.child(Constants.USER_ID).getValue();
        String userId;
        try {
            userId = (String) value;
        } catch (ClassCastException e) {
            HashMap<String, Object> mapping = (HashMap) value;
            userId = (String) mapping.get(Constants.USER_ID);
        }

        if (userId == null) {
            Log.e(TAG, String.format(
                    "WARNING! Can not extract User ID for a user in chat [Chat ID: %s].\n(Path: %s)",
                    snapshot.getRef().getParent().getKey(),
                    snapshot.getRef().toString()));
        }

        return userId;
    }
}
