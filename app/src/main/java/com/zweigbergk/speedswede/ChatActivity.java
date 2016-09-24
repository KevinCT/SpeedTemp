package com.zweigbergk.speedswede;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zweigbergk.speedswede.adapter.ChatAdapter;
import com.zweigbergk.speedswede.fragment.ChatFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.presenter.ChatPresenter;
import com.zweigbergk.speedswede.view.ChatView;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity implements ChatView {

    public interface ViewListener {

    }

    private ViewListener mViewListener;

    private ChatAdapter mAdapter;
    private ViewPager mPager;

    private final Fragment mChatFragment = new ChatFragment();
    private final Fragment mChatListFragment = new ChatListFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d("DEBUGGING", "ChatActivity!");

        setUpContent();

        mViewListener = new ChatPresenter(this);
    }

    private void setUpContent() {
        mAdapter = new ChatAdapter(getSupportFragmentManager(), Arrays.asList(mChatListFragment, mChatFragment));
        mPager = (ViewPager) findViewById(R.id.chat_viewpager);
        mPager.setAdapter(mAdapter);
//        Log.d("a", mChatText.toString());
    }

}
