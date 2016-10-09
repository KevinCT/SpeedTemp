package com.zweigbergk.speedswede.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.fragment.ChangeLanguageFragment;
import com.zweigbergk.speedswede.fragment.ChatFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.presenter.ChatPresenter;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.view.ChatView;

import java.util.List;


public class ChatActivity extends AppCompatActivity implements ChatView {

    public static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

    private static final String CHAT_FRAGMENT_NAME = ChatFragment.class.getSimpleName();
    private static final String CHAT_LIST_FRAGMENT_NAME = ChatListFragment.class.getSimpleName();
    private static final String LANGUAGE_SETTINGS_FRAGMENT_NAME = ChatListFragment.class.getSimpleName();

    private ChatFragment mChatFragment;
    private Fragment mChatListFragment;
    private Fragment mLanguageFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mChatFragment = new ChatFragment();
            addFragment(mChatFragment);

            mChatListFragment = new ChatListFragment();
            addFragment(mChatListFragment);

            mLanguageFragment = new ChangeLanguageFragment();
            addFragment(mLanguageFragment);


        } else {
            mChatFragment = (ChatFragment) getSupportFragmentManager().findFragmentByTag(CHAT_FRAGMENT_NAME);
            mChatListFragment = getSupportFragmentManager().findFragmentByTag(CHAT_LIST_FRAGMENT_NAME);
            mLanguageFragment = getSupportFragmentManager().findFragmentByTag(LANGUAGE_SETTINGS_FRAGMENT_NAME);
        }

        showFragment(mChatListFragment);


        setContentView(R.layout.activity_chat);

        new ChatPresenter(this);
    }

    private void addFragment(Fragment fragment) {
        String name = getFragmentName(fragment);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_chat_root, fragment, name)
                .commit();
    }

    private String getFragmentName(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    private void showFragment(Fragment fragment) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        //Get all fragments except the argument fragment, and hide them.
        Lists.forEach(Lists.reject(fragments,
                currentFragment -> hasSameTag(currentFragment, fragment)),
                this::hide);

        //Show the argument fragment
        show(fragment);
    }

    public void showLanguageFragment() {

    }

    private boolean hasSameTag(Fragment first, Fragment second) {
        return first.getTag().equals(second.getTag());
    }

    @Override
    public void displayChat(Chat chat) {
        if (chat != null) {
            Log.d(TAG, "Displaying chat with ID: " + chat.getId());
            mChatFragment.setChat(chat);
            showFragment(mChatFragment);

        } else {
            Log.e(TAG, "WARNING! Tried to display a null chat. ");
            new Exception().printStackTrace();
        }
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
        if (!isInFragment(mChatListFragment)) {
            Log.d(TAG, "Changing fragment...");
            showFragment(mChatListFragment);
        } else {
            Log.d(TAG, "Super takes charge...");
            super.onBackPressed();
        }
    }

    private boolean isInFragment(Fragment fragment) {
        return getSupportFragmentManager().findFragmentByTag(getFragmentName(fragment)).isVisible();
    }

    public void startSettings() {
        Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
        startActivity(intent);
    }
}
