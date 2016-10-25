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

public class ChatReference {
    private static final String TAG = ChatReference.class.getSimpleName().toUpperCase();

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

    public void setFirstUser(User user) {
        ifStillValid().then(() ->
                DbChatHandler.getInstance().setChatAttribute(mChat, ChatAttribute.FIRST_USER, user));

    }

    public void setSecondUser(User user) {
        ifStillValid().then(() ->
                DbChatHandler.getInstance().setChatAttribute(mChat, ChatAttribute.SECOND_USER, user));
    }

    public void setLikeStatusForFirstUser(Boolean likeStatus) {
        ifStillValid().then(() ->
                DbChatHandler.getInstance().setChatAttribute(mChat, ChatAttribute.LIKED_BY_FIRST_USER, likeStatus));
    }

    public void setLikeStatusForSecondUser(Boolean likeStatus) {
        ifStillValid().then(() ->
                DbChatHandler.getInstance().setChatAttribute(mChat, ChatAttribute.LIKED_BY_SECOND_USER, likeStatus));
    }

    public void sendMessage(Message message) {
        ifStillValid().then(
                () -> DbChatHandler.getInstance().postMessageToChat(mChat, message));
    }

    public void bind(Client<DataChange<Chat>> client) {
        DbChatHandler.getInstance().getChatListener().addClient(mChat, client);
    }

    public void unbind(Client<DataChange<Chat>> client) {
        DbChatHandler.getInstance().getChatListener().removeClient(mChat, client);
    }

    public void bindMessages(Client<DataChange<Message>> client) {
        //Register the client with our message listener
        DbChatHandler.getInstance().addMesageClient(mChat, client);
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
