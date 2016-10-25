package com.zweigbergk.speedswede.eyecandy;

import android.view.View;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

public class PanelSlideListener implements SlidingUpPanelLayout.PanelSlideListener {

    ListExtension<Client<Float>> slideListeners = new ArrayListExtension<>();
    ListExtension<Client<SlidingUpPanelLayout.PanelState>> stateChangedListeners = new ArrayListExtension<>();

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
