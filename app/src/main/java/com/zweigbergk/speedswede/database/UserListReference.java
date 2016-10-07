package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.ProductBuilder;

public enum UserListReference {
    INSTANCE;

    static UserListReference getInstance() {
        return INSTANCE;
    }


    public ProductBuilder<Boolean> ifContains(User user) {
        return DbUserHandler.getInstance().userExists(user);
    }
    public ProductBuilder<Boolean> ifContains(String userId) {
        return DbUserHandler.getInstance().userExists(userId);
    }

    public void push(User user) {
        DbUserHandler.getInstance().pushUser(user);
    }

    public ProductBuilder<User> pull(String userId) {
        return DbUserHandler.getInstance().getUser(userId);
    }
}
