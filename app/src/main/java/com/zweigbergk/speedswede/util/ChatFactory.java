package com.zweigbergk.speedswede.util;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.firebase.DbChatHandler;
import com.zweigbergk.speedswede.database.firebase.DbUserHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatFactory {

    public static final String TAG = ChatFactory.class.getSimpleName().toUpperCase();

    enum ClassType { INT, LONG, STRING }

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
        String activeUserId = DbUserHandler.INSTANCE.getActiveUserId();

        ProductBuilder<Chat> builder = new ProductBuilder<>(newChatBlueprint);
        builder.require(BuilderKey.FIRST_USER,
                BuilderKey.SECOND_USER);

        for (Client<Chat> client : clients) {
            builder.addClient(client);
        }

        //Append active user
        DbUserHandler.INSTANCE.getUserById(activeUserId, user -> builder.append(BuilderKey.FIRST_USER, user));

        //Append test user
        DbUserHandler.INSTANCE.getUserById(Constants.TEST_USER_UID, user -> builder.append(BuilderKey.SECOND_USER, user));
    }

    private static ProductBuilder.Blueprint<Chat> newChatBlueprint = items -> {
        User user1 = (User) items.get(BuilderKey.FIRST_USER);
        User user2 = (User) items.get(BuilderKey.SECOND_USER);

        return new Chat(user1, user2);
    };

    public static Chat getReconstructionBlueprint(Map<BuilderKey, Object> items) {
            User user1 = (User) items.get(BuilderKey.FIRST_USER);
            User user2 = (User) items.get(BuilderKey.SECOND_USER);

            long timestamp = (long) items.get(BuilderKey.TIMESTAMP);
            String id = (String) items.get(BuilderKey.ID);
            List<Message> messages = (List) items.get(BuilderKey.MESSAGE_LIST);

            return new Chat(id, timestamp, messages, user1, user2);
    }

    public static String getUserId(DataSnapshot snapshot) {
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
