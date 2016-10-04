package com.zweigbergk.speedswede.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.firebase.DbUserHandler;
import com.zweigbergk.speedswede.util.ActivityAttachable;
import com.zweigbergk.speedswede.activity.LoginActivity;
import com.zweigbergk.speedswede.interactor.LoginInteractor;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.LocalStorage;

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
        boolean connected = DatabaseHandler.INSTANCE.isNetworkAvailable(context);
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
        User user = LocalStorage.INSTANCE.getSavedUser(mActivity);

        if (user != null) {
            Log.d(TAG, "Found saved user in LocalStorage. Starting ChatActivity.");
            DbUserHandler.INSTANCE.setLoggedInUser(user);
            mActivity.startChatActivity();
        } else {
            Toast.makeText(mActivity, "No previous user could be found.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "loginInOfflineMode: No previous user could be found.");
        }
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
                mActivity.startChatActivity();
                break;
            case FAIL:
                Log.d(TAG, "onAuthStateChanged:signed_out");
                DbUserHandler.INSTANCE.logout();
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
