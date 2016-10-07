package com.zweigbergk.speedswede.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.adapter.ChatAdapter;
import com.zweigbergk.speedswede.adapter.ChatListFragmentAdapter;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.fragment.ChatFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.presenter.ChatPresenter;
import com.zweigbergk.speedswede.view.ChatView;

import java.util.Arrays;

public class ChatActivity extends AppCompatActivity implements ChatView {

    public static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

    private ChatAdapter mAdapter;
    private ChatListFragmentAdapter mChatlistFragmentAdapter;
    private ViewPager mPager;

    private final ChatFragment mChatFragment = new ChatFragment();
    private final Fragment mChatListFragment = new ChatListFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        setUpContent();

        new ChatPresenter(this);
    }

    private void setUpContent() {
        mAdapter = new ChatAdapter(getSupportFragmentManager(), Arrays.asList(mChatListFragment, mChatFragment));
        mPager = (ViewPager) findViewById(R.id.chat_viewpager);
        mPager.setAdapter(mAdapter);
    }

    @Override
    public void setChatForChatFragment(Chat chat) {
        if (chat != null) {
            Log.d(TAG, "Setting chat for ChatFragment, chat ID: " + chat.getId());
            mChatFragment.setChat(chat);
        } else {
            Log.e(TAG, "WARNING! Tried to set a null chat as the active chat for ChatFragment.");
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(LanguageChanger.isChanged()){
            LanguageChanger.languageChanged(false);
            recreate();

        }
    }

    public void startSettings() {
        Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
