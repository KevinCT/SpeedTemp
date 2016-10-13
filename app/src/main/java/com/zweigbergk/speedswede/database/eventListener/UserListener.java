package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.util.async.GoodStatement;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.Lists;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserListener implements ChildEventListener {
    public static final String TAG = UserListener.class.getSimpleName().toUpperCase();

    private static final String CLIENT_FOR_ALL_USERS = "key_to_listen_to_every_user";

    private Map<String, Set<Client<DataChange<User>>>> userClients;


    public UserListener() {
        super();

        userClients = new HashMap<>();
    }

    // NOTE: onChildAdded() runs once for every existing child at the time of attaching.
    // Thus there is no need for an initial SingleValueEventListener.
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        User user = convertToUser(dataSnapshot);
        notifyAdded(user);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        User user = convertToUser(dataSnapshot);
        notifyChanged(user);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        User user = convertToUser(dataSnapshot);
        notifyRemoved(user);
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        Log.d(Constants.ERROR, databaseError.getMessage());
    }


    private void notifyClients(DatabaseEvent event, User user) {
        String id = user.getUid();
        Set<Client<DataChange<User>>> chatClients =
                Lists.union(this.userClients.get(id), this.userClients.get(CLIENT_FOR_ALL_USERS));

        DataChange<User> dataChange;
        switch(event) {
            case ADDED:
                dataChange = DataChange.added(user);
                break;
            case REMOVED:
                dataChange = DataChange.removed(user);
                break;
            case CHANGED:
                dataChange = DataChange.modified(user);
                break;
            case INTERRUPTED: default:
                dataChange = DataChange.cancelled(user);
                break;
        }

        Lists.forEach(chatClients, client -> client.supply(dataChange));
    }

    private void notifyAdded(User user) {
        notifyClients(DatabaseEvent.ADDED, user);
    }

    private void notifyRemoved(User user) {
        notifyClients(DatabaseEvent.REMOVED, user);
    }

    private void notifyChanged(User user) {
        notifyClients(DatabaseEvent.CHANGED, user);
    }

    private void notifyInterrupted() {
        notifyClients(DatabaseEvent.INTERRUPTED, null);
    }


     /**
     * Adds a client that will receive updates whenever the user is added/removed/changed.
     * */
    public void addClient(String userId, Client<DataChange<User>> client) {
        if (!userClients.containsKey(userId)) {
            userClients.put(userId, new HashSet<>());
        }

        userClients.get(userId).add(client);
    }

    /**
     * Adds a client that will receive updates whenever the user is added/removed/changed.
     * */
    public void addClient(User user, Client<DataChange<User>> client) {
        addClient(user.getUid(), client);
    }

    /**
     * Adds a client that will receive updates whenever <u>any</u> user is added/removed/changed.
     * */
    public void addClient(Client<DataChange<User>> client) {
        addClient(CLIENT_FOR_ALL_USERS, client);
    }

    /**
     * Stops a client from receiving updates from the particular user.
     * */
    public void removeClient(String userId, Client<DataChange<User>> client) {
        if (!userClients.containsKey(userId)) {
            userClients.put(userId, new HashSet<>());
        }

        userClients.get(userId).remove(client);
    }

    /**
     * Stops a client from receiving updates from the particular user.
     * */
    public void removeClient(User user, Client<DataChange<User>> client) {
        removeClient(user.getUid(), client);
    }

    /**
     * Removes a client from the set of clients that will receive updates whenever
     * <u>any</u> user is added/removed/changed.
     * */
    public void removeClient(Client<DataChange<User>> client) {
        removeClient(CLIENT_FOR_ALL_USERS, client);
    }

    private User convertToUser(DataSnapshot snapshot) {
        Log.d(TAG, "convertToUser: snapshot: " + snapshot.toString());
        if (snapshot.child(Constants.DISPLAY_NAME).getValue() != null &&
                snapshot.child(Constants.USER_ID).getValue() != null) {
            return new UserProfile(snapshot.child(Constants.DISPLAY_NAME).getValue().toString(),
                    snapshot.child(Constants.USER_ID).getValue().toString());
        }
        return null;
    }

    @Override
    public boolean equals(Object other) {
        return other != null && this.getClass() == other.getClass() && hashCode() == other.hashCode();
    }
}
