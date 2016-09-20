package com.zweigbergk.speedswede;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.zweigbergk.speedswede.presenter.MainPresenter;
import com.zweigbergk.speedswede.view.MainView;

public class MainActivity extends AppCompatActivity implements MainView {

    public interface ViewListener {
//        void onLoginClick();
    }

    private ViewListener mViewListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewListener = new MainPresenter(this);
        setUpContent();

//        startLoginActivity();
        startChatActivity();
    }

    private void setUpContent() {

    }


//    @Override
//    public void updateButtonText(String text) {
//        ((Button) findViewById(R.id.button)).setText(text);
//    }

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void startChatActivity() {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        startActivity(intent);
    }
}
