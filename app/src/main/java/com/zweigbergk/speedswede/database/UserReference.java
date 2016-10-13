package com.zweigbergk.speedswede.database;


import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.activity.Language;
import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.core.SkillCategory;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.methodwrapper.Client;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

public class UserReference {
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


    private final DbUserHandler userHandler = DbUserHandler.getInstance();
    private final User mUser;

    private UserReference(User user) {
        mUser = user;
    }

    static UserReference create(User user) {
        return new UserReference(user);
    }

    public void push() {
        userHandler.pushUser(mUser);
    }

    public Promise<User> pull() {
        return userHandler.pullUser(mUser.getUid());
    }

    public void setName(String name) {
        ifStillValid().then(() ->
                userHandler.setUserAttribute(mUser, UserAttribute.NAME, name));
    }

    private void setNotifications(boolean value) {
        ifStillValid().then(() ->
                userHandler.setUserAttribute(mUser, UserAttribute.NOTIFICATIONS, value));
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

    public void setPreference(User.Preference preference, String value) {
        if (!preference.accepts(value)) {
            throw new RuntimeException(String.format(
                    "Preference [ %s ] can not be set to a string value.", preference));
        }

        switch (preference) {
            case LANGUAGE:
                setLanguage(Language.fromString(value));
                break;
            case SKILL_CATEGORY:
                setSkillCategory(SkillCategory.fromString(value));
        }
    }

    public void setSkillCategory(SkillCategory skill) {
        attempt(() -> userHandler.setUserSkill(mUser, skill));
    }

    private void setLanguage(Language language) {
        attempt(() -> userHandler.setUserAttribute(mUser, UserAttribute.LANGUAGE, language.getLanguageCode()));
    }

    public Promise<Banner> bannerPromised() {
        return userHandler.getBans(mUser.getUid());
    }

    public Statement hasBlocked(User user) {
        return userHandler.hasBlockedUser(mUser, user);
    }

    public void block(User user) {
        userHandler.blockUser(mUser, user);
    }

    public void liftBlock(String strangerUid) {
        userHandler.liftBlock(strangerUid);
    }

    public void setId(String id) {
        attempt(() -> userHandler.setUserAttribute(mUser, UserAttribute.ID, id));
    }

    public void bind(Client<DataChange<User>> client) {
        userHandler.getUserListener().addClient(mUser, client);
    }

    public void unbind(Client<DataChange<User>> client) {
        userHandler.getUserListener().removeClient(mUser, client);
    }

    private void attempt(Executable executable) {
        ifStillValid().onTrue(executable);
    }

    private Statement ifStillValid() {
        return userHandler.exists(mUser);
    }


}
