package com.zweigbergk.speedswede.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;

import com.google.firebase.auth.FirebaseAuth;

import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.util.ActivityAttachable;
import com.zweigbergk.speedswede.activity.LoginActivity;
import com.zweigbergk.speedswede.interactor.LoginInteractor;
import com.zweigbergk.speedswede.database.DatabaseHandler;

public class LoginPresenter implements ActivityAttachable {

    private enum State { NORMAL, LOADING }

    private static final String TAG = "LoginPresenter";

    private LoginActivity mActivity;
    private LoginInteractor mInteractor;

    public LoginPresenter(LoginActivity activity) {
        mActivity = activity;

        mInteractor = new LoginInteractor();
        mInteractor.registerLoginCallback(this::onAuthResult, activity.getLoginButton());

        mActivity.useContextTo(this::handleAutomaticLogin);

        mActivity.onLoginClick(button -> setViewState(State.LOADING));
    }

    private void handleAutomaticLogin(Context context) {
        boolean connected = DatabaseHandler.isNetworkAvailable(context);
        if (connected) {
            Log.d(TAG, "Network is available.");
            if (hasLoggedInUser()) {
                Log.d(TAG, "We have a logged in user. Using token to log in.");
                AccessToken token = AccessToken.getCurrentAccessToken();
                loginWithToken(token);
            }
        } else {
            Log.d(TAG, "Network is unavailable. Trying Offline Mode...");
            loginInOfflineMode();
        }
    }

    private void loginWithToken(AccessToken token) {
        setViewState(State.LOADING);
        mInteractor.handleFacebookAccessToken(this::onAuthResult, token);
    }

    private void loginInOfflineMode() {
        mActivity.onLogin(true);
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

    private void onAuthResult(LoginInteractor.AuthResult result) {
        switch (result) {
            case SUCCESS:
                Log.d(TAG, "onAuthStateChanged:signed_in");
                mActivity.onLogin(false);
                break;
            case FAIL:
                Log.d(TAG, "onAuthStateChanged:signed_out");
                DatabaseHandler.logout();
                setViewState(State.NORMAL);
                break;
        }
    }

    public void invalidateState() {
        if (hasLoggedInUser()) {
            setViewState(State.LOADING);
        } else {
            setViewState(State.NORMAL);
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mInteractor.onActivityResult(requestCode, resultCode, data);
    }
}
