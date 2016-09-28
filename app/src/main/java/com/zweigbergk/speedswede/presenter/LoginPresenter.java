package com.zweigbergk.speedswede.presenter;

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

public class LoginPresenter implements ActivityAttachable {

    public enum State { NORMAL, LOADING }

    public static final String TAG = "LoginPresenter";

    private LoginActivity mActivity;
    private LoginInteractor mInteractor;

    public LoginPresenter(LoginActivity activity) {
        mActivity = activity;

        mInteractor = new LoginInteractor(this::onAuthResult);
        mInteractor.registerLoginCallback(mActivity, activity.getLoginButton());

        mActivity.useContextTo(this::handleAutomaticLogin);

        mActivity.onLoginClick(button -> setViewState(State.LOADING));
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
        setViewState(State.LOADING);
        mInteractor.handleFacebookAccessToken(mActivity, token);
    }

    private void loginInOfflineMode() {
        LocalStorage.INSTANCE.loadSavedUserId(mActivity);
    }

    private boolean hasLoggedInUser() {
        return AccessToken.getCurrentAccessToken() != null;
    }

    private void setViewState(State state) {
        int progressCircleVisibility, contentVisibility;

        switch (state) {
            case NORMAL:
                progressCircleVisibility = View.GONE;
                contentVisibility = View.VISIBLE;
                break;
            case LOADING:
                progressCircleVisibility = View.VISIBLE;
                contentVisibility = View.GONE;
                break;
            default:
                progressCircleVisibility = View.GONE;
                contentVisibility = View.VISIBLE;
        }

        mActivity.setProgressCircleVisibility(progressCircleVisibility);
        mActivity.setContentVisibility(contentVisibility);
    }

    public void onAuthResult(boolean result) {
        if (result == LoginInteractor.LOGIN_SUCCESS) {
            Log.d(TAG, "onAuthStateChanged:signed_in");
            mActivity.startChatActivity();
        } else {
            Log.d(TAG, "onAuthStateChanged:signed_out");
        }
    }

    public void invalidateState() {
        setViewState(State.NORMAL);
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
