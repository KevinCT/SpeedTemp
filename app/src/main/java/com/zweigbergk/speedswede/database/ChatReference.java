package com.zweigbergk.speedswede.database;

import android.util.Log;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.Client;

public class ChatReference {
    public static final String TAG = ChatReference.class.getSimpleName().toUpperCase();

    enum ChatAttribute {
        FIRST_USER, SECOND_USER;

        public String getDbKey() {
            switch(this) {
                case FIRST_USER:
                    return Constants.FIRST_USER;
                case SECOND_USER:
                    return Constants.SECOND_USER;
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
                                "does not contain a user with ID: [%s]",
                        mChat.getId(), user.getUid()));
                return;
            }

            ChatAttribute node = mChat.getFirstUser().equals(user) ?
                    ChatAttribute.FIRST_USER : ChatAttribute.SECOND_USER;

            DbChatHandler.INSTANCE.setChatAttribute(mChat, node, null);
        });

    }

    public void push() {
        DbChatHandler.getInstance().pushChat(mChat);
    }

    public void setFirstUser(User user) {
        ifStillValid().then(() ->
                DbChatHandler.INSTANCE.setChatAttribute(mChat, ChatAttribute.FIRST_USER, user));

    }

    public void setSecondUser(User user) {
        ifStillValid().then(() ->
                DbChatHandler.INSTANCE.setChatAttribute(mChat, ChatAttribute.SECOND_USER, user));
    }

    public void sendMessage(Message message) {
        ifStillValid().then(
                () -> DbChatHandler.INSTANCE.postMessageToChat(mChat, message));
    }

    public void bind(Client<DataChange<Chat>> client) {
        DbChatHandler.INSTANCE.getChatListener().addClient(mChat, client);
    }

    public void unbind(Client<DataChange<Chat>> client) {
        DbChatHandler.INSTANCE.getChatListener().removeClient(mChat, client);
    }

    public void bindMessageClient(Client<DataChange<Message>> client) {
        DbChatHandler.INSTANCE.getChatListener().addMessageClient(mChat, client);
    }

    public void unbindMessageClient(Client<DataChange<Message>> client) {
        DbChatHandler.INSTANCE.getChatListener().removeMessageClient(mChat, client);
    }

    /**
     * Checks if the chat is still present in the database.
     * @return an ExistanceCheck object that can be used as:
     * <p><p><code>ifStillValid().then(() -> { run code here... })</code></p></p>
     * */
    public ExistanceCheck ifStillValid() {
        return ExistanceCheck.ifExists(mChat);
    }
}