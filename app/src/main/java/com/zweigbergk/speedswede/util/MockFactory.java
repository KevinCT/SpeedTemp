package com.zweigbergk.speedswede.util;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class MockFactory {

    public final static String USER_1_NAME = "Sir";
    public final static String USER_2_NAME = "Lord";
    public final static String USER_3_NAME = "Igor";

    public final static String USER_1_ID = "uid_user1";
    public final static String USER_2_ID = "uid_user2";
    public final static String USER_3_ID = "uid_user3";

    public static User mockUser(String name, String uid) {
        return new UserProfile(name, uid);
    }

    public static void runChatBuilder(Collection<Client<Chat>> clients, String chatId) {
        // (String id, long timeStamp, List<Message> messages, User firstUser, User secondUser)
        long timeStamp = new Date().getTime();
        String activeUserId = DatabaseHandler.INSTANCE.getActiveUserId();

        ProductBuilder<Chat> builder = new ProductBuilder<>(chatBlueprint);
        builder.require(BuilderKey.ID, BuilderKey.TIMESTAMP, BuilderKey.FIRST_USER,
                BuilderKey.SECOND_USER);

        for (Client<Chat> client : clients) {
            builder.addClient(client);
        }

        //Append chat ID
        builder.append(BuilderKey.ID, chatId);

        //Append timestamp
        builder.append(BuilderKey.TIMESTAMP, timeStamp);

        //Append active user
        DatabaseHandler.INSTANCE.getUserById(activeUserId, user -> builder.append(BuilderKey.FIRST_USER, user));

        //Append test user
        DatabaseHandler.INSTANCE.getUserById(Constants.TEST_USER_UID, user -> builder.append(BuilderKey.SECOND_USER, user));
    }

    private static ProductBuilder.Blueprint<Chat> chatBlueprint = items -> {
        String newId = (String) items.get(BuilderKey.ID);
        long newTimeStamp = (long) items.get(BuilderKey.TIMESTAMP);
        User user1 = (User) items.get(BuilderKey.FIRST_USER);
        User user2 = (User) items.get(BuilderKey.SECOND_USER);

        return new Chat(newId, newTimeStamp, new ArrayList<>(), user1, user2);
    };

}
