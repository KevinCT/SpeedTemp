package com.zweigbergk.speedswede.eyecandy;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import com.ogaclejapan.arclayout.ArcLayout;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.collection.Point;
import com.zweigbergk.speedswede.view.ChatView;

import com.zweigbergk.speedswede.util.collection.HashMap;


public class ArcMenu {
    private static final String TAG = ArcMenu.class.getSimpleName().toUpperCase();


    private ViewGroup rootLayout;
    private ArcLayout arcLayout;
    private TransparentLayout maskLayout;
    private View bgCircleView;

    private Point origin = new Point(0, 0);
    private ChatView contextProvider;

    public ArcMenu(ChatView contextProvider) {
        this.contextProvider = contextProvider;

        HashMap<Integer, View> components = contextProvider.getArcComponents();

        rootLayout = (ViewGroup) components.get(R.id.arc_root_layout);
        arcLayout = (ArcLayout) components.get(R.id.arc_layout);
        maskLayout = (TransparentLayout) components.get(R.id.arc_clickable_view_or_no);
        bgCircleView = components.get(R.id.arc_layout_background_circle);


        maskLayout.onTouchRegistered(this::hide);
    }

    public void update() {
        if (rootLayout.getVisibility() == View.VISIBLE) {
            hide();
        } else {
            show();
        }
    }

    public void setOrigin(Point point) {
        origin = point;
    }

    private void fadeOut(View view) {
        contextProvider.useContext(context ->
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_out)));
    }

    private void fadeIn(View view) {
        contextProvider.useContext(context ->
                view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in)));
    }

    private void show() {
        rootLayout.setVisibility(View.VISIBLE);
        fadeIn(bgCircleView);

        /*Point p1 = calculateDelta(22.5f, 300);
        Point p2 = calculateDelta(67.5f, 300);*/
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 0);
        Animator firstAnimator = createShowItemAnimator(arcLayout.getChildAt(0), p1);
        Animator secondAnimator = createShowItemAnimator(arcLayout.getChildAt(1), p2);

        firstAnimator.setDuration(400);
        firstAnimator.setInterpolator(new OvershootInterpolator(1.5f));
        secondAnimator.setDuration(400);
        secondAnimator.setInterpolator(new OvershootInterpolator(1.5f));
        firstAnimator.start();
        secondAnimator.setStartDelay(35);
        secondAnimator.start();
    }

    public Button getButton(int resId) {
        return (Button) arcLayout.findViewById(resId);
    }

    private void hide(boolean instant) {
        if (instant) {
            rootLayout.setVisibility(View.GONE);
        }

        fadeOut(bgCircleView);

        /*Point p1 = calculateDelta(22.5f, 80);
        Point p2 = calculateDelta(67.5f, 80);*/
        Point p1 = new Point(0, 0);
        Point p2 = new Point(0, 0);
        Animator firstAnimator = createHideItemAnimator(arcLayout.getChildAt(0), p1);
        Animator secondAnimator = createHideItemAnimator(arcLayout.getChildAt(1), p2);

        firstAnimator.setDuration(400);
        firstAnimator.setInterpolator(new AnticipateInterpolator(1.5f));
        secondAnimator.setDuration(400);
        secondAnimator.setInterpolator(new AnticipateInterpolator(1.5f));
        firstAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootLayout.setVisibility(View.INVISIBLE);
            }
        });

        secondAnimator.start();
        firstAnimator.setStartDelay(35);
        firstAnimator.start();
    }

    private void hide() {
        hide(false);
    }

    /*private void printAngleInCelcius(Point point) {
        double celcius = (Math.PI * rad) / 180
    }*/

    private Animator createShowItemAnimator(View item, Point delta) {
        float dx = origin.x - item.getX() - delta.x;
        float dy = origin.y - item.getY() + delta.y;

        Log.d(TAG, Stringify.curlyFormat("createShowItemAnimator: x: {x}, y: {y}", dx, dy));

        item.setRotation(0f);
        item.setTranslationX(dx);
        item.setTranslationY(dy);

        Animator animator =  ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(0f, 360f),
                AnimatorUtils.translationX(dx, 0f),
                AnimatorUtils.translationY(dy, 0f)
        );

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                item.setVisibility(View.VISIBLE);
            }
        });

        return animator;
    }

    private Animator createHideItemAnimator(final View item, Point delta) {
        float dx = origin.x - item.getX() + delta.x;
        float dy = origin.y - item.getY() - delta.y;

        //printAngleInCelcius(new Point(origin.x - item.getX(), origin.y - item.getY()));

        Log.d(TAG, Stringify.curlyFormat("createHideItemAnimator: x: {x}, y: {y}", dx, dy));

        Animator animator = ObjectAnimator.ofPropertyValuesHolder(
                item,
                AnimatorUtils.rotation(360f, 0f),
                AnimatorUtils.translationX(0f, dx),
                AnimatorUtils.translationY(0f, dy)
        );

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                item.setVisibility(View.INVISIBLE);
                item.setTranslationX(0f);
                item.setTranslationY(0f);
            }
        });

        return animator;
    }

    private Animator createShowItemAnimator(View item) {
        return createShowItemAnimator(item, new Point(0, 0));
    }

    private Animator createHideItemAnimator(final View item) {
        return createHideItemAnimator(item, new Point(0, 0));
    }

    /**
     *
     * @param angle in celcius
     * @param value distance
     * return
     */
    private Point calculateDelta(float angle, float value) {
        double rad = (angle * Math.PI) / 180;
        double x = Math.sin(rad) * value;
        double y = Math.cos(rad) * value;
        Log.d(TAG, Stringify.curlyFormat("calculateDelta: angle: {angle} x: {x}, y: {y}", angle, x, y));
        return new Point((float) x, (float) y);
    }

    public void onDestroy() {
        hide(true);
    }
}
