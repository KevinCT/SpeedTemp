package com.zweigbergk.speedswede.eyecandy;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.SingleChatActivity;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.pathmenu.FloatingActionMenu;
import com.zweigbergk.speedswede.pathmenu.SubActionButton;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.Timer;
import java.util.TimerTask;

public class PathMenu {

    private ListExtension<Client<Boolean>> stateClients = new ArrayListExtension<>();

    public void addStateClient(Client<Boolean> client) {
        stateClients.add(client);
    }

    private FloatingActionMenu pathMenu;
    private View btnShowActions;

    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();
    private SubActionButton.Builder itemBuilder;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ListExtension<SubActionButton> buttons = new ArrayListExtension<>();


    private SingleChatActivity contextProvider;

    public PathMenu(SingleChatActivity contextProvider, SubActionButton.Builder itemBuilder) {
        this.contextProvider = contextProvider;
        this.itemBuilder = itemBuilder;
        this.pathMenu = null;
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
                    btnShowActions = contextProvider.findViewById(R.id.show_actions);
                    if (btnShowActions != null) {
                        stopTimerTask();

                        int firstAngle = 112;
                        int secondAngle = 158;

                        String currentLocale = contextProvider.contextualize(LanguageChanger::getCurrentLanguage);
                        if (currentLocale.equals("ar")) {
                            firstAngle = 22;
                            secondAngle = 68;
                        }

                        FloatingActionMenu.Builder builder = new FloatingActionMenu.Builder(contextProvider)
                                .setRadius(contextProvider.getResources().getDimensionPixelSize(R.dimen.path_menu_radius));
                        buttons.foreach(button -> builder.addSubActionView(button, 210, 210));
                        pathMenu = builder.setStartAngle(firstAngle)
                                .setEndAngle(secondAngle)
                                .attachTo(btnShowActions)
                                .build();

                        pathMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
                            @Override
                            public void onMenuOpened(FloatingActionMenu menu) {
                                if (btnShowActions != null) {
                                    int angle = 45;

                                    String currentLocale = contextProvider.contextualize(LanguageChanger::getCurrentLanguage);
                                    if (currentLocale.equals("ar")) {
                                        angle = -45;
                                    }

                                    btnShowActions.setRotation(0);
                                    PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, angle);
                                    ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(btnShowActions, pvhR);
                                    animation.start();
                                    new Handler().postDelayed(() -> stateClients.foreach(client -> client.supply(true)),
                                            0);
                                }
                            }

                            @Override
                            public void onMenuClosed(FloatingActionMenu menu) {
                                if (btnShowActions != null) {
                                    int angle = 45;

                                    String currentLocale = contextProvider.contextualize(LanguageChanger::getCurrentLanguage);
                                    if (currentLocale.equals("ar")) {
                                        angle = -45;
                                    }

                                    btnShowActions.setRotation(angle);
                                    PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                                    ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(btnShowActions, pvhR);
                                    animation.start();
                                    new Handler().postDelayed(() -> stateClients.foreach(client -> client.supply(false)),
                                            0);
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

    private void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
