package com.zweigbergk.speedswede.presenter;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;

import com.zweigbergk.speedswede.ActivityAttachable;
import com.zweigbergk.speedswede.LoginActivity;
import com.zweigbergk.speedswede.interactor.LoginInteractor;
import com.zweigbergk.speedswede.service.DatabaseHandler;
import com.zweigbergk.speedswede.service.LocalStorage;

public class LoginPresenter implements ActivityAttachable, LoginInteractor.LoginListener {

    private LoginActivity mActivity;
    private LoginInteractor mInteractor;

    public LoginPresenter(LoginActivity activity) {
        mActivity = activity;

        mInteractor = new LoginInteractor();
        mInteractor.setLoginListener(this);
        mInteractor.registerLoginCallback(mActivity, activity.getLoginButton());

        DatabaseHandler.INSTANCE.onGetConnectionStatus(this::handleAutomaticLogin);

        mActivity.onLoginClick(this::changeToLoadingScreen);
    }

    private void changeToLoadingScreen(View button) {
        if (!hasLoggedInUser()) {
            //LoginButton handles login stuff, we just show loading screen below the
            // facebook stuff
            showLoadingScreen();
        } else {
            Log.d("DEBUG", "We have AccessToken. Do nothing.");
        }
    }

    private void handleAutomaticLogin(boolean connected) {
        if (connected) {
            if (hasLoggedInUser()) {
                AccessToken token = AccessToken.getCurrentAccessToken();
                loginWithToken(token);
            }
        } else {
            loginInOfflineMode();
        }
    }

    private void loginWithToken(AccessToken token) {
        showLoadingScreen();
        mInteractor.handleFacebookAccessToken(mActivity, token);
    }

    private void loginInOfflineMode() {
        LocalStorage.INSTANCE.loadSavedUserId(mActivity);
    }

    private boolean hasLoggedInUser() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private void showLoadingScreen() {
        mActivity.showProgressCircle();
        mActivity.hideContent();
    }

    @Override
    public void onLogin() {
        mActivity.startChatActivity();
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
