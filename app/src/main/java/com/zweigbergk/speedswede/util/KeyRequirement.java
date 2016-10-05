package com.zweigbergk.speedswede.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KeyRequirement implements Requirement {

    private Map<BuilderKey, Object> mObjects;

    private Set<BuilderKey> mRequiredKeys;

    private Set<BuilderKey> mFinishedKeys;

    public KeyRequirement() {
        mObjects = new HashMap<>();

        mRequiredKeys = new HashSet<>();
        mFinishedKeys = new HashSet<>();
    }

    public void addKey(BuilderKey key) {
        mRequiredKeys.add(key);
    }

    @Override
    public boolean isFulfilled() {
        return false;
    }
}
