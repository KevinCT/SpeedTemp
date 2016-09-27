package com.zweigbergk.speedswede.core;

import android.util.Log;

import com.zweigbergk.speedswede.service.DatabaseHandler;

import java.util.LinkedList;
import java.util.List;

public enum ChatMatcher {
    INSTANCE;

    private List<User> mUserPool;



    ChatMatcher() {

        mUserPool = new LinkedList<>();

        DatabaseHandler.INSTANCE.getMatchingPool(this::handleUser);

    }


    private void handleUser(User user) {
        Log.d("User: ", user.getUid());
    }

    /** Include user in the matching process */
    public void pushUser(User user) {
        mUserPool.add(user);
//        DatabaseHandler.INSTANCE.setMatchingPool();
    }

    /** Remove user from the matching process */
    public void removeUser(User user) {
        pullPool();
        if(mUserPool.contains(user)) {
            mUserPool.remove(user);
        }
        DatabaseHandler.INSTANCE.setMatchingPool();
    }

    public boolean hasUserInPool(User user) {
        pullPool();
        return mUserPool.contains(user);
    }

    public User getFirstInPool() {
        pullPool();
        if(mUserPool.size() > 0) {
            return mUserPool.get(0);
        }
        return null;
    }

    public Chat match() {
        pullPool();
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
        pullPool();
        mUserPool.clear();
        DatabaseHandler.INSTANCE.setMatchingPool();
    }

    public List<User> getPool() {
        return mUserPool;
    }

    private void pullPool() {
//        mUserPool = DatabaseHandler.INSTANCE.getMatchingPool();
    }
}
