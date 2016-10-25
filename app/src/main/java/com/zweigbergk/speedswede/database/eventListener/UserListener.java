package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import com.zweigbergk.speedswede.util.collection.MapExtension;
import com.zweigbergk.speedswede.util.factory.UserFactory;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.HashSet;
import java.util.Set;

public class UserListener implements ChildEventListener {
    private static final String CLIENT_FOR_ALL_USERS = "key_to_listen_to_every_user";

    private MapExtension<String, Set<Client<DataChange<User>>>> userClients;


    public UserListener() {
        super();

        userClients = new HashMapExtension<>();
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


     /**
     * Adds a client that will receive updates whenever the user is added/removed/changed.
     * */
    @SuppressWarnings("WeakerAccess")
    public void addClient(String userId, Client<DataChange<User>> client) {
        if (!userClients.containsKey(userId)) {
            userClients.put(userId, new HashSet<>());
        }

        userClients.get(userId).add(client);
    }

    /**
     * Adds a client that will receive updates whenever the user is added/removed/changed.
     * */
    @SuppressWarnings("unused")
    public void addClient(User user, Client<DataChange<User>> client) {
        addClient(user.getUid(), client);
    }

    /**
     * Adds a client that will receive updates whenever <u>any</u> user is added/removed/changed.
     * */
    @SuppressWarnings("unused")
    public void addClient(Client<DataChange<User>> client) {
        addClient(CLIENT_FOR_ALL_USERS, client);
    }

    /**
     * Stops a client from receiving updates from the particular user.
     * */
    @SuppressWarnings("WeakerAccess")
    public void removeClient(String userId, Client<DataChange<User>> client) {
        if (!userClients.containsKey(userId)) {
            userClients.put(userId, new HashSet<>());
        }

        userClients.get(userId).remove(client);
    }

    /**
     * Stops a client from receiving updates from the particular user.
     * */
    @SuppressWarnings("unused")
    public void removeClient(User user, Client<DataChange<User>> client) {
        removeClient(user.getUid(), client);
    }

    private User convertToUser(DataSnapshot snapshot) {
        return UserFactory.deserializeUser(snapshot);
    }

    @Override
    public boolean equals(Object other) {
        return other != null && this.getClass() == other.getClass() && hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        return this.userClients.hashCode();
    }
}
