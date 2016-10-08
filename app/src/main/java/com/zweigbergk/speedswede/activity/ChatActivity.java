package com.zweigbergk.speedswede.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.fragment.ChatFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.presenter.ChatPresenter;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.view.ChatView;


public class ChatActivity extends AppCompatActivity implements ChatView {

    public static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

    private static final String CHAT_FRAGMENT_NAME = ChatFragment.class.getSimpleName();
    private static final String CHAT_LIST_FRAGMENT_NAME = ChatListFragment.class.getSimpleName();

    private ChatFragment mChatFragment;
    private ChatListFragment mChatListFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mChatFragment = new ChatFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_chat_root, mChatFragment, CHAT_FRAGMENT_NAME)
                    .commit();

            mChatListFragment = new ChatListFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.activity_chat_root, mChatListFragment, CHAT_LIST_FRAGMENT_NAME)
                    .commit();
        } else {
            mChatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_NAME);
            mChatListFragment = (ChatListFragment) getSupportFragmentManager().findFragmentByTag(CHAT_LIST_FRAGMENT_NAME);
        }

        gotoChatListFragment();


        setContentView(R.layout.activity_chat);

        new ChatPresenter(this);
    }

    @Override
    public void displayChat(Chat chat) {
        if (chat != null) {
            Log.d(TAG, "Displaying chat with ID: " + chat.getId());
            mChatFragment.setChat(chat);
            gotoChatFragment();

        } else {
            Log.e(TAG, "WARNING! Tried to display a null chat. ");
            new Exception().printStackTrace();
        }
    }

    private void gotoChatFragment() {
        hide(mChatListFragment);
        show(mChatFragment);

    }

    private void gotoChatListFragment() {
        hide(mChatFragment);
        show(mChatListFragment);
    }

    private void hide(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().hide(fragment).commit();
    }

    private void show(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(LanguageChanger.isChanged()){
            LanguageChanger.languageChanged(false);
            recreate();
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back pressed.");
        if (!isInChatList()) {
            Log.d(TAG, "Changing fragment...");
            gotoChatListFragment();
        } else {
            Log.d(TAG, "Super takes charge...");
            super.onBackPressed();
        }
    }

    private boolean isInChatList() {
        return getSupportFragmentManager().findFragmentByTag(CHAT_LIST_FRAGMENT_NAME).isVisible();
    }

    public void startSettings() {
        Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
