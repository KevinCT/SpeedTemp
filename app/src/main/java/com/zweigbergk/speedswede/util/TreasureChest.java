package com.zweigbergk.speedswede.util;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Holds an unfinished object. Only releases it once it is completed,
 * i.e. all requirements are met. */
public class TreasureChest {

    public static final String TAG = TreasureChest.class.getSimpleName().toUpperCase();

    private Map<ProductLock, Object> mItems;

    private Set<ProductLock> mLocks;

    private Set<ProductLock> mOpenedLocks;

    private Map<ProductLock, StateRequirement> stateRequirements;

    public TreasureChest() {
        mItems = new HashMap<>();

        mLocks = new HashSet<>();
        mOpenedLocks = new HashSet<>();

        stateRequirements = new HashMap<>();
    }

    public void requireState(ProductLock key, StateRequirement requirement) {
        stateRequirements.put(key, requirement);
        Log.d(TAG, "Adding state requirement");
    }

    private void updateLock(ProductLock key) {
        Object object = mItems.get(key);
        if (hasStateRequirement(key)) {
            Log.d(TAG, "Check if lock should open");
            if (stateRequirements.get(key).isFulfilled(object)) {
                Log.d(TAG, "Lock is open!");
                mOpenedLocks.add(key);
            }
        } else {
            Log.d(TAG, "There were no state reqs...");
            mOpenedLocks.add(key);
        }
    }

    public void updateState() {
        Lists.forEach(mLocks, this::updateLock);
    }

    void put(ProductLock lock, Object product) {
        mItems.put(lock, product);
        updateLock(lock);
    }

    private boolean hasStateRequirement(ProductLock key) {
        Log.d(TAG, "Requirement for key " + key.toString() + " is " + stateRequirements.get(key));
        return stateRequirements.get(key) != null;
    }

    void addLock(ProductLock lock) {
        mLocks.add(lock);
    }

    boolean isOpened() {
        return allLocksOpened();
    }

    private boolean allLocksOpened() {
        return mLocks.equals(mOpenedLocks);
    }

    Map<ProductLock, Object> getItems() {
        return isOpened() ? mItems : null;
    }
}
