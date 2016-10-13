package com.zweigbergk.speedswede.database.eventListener;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.Lists;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class FirebaseDataListener<T> {

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

        Lists.forEach(mClients, client -> client.supply(dataChange));
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
