package com.zweigbergk.speedswede.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.fragment.ChangeLanguageFragment;
import com.zweigbergk.speedswede.fragment.ChatFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.methodwrapper.CallerMethod;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;
import com.zweigbergk.speedswede.view.ChatView;


public class ChatActivity extends AppCompatActivity implements ChatView {
    private static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

    private static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    private HashMap<Integer, View> arcComponents;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (savedInstanceState == null) {
            createActivity();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_go_back_left_arrow);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        arcComponents = new HashMap<>();
        Integer[] arcComponentIds = {
                R.id.arc_layout_background_circle, R.id.arc_root_layout, R.id.arc_layout,
                R.id.arc_clickable_view_or_no
        };

        Arrays.asList(arcComponentIds).foreach(this::addArcComponent);
    }

    private void addArcComponent(int resId) {
        arcComponents.put(resId, findViewById(resId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
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
                .add(FRAGMENT_CONTAINER, fragment, name);

        if (addToBackstack) {
            transaction.addToBackStack(name);
        }

        transaction.commit();
    }

    public void switchToFragment(Fragment fragment, boolean addToBackstack) {
        String name = getFragmentName(fragment);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(FRAGMENT_CONTAINER, fragment, name);

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
            switchToFragment(chatFragment, true);
        } else {
            Log.w(TAG, "WARNING! Tried to display a null chat. ");
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
        invalidateOptionsMenu();
    }

    public void startSettings() {
        Intent intent = new Intent(ChatActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public <T> T contextualize(ProviderMethod<T, Context> method) {
        return method.call(this);
    }

    @Override
    public void useContext(CallerMethod<Context> method) {
        method.call(this);
    }

    @Override
    public HashMap<Integer, View> getArcComponents() {
        return arcComponents;
    }
}
