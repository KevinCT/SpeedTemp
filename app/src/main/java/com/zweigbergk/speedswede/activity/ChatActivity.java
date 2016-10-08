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
import com.zweigbergk.speedswede.fragment.ChatFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.presenter.ChatPresenter;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.view.ChatView;


public class ChatActivity extends AppCompatActivity implements ChatView {

    public static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

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
        changeFragment(mChatListFragment, true, false);
    }

    @Override
    public void displayChat(Chat chat) {
        if (chat != null) {
            Log.d(TAG, "Displaying chat with ID: " + chat.getId());
            mChatFragment.setChat(chat);
            changeFragment(mChatFragment, false, false);
        } else {
            Log.e(TAG, "WARNING! Tried to display a null chat. ");
            new Exception().printStackTrace();
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

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back pressed.");
        if (!isActive(mChatListFragment)) {
            Log.d(TAG, "Changing fragment...");
            changeFragment(mChatListFragment, true, false);
        } else {
            Log.d(TAG, "Super takes charge...");
            super.onBackPressed();
        }
    }

    private boolean isActive(Fragment fragment) {
        FragmentManager manager = getSupportFragmentManager();
        return fragment != null && !manager.findFragmentByTag(getFragmentName(fragment)).isDetached();
    }

    public void startSettings() {
        Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * From: https://gist.github.com/HugoGresse/c74adef25d76efada4d9
     * Date: 08/10/206
     *
     * Change the current displayed fragment by a new one.
     * - if the fragment is in backstack, it will pop it
     * - if the fragment is already displayed (trying to change the fragment with the same), it will not do anything
     *
     * @param fragment            the new fragment to display
     * @param saveInBackstack if we want the fragment to be in backstack
     * @param animate         if we want an animation
     */
    private void changeFragment(Fragment fragment, boolean saveInBackstack, boolean animate) {
        String backStateName = getFragmentName(fragment);
        Log.d(TAG, "1");
        try {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

            if (manager.getFragments() != null) {
                Lists.forEach(manager.getFragments(), frag -> {
                    if (!getFragmentName(frag).equals(backStateName)) {
                        Log.d(TAG, "Detaching " + getFragmentName(frag));
                        transaction.detach(frag);
                    }
                });
            }

            if (!fragmentPopped && manager.findFragmentByTag(backStateName) == null) {

                if (animate) {
                    Log.d(TAG, "Change Fragment: animate");
                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
                }

                transaction.add(R.id.activity_chat_root, fragment, backStateName);

                if (saveInBackstack) {
                    Log.d(TAG, "Change Fragment: addToBackTack " + backStateName);
                    transaction.addToBackStack(backStateName);
                } else {
                    Log.d(TAG, "Change Fragment: NO addToBackTack");
                }

                transaction.commit();
            } else {
                Fragment existingFragment = manager.findFragmentByTag(backStateName);
                transaction.attach(existingFragment);
                transaction.commit();
            }
        } catch (IllegalStateException exception) {
            Log.w(TAG, "Unable to commit fragment, activity might have been killed in background. " + exception.toString());
        }
    }

    private String getFragmentName(Fragment fragment) {
        return ((Object) fragment).getClass().getName();
    }
}
