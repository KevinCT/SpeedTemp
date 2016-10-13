package com.zweigbergk.speedswede.database;


import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.core.MatchSkill;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.async.GoodStatement;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.async.Promise;

import java.util.Arrays;
import java.util.List;

public class UserReference {
    private static DbChatHandler INSTANCE;

    private static final String TAG = UserReference.class.getSimpleName().toUpperCase();

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

    public Promise<User> pull() {
        return DbUserHandler.getInstance().getUser(mUser.getUid());
    }

    public void setName(String name) {
        ifStillValid().then(() ->
                DbUserHandler.getInstance().setUserAttribute(mUser, UserAttribute.NAME, name));
    }

    private void setNotifications(boolean value) {
        ifStillValid().then(() ->
                DbUserHandler.getInstance().setUserAttribute(mUser, UserAttribute.NOTIFICATIONS, value));
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
            case OWN_SKILL:
                setUsage(value);
                break;
        }
    }

    public void setOwnSkill(MatchSkill skill) {
        ifStillValid().then(() -> {
            DbUserHandler.getInstance().setUserSkill(mUser, skill);
        });
    }

    private void setLanguage(String language) {
        ifStillValid().then(() -> {
            List<String> languages = Arrays.asList(Constants.LANGUAGES);
            String newLanguage = languages.contains(language) ? language : Constants.ENGLISH;
            DbUserHandler.getInstance().setUserAttribute(mUser, UserAttribute.LANGUAGE, newLanguage);
        });
    }

    private void setUsage(String usage) {
        ifStillValid().then(() -> {
            List<String> languages = Arrays.asList(Constants.USAGE);
            String newLanguage = languages.contains(usage) ? usage : Constants.ENGLISH;
            DbUserHandler.getInstance().setUserAttribute(mUser, UserAttribute.LANGUAGE, newLanguage);
        });
    }

    public Promise<Banner> getBanner() {
        return DbUserHandler.getInstance().getBans(mUser.getUid());
    }

    public GoodStatement hasBlocked(User user) {
        return DbUserHandler.getInstance().hasBlockedUser(mUser, user);
    }

    public void block(User user) {
        DbUserHandler.getInstance().blockUser(mUser, user);
    }

    public void liftBlock(String strangerUid) {
        DbUserHandler.getInstance().liftBlock(strangerUid);
    }

    public void setId(String id) {
        ifStillValid().then(() ->
                DbUserHandler.getInstance().setUserAttribute(mUser, UserAttribute.ID, id));
    }

    public void bind(Client<DataChange<User>> client) {
        DbUserHandler.getInstance().getUserListener().addClient(mUser, client);
    }

    public void unbind(Client<DataChange<User>> client) {
        DbUserHandler.getInstance().getUserListener().removeClient(mUser, client);
    }

    private GoodStatement ifStillValid() {
        return DbUserHandler.getInstance().exists(mUser);
    }


}
