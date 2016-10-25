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
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.util.ActivityAttachable;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

public class LoginInteractor implements ActivityAttachable {

    private static AuthCredential userCredential;

    private static final String TAG = "LoginInteractor";

    public enum AuthResult { SUCCESS, FAIL }

    private CallbackManager mCallbackManager;

    public LoginInteractor() {
        mCallbackManager = CallbackManager.Factory.create();
    }

    public void registerLoginCallback(Client<AuthResult> authClient, LoginButton button) {
        button.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(authClient, loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                // TODO: Probably should give some feedback to the user?
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                if (isConnectionError(exception)) {
                    //mLoginListener
                    Log.d(TAG, "Handle connection error~");
                }
            }
        });
    }

    private boolean isConnectionError(FacebookException exception) {
        // We are just interested in the first 18 characters.
        String msg = exception.getMessage().substring(0, 18);
        return msg.equals("CONNECTION_FAILURE");
    }

    private static void setUserCredential(AuthCredential authCredential) {
        userCredential = authCredential;
    }

    public void handleFacebookAccessToken(Client<AuthResult> authClient, AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        Log.d(TAG, "user: " + token.getUserId());

        //Unsure about merge conflict
        UserProfile.facebookUserID = token.getUserId();
        userCredential = FacebookAuthProvider.getCredential(token.getToken());
        setUserCredential(FacebookAuthProvider.getCredential(token.getToken()));
        FirebaseAuth.getInstance().signInWithCredential(userCredential)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()) {
                        authClient.supply(AuthResult.SUCCESS);
                    } else {
                        authClient.supply(AuthResult.FAIL);
                        Log.w(TAG, "signInWithCredential failed: ", task.getException());
                    }
                    });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
