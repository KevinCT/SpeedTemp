/*
 *   Copyright 2014 Oguz Bilgener
 */
package com.zweigbergk.speedswede.pathmenu;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Taken from: https://github.com/oguzbilgener/CircularFloatingActionMenu/
 *  Date: 17/10/2016
 *  All credit to Oguz Bilgener
 * Provides the main structure of the menu.
 */

public class FloatingActionMenu {
    private static final String TAG = FloatingActionMenu.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    /** Reference to the view (usually a button) to trigger the menu to show */
    private View mainActionView;
    /** The angle (in degrees, modulus 360) which the circular menu starts from  */
    private int startAngle;
    /** The angle (in degrees, modulus 360) which the circular menu ends at  */
    private int endAngle;
    /** Distance of menu items from mainActionView */
    private int radius;
    /** ListExtension of menu items */
    private List<Item> subActionItems;
    /** Reference to the preferred {@link MenuAnimationHandler} object */
    private MenuAnimationHandler animationHandler;
    /** Reference to a listener that listens open/close actions */
    private MenuStateChangeListener stateChangeListener;
    /** whether the openings and closings should be animated or not */
    private boolean animated;
    /** whether the menu is currently open or not */
    private boolean open;
    /** whether the menu is an overlay for all other activities */
    private boolean systemOverlay;
    /** a simple layout to contain all the sub action views in the system overlay mode */
    private FrameLayout overlayContainer;
    /**
     * Constructor that takes the parameters collected using {@link Builder}
     */
    private FloatingActionMenu(final View mainActionView,
                              int startAngle,
                              int endAngle,
                              int radius,
                              List<Item> subActionItems,
                              MenuAnimationHandler animationHandler,
                              boolean animated,
                              MenuStateChangeListener stateChangeListener,
                              final boolean systemOverlay) {
        Log.d(TAG, "This is called. (1)");
        this.mainActionView = mainActionView;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.radius = radius;
        this.subActionItems = subActionItems;
        this.animationHandler = animationHandler;
        this.animated = animated;
        this.systemOverlay = systemOverlay;
        // The menu is initially closed.
        this.open = false;

        this.stateChangeListener = stateChangeListener;

        // Listen click events on the main action view
        // In the future, touch and drag events could be listened to offer an alternative behaviour
        this.mainActionView.setClickable(true);
        this.mainActionView.setOnClickListener(new ActionViewClickListener());

        // Do not forget to set the menu as self to our customizable animation handler
        if(animationHandler != null) {
            animationHandler.setMenu(this);
        }

        if(systemOverlay) {
            overlayContainer = new FrameLayout(mainActionView.getContext());
        }
        else {
            overlayContainer = null; // beware NullPointerExceptions!
        }

        // Find items with undefined sizes
        for(final Item item : subActionItems) {
            if(item.width == 0 || item.height == 0) {
                if(systemOverlay) {
                    throw new RuntimeException("Sub action views cannot be added without " +
                            "definite width and height.");
                }
                // Figure out the size by temporarily adding it to the Activity content view hierarchy
                // and ask the size from the system
                addViewToCurrentContainer(item.view);
                // Make item view invisible, just in case
                item.view.setAlpha(0);
                // Wait for the right time
                item.view.post(new ItemViewQueueListener(item));
            }
        }

        if(systemOverlay) {
            OrientationEventListener orientationListener = new OrientationEventListener(mainActionView.getContext(), SensorManager.SENSOR_DELAY_UI) {
                private int lastState = -1;

                public void onOrientationChanged(int orientation) {

                    Display display = getWindowManager().getDefaultDisplay();
                    int rotation = display.getRotation();
                    if(rotation != lastState) {
                        lastState = display.getRotation();

                        //
                        if(isOpen()) {
                            close(false);
                        }
                    }
                }
            };
            orientationListener.enable();
        }
    }

    /**
     * Simply opens the menu by doing necessary calculations.
     * @param animated if true, this action is executed by the current {@link MenuAnimationHandler}
     */
    @SuppressWarnings("WeakerAccess")
    public void open(boolean animated) {

        // Get the center of the action view from the following function for efficiency
        // populate destination x,y coordinates of Items
        Point center = calculateItemPositions();

        WindowManager.LayoutParams overlayParams = null;

        if(systemOverlay) {
            // If this is a system overlay menu, use the overlay container and place it behind
            // the main action button so that all the views will be added into it.
            attachOverlayContainer();

            overlayParams = (WindowManager.LayoutParams) overlayContainer.getLayoutParams();
        }

        if(animated && animationHandler != null) {
            // If animations are enabled and we have a MenuAnimationHandler, let it do the heavy work
            if(animationHandler.isAnimating()) {
                // Do not proceed if there is an animation currently going on.
                return;
            }

            for (int i = 0; i < subActionItems.size(); i++) {
                // It is required that these Item views are not currently added to any parent
                // Because they are supposed to be added to the Activity content view,
                // just before the animation starts
                if (subActionItems.get(i).view.getParent() != null) {
                    ((ViewGroup) subActionItems.get(i).view.getParent()).removeView(subActionItems.get(i).view);
                    throw new RuntimeException("All of the sub action items have to be independent from a parent.");
                }

                // Initially, place all items right at the center of the main action view
                // Because they are supposed to start animating from that point.
                final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subActionItems.get(i).width, subActionItems.get(i).height, Gravity.TOP | Gravity.START);

                if(systemOverlay && overlayParams != null) {
                    params.setMargins(center.x - overlayParams.x - subActionItems.get(i).width / 2, center.y - overlayParams.y - subActionItems.get(i).height / 2, 0, 0);
                }
                else {
                    params.setMargins(center.x - subActionItems.get(i).width / 2, center.y - subActionItems.get(i).height / 2, 0, 0);
                }
                addViewToCurrentContainer(subActionItems.get(i).view, params);
            }
            // Tell the current MenuAnimationHandler to animate from the center
            animationHandler.animateMenuOpening(center);
        }
        else {
            // If animations are disabled, just place each of the items to their calculated destination positions.
            for (int i = 0; i < subActionItems.size(); i++) {
                // This is currently done by giving them large margins

                final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(subActionItems.get(i).width, subActionItems.get(i).height, Gravity.TOP | Gravity.START);
                if(systemOverlay && overlayParams != null) {
                    params.setMargins(subActionItems.get(i).x - overlayParams.x, subActionItems.get(i).y - overlayParams.y, 0, 0);
                    subActionItems.get(i).view.setLayoutParams(params);
                }
                else {
                    params.setMargins(subActionItems.get(i).x, subActionItems.get(i).y, 0, 0);
                    subActionItems.get(i).view.setLayoutParams(params);
                    // Because they are placed into the main content view of the Activity,
                    // which is itself a FrameLayout
                }
                addViewToCurrentContainer(subActionItems.get(i).view, params);
            }
        }
        // do not forget to specify that the menu is open.
        open = true;

        if(stateChangeListener != null) {
            stateChangeListener.onMenuOpened(this);
        }

    }

    /**
     * Closes the menu.
     * @param animated if true, this action is executed by the current {@link MenuAnimationHandler}
     */
    public void close(boolean animated) {
        // If animations are enabled and we have a MenuAnimationHandler, let it do the heavy work
        if(animated && animationHandler != null) {
            if(animationHandler.isAnimating()) {
                // Do not proceed if there is an animation currently going on.
                return;
            }
            animationHandler.animateMenuClosing(getActionViewCenter());
        }
        else {
            // If animations are disabled, just detach each of the Item views from the Activity content view.
            for (int i = 0; i < subActionItems.size(); i++) {
                removeViewFromCurrentContainer(subActionItems.get(i).view);
            }
            detachOverlayContainer();
        }
        // do not forget to specify that the menu is now closed.
        open = false;

        if(stateChangeListener != null) {
            stateChangeListener.onMenuClosed(this);
        }
    }

    /**
     * Toggles the menu
     * @param animated if true, the open/close action is executed by the current {@link MenuAnimationHandler}
     */
    private void toggle(boolean animated) {
        if(open) {
            close(animated);
        }
        else {
            open(animated);
        }
    }

    /**
     * @return whether the menu is open or not
     */
    @SuppressWarnings("WeakerAccess")
    public boolean isOpen() {
        return open;
    }

    /**
     * @return whether the menu is a system overlay or not
     */
    boolean isSystemOverlay() {
        return systemOverlay;
    }

    FrameLayout getOverlayContainer() {
        return overlayContainer;
    }

    /**
     * Gets the coordinates of the main action view
     * This method should only be called after the main layout of the Activity is drawn,
     * such as when a user clicks the action button.
     * @return a Point containing x and y coordinates of the top left corner of action view
     */
    private Point getActionViewCoordinates() {
        int[] coordinates = new int[2];
        // This method returns a x and y values that can be larger than the dimensions of the device screen.
        mainActionView.getLocationOnScreen(coordinates);

        // So, we need to deduce the offsets.
        if(systemOverlay) {
            coordinates[1] -= getStatusBarHeight();
        }
        else {
            if (getActivityContentView() != null) {
                Rect activityFrame = new Rect();
                getActivityContentView().getWindowVisibleDisplayFrame(activityFrame);
                coordinates[0] -= (getScreenSize().x - getActivityContentView().getMeasuredWidth());
                coordinates[1] -= (activityFrame.height() + activityFrame.top - getActivityContentView().getMeasuredHeight());
            }
        }
        return new Point(coordinates[0], coordinates[1]);
    }

    /**
     * Returns the center point of the main action view
     * @return the action view center point
     */
    Point getActionViewCenter() {
        Point point = getActionViewCoordinates();
        point.x += mainActionView.getMeasuredWidth() / 2;
        point.y += mainActionView.getMeasuredHeight() / 2;
        return point;
    }

    /**
     * Calculates the desired positions of all items.
     * @return getActionViewCenter()
     */
    private Point calculateItemPositions() {
        // Create an arc that starts from startAngle and ends at endAngle
        // in an area that is as large as 4*radius^2
        final Point center = getActionViewCenter();
        RectF area = new RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius);

        Path orbit = new Path();
        orbit.addArc(area, startAngle, endAngle - startAngle);

        PathMeasure measure = new PathMeasure(orbit, false);

        // Prevent overlapping when it is a full circle
        int divisor;
        if(Math.abs(endAngle - startAngle) >= 360 || subActionItems.size() <= 1) {
            divisor = subActionItems.size();
        }
        else {
            divisor = subActionItems.size() -1;
        }

        // Measure this path, in order to find points that have the same distance between each other
        for(int i=0; i<subActionItems.size(); i++) {
            float[] coordinates = new float[] {0f, 0f};
            measure.getPosTan((i) * measure.getLength() / divisor, coordinates, null);
            // get the x and y values of these points and set them to each of sub action items.
            subActionItems.get(i).x = (int) coordinates[0] - subActionItems.get(i).width / 2;
            subActionItems.get(i).y = (int) coordinates[1] - subActionItems.get(i).height / 2;
        }
        return center;
    }

    /**
     * @return a reference to the sub action items list
     */
    List<Item> getSubActionItems() {
        return subActionItems;
    }

    /**
     * Finds and returns the main content view from the Activity context.
     * @return the main content view
     */
    private View getActivityContentView() {
        try {
            if (getActivity() != null && getActivity().getWindow() != null) {
                return getActivity().getWindow().getDecorView().findViewById(android.R.id.content);
            }
        }
        catch(ClassCastException e) {
            throw new ClassCastException("Please provide an Activity context for this FloatingActionMenu.");
        }

        return null;
    }

    private Activity getActivity() {
        Context context = mainActionView.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity)context;
            }
            context = ((ContextWrapper)context).getBaseContext();
        }
        return null;
    }

    /**
     * Intended to use for systemOverlay mode.
     * @return the WindowManager for the current context.
     */
    private WindowManager getWindowManager() {
        return (WindowManager) mainActionView.getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    private void addViewToCurrentContainer(View view, ViewGroup.LayoutParams layoutParams) {
        if(systemOverlay) {
            overlayContainer.addView(view, layoutParams);
        }
        else {
            try {
                if (getActivityContentView() != null)
                if(layoutParams != null) {
                    FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) layoutParams;
                    ((ViewGroup) getActivityContentView()).addView(view, lp);
                }
                else {
                    ((ViewGroup) getActivityContentView()).addView(view);
                }
            }
            catch(ClassCastException e) {
                throw new ClassCastException("layoutParams must be an instance of " +
                        "FrameLayout.LayoutParams.");
            }
        }
    }

    private void attachOverlayContainer() {
        try {
            WindowManager.LayoutParams overlayParams = calculateOverlayContainerParams();

            overlayContainer.setLayoutParams(overlayParams);
            if(overlayContainer.getParent() == null) {
                getWindowManager().addView(overlayContainer, overlayParams);
            }
            getWindowManager().updateViewLayout(mainActionView, mainActionView.getLayoutParams());
        }
        catch(SecurityException e) {
            throw new SecurityException("Your application must have SYSTEM_ALERT_WINDOW " +
                    "permission to create a system window.");
        }
    }

    private WindowManager.LayoutParams calculateOverlayContainerParams() {
        // calculate the minimum viable size of overlayContainer
        WindowManager.LayoutParams overlayParams = getDefaultSystemWindowParams();
        int left = 9999, right = 0, top = 9999, bottom = 0;
        for(int i=0; i < subActionItems.size(); i++) {
            int lm = subActionItems.get(i).x;
            int tm = subActionItems.get(i).y;

            if(lm < left) {
                left = lm;
            }
            if(tm < top) {
                top = tm;
            }
            if(lm + subActionItems.get(i).width > right) {
                right = lm + subActionItems.get(i).width;
            }
            if(tm + subActionItems.get(i).height > bottom) {
                bottom = tm + subActionItems.get(i).height;
            }
        }
        overlayParams.width = right - left;
        overlayParams.height = bottom - top;
        overlayParams.x = left;
        overlayParams.y = top;
        overlayParams.gravity = Gravity.TOP | Gravity.START;
        return overlayParams;
    }

    void detachOverlayContainer() {
        if (overlayContainer != null) {
            getWindowManager().removeView(overlayContainer);
        }
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = mainActionView.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mainActionView.getContext().getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void addViewToCurrentContainer(View view) {
        addViewToCurrentContainer(view, null);
    }

    void removeViewFromCurrentContainer(View view) {
        if(systemOverlay) {
            overlayContainer.removeView(view);
        }
        else {
            if (getActivityContentView() != null) {
                ((ViewGroup) getActivityContentView()).removeView(view);
            }
        }
    }

    /**
     * Retrieves the screen size from the Activity context
     * @return the screen size as a Point object
     */
    private Point getScreenSize() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        return size;
    }

    public void setStateChangeListener(MenuStateChangeListener listener) {
        this.stateChangeListener = listener;
    }

    /**
     * A simple click listener used by the main action view
     */
    private class ActionViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            toggle(animated);
        }
    }

    /**
     * This runnable calculates sizes of Item views that are added to the menu.
     */
    private class ItemViewQueueListener implements Runnable {

        private static final int MAX_TRIES = 10;
        private Item item;
        private int tries;

        ItemViewQueueListener(Item item) {
            this.item = item;
            this.tries = 0;
        }

        @Override
        public void run() {
            // Wait until the the view can be measured but do not push too hard.
            if(item.view.getMeasuredWidth() == 0 && tries < MAX_TRIES) {
                item.view.post(this);
                return;
            }
            // Measure the size of the item view
            item.width = item.view.getMeasuredWidth();
            item.height = item.view.getMeasuredHeight();

            // Revert everything back to normal
            item.view.setAlpha(item.alpha);
            // Remove the item view from view hierarchy
            removeViewFromCurrentContainer(item.view);
        }
    }

    /**
     * A simple structure to put a view and its x, y, width and height values together
     */
    static class Item {
        int x;
        int y;
        public int width;
        public int height;

        public float alpha;

        public View view;

        Item(View view, int width, int height) {
            this.view = view;
            this.width = width;
            this.height = height;
            alpha = view.getAlpha();
            x = 0;
            y = 0;
        }
    }

    /**
     * A listener to listen open/closed state changes of the Menu
     */
    @SuppressWarnings("UnusedParameters")
    public interface MenuStateChangeListener {
        void onMenuOpened(FloatingActionMenu menu);
        void onMenuClosed(FloatingActionMenu menu);
    }

    /**
     * A builder for {@link FloatingActionMenu} in conventional Java Builder format
     */
    public static class Builder {

        private int startAngle;
        private int endAngle;
        private int radius;
        private View actionView;
        private List<Item> subActionItems;
        private MenuAnimationHandler animationHandler;
        private boolean animated;
        private boolean systemOverlay;

        public Builder(Context context) {
            subActionItems = new ArrayList<>();
            // Default settings
            radius = context.getResources().getDimensionPixelSize(com.oguzdev.circularfloatingactionmenu.library.R.dimen.action_menu_radius);
            startAngle = 180;
            endAngle = 270;
            animationHandler = new DefaultAnimationHandler();
            animated = true;
            this.systemOverlay = false;
        }

        public Builder setStartAngle(int startAngle) {
            this.startAngle = startAngle;
            return this;
        }

        public Builder setEndAngle(int endAngle) {
            this.endAngle = endAngle;
            return this;
        }

        public Builder setRadius(int radius) {
            this.radius = radius;
            return this;
        }

        public Builder addSubActionView(View subActionView, int width, int height) {
            subActionItems.add(new Item(subActionView, width, height));
            return this;
        }

        /**
         * Attaches the whole menu around a main action view, usually a button.
         * All the calculations are made according to this action view.
         * @return the builder object itself
         */
        public Builder attachTo(View actionView) {
            this.actionView = actionView;
            return this;
        }

        public FloatingActionMenu build() {
            return new FloatingActionMenu(actionView,
                                          startAngle,
                                          endAngle,
                                          radius,
                                          subActionItems,
                                          animationHandler,
                                          animated,
                                          null,
                                          systemOverlay);
        }
    }

    private static WindowManager.LayoutParams getDefaultSystemWindowParams() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.format = PixelFormat.RGBA_8888;
        params.gravity = Gravity.TOP | Gravity.START;
        return params;
    }

}