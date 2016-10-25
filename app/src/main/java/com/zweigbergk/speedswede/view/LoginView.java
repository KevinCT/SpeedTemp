package com.zweigbergk.speedswede.view;

import android.content.Context;
import android.view.View;

import com.zweigbergk.speedswede.util.methodwrapper.Client;

@SuppressWarnings("unused")
public interface LoginView {
    void onLoginClick(View.OnClickListener listener);
    void setProgressCircleVisibility(int visibility);
    void setContentVisibility(int visibility);
    void useContextTo(Client<Context> client);
}
