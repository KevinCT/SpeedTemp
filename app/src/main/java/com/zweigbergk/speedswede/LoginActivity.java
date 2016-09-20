package com.zweigbergk.speedswede;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.zweigbergk.speedswede.presenter.LoginPresenter;
import com.zweigbergk.speedswede.view.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView {

    private ActivityAttachable mPresenter;

    LoginButton mLoginButton;

    @Override
    public LoginButton getLoginButton() {
        return mLoginButton;
    }

    @Override
    public void startChatActivity() {
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        setContentView(R.layout.activity_login);

        mLoginButton = (LoginButton) findViewById(R.id.activity_login_login_button);
        mLoginButton.setReadPermissions("email", "public_profile");
        mPresenter = new LoginPresenter(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.onStop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
