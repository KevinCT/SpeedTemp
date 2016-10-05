package com.zweigbergk.speedswede.presenter;

import android.content.Context;
import android.util.Log;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DbChatHandler;
import com.zweigbergk.speedswede.database.DbUserHandler;
import com.zweigbergk.speedswede.interactor.ChatInteractor;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.LocalStorage;

import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ChatFactory;
import com.zweigbergk.speedswede.view.ChatView;

import java.util.Arrays;
import java.util.List;

public class ChatPresenter {

    public static final String TAG = ChatPresenter.class.getSimpleName().toUpperCase();


    private ChatView mView;
    private ChatInteractor mInteractor;

    public ChatPresenter(ChatView view) {
        mView = view;
        mInteractor = new ChatInteractor();

        DbChatHandler.INSTANCE.initialize();
        DbUserHandler.INSTANCE.initialize();

        mView.useContextTo(this::addUserToDatabase);

        updateDeveloperChat();

        Log.d("CHATPRESENTER", " we in chatpresenter");
        ChatMatcher.INSTANCE.addPoolClient(DatabaseEvent.ADDED, this::onUserAddedToChatPool);
        DbUserHandler.INSTANCE.addUserPoolClient(ChatMatcher.INSTANCE::handleUser);
        //DbChatHandler.INSTANCE.addChatListClient(this::handleChat);
    }

    private void addUserToDatabase(Context context) {
        if (DatabaseHandler.INSTANCE.isNetworkAvailable(context)) {
            String uid = DbUserHandler.INSTANCE.getActiveUserId();
            Log.d("DEBUG", "User id: " + uid);
            DbUserHandler.INSTANCE.pushUser(DbUserHandler.INSTANCE.getLoggedInUser());

            mView.useContextTo(LocalStorage.INSTANCE::saveActiveUser);
        }
    }

    private void onUserAddedToChatPool(User user) {
        Log.d(TAG, " onUserAddedToChatPool " + user.getUid());
        ChatMatcher.INSTANCE.match(mView::setChatForChatFragment);
    }

    //Creates a developer chat if one is not present
    private void updateDeveloperChat() {
        DbUserHandler.INSTANCE.pushTestUser();

        String tempId = String.format("%s-%s",
                DbUserHandler.INSTANCE.getActiveUserId(),
                Constants.TEST_USER_UID);

        // These are the methods that want to use the Chat once it's built
        List<Client<Chat>> clientList = Arrays.asList(
                DbChatHandler.INSTANCE::pushChat,
                mView::setChatForChatFragment);

        DbChatHandler.INSTANCE.getChatWithId(tempId, chat -> {
            if (chat == null) {
                Log.d(TAG, "Chat with id: " + tempId + " is null.");
                ChatFactory.createChat(clientList);
            } else {
                Log.d(TAG, "Chat with id: " + tempId + " is not null." +
                        "Setting it as chat for ChatFragment.");
                mView.setChatForChatFragment(chat);
            }
        });
    }

    public void handleChat(DataChange<Chat> dataChange) {
        Chat chat = dataChange.getItem();

        switch (dataChange.getEvent()) {
            case ADDED:
                Log.d("Adding chat", chat.toString());
                break;
            case REMOVED:

                break;
            default:
                break;
        }
    }
}
