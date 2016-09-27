package com.zweigbergk.speedswede.presenter;

import android.app.Activity;
import android.content.Context;
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

        mActivity.useContextTo(this::handleAutomaticLogin);

        mActivity.onLoginClick(button -> showLoadingScreen());
    }

    private void handleAutomaticLogin(Context context) {
        boolean connected = DatabaseHandler.INSTANCE.isNetworkAvailable(context);
        if (connected) {
            Log.d("DEBUG2", "Connected.");
            if (hasLoggedInUser()) {
                Log.d("DEBUG2", "hasLoggedInUser.");
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
