package com.zweigbergk.speedswede.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores data
 */
public class Backpack {

    public enum Type { STRING, INT, USER }
    private final Map<Type, Map<String, Object>> map = new HashMap<>();

    public Backpack() {
        for (Type type : Type.values())
            map.put(type, new HashMap<>());
    }

    public String getString(String key) {
        return (String) map.get(Type.STRING).get(key);
    }

    public void putString(String key, String value) {
        map.get(Type.STRING).put(key, value);
    }

    public int getInt(String key) {
        return (int) map.get(Type.INT).get(key);
    }

    public void putInt(String key, int value) {
        map.get(Type.INT).put(key, value);
    }

    public Integer getFirstInt() {
        for (Map.Entry entry : map.get(Type.INT).entrySet())
            return (int) entry.getValue();

        return null;
    }

    public Object getFirst(Type type) {
        for (Map.Entry entry : map.get(type).entrySet())
            return entry.getValue();

        return null;
    }

    public void putUser(String key, User user) {
        map.get(Type.USER).put(key, user);
    }

    public User getUser(String key) {
        return (User) map.get(Type.USER).get(key);
    }



    public Backpack withInt(String key, int value) {
        putInt(key, value);
        return this;
    }
}
