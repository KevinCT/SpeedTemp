package com.zweigbergk.speedswede.eyecandy;

import android.content.Context;
import android.util.AttributeSet;
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

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                break;
            default:
        }
        executables.foreach(Executable::run);
        return false;
    }

    public void onTouchRegistered(Executable executable) {
        executables.add(executable);
    }
}
