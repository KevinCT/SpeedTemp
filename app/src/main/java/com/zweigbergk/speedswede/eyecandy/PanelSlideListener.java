package com.zweigbergk.speedswede.eyecandy;

import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

public class PanelSlideListener implements SlidingUpPanelLayout.PanelSlideListener {

    List<Client<Float>> slideListeners = new ArrayList<>();
    List<Client<SlidingUpPanelLayout.PanelState>> stateChangedListeners = new ArrayList<>();

    public PanelSlideListener onPanelSlide(Client<Float> client) {
        slideListeners.add(client);

        return this;
    }

    public PanelSlideListener onPanelStateChanged(Client<SlidingUpPanelLayout.PanelState> client) {
        stateChangedListeners.add(client);

        return this;
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        slideListeners.foreach(client -> client.supply(slideOffset));
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
        stateChangedListeners.foreach(client -> client.supply(newState));
    }
}
