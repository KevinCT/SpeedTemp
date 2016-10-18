package com.zweigbergk.speedswede.eyecandy;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.view.ChatView;

import mehdi.sakout.fancybuttons.FancyButton;

public class MatchLoadingIndicatorLayout extends LinearLayout {

    private Client<DataChange<User>> poolClient;
    private AVLoadingIndicatorView loadingIndicator;
    private TextView loadingText;
    private FancyButton matchButton;

    private ChatView contextProvider;

    public MatchLoadingIndicatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MatchLoadingIndicatorLayout(Context context) {
        super(context);
    }

    public MatchLoadingIndicatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initialize(ChatView contextProvider, FancyButton button) {
        this.contextProvider = contextProvider;
        matchButton = button;
        loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.match_loading_indicator);
        loadingText = (TextView) findViewById(R.id.match_loading_text);

        createPoolClient();

        DatabaseHandler.getPool().bind(getPoolClient());
    }

    public Client<DataChange<User>> getPoolClient() {
        return poolClient;
    }

    public Animation fadeInAnimation() {
        return contextProvider.contextualize(context -> AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
    }

    public Animation fadeOutAnimation() {
        return contextProvider.contextualize(context -> AnimationUtils.loadAnimation(context, android.R.anim.fade_out));
    }

    public void show() {
        setVisibility(VISIBLE);
        loadingText.setVisibility(VISIBLE);
        loadingText.setAnimation(fadeInAnimation());

        Animation animation = fadeOutAnimation();
        animation.setAnimationListener(new AnimationListener().onEnd(() -> {
            matchButton.setVisibility(View.GONE);
            loadingIndicator.smoothToShow();
        }));

        matchButton.startAnimation(animation);
    }

    private void createPoolClient() {
        poolClient = change -> {
            User user = change.getItem();
            User activeUser = DatabaseHandler.getActiveUser();
            if (activeUser.equals(user)) {
                switch (change.getEvent()) {
                    case REMOVED:
                        loadingIndicator.hide();
                        Animation fadeout = fadeOutAnimation();
                        loadingText.setVisibility(GONE);

                        //Show matcher button again after a brief moment
                        new Handler().postDelayed(() -> {
                            matchButton.setVisibility(View.VISIBLE);
                            matchButton.startAnimation(fadeInAnimation());
                            this.setVisibility(View.GONE);
                        }, 450);

                }
            }
        };
    }
}
