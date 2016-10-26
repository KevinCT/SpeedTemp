package com.zweigbergk.speedswede.util.factory;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.async.Commitment;
import com.zweigbergk.speedswede.util.async.Guarantee;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.async.PromiseNeed;
import com.zweigbergk.speedswede.util.Tuple;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.collection.ListExtension;

import static com.zweigbergk.speedswede.util.async.PromiseNeed.*;

public class ChatFactory {

    public static Promise<Chat> deserializeChat(DataSnapshot snapshot) {
        String firstUserId = ChatFactory.getUserId(snapshot.child(Constants.FIRST_USER));
        String secondUserId = ChatFactory.getUserId(snapshot.child(Constants.SECOND_USER));
        Boolean likeStatusFirstUser = ChatFactory.getLikeStatus(snapshot.child(Constants.LIKED_BY_FIRST_USER));
        Boolean likeStatusSecondUser = ChatFactory.getLikeStatus(snapshot.child(Constants.LIKED_BY_SECOND_USER));

        Chat chatWithoutUsers = getChatWithoutUsers(snapshot);
        Commitment<Chat> chatCommitment = new Guarantee<>(chatWithoutUsers);

        Promise<User> firstUserPromised = DatabaseHandler.users().pull(firstUserId);
        Promise<User> secondUserPromised = DatabaseHandler.users().pull(secondUserId);

        Promise.Result<Chat> chatResult = items -> {
            Chat chat = (Chat) items.get(CHAT);
            User firstUser = (User) items.get(FIRST_USER);
            User secondUser = (User) items.get(SECOND_USER);
            chat.setFirstUser(firstUser);
            chat.setSecondUser(secondUser);
            chat.setLikeStatusFirstUser(likeStatusFirstUser);
            chat.setLikeStatusSecondUser(likeStatusSecondUser);
            return chat;
        };

        ListExtension<Tuple<PromiseNeed, Commitment<?>>> commitments = new ArrayListExtension<>();
        commitments.add(new Tuple<>(CHAT, chatCommitment));
        commitments.add(new Tuple<>(FIRST_USER, firstUserPromised));
        commitments.add(new Tuple<>(SECOND_USER, secondUserPromised));

        return Promise.group(chatResult, commitments);
    }

    private static Chat getChatWithoutUsers(DataSnapshot snapshot) {
        String id = snapshot.getKey();

        String name = (String) snapshot.child(Constants.NAME).getValue();

        long timestamp = (long) snapshot.child(Constants.TIMESTAMP).getValue();

        Object firstLikeObj = snapshot.child(Constants.LIKED_BY_FIRST_USER).getValue();
        Object secondLikeObj = snapshot.child(Constants.LIKED_BY_SECOND_USER).getValue();

        Boolean likeStatusFirstUser = firstLikeObj != null && (boolean) firstLikeObj;
        Boolean likeStatusSecondUser = secondLikeObj != null && (boolean) secondLikeObj;


        Iterable<DataSnapshot> messageSnapshots = snapshot.child(Constants.MESSAGES).getChildren();
        ListExtension<Message> messages = asMessageList(messageSnapshots);

        Chat chat = new Chat(id, name, timestamp, messages);
        chat.setLikeStatusFirstUser(likeStatusFirstUser);
        chat.setLikeStatusSecondUser(likeStatusSecondUser);

        return chat;
    }

    private static ListExtension<Message> asMessageList(Iterable<DataSnapshot> snapshot) {
        return Arrays.asList(snapshot)
                .map(messageSnapshot -> messageSnapshot.getValue(Message.class));
    }

    private static String getUserId(DataSnapshot snapshot) {
        Object value = snapshot.child(Constants.USER_ID).getValue();
        return value != null && value.getClass().equals(String.class) ? (String) value : "N/A";
    }

    private static Boolean getLikeStatus(DataSnapshot snapshot) {
        Object value = snapshot.getValue();
        return value != null && value.getClass().equals(Boolean.class) ? (Boolean) value : false;
    }
}
