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
    public void includeUser(User user) {
    }

    /** Remove user from the matching process */
    public void removeUser(User user) {
    }

    public boolean hasUserInPool(User user) {
        return false;
    }
}
