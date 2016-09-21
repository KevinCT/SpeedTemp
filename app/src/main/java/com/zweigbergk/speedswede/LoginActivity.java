package com.zweigbergk.speedswede;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.login.widget.LoginButton;
import com.zweigbergk.speedswede.presenter.LoginPresenter;
import com.zweigbergk.speedswede.view.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView {


    public static final String TAG = "LoginActivity";

    private ActivityAttachable mPresenter;

    LoginButton mLoginButton;
    ProgressBar mProgressCircle;
    RelativeLayout mContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContentLayout = (RelativeLayout) findViewById(R.id.activity_login_content);

        mLoginButton = (LoginButton) findViewById(R.id.activity_login_login_button);
        mLoginButton.setReadPermissions("email", "public_profile");
        mPresenter = new LoginPresenter(this);

        mProgressCircle = (ProgressBar) findViewById(R.id.login_progress_circle);
    }

    @Override
    public void startChatActivity() {
        startActivity(new Intent(this, ChatActivity.class));
        finish();
    }

    @Override
    public void onLoginClick(View.OnClickListener listener) {
        mLoginButton.setOnClickListener(listener);
    }

    @Override
    public void showProgressCircle() {
        mProgressCircle.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideContent() {
        mContentLayout.setVisibility(View.GONE);
    }

    @Override
    public LoginButton getLoginButton() {
        return mLoginButton;
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
