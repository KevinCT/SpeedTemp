package com.zweigbergk.speedswede.view;

import android.content.Context;
import android.view.View;

import com.facebook.login.widget.LoginButton;
import com.zweigbergk.speedswede.util.Client;

public interface LoginView {
    LoginButton getLoginButton();
    void startChatActivity();
    void onLoginClick(View.OnClickListener listener);
    void showProgressCircle();
    void hideContent();
    void useContextTo(Client<Context> client);
}
