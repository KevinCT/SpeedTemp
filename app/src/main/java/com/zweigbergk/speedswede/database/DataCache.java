package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum DataCache {
    INSTANCE;

    private List<User> users;

    DataCache() {
        users = new ArrayList<>();
    }

    public User getCachedUserById(String uid) {
        return query(users, user -> user.getUid().equals(uid));
    }

    /** Returns the first element of the collection matching the query.
     * If no match, returns null. */
    <E> E query(Collection<E> collection, Query<E> query) {
        for (E element : collection) {
            if (query.matches(element))
                return element;
        }

        return null;
    }

    public void cache(User user) {
        if (!users.contains(user))
            users.add(user);
    }

}
