package com.zweigbergk.speedswede.view;

import android.content.Context;
import android.view.View;

import com.facebook.login.widget.LoginButton;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

public interface LoginView {
    LoginButton getLoginButton();
    void onLogin(boolean offlineMode);
    void onLoginClick(View.OnClickListener listener);
    void setProgressCircleVisibility(int visibility);
    void setContentVisibility(int visibility);
    void useContextTo(Client<Context> client);
}
