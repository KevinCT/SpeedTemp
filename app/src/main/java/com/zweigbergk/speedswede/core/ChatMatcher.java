package com.zweigbergk.speedswede.core;

import android.provider.ContactsContract;
import android.util.Log;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public enum ChatMatcher {
    INSTANCE;

    private List<User> mUserPool;

    private Map<DatabaseEvent, List<Client<User>>> eventCallbacks;

    ChatMatcher() {
        mUserPool = new LinkedList<>();

        eventCallbacks = new HashMap<>();

        for(DatabaseEvent event : DatabaseEvent.values()) {
            eventCallbacks.put(event, new ArrayList<>());
        }
    }

    public void handleUser(DataChange<User> dataChange) {
        User user = dataChange.getItem();

        switch (dataChange.getEvent()) {
            case ADDED:
                mUserPool.add(user);
                executeCallbacks(DatabaseEvent.ADDED, user);
                break;
            case REMOVED:
                mUserPool.remove(user);
                executeCallbacks(DatabaseEvent.REMOVED, user);
                break;
            default:
                break;
        }
    }

    /** Include user in the matching process */
    public void pushUser(User user) {
        DatabaseHandler.INSTANCE.addUserToPool(user);
    }

    /** Remove user from the matching process */
    public void removeUser(User user) {
        DatabaseHandler.INSTANCE.removeUserFromPool(user);
    }

    public boolean hasUserInPool(User user) {
        return mUserPool.contains(user.getUid());
    }

    public User getFirstInPool() {
        if(mUserPool.size() > 0) {
            return mUserPool.get(0);
        }
        return null;
    }

    public void addEventCallback(DatabaseEvent event, Client<User> callback) {
        eventCallbacks.get(event).add(callback);
    }

    public void removeEventCallback(DatabaseEvent event, Client<User> callback) {
        eventCallbacks.get(event).remove(callback);
    }

    private void executeCallbacks(DatabaseEvent event, User user) {
        List<Client<User>> clients = eventCallbacks.get(event);
        for (Client<User> client : clients) {
            client.supply(user);
        }
    }

    public void match() {
        Log.d("Users in pool: ", ""+mUserPool.size());
        if(!containsBannedUser()) {
            if (mUserPool.size() > 1) {
                // TODO: Change to a more sofisticated matching algorithm in future. Maybe match depending on personal best in benchpress?
                List<User> copiedList = new LinkedList<>();
                copiedList.add(mUserPool.get(0));
                copiedList.add(mUserPool.get(1));

                DatabaseHandler.INSTANCE.removeUserFromPool(copiedList.get(0));
                DatabaseHandler.INSTANCE.removeUserFromPool(copiedList.get(1));

                DatabaseHandler.INSTANCE.pushChat(new Chat(copiedList.get(0), copiedList.get(1)));
            }
        }
        else {
            removeBannedUser();
            match();
        }

    }

    private void removeBannedUser(){
        List<User> unionList = mUserPool;
        unionList.retainAll(DatabaseHandler.INSTANCE.getBans(DatabaseHandler.INSTANCE.getActiveUserId()).getBanList());
        mUserPool.removeAll(unionList);
    }

    private boolean containsBannedUser(){
        return !Collections.disjoint(mUserPool,DatabaseHandler.INSTANCE.getBans(DatabaseHandler.INSTANCE.getActiveUserId()).getBanList());
    }

    public void clear() {
        mUserPool.clear();
    }

    public List<User> getPool() {
        return mUserPool;
    }
}
