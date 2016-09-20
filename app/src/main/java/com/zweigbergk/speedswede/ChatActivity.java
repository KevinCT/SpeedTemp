package com.zweigbergk.speedswede;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.zweigbergk.speedswede.presenter.ChatPresenter;
import com.zweigbergk.speedswede.view.ChatView;

public class ChatActivity extends AppCompatActivity implements ChatView {

    public interface ViewListener {

    }

    private ViewListener mViewListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mViewListener = new ChatPresenter(this);
    }
}
