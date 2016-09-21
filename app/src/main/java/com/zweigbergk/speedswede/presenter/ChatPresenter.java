package com.zweigbergk.speedswede.presenter;

import android.util.Log;

import com.zweigbergk.speedswede.ChatActivity;
import com.zweigbergk.speedswede.interactor.ChatInteractor;
import com.zweigbergk.speedswede.view.ChatView;

public class ChatPresenter implements ChatActivity.ViewListener {

    private ChatView mView;
    private ChatInteractor mInteractor;

    public ChatPresenter(ChatView view) {
        mView = view;
        mInteractor = new ChatInteractor();
    }
}
