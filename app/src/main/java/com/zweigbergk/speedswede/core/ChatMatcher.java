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
    public void pushUser(User user) {
        mUserPool.add(user);
    }

    /** Remove user from the matching process */
    public void removeUser(User user) {
        if(mUserPool.contains(user)) {
            mUserPool.remove(user);
        }
    }

    public boolean hasUserInPool(User user) {
        return mUserPool.contains(user);
    }

    public User getFirstInPool() {
        if(mUserPool.size() > 0) {
            return mUserPool.get(0);
        }
        return null;
    }

    public Chat match() {
        if(mUserPool.size() > 1) {
            // TODO: Change to a more sofisticated matching algorithm in future. Maybe match depending on personal best in benchpress?
            List<User> copiedList = new LinkedList<>();
            copiedList.add(mUserPool.get(0));
            copiedList.add(mUserPool.get(1));
            mUserPool.remove(0);
            mUserPool.remove(0);
            return new Chat(copiedList.get(0), copiedList.get(1));
        }
        return null;
    }

    public void clear() {
        mUserPool.clear();
    }
}
