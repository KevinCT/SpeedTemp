package com.zweigbergk.speedswede.presenter;

import android.util.Log;

import com.zweigbergk.speedswede.ChatActivity;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.interactor.ChatInteractor;
import com.zweigbergk.speedswede.service.DatabaseHandler;
import com.zweigbergk.speedswede.util.TestFactory;
import com.zweigbergk.speedswede.view.ChatView;

public class ChatPresenter implements ChatActivity.ViewListener {

    private ChatView mView;
    private ChatInteractor mInteractor;

    public ChatPresenter(ChatView view) {
        mView = view;
        mInteractor = new ChatInteractor();

        ChatMatcher.INSTANCE.pushUser(TestFactory.mockUser("Kompis1", "Kompis1"));
        ChatMatcher.INSTANCE.pushUser(TestFactory.mockUser("Kompis2", "Kompis2"));
        ChatMatcher.INSTANCE.pushUser(TestFactory.mockUser("Kompis3", "Kompis3"));
        System.out.println("Im here brah");
        DatabaseHandler.INSTANCE.setMatchingPool();
    }
}
