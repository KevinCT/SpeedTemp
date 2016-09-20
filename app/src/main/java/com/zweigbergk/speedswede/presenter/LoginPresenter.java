package com.zweigbergk.speedswede.presenter;

import com.zweigbergk.speedswede.LoginActivity;
import com.zweigbergk.speedswede.interactor.LoginInteractor;
import com.zweigbergk.speedswede.view.LoginView;

public class LoginPresenter implements LoginActivity.ViewListener {

    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenter(LoginView view) {
        mView = view;
        mInteractor = new LoginInteractor();
    }
}
