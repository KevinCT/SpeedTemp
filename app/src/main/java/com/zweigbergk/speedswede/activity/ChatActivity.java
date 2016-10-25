package com.zweigbergk.speedswede.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.eyecandy.MatchLoadingIndicatorLayout;
import com.zweigbergk.speedswede.eyecandy.PanelSlideListener;
import com.zweigbergk.speedswede.eyecandy.TransparentLayout;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.methodwrapper.CallerMethod;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;
import com.zweigbergk.speedswede.view.ChatView;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class ChatActivity extends AppCompatActivity implements ChatView {
    private static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

    private static final int FRAGMENT_CONTAINER = R.id.fragment_container;
    private static final String CHATLIST_FRAGMENT_NAME = "ChatListFragment";

    public static final List<String> fragmentStack = new ArrayList<>();

    private Toolbar toolbar;

    private SlidingUpPanelLayout slidingLayout;
    private MatchLoadingIndicatorLayout matchLoadingLayout;

    private FancyButton matchButton;
    private TransparentLayout coverMatchButtonLayout;
    private static final float MATCH_BUTTON_MIN_ALPHA = 0.25f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Log.d(TAG, "onCreate()");

        setupToolbar();

        //We need our ChatListFragment if it's the first run
        if (savedInstanceState == null) {
            addFragment(new ChatListFragment());
        }

        //Grab the views
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        coverMatchButtonLayout = (TransparentLayout) findViewById(R.id.cover_match_button_layout);
        matchButton = (FancyButton) findViewById(R.id.button_start_match);
        matchLoadingLayout = (MatchLoadingIndicatorLayout) findViewById(R.id.match_loading_layout);

        //Don't let clicks through to matching button
        coverMatchButtonLayout.setBlockClickEvents(true);

        /*coverMatchButtonLayout.setOnClickListener(v -> {
            if (slidingLayout.getPanelState() == PanelState.EXPANDED)
                return;

            if (coverMatchButtonLayout.isBlockingClickEvents()) {
                slidingLayout.setPanelState(PanelState.EXPANDED);
                coverMatchButtonLayout.setBlockClickEvents(false);
            } else {
                if (slidingLayout.getPanelState() != PanelState.EXPANDED) {
                    slidingLayout.setPanelState(PanelState.COLLAPSED);
                    coverMatchButtonLayout.setBlockClickEvents(true);
                }
            }
        });*/

        coverMatchButtonLayout.onTouchRegistered(() -> {
            if (slidingLayout.getPanelState() == PanelState.EXPANDED)
                return;

            if (coverMatchButtonLayout.isBlockingClickEvents()) {
                slidingLayout.setPanelState(PanelState.EXPANDED);
                coverMatchButtonLayout.setBlockClickEvents(false);
            } else {
                if (slidingLayout.getPanelState() != PanelState.EXPANDED) {
                    slidingLayout.setPanelState(PanelState.COLLAPSED);
                    coverMatchButtonLayout.setBlockClickEvents(true);
                }
            }
        });

        slidingLayout.addPanelSlideListener(new PanelSlideListener()
                .onPanelSlide(slideOffset ->
                        matchButton.setAlpha(Math.max(slideOffset, MATCH_BUTTON_MIN_ALPHA)))
                .onPanelStateChanged(newState -> {
                    switch (newState) {
                        case COLLAPSED:
                            coverMatchButtonLayout.setBlockClickEvents(true);
                            break;
                        default:
                            coverMatchButtonLayout.setBlockClickEvents(false);
                    }
                }));

        //Matcher button
        matchButton.setOnClickListener(v -> {
            if (matchButton.getVisibility() == View.VISIBLE) {
                matchLoadingLayout.show();
                ChatMatcher.INSTANCE.pushUser(DatabaseHandler.getActiveUser());
            }
        });

        matchLoadingLayout.initialize(this, matchButton);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_go_back_left_arrow);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
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

    private void addFragment(Fragment fragment) {
        String name = getFragmentName(fragment);
        fragmentStack.add(name);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .add(FRAGMENT_CONTAINER, fragment, name);

        transaction.commit();
    }

    public void switchToFragment(Fragment fragment, boolean addToBackstack) {
        String name = getFragmentName(fragment);
        fragmentStack.add(name);

        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(FRAGMENT_CONTAINER, fragment, name);

        if (addToBackstack) {
            transaction.addToBackStack(name);
        }

        transaction.commit();

        updateMatcherLayout();
        supportInvalidateOptionsMenu();
    }

    private String getFragmentName(Fragment fragment) {
        return fragment.getClass().getSimpleName();
    }

    @Override
    public void displayChat(Chat chat) {
        if (chat != null) {
            Log.d(TAG, "Displaying chat with ID: " + chat.getId());
            Intent chatIntent = new Intent(this, SingleChatActivity.class);
            chatIntent.putExtra(Constants.CHAT_PARCEL, chat);
            startActivity(chatIntent);
        } else {
            Log.w(TAG, "WARNING! Tried to display a null chat. ");
            new Exception().printStackTrace();
        }
    }

    @Override
    public void popBackStack() {
        getSupportFragmentManager().popBackStack();
        Log.d(TAG, "popBackStack()");
        updateMatcherLayout();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putIntegerArrayList("viewStates", Arrays.asList(
                matchButton.getVisibility(),
                matchLoadingLayout.getVisibility()
        ));

        bundle.putFloat("matchButtonAlpha", matchButton.getAlpha());

        bundle.putBoolean("matchButtonContainerState", coverMatchButtonLayout.isBlockingClickEvents());

        super.onSaveInstanceState(bundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle bundle) {
        super.onRestoreInstanceState(bundle);

        java.util.ArrayList<Integer> states = bundle.getIntegerArrayList("viewStates");

        if (states != null) {
            Log.d(TAG, "Not null!");

            switch (states.get(0)) {
                case View.GONE:
                    matchButton.setVisibility(View.GONE);
                    break;
                default:
                    matchButton.setVisibility(View.VISIBLE);
            }

            switch (states.get(1)) {
                case View.GONE:
                    matchLoadingLayout.setVisibility(View.GONE);
                    break;
                default:
                    matchLoadingLayout.setVisibility(View.VISIBLE);
            }
        }

        matchButton.setAlpha(bundle.getFloat("matchButtonAlpha"));

        coverMatchButtonLayout.setBlockClickEvents(bundle.getBoolean("matchButtonContainerState"));
    }

    public void updateMatcherLayout() {
        if (fragmentStack.size() > 0) {
            if (fragmentStack.getLast().equalsIgnoreCase(CHATLIST_FRAGMENT_NAME)) {
                slidingLayout.setPanelState(PanelState.COLLAPSED);
            } else {
                slidingLayout.setPanelState(PanelState.HIDDEN);
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        if(LanguageChanger.isChanged()){
            LanguageChanger.languageChanged(false);
            //Delay by 1 ms to avoid RuntimeException:
            // Performing pause of activity that is not resumed
            new Handler().postDelayed(this::recreate, 1);
        }
    }

    @Override
    public void onBackPressed() {
        setTitle(R.string.app_name);

        FragmentManager manager = getSupportFragmentManager();

        //Pop the latest fragment off the stack. If there is no fragment on the stack,
        // let default behavior take over
        boolean isStackEmpty = !manager.popBackStackImmediate();
        fragmentStack.removeLast();
        if (isStackEmpty) {
            super.onBackPressed();
        }
        invalidateOptionsMenu();

        updateMatcherLayout();
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
}
