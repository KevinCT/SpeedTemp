package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.async.GoodStatement;
import com.zweigbergk.speedswede.util.async.Promise;

public enum UserListReference {
    INSTANCE;

    static UserListReference getInstance() {
        return INSTANCE;
    }

    public GoodStatement contains(User user) {
        return DbUserHandler.getInstance().userExists(user);
    }

    public GoodStatement contains(String userId) {
        return DbUserHandler.getInstance().userExists(userId);
    }

    public GoodStatement not(GoodStatement statement) {
        return statement.invert();
    }

    public void push(User user) {
        DbUserHandler.getInstance().pushUser(user);
    }

    public Promise<User> pull(String userId) {
        return DbUserHandler.getInstance().getUser(userId);
    }
}
