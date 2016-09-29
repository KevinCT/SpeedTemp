package com.zweigbergk.speedswede.presenter;

import android.provider.ContactsContract;
import android.util.Log;

import com.zweigbergk.speedswede.ChatActivity;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.interactor.ChatInteractor;
import com.zweigbergk.speedswede.service.DatabaseEvent;
import com.zweigbergk.speedswede.service.DatabaseHandler;
import com.zweigbergk.speedswede.service.LocalStorage;
import com.zweigbergk.speedswede.view.ChatView;

public class ChatPresenter implements ChatActivity.ViewListener {

    public static final String USER_ID = "user_state";

    private ChatView mView;
    private ChatInteractor mInteractor;

    public ChatPresenter(ChatActivity activity) {
        mView = activity;
        mInteractor = new ChatInteractor();

        Log.d("DEBUG", "User id: " + DatabaseHandler.INSTANCE.getActiveUserId());

        DatabaseHandler.INSTANCE.addUser();

        mView.useContextTo(LocalStorage.INSTANCE::saveActiveUserId);
        DatabaseHandler.INSTANCE.registerPoolListener(dataChange -> ChatMatcher.INSTANCE.handleUser(dataChange));


    }
}
