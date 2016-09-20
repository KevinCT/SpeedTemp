package com.zweigbergk.speedswede;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zweigbergk.speedswede.presenter.LoginPresenter;
import com.zweigbergk.speedswede.view.LoginView;

public class LoginActivity extends AppCompatActivity implements LoginView {

    public interface ViewListener {
//        void onLoginClick();
    }

    private ViewListener mViewListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mViewListener= new LoginPresenter(this);
    }
}
