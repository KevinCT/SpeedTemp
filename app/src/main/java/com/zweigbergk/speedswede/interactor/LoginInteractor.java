package com.zweigbergk.speedswede.interactor;

import android.app.Activity;
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
import com.google.firebase.auth.FirebaseUser;
import com.zweigbergk.speedswede.ActivityAttachable;
import com.zweigbergk.speedswede.util.Client;

public class LoginInteractor implements ActivityAttachable {

    //public static final String TAG = "LoginInteractor";
    public static final String TAG = "DEBUG";

    public static final boolean LOGIN_SUCCESS = true;
    public static final boolean LOGIN_FAILED = false;

    private CallbackManager mCallbackManager;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    public LoginInteractor(Client<Boolean> client) {
        mCallbackManager = CallbackManager.Factory.create();
        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            boolean result = user != null ? LOGIN_SUCCESS : LOGIN_FAILED;
            client.supply(result);
        };
    }

    public void registerLoginCallback(Activity activity, LoginButton button) {
        button.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(activity, loginResult.getAccessToken());
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

    public void handleFacebookAccessToken(Activity activity, AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        Log.d(TAG, "user: " + token.getUserId());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(activity, task -> {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                        }

                        // ...
                    });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        if (mAuthStateListener != null)
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
    }
}
