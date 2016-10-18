package com.zweigbergk.speedswede.eyecandy;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.pathmenu.FloatingActionMenu;
import com.zweigbergk.speedswede.pathmenu.SubActionButton;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.view.ChatFragmentView;

import java.util.Timer;
import java.util.TimerTask;

public class PathMenu {
    private static final String TAG = PathMenu.class.getSimpleName().toUpperCase();

    private View.OnClickListener disabledClickListener;

    private FloatingActionMenu pathMenu;
    private View btnShowActions;

    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();
    private SubActionButton.Builder itemBuilder;
    private List<SubActionButton> buttons = new ArrayList<>();


    private ChatFragmentView contextProvider;

    public PathMenu(ChatFragmentView contextProvider, SubActionButton.Builder itemBuilder) {
        this.contextProvider = contextProvider;
        this.itemBuilder = itemBuilder;
    }

    public void setDisabledOnClick(View.OnClickListener listener) {
        this.disabledClickListener = listener;
    }

    public void allGone() {
        Log.d(TAG, "allGone()");
        pathMenu.disable();
        btnShowActions.setVisibility(View.GONE);
        buttons.foreach(button -> button.setVisibility(View.GONE));
        //contextProvider.useContext(context -> btnShowActions.setX(context.getResources().getDimension(R.dimen.hide_view)));
    }

    public void addImageViewWithAction(ImageView imageView, View.OnClickListener listener) {
        SubActionButton button = createPathButton(imageView);
        button.setOnClickListener(listener);
        buttons.add(button);
    }

    public void create() {
        startTimer();
    }

    public void close(boolean animated) {
        pathMenu.close(animated);
    }

    private SubActionButton createPathButton(ImageView view) {
        return itemBuilder.setContentView(view).build();
    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    btnShowActions = contextProvider.getParent().findViewById(R.id.show_actions);
                    if (btnShowActions != null) {
                        stoptimertask();

                        FloatingActionMenu.Builder builder = new FloatingActionMenu.Builder(contextProvider.getParent())
                                .setRadius(contextProvider.getParent().getResources().getDimensionPixelSize(R.dimen.path_menu_radius));
                        buttons.foreach(button -> builder.addSubActionView(button, 210, 210));
                        pathMenu = builder.setStartAngle(112)
                                .setEndAngle(158)
                                .attachTo(btnShowActions)
                                .build();

                        pathMenu.disabledClickListener = disabledClickListener;

                        pathMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
                            @Override
                            public void onMenuOpened(FloatingActionMenu menu) {
                                if (btnShowActions != null) {
                                    btnShowActions.setRotation(0);
                                    PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                                    ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(btnShowActions, pvhR);
                                    animation.start();
                                }
                            }

                            @Override
                            public void onMenuClosed(FloatingActionMenu menu) {
                                if (btnShowActions != null) {
                                    btnShowActions.setRotation(45);
                                    PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                                    ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(btnShowActions, pvhR);
                                    animation.start();
                                }
                            }
                        });
                    }
                });
            }};
    }

    private void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        timer.schedule(timerTask, 0, 50); //
    }

    private void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
