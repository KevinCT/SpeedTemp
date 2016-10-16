package com.zweigbergk.speedswede.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
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
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Toast;


import com.ogaclejapan.arclayout.ArcLayout;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.eyecandy.AnimatorUtils;
import com.zweigbergk.speedswede.eyecandy.TransparentLayout;
import com.zweigbergk.speedswede.fragment.ChangeLanguageFragment;
import com.zweigbergk.speedswede.fragment.ChatFragment;
import com.zweigbergk.speedswede.fragment.ChatListFragment;
import com.zweigbergk.speedswede.presenter.ChatPresenter;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.view.ChatView;


public class ChatActivity extends AppCompatActivity implements ChatView {
    private static final String TAG = ChatActivity.class.getSimpleName().toUpperCase();

    private static final int FRAGMENT_CONTAINER = R.id.fragment_container;

    private Point arcLayoutPosition = new Point(0, 0);

    private View menuLayout;
    private ArcLayout arcLayout;
    private Toolbar toolbar;

    Toast toast = null;

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
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        new ChatPresenter(this);

        //Eyecandy
        menuLayout = findViewById(R.id.menu_layout);
        arcLayout = (ArcLayout) findViewById(R.id.arc_layout);

        //Test REMOVE
        TransparentLayout test = (TransparentLayout) findViewById(R.id.arc_clickable_view_or_no);

        test.onTouchRegistered(() -> {
            Log.d(TAG, "Touch received~");
            hideMenu();
        });
    }

    public void setArcLayoutPosition(Point point) {
        arcLayoutPosition = point;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat_activity, menu);
        return true;
    }

    public void updateArcMenu() {
        if (menuLayout.getVisibility() == View.VISIBLE) {
            hideMenu();
        } else {
            showMenu();
        }
    }

    public void hideArcMenu() {
        menuLayout.setVisibility(View.INVISIBLE);
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

    //TODO Refactor this garbage

    private void showToast(Button btn) {
        if (toast != null) {
            toast.cancel();
        }

        String text = "Clicked: " + btn.getText();
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void showMenu() {
        menuLayout.setVisibility(View.VISIBLE);
        menuLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        List<Animator> animList = new ArrayList<>();

        for (int i = 0; i < arcLayout.getChildCount(); ++i) {
            View currentView = arcLayout.getChildAt(i);
            animList.add(createShowItemAnimator(currentView));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    public Button getBlockUserButton() {
        return (Button) arcLayout.findViewById(R.id.btn_arc_menu_block_user);
    }

    public Button getLeaveChatButton() {
        return (Button) arcLayout.findViewById(R.id.btn_arc_menu_leave_chat);
    }

    public void hideMenu() {
        menuLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));

        java.util.List<Animator> animList = new java.util.ArrayList<>();

        for (int i = 0; i < arcLayout.getChildCount(); ++i) {
            View currentView = arcLayout.getChildAt(i);
            animList.add(createHideItemAnimator(currentView));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                menuLayout.setVisibility(View.INVISIBLE);
            }
        });
        animSet.start();

    }

    private Animator createShowItemAnimator(View item) {
        float dx = arcLayoutPosition.x - item.getX();
        float dy = arcLayoutPosition.y - item.getY();

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        return ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 720f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );
    }

    private Animator createHideItemAnimator(final View item) {
        float dx = arcLayoutPosition.x - item.getX();
        float dy = arcLayoutPosition.y - item.getY();

        Animator anim = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(720f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return anim;
    }
}
