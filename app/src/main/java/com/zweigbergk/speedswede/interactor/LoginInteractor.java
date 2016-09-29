package com.zweigbergk.speedswede.interactor;

import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import com.zweigbergk.speedswede.ActivityAttachable;
import com.zweigbergk.speedswede.util.Client;

import java.util.concurrent.Executor;

public class LoginInteractor implements ActivityAttachable {

    //public static final String TAG = "LoginInteractor";
    public static final String TAG = "DEBUG";

    public static final boolean LOGIN_SUCCESS = true;
    public static final boolean LOGIN_FAILED = false;

    private CallbackManager mCallbackManager;

    public LoginInteractor() {
        mCallbackManager = CallbackManager.Factory.create();
    }

    public void registerLoginCallback(Client<Boolean> authClient, LoginButton button) {
        button.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(authClient, loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                if (isConnectionError(exception)) {
                    //mLoginListener
                    Log.d("DEBUG", "Handle connection error~");
                }
            }
        });
    }

    private boolean isConnectionError(FacebookException exception) {
        // We are just interested in the first 18 characters.
        String msg = exception.getMessage().substring(0, 18);
        return msg.equals("CONNECTION_FAILURE");
    }

    public void handleFacebookAccessToken(Client<Boolean> authClient, AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        Log.d(TAG, "user: " + token.getUserId());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    Log.d("DEBUG2", "WE GOT IN!!");
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
                        authClient.supply(LOGIN_SUCCESS);
                    } else {
                        authClient.supply(LOGIN_FAILED);
                        Log.w(TAG, "signInWithCredential failed: ", task.getException());
                    }
                    });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }
}
