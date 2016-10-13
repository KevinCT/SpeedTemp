package com.zweigbergk.speedswede.util.async;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.methodwrapper.StateRequirement;
import com.zweigbergk.speedswede.util.async.Promise.ItemMap;

/** Holds an unfinished object. Only releases it once it is completed,
 * i.e. all requirements are met. */
class PromiseState {

    public static final String TAG = PromiseState.class.getSimpleName().toUpperCase();

    private ItemMap mItems;

    private Set<PromiseNeed> needs;

    private Set<PromiseNeed> fulfilledNeeds;

    private Map<PromiseNeed, StateRequirement> stateRequirements;

    PromiseState() {
        mItems = new ItemMap();

        needs = new HashSet<>();
        fulfilledNeeds = new HashSet<>();

        stateRequirements = new HashMap<>();
    }

    void requireState(PromiseNeed key, StateRequirement requirement) {
        stateRequirements.put(key, requirement);
        Log.d(TAG, "Adding state requirement");
    }

    private void updateState(PromiseNeed need) {
        Object object = mItems.get(need);
        if (hasStateRequirement(need)) {
            Log.d(TAG, "Check if need is fulfilled");
            StateRequirement req = stateRequirements.get(need);
            if (req.isFulfilled(object)) {
                Log.d(TAG, Stringify.curlyFormat("Need {need} is fulfilled!", need.name()));
                fulfilledNeeds.add(need);
            }
        } else {
            fulfilledNeeds.add(need);
        }
    }

    void updateState() {
        Lists.forEach(needs, this::updateState);
    }

    void put(PromiseNeed need, Object item) {
        mItems.put(need, item);
        updateState(need);
    }

    private boolean hasStateRequirement(PromiseNeed need) {
        return stateRequirements.get(need) != null;
    }

    void addNeed(PromiseNeed need) {
        needs.add(need);
    }

    boolean isFulfilled() {
        return needs.equals(fulfilledNeeds);
    }

    ItemMap getItems() {
        return isFulfilled() ? mItems : null;
    }
}
