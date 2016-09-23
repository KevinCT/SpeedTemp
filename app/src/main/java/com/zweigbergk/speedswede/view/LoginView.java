package com.zweigbergk.speedswede.view;

import android.view.View;
import android.widget.Button;

import com.facebook.login.widget.LoginButton;

public interface LoginView {
    LoginButton getLoginButton();
//    Button getLoginButton();
    void startChatActivity();
    void onLoginClick(View.OnClickListener listener);
    void showProgressCircle();
    void hideContent();
}
