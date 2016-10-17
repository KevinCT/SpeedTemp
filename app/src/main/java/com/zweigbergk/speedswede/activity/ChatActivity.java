package com.zweigbergk.speedswede.activity;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;



import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.fragment.ChangeLanguageFragment;
import com.zweigbergk.speedswede.fragment.ChatFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.presenter.ChatPresenter;
import com.zweigbergk.speedswede.view.ChatView;


public class ChatActivity extends AppCompatActivity implements ChatView {

    public static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            createActivity();
        }

        new ChatPresenter(this);
    }

    private void createActivity() {
        addFragment(new ChatListFragment(), false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addFragment(Fragment fragment, boolean addToBackstack) {
        String name = getFragmentName(fragment);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.activity_chat_root, fragment, name);

        if (addToBackstack) {
            transaction.addToBackStack(name);
        }

        transaction.commit();
    }

    public void switchToFragment(Fragment fragment, boolean addToBackstack) {
        String name = getFragmentName(fragment);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_chat_root, fragment, name);

        if (addToBackstack) {
            transaction.addToBackStack(name);
        }

        transaction.commit();
    }

    private String getFragmentName(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    @Override
    public void displayChat(Chat chat) {

        if (chat != null) {

            Log.d(TAG, "Displaying chat with ID: " + chat.getId());
            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setChat(chat);
            setTitle(chat.getName());
            setUpActionBar(chatFragment);
            switchToFragment(chatFragment, true);
        } else {
            Log.e(TAG, "WARNING! Tried to display a null chat. ");
            new Exception().printStackTrace();
        }
    }

    @Override
    public void popBackStack() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(LanguageChanger.isChanged()){
            LanguageChanger.languageChanged(false);
            recreate();
        }
    }

    public void openLanguageFragment() {
        ChangeLanguageFragment fragment = new ChangeLanguageFragment();
        switchToFragment(fragment, true);
    }

    @Override
    public void onBackPressed() {
        setTitle(R.string.app_name);

        FragmentManager manager = getSupportFragmentManager();

        //Pop the latest fragment off the stack. If there is no fragment on the stack,
        // let default behavior take over
        boolean isStackEmpty = !manager.popBackStackImmediate();
        if (isStackEmpty) {
            super.onBackPressed();
        }
    }

    public void startSettings() {
        Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public void setUpActionBar(ChatFragment chatFragment) {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

        actionBar.setTitle("Title");

        ImageView imageView = new ImageView(actionBar.getThemedContext());
        imageView.setScaleType(ImageView.ScaleType.CENTER);

        likeCheck(chatFragment, imageView, actionBar, false);

        imageView.setClickable(true);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                likeCheck(chatFragment, imageView, actionBar, true);
            }
        });

    }

    public void likeCheck(ChatFragment chatFragment, ImageView imageView, ActionBar actionBar, boolean hasClicked) {


        if (chatFragment.hasBothUsersLiked() && hasClicked) {
            //Open facebook link
            imageView.setImageResource(R.drawable.com_facebook_button_icon_blue);

        }

        else if(chatFragment.hasBothUsersLiked() && !hasClicked) {
            imageView.setImageResource(R.drawable.com_facebook_button_icon_blue);
        }

        else if(chatFragment.hasLocalUserLiked() && hasClicked) {
            chatFragment.setLikeForLocalUser(false);
            imageView.setImageResource(R.drawable.com_facebook_button_send_icon_blue);

        }
        else if(chatFragment.hasLocalUserLiked() && !hasClicked) {
            imageView.setImageResource(R.drawable.com_facebook_button_like_icon_selected);
        }

        else if(chatFragment.hasOtherUserLiked() && hasClicked) {
            chatFragment.setLikeForLocalUser(true);
            imageView.setImageResource(R.drawable.com_facebook_button_icon_blue);

        }

        else if (!chatFragment.hasLocalUserLiked() && hasClicked) { //Double check
            chatFragment.setLikeForLocalUser(true);
            imageView.setImageResource(R.drawable.com_facebook_button_like_icon_selected);
        }

        else if(!hasClicked) {
            imageView.setImageResource(R.drawable.com_facebook_button_send_icon_blue);
        }

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT
                | Gravity.CENTER_VERTICAL);
        layoutParams.rightMargin = 40;
        imageView.setLayoutParams(layoutParams);
        actionBar.setCustomView(imageView);
    }
}
