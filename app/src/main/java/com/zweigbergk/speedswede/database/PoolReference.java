package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.Statement;
import com.zweigbergk.speedswede.methodwrapper.Client;

public enum PoolReference {
    INSTANCE;

    static PoolReference getInstance() {
        return INSTANCE;
    }


    public Statement contains(User user) {
        return DbUserHandler.getInstance().isInUserPool(user);
    }

    public Statement not(Statement statement) {
        return statement.invert();
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
