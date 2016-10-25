package com.zweigbergk.speedswede.eyecandy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

/**
 * A layout that doesn't intercept click events
 */
public class TransparentLayout extends RelativeLayout {
    private static final String TAG = TransparentLayout.class.getSimpleName().toUpperCase();


    private boolean catchClickEvents = false;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private ListExtension<Executable> executables = new ArrayListExtension<>();

    public TransparentLayout(Context context) {
        super(context);
    }

    public TransparentLayout(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
    }

    public TransparentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setBlockClickEvents(boolean value) {
        catchClickEvents = value;
    }

    public boolean isBlockingClickEvents() {
        return catchClickEvents;
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
        return catchClickEvents;
    }

    public void onTouchRegistered(Executable executable) {
        executables.add(executable);
    }
}
