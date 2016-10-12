package com.zweigbergk.speedswede.util;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.zweigbergk.speedswede.util.methodwrapper.StateRequirement;
import com.zweigbergk.speedswede.util.Promise.ItemMap;

/** Holds an unfinished object. Only releases it once it is completed,
 * i.e. all requirements are met. */
public class PromiseState {

    public static final String TAG = PromiseState.class.getSimpleName().toUpperCase();

    private ItemMap mItems;

    private Set<PromiseNeed> mLocks;

    private Set<PromiseNeed> mOpenedLocks;

    private Map<PromiseNeed, StateRequirement> stateRequirements;

    public PromiseState() {
        mItems = new ItemMap();

        mLocks = new HashSet<>();
        mOpenedLocks = new HashSet<>();

        stateRequirements = new HashMap<>();
    }

    public void requireState(PromiseNeed key, StateRequirement requirement) {
        stateRequirements.put(key, requirement);
        Log.d(TAG, "Adding state requirement");
    }

    private void updateLock(PromiseNeed key) {
        Object object = mItems.get(key);
        if (hasStateRequirement(key)) {
            Log.d(TAG, "Check if lock should open");
            if (stateRequirements.get(key).isFulfilled(object)) {
                Log.d(TAG, "Lock is open!");
                mOpenedLocks.add(key);
            }
        } else {
            mOpenedLocks.add(key);
        }
    }

    public void updateState() {
        Lists.forEach(mLocks, this::updateLock);
    }

    void put(PromiseNeed lock, Object product) {
        mItems.put(lock, product);
        updateLock(lock);
    }

    private boolean hasStateRequirement(PromiseNeed key) {
        return stateRequirements.get(key) != null;
    }

    void addLock(PromiseNeed lock) {
        mLocks.add(lock);
    }

    boolean isOpened() {
        return allLocksOpened();
    }

    private boolean allLocksOpened() {
        return mLocks.equals(mOpenedLocks);
    }

    ItemMap getItems() {
        return isOpened() ? mItems : null;
    }
}
