package com.zweigbergk.speedswede.core;

import java.util.LinkedList;
import java.util.List;

public enum ChatMatcher {
    INSTANCE;

    private List<User> mUserPool;

    ChatMatcher() {
        mUserPool = new LinkedList<>();
    }


    /** Include user in the matching process */
    public void addUser(User user) {
        mUserPool.add(user);
    }

    /** Remove user from the matching process */
    public void removeUser(User user) {
        mUserPool.remove(user);
    }

    public boolean hasUserInPool(User user) {
        return mUserPool.contains(user);
    }

    /** Reset to initial state */
    public void reset() {
        mUserPool = new LinkedList<>();
    }

    public int getUserPoolSize() {
        return mUserPool.size();
    }
}
