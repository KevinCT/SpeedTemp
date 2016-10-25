package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.methodwrapper.Client;

public enum PoolReference {
    INSTANCE;

    @SuppressWarnings("SameReturnValue")
    static PoolReference getInstance() {
        return INSTANCE;
    }

    public void push(User user) {
        DbUserHandler.getInstance().addUserToPool(user);
    }

    @SuppressWarnings("unused")
    public void remove(User user) {
        removeUser(user);
    }

    public void removeUser(User user) {
        DbUserHandler.getInstance().removeUserFromPool(user);
    }

    public void bind(Client<DataChange<User>> client) {
        DbUserHandler.getInstance().getPoolListener().bind(client);
    }
}
