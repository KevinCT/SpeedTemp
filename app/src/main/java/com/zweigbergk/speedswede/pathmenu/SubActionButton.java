/*
 *   Copyright 2014 Oguz Bilgener
 */
package com.zweigbergk.speedswede.pathmenu;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;

/** Taken from: https://github.com/oguzbilgener/CircularFloatingActionMenu/
 *  Date: 17/10/2016
 *  All credit to Oguz Bilgener
 * A simple button implementation with a similar look an feel to{@link FloatingActionButton}.
 */
public class SubActionButton extends FrameLayout {

    private static final int THEME_LIGHT = 0;
    private static final int THEME_DARK = 1;
    private static final int THEME_LIGHTER = 2;
    private static final int THEME_DARKER = 3;

    private SubActionButton(Context context, LayoutParams layoutParams, int theme, Drawable backgroundDrawable, View contentView, LayoutParams contentParams) {
        super(context);
        setLayoutParams(layoutParams);
        // If no custom backgroundDrawable is specified, use the background drawable of the theme.
        if(backgroundDrawable == null) {
            if(theme == THEME_LIGHT) {
                backgroundDrawable = ContextCompat.getDrawable(context, com.oguzdev.circularfloatingactionmenu.library.R.drawable.button_sub_action_selector);
            }
            else if(theme == THEME_DARK) {
                backgroundDrawable = ContextCompat.getDrawable(context, com.oguzdev.circularfloatingactionmenu.library.R.drawable.button_sub_action_dark_selector);
            }
            else if(theme == THEME_LIGHTER) {
                backgroundDrawable = ContextCompat.getDrawable(context, com.oguzdev.circularfloatingactionmenu.library.R.drawable.button_action_selector);
            }
            else if(theme == THEME_DARKER) {
                backgroundDrawable = ContextCompat.getDrawable(context, com.oguzdev.circularfloatingactionmenu.library.R.drawable.button_action_dark_selector);
            }
            else {
                throw new RuntimeException("Unknown SubActionButton theme: " + theme);
            }
        }
        else {
            //noinspection ConstantConditions
            backgroundDrawable = backgroundDrawable.mutate().getConstantState().newDrawable();
        }
        setBackgroundResource(backgroundDrawable);
        if(contentView != null) {
            setContentView(contentView, contentParams);
        }
        setClickable(true);
    }

    public SubActionButton(Context context) {
        super(context);
    }

    public SubActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SubActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets a content view with custom LayoutParams that will be displayed inside this SubActionButton.
     */
    private void setContentView(View contentView, LayoutParams params) {
        if(params == null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            final int margin = getResources().getDimensionPixelSize(com.oguzdev.circularfloatingactionmenu.library.R.dimen.sub_action_button_content_margin);
            params.setMargins(margin, margin, margin, margin);
        }

        contentView.setClickable(false);
        this.addView(contentView, params);
    }

    /**
     * Sets a content view with default LayoutParams
     */
    @SuppressWarnings("unused")
    public void setContentView(View contentView) {
        setContentView(contentView, null);
    }

    private void setBackgroundResource(Drawable drawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        }
        else {
            setBackground(drawable);
        }
    }

    /**
     * A builder for {@link SubActionButton} in conventional Java Builder format
     */
    public static class Builder {

        private Context context;
        private LayoutParams layoutParams;
        private int theme;
        private View contentView;
        private LayoutParams contentParams;

        public Builder(Context context) {
            this.context = context;

            // Default SubActionButton settings
            int size = context.getResources().getDimensionPixelSize(com.oguzdev.circularfloatingactionmenu.library.R.dimen.sub_action_button_size);
            LayoutParams params = new LayoutParams(size, size, Gravity.TOP | Gravity.START);
            setLayoutParams(params);
            setTheme(SubActionButton.THEME_LIGHT);
        }

        @SuppressWarnings("UnusedReturnValue")
        Builder setLayoutParams(LayoutParams params) {
            this.layoutParams = params;
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public Builder setTheme(int theme) {
            this.theme = theme;
            return this;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder setContentView(View contentView, LayoutParams contentParams) {
            this.contentView = contentView;
            this.contentParams = contentParams;
            return this;
        }

        public SubActionButton build() {
            return new SubActionButton(context,
                    layoutParams,
                    theme,
                    null,
                    contentView,
                    contentParams);
        }
    }
}
