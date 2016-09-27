package com.zweigbergk.speedswede.view;

import android.view.View;

import com.facebook.login.widget.LoginButton;

public interface LoginView {
    LoginButton getLoginButton();
    void startChatActivity();
    void onLoginClick(View.OnClickListener listener);
    void showProgressCircle();
    void hideContent();
}
