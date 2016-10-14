package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.async.Promise;

public enum UserListReference {
    INSTANCE;

    static UserListReference getInstance() {
        return INSTANCE;
    }

    public Statement not(Statement statement) {
        return statement.invert();
    }

    public void push(User user) {
        DbUserHandler.getInstance().pushUser(user);
    }

    public Promise<User> pull(String userId) {
        return DbUserHandler.getInstance().pullUser(userId);
    }
}
