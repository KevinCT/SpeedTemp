package com.zweigbergk.speedswede.presenter;
import com.zweigbergk.speedswede.interactor.ChatInteractor;
import com.zweigbergk.speedswede.view.ChatView;

public class ChatPresenter {

    public static final String TAG = ChatPresenter.class.getSimpleName().toUpperCase();

    private ChatView mView;
    private ChatInteractor mInteractor;

    public ChatPresenter(ChatView view) {
        mView = view;
        mInteractor = new ChatInteractor();
    }

}

