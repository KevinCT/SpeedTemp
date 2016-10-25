package com.zweigbergk.speedswede.database.eventListener;

import android.util.Log;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.util.collection.CollectionExtension;
import com.zweigbergk.speedswede.util.collection.HashSetExtension;
import com.zweigbergk.speedswede.util.collection.SetExtension;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

import java.util.Locale;

public abstract class FirebaseDataListener<T> {
    private static final String TAG = FirebaseDataListener.class.getSimpleName().toUpperCase(Locale.ENGLISH);


    private SetExtension<Client<DataChange<T>>> mClients;

    FirebaseDataListener(CollectionExtension<Client<DataChange<T>>> clients) {
        mClients = new HashSetExtension<>(clients);
    }

    public void bind(Client<DataChange<T>> client) {
        mClients.add(client);
    }

    public void unbind(Client<DataChange<T>> client) {
        mClients.remove(client);
    }

    void notifyClients(DatabaseEvent event, T item) {
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

    void notifyAdded(T item) {
        notifyClients(DatabaseEvent.ADDED, item);
    }

    void notifyRemoved(T item) {
        notifyClients(DatabaseEvent.REMOVED, item);
    }

    void notifyChanged(T item) {
        notifyClients(DatabaseEvent.CHANGED, item);
    }
}
