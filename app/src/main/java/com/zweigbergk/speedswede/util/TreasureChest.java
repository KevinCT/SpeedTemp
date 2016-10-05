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
    }

    private void updateLock(ProductLock key) {
        Object object = mItems.get(key);
        if (hasStateRequirement(key)) {
            if (stateRequirements.get(key).isFulfilled(object)) {
                mOpenedLocks.add(key);
            }
        } else {
            mOpenedLocks.add(key);
        }
    }

    void put(ProductLock lock, Object product) {
        mItems.put(lock, product);
        updateLock(lock);
    }

    private boolean hasStateRequirement(ProductLock key) {
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
