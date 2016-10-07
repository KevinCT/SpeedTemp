package com.zweigbergk.speedswede.database;


import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.ProductBuilder;

import java.util.Arrays;
import java.util.List;

public class UserReference {
    public static final String TAG = UserReference.class.getSimpleName().toUpperCase();

    enum UserAttribute {
        NAME, ID, NOTIFICATIONS, LANGUAGE, SWEDISH_SKILL, STRANGER_SWEDISH_SKILL;

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
                case SWEDISH_SKILL:
                    return Constants.makePath(Constants.PREFERENCES, Constants.SWEDISH_SKILL);
                case STRANGER_SWEDISH_SKILL:
                    Constants.makePath(Constants.PREFERENCES, Constants.STRANGER_SWEDISH_SKILL);
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
                    "Preference [ %s ] can not be set to a boolean value.", preference));
        }

        switch (preference) {
            case NOTIFICATIONS:
                setNotifications(value);
                break;
        }
    }

    public void setPreference(User.Preference preference, long value) {
        if (!preference.accepts(value)) {
            throw new RuntimeException(String.format(
                    "Preference [ %s ] can not be set to a long value.", preference));
        }

        switch (preference) {
            case SWEDISH_SKILL:
                setSwedishSkill(value);
                break;
            case STRANGER_SWEDISH_SKILL:
                setStrangerSwedishSkill(value);
                break;
        }
    }

    public void setPreference(User.Preference preference, String value) {
        if (!preference.accepts(value)) {
            throw new RuntimeException(String.format(
                    "Preference [ %s ] can not be set to a string value.", preference));
        }

        switch (preference) {
            case LANGUAGE:
                setLanguage(value);
                break;
        }
    }

    private void setSwedishSkill(long value) {
        ifStillValid().then(() ->
                DbUserHandler.INSTANCE.setUserAttribute(mUser, UserAttribute.SWEDISH_SKILL, value));
    }

    private void setStrangerSwedishSkill(long value) {
        ifStillValid().then(() ->
                DbUserHandler.INSTANCE.setUserAttribute(mUser,
                        UserAttribute.STRANGER_SWEDISH_SKILL,
                        value));
    }

    private void setLanguage(String language) {
        ifStillValid().then(() -> {
            List<String> languages = Arrays.asList(Constants.LANGUAGES);
            String newLanguage = languages.contains(language) ? language : Constants.ENGLISH;
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

    /**
     * Checks if the user is still present in the database.
     * @return an ExistanceCheck object that can be used as:
     * <p><p><code>ifStillValid().then(() -> { run code here... })</code></p></p>
     * */
    private ExistanceCheck ifStillValid() {
        return ExistanceCheck.ifExists(mUser);
    }
}