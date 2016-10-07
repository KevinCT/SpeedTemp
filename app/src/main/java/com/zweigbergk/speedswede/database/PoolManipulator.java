package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;

public enum PoolManipulator {
    INSTANCE;

    static PoolManipulator getInstance() {
        return INSTANCE;
    }


    public ProductBuilder<Boolean> ifContains(User user) {
        return DbUserHandler.getInstance().isInUserPool(user);
    }

    public void push(User user) {
        DbUserHandler.getInstance().addUserToPool(user);
    }

    public void remove(User user) {
        removeUser(user);
    }

    public void removeUser(User user) {
        DbUserHandler.getInstance().removeUserFromPool(user);
    }

    public void bind(Client<DataChange<User>> client) {
        DbUserHandler.getInstance().getPoolListener().bind(client);
    }

    public void unbind(Client<DataChange<User>> client) {
        DbUserHandler.getInstance().getPoolListener().unbind(client);
    }
}
