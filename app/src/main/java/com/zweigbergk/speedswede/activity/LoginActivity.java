package com.zweigbergk.speedswede.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.facebook.login.widget.LoginButton;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.Initializer;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.presenter.LoginPresenter;
import com.zweigbergk.speedswede.util.ActivityAttachable;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.view.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView {
    private ActivityAttachable mPresenter;

    private LoginButton mLoginButton;
    private ProgressBar mProgressCircle;
    private RelativeLayout mContentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mContentLayout = (RelativeLayout) findViewById(R.id.activity_login_content);

        mLoginButton = (LoginButton) findViewById(R.id.activity_login_login_button);
        mLoginButton.setReadPermissions("email", "public_profile");

        mProgressCircle = (ProgressBar) findViewById(R.id.login_progress_circle);

        mPresenter = new LoginPresenter(this);
    }

    public void onLogin(boolean isOfflineMode) {
        Initializer.onLogin(isOfflineMode);

        Initializer.runOnLogin(user -> {
            if (user.isFirstLogin()) {
                //Show settings setup
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                settingsIntent.putExtra(Constants.SETTINGS_FIRST_SETUP, true);
                startActivity(settingsIntent);
            } else {
                startActivity(new Intent(this, ChatActivity.class));
            }

            finish();
        });
    }

    @Override
    public void onLoginClick(View.OnClickListener listener) {
        mLoginButton.setOnClickListener(listener);
    }

    @Override
    public void setProgressCircleVisibility(int visibility) {
        mProgressCircle.setVisibility(visibility);
    }


    @Override
    public void setContentVisibility(int visibility) {
        mContentLayout.setVisibility(visibility);
    }

    public LoginButton getLoginButton() {
        return mLoginButton;
    }

    @Override
    public void onResume() {
        super.onResume();

        ((LoginPresenter)mPresenter).invalidateState();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void useContextTo(Client<Context> client) {
        client.supply(this.getBaseContext());
    }
}
