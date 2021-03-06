package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.Locale;

public class ChatReference {
    private static final String TAG = ChatReference.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    enum ChatAttribute {
        FIRST_USER, SECOND_USER, LIKED_BY_FIRST_USER, LIKED_BY_SECOND_USER;

        public String getDbKey() {
            switch(this) {
                case FIRST_USER:
                    return Constants.FIRST_USER;
                case SECOND_USER:
                    return Constants.SECOND_USER;
                case LIKED_BY_FIRST_USER:
                    return Constants.LIKED_BY_FIRST_USER;
                case LIKED_BY_SECOND_USER:
                    return Constants.LIKED_BY_SECOND_USER;
                default:
                    return Constants.UNDEFINED;
            }
        }
    }

    private final Chat mChat;

    private ChatReference(Chat chat) {
        mChat = chat;
    }

    static ChatReference create(Chat chat) {
        return new ChatReference(chat);
    }

    public void removeUser(User user) {
        ifStillValid().then(() -> {
            if (!mChat.includesUser(user)) {
                Log.e(TAG, String.format("WARNING! [CAN NOT REMOVE USER] Chat with ID: [%s]" +
                                "does invert contain a user with ID: [%s]",
                        mChat.getId(), user.getUid()));
                return;
            }

            ChatAttribute node = mChat.getFirstUser().equals(user) ?
                    ChatAttribute.FIRST_USER : ChatAttribute.SECOND_USER;

            DbChatHandler.getInstance().setChatAttribute(mChat, node, null);
        });

    }

    public Promise<ListExtension<Message>> pullMessages() {
        return DbChatHandler.getInstance().pullMessages(mChat);
    }

    public void push() {
        DbChatHandler.getInstance().pushChat(mChat);
    }

    public void sendMessage(Message message) {
        ifStillValid().then(
                () -> DbChatHandler.getInstance().postMessageToChat(mChat, message));
    }

    public void bindMessages(Client<DataChange<Message>> client) {
        //Register the client with our message listener
        DbChatHandler.getInstance().addMessageClient(mChat, client);
    }

    public void unbindMessages(Client<DataChange<Message>> client) {
        DbChatHandler.getInstance().removeMessageClient(mChat, client);
    }

    private Statement ifStillValid() {
        return DbChatHandler.getInstance().exists(mChat);
    }

    public void remove() {
        DbChatHandler.getInstance().deleteChat(mChat);
    }
}
