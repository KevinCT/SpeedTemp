package com.zweigbergk.speedswede.database.firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.eventListener.DataQuery;
import com.zweigbergk.speedswede.util.BuilderKey;
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.ProductBuilder;

import java.util.ArrayList;
import java.util.List;

public enum DbChatHandler {
    INSTANCE;

    public static final String CHATS = "chats";
    public static final String MESSAGES = "messages";
    public static final String TIMESTAMP = "timeStamp";
    public static final String FIRST_USER = "firstUser";
    public static final String SECOND_USER = "firstUser";


    private DatabaseReference mDatabaseReference;

    DbChatHandler() {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void postMessageToChat(Chat chat, Message message) {
        getChatWithId(chat.getId(), chatInDatabase -> {
            if (chatInDatabase != null) {
                mDatabaseReference.child(CHATS).child(chat.getId()).child(MESSAGES).push().setValue(message);
            }
        });
    }

    public void getChatWithId(String id, Client<Chat> client) {
        mDatabaseReference.child(CHATS).child(id).addListenerForSingleValueEvent(
                new DataQuery(snapshot -> {
                    Chat chat = convertToChat(snapshot);
                    client.supply(chat);
                }));
    }

    /** Supplies a Client with a Chat created from a DataSnapshot. Returns null if the snapshot
     * points to nothing. */
    public void convertToChat(DataSnapshot snapshot, Client<Chat> client) {
        if (snapshot.getValue() == null)
            client.supply(null);

        ProductBuilder<Chat> chatBuilder = new ProductBuilder<>(ChatFactory::getReconstructionBlueprint);
        chatBuilder.require(BuilderKey.ID, BuilderKey.TIMESTAMP, BuilderKey.MESSAGE_LIST,
                BuilderKey.FIRST_USER, BuilderKey.SECOND_USER);

        chatBuilder.addClient(client);

        String chatId = snapshot.getKey();

        long chatTimestamp = (long) snapshot.child(TIMESTAMP).getValue();

        Iterable<DataSnapshot> messageSnapshots = snapshot.child(MESSAGES).getChildren();
        List<Message> messageList = asMessageList(messageSnapshots);

        String firstUserId = (String) snapshot.child(FIRST_USER).getValue();
        String secondUserId = (String) snapshot.child(SECOND_USER).getValue();

        chatBuilder.append(BuilderKey.ID, chatId);
        chatBuilder.append(BuilderKey.TIMESTAMP, chatTimestamp);
        chatBuilder.append(BuilderKey.MESSAGE_LIST, messageList);
        DatabaseHandler.INSTANCE.getUserById(firstUserId, user -> chatBuilder.append(BuilderKey.FIRST_USER, user));
        DatabaseHandler.INSTANCE.getUserById(secondUserId, user -> chatBuilder.append(BuilderKey.SECOND_USER, user));
    }

    private List<Message> asMessageList(Iterable<DataSnapshot> snapshot) {
        List<Message> messages = new ArrayList<>();
        Lists.forEach(snapshot, messageSnapshot -> {
            Message message = messageSnapshot.getValue(Message.class);
            messages.add(message);
        });

        return messages;
    }


}
