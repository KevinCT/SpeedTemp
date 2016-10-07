package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.ProductBuilder;

public enum UsersManipulator {
    INSTANCE;

    static UsersManipulator getInstance() {
        return INSTANCE;
    }


    public UserCheck ifContains(User user) {
        return NormalUserCheck.ifExists(user);
    }
    public UserCheck ifContains(String userId) {
        return NormalUserCheck.ifExists(userId);
    }

    public void push(User user) {
        DbUserHandler.getInstance().pushUser(user);
    }

    public ProductBuilder<User> pull(String userId) {
        return DbUserHandler.getInstance().getUserById(userId);
    }
}
