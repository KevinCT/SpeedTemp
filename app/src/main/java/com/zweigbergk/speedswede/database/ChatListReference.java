package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.Chat;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.Statement;

enum ChatListReference {
    INSTANCE;

    static ChatListReference getInstance() {
        return INSTANCE;
    }

    public Statement not(Statement statement) {
        return statement.invert();
    }

    public void push(User user) {
        DbUserHandler.getInstance().pushUser(user);
    }

    public ProductBuilder<User> pull(String userId) {
        return DbUserHandler.getInstance().getUser(userId);
    }
}
