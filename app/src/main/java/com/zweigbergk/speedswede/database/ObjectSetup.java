package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.util.Client;

public class ObjectSetup<T> {

    AsyncItem<T> item;

    public ObjectSetup() {

    }

    public ObjectSetup first(AsyncItem item) {
        this.item = item;

        return this;
    }

    public void then(Client<T> client) {
        item.setClient(client);
    }
}
