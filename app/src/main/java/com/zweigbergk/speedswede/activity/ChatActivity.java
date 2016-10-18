package com.zweigbergk.speedswede.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.view.MenuItem;

import android.view.Menu;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.wang.avi.AVLoadingIndicatorView;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.ChatMatcher;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.eyecandy.TransparentLayout;
import com.zweigbergk.speedswede.fragment.ChangeLanguageFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.util.collection.Arrays;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.methodwrapper.CallerMethod;
import com.zweigbergk.speedswede.util.methodwrapper.ProviderMethod;
import com.zweigbergk.speedswede.view.ChatView;

import mehdi.sakout.fancybuttons.FancyButton;

import static com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;


public class ChatActivity extends AppCompatActivity implements ChatView {
    private static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

    private static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    private String currentFragmentName = "ChatListFragment";
    public static final List<String> fragmentStack = new ArrayList<>();

    private boolean draggerState = true;

    private Toolbar toolbar;

    private SlidingUpPanelLayout slidingLayout;
    private AVLoadingIndicatorView matchLoadingView;

    private FancyButton matchButton;
    private TransparentLayout coverMatchButtonLayout;
    private static final float MATCH_BUTTON_MIN_ALPHA = 0.25f;

    private TextView matchLoadingText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Log.d(TAG, "onCreate()");
        if (savedInstanceState == null) {
            createActivity();
        }
        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        coverMatchButtonLayout = (TransparentLayout) findViewById(R.id.cover_match_button_layout);
        coverMatchButtonLayout.setStopClicks(true);
        coverMatchButtonLayout.setOnClickListener(v -> {
            slidingLayout.setPanelState(PanelState.EXPANDED);
            coverMatchButtonLayout.setVisibility(View.GONE);
        });

        //Can probably be removed, not used right now~
        ImageView draggerIcon = (ImageView) findViewById(R.id.dragger_icon);
        draggerIcon.setOnClickListener(v -> {
            if (draggerState) {
                v.setRotation(0);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 180);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(v, pvhR);
                animation.start();
            } else {
                v.setRotation(180);
                PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(v, pvhR);
                animation.start();
            }

            if (slidingLayout.getPanelState().equals(PanelState.COLLAPSED)) {
                slidingLayout.setPanelState(PanelState.EXPANDED);
            } else {
                slidingLayout.setPanelState(PanelState.COLLAPSED);
            }

            draggerState = !draggerState;
        });

        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.d(TAG, "Offset: " + slideOffset);
                matchButton.setAlpha(Math.max(slideOffset, MATCH_BUTTON_MIN_ALPHA));
                if (matchButton.getAlpha() == MATCH_BUTTON_MIN_ALPHA ) {
                    matchButton.setClickable(false);
                    matchButton.setFocusable(false);
                }

                if (matchButton.getAlpha() == 1) {
                    matchButton.setClickable(true);
                    matchButton.setFocusable(true);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, PanelState previousState, PanelState newState) {
                switch (newState) {
                    case COLLAPSED:
                        coverMatchButtonLayout.setVisibility(View.VISIBLE);
                }
            }
        });

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

        setTitle(R.string.chat_header);

        Arrays.asList(arcComponentIds).foreach(this::addArcComponent);
    }

    private void addArcComponent(int resId) {
        arcComponents.put(resId, findViewById(resId));

        //Text displayed below matching indicator
        matchLoadingText = (TextView) findViewById(R.id.text_start_match);

        //Matcher button
        matchButton = (FancyButton) findViewById(R.id.button_start_match);
        matchButton.setClickable(false);
        matchButton.setFocusable(false);
        matchButton.setOnClickListener(v -> {
            if (matchButton.getVisibility() == View.VISIBLE) {
                matchLoadingText.setVisibility(View.VISIBLE);
                Animation fadein = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                matchLoadingText.setAnimation(fadein);

                Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        matchButton.setVisibility(View.GONE);
                        matchLoadingView.smoothToShow();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                matchButton.startAnimation(animation);
                ChatMatcher.INSTANCE.pushUser(DatabaseHandler.getActiveUser());

            }
        });

        //Setup match loading view
        matchLoadingView = (AVLoadingIndicatorView) findViewById(R.id.match_loading_indicator);
        DatabaseHandler.getPool().bind(change -> {
            User user = change.getItem();
            User activeUser = DatabaseHandler.getActiveUser();
            if (activeUser.equals(user)) {
                switch (change.getEvent()) {
                    case REMOVED:
                        matchLoadingView.hide();
                        Animation fadeout = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
                        matchLoadingText.setAnimation(fadeout);
                        fadeout.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                matchLoadingText.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        //Show matcher button again after 1 second
                        new Handler().postDelayed(() -> {
                            matchButton.setVisibility(View.VISIBLE);
                            Animation animation = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });

                            matchButton.startAnimation(animation);
                        }, 1000);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }

    private Animation fadeout() {
        return AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
    }

    private Animation partialFadeIn() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.partial_fade_in);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);

        return anim;
    }

    private Animation partialFadeOut() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.partial_fade_out);
        anim.setFillEnabled(true);
        anim.setFillAfter(true);

        return anim;
    }

    private Animation fadein() {
        return AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
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
        fragmentStack.add(name);

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
        currentFragmentName = name;
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
            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setChat(chat);
            setTitle(chat.getName());
            //setUpActionBar(chatFragment);
            switchToFragment(chatFragment, true);
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
        ArrayList<Integer> states = new ArrayList<>();
        states.add(matchLoadingView.getVisibility());
        states.add(matchButton.getVisibility());
        states.add(matchLoadingText.getVisibility());
        bundle.putIntegerArrayList("viewStates", states);

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
                    matchLoadingView.setVisibility(View.GONE);
                    break;
                default:
                    matchLoadingView.setVisibility(View.VISIBLE);
            }

            switch (states.get(1)) {
                case View.GONE:
                    matchButton.setVisibility(View.GONE);
                    break;
                default:
                    matchButton.setVisibility(View.VISIBLE);
            }

            switch (states.get(0)) {
                case View.GONE:
                    matchLoadingText.setVisibility(View.GONE);
                    break;
                default:
                    matchLoadingText.setVisibility(View.VISIBLE);
            }
        }

        if (slidingLayout.getPanelState() == PanelState.EXPANDED) {
            matchButton.setClickable(false);
            matchButton.setFocusable(false);
        }
    }

    public void updateMatcherLayout() {
        Log.d(TAG, "NAMEYY: " + getFragmentName(new ChatListFragment()));

        Log.d(TAG, "fragmentStack last: " + fragmentStack.getLast());
        if (fragmentStack.getLast().equalsIgnoreCase("ChatListFragment")) {
            Log.d(TAG, "We be collapsin~");
            slidingLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            Log.d(TAG, "We be hidin~");
            slidingLayout.setPanelState(PanelState.HIDDEN);
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

    public void openLanguageFragment() {
        ChangeLanguageFragment languageFragment = new ChangeLanguageFragment();
        switchToFragment(languageFragment, true);
    }

    @Override
    public void onBackPressed() {
        setTitle(R.string.chat_header);

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
/**
    public void setUpActionBar(ChatFragment chatFragment) {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);

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

        } else if (chatFragment.hasBothUsersLiked() && !hasClicked) {
            imageView.setImageResource(R.drawable.com_facebook_button_icon_blue);
        } else if (chatFragment.hasLocalUserLiked() && hasClicked) {
            chatFragment.setLikeForLocalUser(false);
            imageView.setImageResource(R.drawable.com_facebook_button_send_icon_blue);

        } else if (chatFragment.hasLocalUserLiked() && !hasClicked) {
            imageView.setImageResource(R.drawable.com_facebook_button_like_icon_selected);
        } else if (chatFragment.hasOtherUserLiked() && hasClicked) {
            chatFragment.setLikeForLocalUser(true);
            imageView.setImageResource(R.drawable.com_facebook_button_icon_blue);

        } else if (!chatFragment.hasLocalUserLiked() && hasClicked) { //Double check
            chatFragment.setLikeForLocalUser(true);
            imageView.setImageResource(R.drawable.com_facebook_button_like_icon_selected);
        } else if (!hasClicked) {
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
 */

    public TransparentLayout getMaskLayout() {
        return (TransparentLayout) findViewById(R.id.transparent_layout);
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
