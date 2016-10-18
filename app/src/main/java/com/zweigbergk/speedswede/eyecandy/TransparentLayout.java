package com.zweigbergk.speedswede.eyecandy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

/**
 * A layout that doesn't intercept click events
 */
public class TransparentLayout extends FrameLayout {
    private static final String TAG = TransparentLayout.class.getSimpleName().toUpperCase();


    private boolean stopClicks = false;

    private List<Executable> executables = new ArrayList<>();

    public TransparentLayout(Context context) {
        super(context);
    }

    public TransparentLayout(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    public TransparentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setStopClicks(boolean value) {
        stopClicks = value;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                break;
            default:
        }

        Log.d(TAG, "onInterceptTouchEvent()");
        executables.foreach(Executable::run);
        return stopClicks;
    }

    public void onTouchRegistered(Executable executable) {
        executables.add(executable);
    }
}
