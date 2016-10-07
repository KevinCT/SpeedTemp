package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;

public enum PoolManipulator {
    INSTANCE;

    static PoolManipulator getInstance() {
        return INSTANCE;
    }


    public UserCheck contains(User user) {
        return PoolUserCheck.ifExists(user);
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
}
