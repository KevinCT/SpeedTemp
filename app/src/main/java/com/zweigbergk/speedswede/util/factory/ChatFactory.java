package com.zweigbergk.speedswede.util.factory;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.async.Commitment;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.async.Guarantee;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.async.PromiseNeed;
import com.zweigbergk.speedswede.util.Tuple;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.collection.List;

import static com.zweigbergk.speedswede.util.async.PromiseNeed.*;

public class ChatFactory {

    public static final String TAG = ChatFactory.class.getSimpleName().toUpperCase();

    public static User mockUser(String name, String uid) {
        return new UserProfile(name, uid);
    }

    public static Promise<Chat> deserializeChat(DataSnapshot snapshot) {
        String firstUserId = ChatFactory.getUserId(snapshot.child(Constants.FIRST_USER));
        String secondUserId = ChatFactory.getUserId(snapshot.child(Constants.SECOND_USER));

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
            return chat;
        };

        List<Tuple<PromiseNeed, Commitment<?>>> commitments = new ArrayList<>();
        commitments.add(new Tuple<>(CHAT, chatCommitment));
        commitments.add(new Tuple<>(FIRST_USER, firstUserPromised));
        commitments.add(new Tuple<>(SECOND_USER, secondUserPromised));

        return Promise.group(chatResult, commitments);
    }

    private static Chat getChatWithoutUsers(DataSnapshot snapshot) {
        String id = snapshot.getKey();

        String name = (String) snapshot.child(Constants.NAME).getValue();

        long timestamp = (long) snapshot.child(Constants.TIMESTAMP).getValue();

        Iterable<DataSnapshot> messageSnapshots = snapshot.child(Constants.MESSAGES).getChildren();
        List<Message> messages = asMessageList(messageSnapshots);

        return new Chat(id, name, timestamp, messages, null, null);
    }

    private static List<Message> asMessageList(Iterable<DataSnapshot> snapshot) {
        List<Message> messages = new ArrayList<>();
        Lists.forEach(snapshot, messageSnapshot -> {
            Message message = messageSnapshot.getValue(Message.class);
            messages.add(message);
        });

        return messages;
    }

    private static String getUserId(DataSnapshot snapshot) {
        Object value = snapshot.child(Constants.USER_ID).getValue();
        String userId;
        if (value.getClass().equals(String.class)) {
            userId = (String) value;
        } else if (value.getClass().equals(HashMap.class)) {
            HashMap<String, Object> mapping = (HashMap) value;
            userId = (String) mapping.get(Constants.USER_ID);
        } else {
            Log.e(TAG, String.format(
                    "WARNING! Can not extract User ID for a user in chat [Chat ID: %s].\n(Path: %s)",
                    snapshot.getRef().getParent().getKey(),
                    snapshot.getRef().toString()));
            userId = "";
        }

        return userId;
    }
}
