package com.zweigbergk.speedswede.eyecandy;

import android.view.animation.Animation;

import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

class AnimationListener implements Animation.AnimationListener {

    private ListExtension<Executable> startExecutables = new ArrayListExtension<>();
    private ListExtension<Executable> endExecutables = new ArrayListExtension<>();
    private ListExtension<Executable> repeatExecutables = new ArrayListExtension<>();

    AnimationListener onEnd(Executable executable) {
        endExecutables.add(executable);

        return this;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        startExecutables.foreach(Executable::run);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        endExecutables.foreach(Executable::run);

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        repeatExecutables.foreach(Executable::run);
    }
}
