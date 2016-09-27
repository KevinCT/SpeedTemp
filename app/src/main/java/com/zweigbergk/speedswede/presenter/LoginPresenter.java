package com.zweigbergk.speedswede.presenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.facebook.AccessToken;

import com.zweigbergk.speedswede.ActivityAttachable;
import com.zweigbergk.speedswede.LoginActivity;
import com.zweigbergk.speedswede.interactor.LoginInteractor;
import com.zweigbergk.speedswede.view.LoginView;

public class LoginPresenter implements ActivityAttachable, LoginInteractor.LoginListener {

    private LoginView mView;
    private LoginInteractor mInteractor;

    public LoginPresenter(LoginActivity activity, ConnectionCheck networkCheck) {
        mView = activity;

        mInteractor = new LoginInteractor();
        mInteractor.setLoginListener(this);
        mInteractor.registerLoginCallback(activity, mView.getLoginButton());

        SharedPreferences localState = PreferenceManager.getDefaultSharedPreferences(activity);
        String state = localState.getString(ChatPresenter.USER_ID, null);
        Log.d("DEBUG", state == null ? "null" : state);


        if (!networkCheck.hasConnection()) {
            //We have user ID from old session...
            if (state != null) {
                Log.d("DEBUG", "Starting ChatActivity with old user session ID");
                mView.startChatActivity();
            }
        }

        if (hasLoggedInUser()) {
            showLoadingScreen();
            AccessToken token = AccessToken.getCurrentAccessToken();
            mInteractor.handleFacebookAccessToken(activity, token);
            }

        mView.onLoginClick(view -> {
            if (!hasLoggedInUser()) {
                showLoadingScreen();
            } else {
                Log.d("DEBUG", "We have AccessToken. Do nothing.");
            }
        });
    }

    private boolean hasLoggedInUser() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private void showLoadingScreen() {
        mView.showProgressCircle();
        mView.hideContent();
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
