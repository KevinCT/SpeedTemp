package com.zweigbergk.speedswede.eyecandy;

import android.view.animation.Animation;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

class AnimationListener implements Animation.AnimationListener {

    private List<Executable> startExecutables = new ArrayList<>();
    private List<Executable> endExecutables = new ArrayList<>();
    private List<Executable> repeatExecutables = new ArrayList<>();

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
