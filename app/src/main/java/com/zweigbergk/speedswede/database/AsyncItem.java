package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.util.Client;

public class AsyncItem<T> {

    private T item;
    private Client<T> client;

    public void setClient(Client<T> client) {
        if (item != null) {
            client.supply(item);
        } else {
            this.client = client;
        }
    }

    public void set(T item) {
        if (client != null) {
            client.supply(item);
        } else {
            this.item = item;
        }
    }
}
