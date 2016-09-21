package com.zweigbergk.speedswede.presenter;


import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.zweigbergk.speedswede.ActivityAttachable;
import com.zweigbergk.speedswede.LoginActivity;
import com.zweigbergk.speedswede.interactor.LoginInteractor;
import com.zweigbergk.speedswede.view.LoginView;

public class LoginPresenter implements ActivityAttachable, LoginInteractor.LoginListener {

    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenter(LoginActivity activity) {
        mView = activity;

        mInteractor = new LoginInteractor();
        mInteractor.setLoginListener(this);
        mInteractor.registerLoginCallback(activity, mView.getLoginButton());

        Profile user = Profile.getCurrentProfile();

        if (user != null) {
            Log.d("DEBUG", "We have a logged in Facebook Profile");
            AccessToken token = AccessToken.getCurrentAccessToken();
            mInteractor.handleFacebookAccessToken(activity, token);
        }
    }

    @Override
    public void onLogin() {
        mView.startChatActivity();
    }

    @Override
    public void onStart() {
        mInteractor.onStart();
    }

    @Override
    public void onStop() {
        mInteractor.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mInteractor.onActivityResult(requestCode, resultCode, data);
    }
}
