package com.zweigbergk.speedswede.eyecandy;

import android.view.animation.Animation;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

public class AnimationListener implements Animation.AnimationListener {

    List<Executable> startExecutables = new ArrayList<>();
    List<Executable> endExecutables = new ArrayList<>();
    List<Executable> repeatExecutables = new ArrayList<>();


    public AnimationListener onStart(Executable executable) {
        startExecutables.add(executable);

        return this;
    }

    public AnimationListener onEnd(Executable executable) {
        endExecutables.add(executable);

        return this;
    }

    public AnimationListener onRepeat(Executable executable) {
        repeatExecutables.add(executable);

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
