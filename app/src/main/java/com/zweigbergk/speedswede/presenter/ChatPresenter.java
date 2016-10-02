package com.zweigbergk.speedswede.presenter;

import android.content.Context;
import android.util.Log;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.interactor.ChatInteractor;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.LocalStorage;

import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.MockFactory;
import com.zweigbergk.speedswede.view.ChatView;

import java.util.Arrays;
import java.util.List;

public class ChatPresenter {

    private ChatView mView;
    private ChatInteractor mInteractor;

    public ChatPresenter(ChatView view) {
        mView = view;
        mInteractor = new ChatInteractor();

        mView.useContextTo(this::addUserToDatabase);

        updateDeveloperChat();

        DatabaseHandler.INSTANCE.registerPoolListener(ChatMatcher.INSTANCE::handleUser);
        DatabaseHandler.INSTANCE.registerChatListener(this::handleChat);
    }

    private void addUserToDatabase(Context context) {
        if (DatabaseHandler.INSTANCE.isNetworkAvailable(context)) {
            Log.d("DEBUG", "User id: " + DatabaseHandler.INSTANCE.getActiveUserId());
            DatabaseHandler.INSTANCE.addUser();

            mView.useContextTo(LocalStorage.INSTANCE::saveActiveUser);
        }
    }

    //Creates a developer chat if one is not present
    private void updateDeveloperChat() {
        // TODO when implementing a real version of the chatBuilder, use
        // TODO DatabaseHandler.INSTANCE.generateId() instead.
        String temporaryIDForSimplicity = DatabaseHandler.INSTANCE.getActiveUserId();

        // These are the methods that want to use the Chat once it's built
        List<Client<Chat>> clientList = Arrays.asList(
                DatabaseHandler.INSTANCE::pushChat,
                mView::setChatForChatFragment);

        DatabaseHandler.INSTANCE.getChatWithId(temporaryIDForSimplicity, chat -> {
            if (chat == null) {
                buildChat(temporaryIDForSimplicity, clientList);
            } else {
                mView.setChatForChatFragment(chat);
            }
        });
    }

    private void buildChat(String chatId, List<Client<Chat>> clientList) {
        MockFactory.runChatBuilder(clientList, chatId);
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
