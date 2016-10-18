package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.util.collection.Collection;
import com.zweigbergk.speedswede.util.collection.Collections;
import com.zweigbergk.speedswede.util.collection.HashSet;
import com.zweigbergk.speedswede.util.collection.Set;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

public abstract class FirebaseDataListener<T> {
    private static final String TAG = FirebaseDataListener.class.getSimpleName().toUpperCase();


    private Set<Client<DataChange<T>>> mClients;

    FirebaseDataListener() {
        this(Collections.emptySet());
    }

    FirebaseDataListener(Collection<Client<DataChange<T>>> clients) {
        mClients = new HashSet<>(clients);
    }

    public void bind(Client<DataChange<T>> client) {
        mClients.add(client);
    }

    public void unbind(Client<DataChange<T>> client) {
        mClients.remove(client);
    }

    public void notifyClients(DatabaseEvent event, T item) {
        DataChange<T> dataChange;
        switch(event) {
            case ADDED:
                dataChange = DataChange.added(item);
                break;
            case REMOVED:
                dataChange = DataChange.removed(item);
                break;
            case CHANGED:
                dataChange = DataChange.modified(item);
                break;
            case INTERRUPTED: default:
                dataChange = DataChange.cancelled(item);
                break;
        }

        Log.d(TAG, "mClients.size(): " + mClients.size());
        mClients.foreach(client -> client.supply(dataChange));
    }

    public int size() {
        return mClients.size();
    }

    void notifyAdded(T item) {
        notifyClients(DatabaseEvent.ADDED, item);
    }

    void notifyRemoved(T item) {
        notifyClients(DatabaseEvent.REMOVED, item);
    }

    void notifyChanged(T item) {
        notifyClients(DatabaseEvent.CHANGED, item);
    }

    void notifyInterrupted() {
        notifyClients(DatabaseEvent.INTERRUPTED, null);
    }

    public void call(Client<DataChange<T>> client) {
        mClients.add(client);
    }
}
