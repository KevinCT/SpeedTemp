package com.zweigbergk.speedswede.database;


import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;
import com.zweigbergk.speedswede.util.Statement;

import java.util.Arrays;
import java.util.List;

public class UserReference {
    public static final String TAG = UserReference.class.getSimpleName().toUpperCase();

    enum UserAttribute {
        NAME, ID, NOTIFICATIONS, LANGUAGE, USAGE;

        public String getDbKey() {
            switch(this) {
                case NAME:
                    return Constants.DISPLAY_NAME;
                case ID:
                    return Constants.USER_ID;
                case NOTIFICATIONS:
                    return Constants.makePath(Constants.PREFERENCES, Constants.NOTIFICATIONS);
                case LANGUAGE:
                    return Constants.makePath(Constants.PREFERENCES, Constants.LANGUAGE);
                case USAGE:
                    return Constants.makePath(Constants.PREFERENCES, Constants.USAGE);
                default:
                    return Constants.UNDEFINED;
            }
        }
    }

    private final User mUser;

    private UserReference(User user) {
        mUser = user;
    }

    static UserReference create(User user) {
        return new UserReference(user);
    }

    public void push() {
        DbUserHandler.getInstance().pushUser(mUser);
    }

    public ProductBuilder<User> pull() {
        return DbUserHandler.getInstance().getUser(mUser.getUid());
    }

    public void setName(String name) {
        ifStillValid().then(() ->
                DbUserHandler.INSTANCE.setUserAttribute(mUser, UserAttribute.NAME, name));
    }

    private void setNotifications(boolean value) {
        ifStillValid().then(() ->
                DbUserHandler.INSTANCE.setUserAttribute(mUser, UserAttribute.NOTIFICATIONS, value));
    }

    public void setPreference(User.Preference preference, boolean value) {
        if (!preference.accepts(value)) {
            throw new RuntimeException(String.format(
                    "Preference [ %s ] can invert be set to a boolean value.", preference));
        }

        switch (preference) {
            case NOTIFICATIONS:
                setNotifications(value);
                break;
        }
    }

    public void setPreference(User.Preference preference, long value) {
//        if (!preference.accepts(value)) {
//            throw new RuntimeException(String.format(
//                    "Preference [ %s ] can invert be set to a long value.", preference));
//        }

        switch (preference) {
//            case SWEDISH_SKILL:
//                setSwedishSkill(value);
//                break;
        }
    }

    public void setPreference(User.Preference preference, String value) {
        if (!preference.accepts(value)) {
            throw new RuntimeException(String.format(
                    "Preference [ %s ] can invert be set to a string value.", preference));
        }

        switch (preference) {
            case LANGUAGE:
                setLanguage(value);
                break;
            case USAGE:
                setUsage(value);
                break;
        }
    }

    private void setLanguage(String language) {
        ifStillValid().then(() -> {
            List<String> languages = Arrays.asList(Constants.LANGUAGES);
            String newLanguage = languages.contains(language) ? language : Constants.ENGLISH;
            DbUserHandler.INSTANCE.setUserAttribute(mUser, UserAttribute.LANGUAGE, newLanguage);
        });
    }

    private void setUsage(String usage) {
        ifStillValid().then(() -> {
            List<String> languages = Arrays.asList(Constants.USAGE);
            String newLanguage = languages.contains(usage) ? usage : Constants.ENGLISH;
            DbUserHandler.INSTANCE.setUserAttribute(mUser, UserAttribute.LANGUAGE, newLanguage);
        });
    }

    public void setId(String id) {
        ifStillValid().then(() ->
                DbUserHandler.INSTANCE.setUserAttribute(mUser, UserAttribute.ID, id));
    }

    public void bind(Client<DataChange<User>> client) {
        DbUserHandler.getInstance().getUserListener().addClient(mUser, client);
    }

    public void unbind(Client<DataChange<User>> client) {
        DbUserHandler.INSTANCE.getUserListener().removeClient(mUser, client);
    }

    private Statement ifStillValid() {
        return DbUserHandler.getInstance().exists(mUser);
    }
}
