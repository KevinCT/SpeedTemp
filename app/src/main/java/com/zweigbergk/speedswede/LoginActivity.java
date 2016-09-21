package com.zweigbergk.speedswede;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;
import com.zweigbergk.speedswede.presenter.LoginPresenter;
import com.zweigbergk.speedswede.view.LoginView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity implements LoginView {


    public static final String TAG = "LoginActivity";

    private ActivityAttachable mPresenter;

    LoginButton mLoginButton;

    @Override
    public LoginButton getLoginButton() {
        return mLoginButton;
    }

    @Override
    public void startChatActivity() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Log.d(TAG, dateFormat.format(date));

        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
