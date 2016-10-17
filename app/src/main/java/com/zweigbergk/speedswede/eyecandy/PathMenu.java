package com.zweigbergk.speedswede.eyecandy;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.pathmenu.FloatingActionMenu;
import com.zweigbergk.speedswede.pathmenu.SubActionButton;
import com.zweigbergk.speedswede.view.ChatFragmentView;

import java.util.Timer;
import java.util.TimerTask;

public class PathMenu {

    private FloatingActionMenu pathMenu;

    Timer timer;
    TimerTask timerTask;
    final Handler handler = new Handler();
    SubActionButton.Builder itemBuilder;


    private ChatFragmentView contextProvider;

    public PathMenu(ChatFragmentView contextProvider, SubActionButton.Builder itemBuilder) {
        this.contextProvider = contextProvider;
        this.itemBuilder = itemBuilder;
    }

    public void create() {
        startTimer();
    }

    public void close(boolean animated) {
        pathMenu.close(animated);
    }

    private SubActionButton createPathButton(int resId) {
        ImageView imageView = contextProvider.getImageView();
        contextProvider.useContext(context -> imageView.setImageDrawable(scaleImage(context.getResources().getDrawable(resId), 2)));
        return itemBuilder.setContentView(imageView).build();
    }

    public Drawable scaleImage(Drawable image, float scaleFactor) {

        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }

        Bitmap b = ((BitmapDrawable)image).getBitmap();

        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);

        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);

        image = contextProvider.contextualize(
                context -> new BitmapDrawable(context.getResources(), bitmapResized));

        return image;

    }

    private void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    View btnShowActions = contextProvider.getParent().findViewById(R.id.show_actions);
                    if (btnShowActions != null) {
                        stoptimertask();

                        SubActionButton leaveButton = createPathButton(R.drawable.ic_trashcan);
                        SubActionButton blockButton = createPathButton(R.drawable.ic_lock);

                        pathMenu = new FloatingActionMenu.Builder(contextProvider.getParent())
                                .setRadius(contextProvider.getParent().getResources().getDimensionPixelSize(R.dimen.path_menu_radius))
                                .addSubActionView(blockButton)
                                .addSubActionView(leaveButton)
                                .setStartAngle(112)
                                .setEndAngle(158)
                                .attachTo(btnShowActions)
                                .build();
                    }
                });
            }};
    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        timer.schedule(timerTask, 0, 50); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}
