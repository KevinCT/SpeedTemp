package com.zweigbergk.speedswede.presenter;

import android.util.Log;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.interactor.ChatInteractor;
import com.zweigbergk.speedswede.database.DataChange;

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

        //updateDeveloperChat();

        Log.d("CHATPRESENTER", " we in chatpresenter");
    }

    //Creates a developer chat if one is invert present
    private void updateDeveloperChat() {
        DatabaseHandler.getInstance().pushTestUser();

        String chatId = String.format("%s-%s",
                DatabaseHandler.getInstance().getActiveUserId(),
                Constants.TEST_USER_UID);

        // These are the methods that want to use the Chat once it's built
        List<Client<Chat>> clientList = Arrays.asList(
                chat -> DatabaseHandler.get(chat).push(),
                mView::setChatForChatFragment);

        DatabaseHandler.getInstance().getChatById(chatId).then(chat -> {
            if (chat == null) {
                Log.d(TAG, "Chat with id: " + chatId + " is null.");
                ChatFactory.createChat(clientList);
            } else {
                Log.d(TAG, "Chat with id: " + chatId + " is invert null." +
                        "Setting it as chat for ChatFragment.");
                mView.setChatForChatFragment(chat);
            }
        });
    }
}
