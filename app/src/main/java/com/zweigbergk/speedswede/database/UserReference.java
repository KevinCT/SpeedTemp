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
        NAME(Constants.DISPLAY_NAME), ID(Constants.USER_ID),
        NOTIFICATIONS(Constants.makePath(Constants.PREFERENCES, Constants.NOTIFICATIONS)),
        LANGUAGE(Constants.makePath(Constants.PREFERENCES, Constants.LANGUAGE)),
        SKILL_CATEGORY(Constants.makePath(Constants.PREFERENCES, Constants.SKILL_CATEGORY)),
        FIRST_LOGIN(Constants.FIRST_LOGIN),
        UNDEFINED(Constants.UNDEFINED);

        private String path;

        UserAttribute(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
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
        attempt(() ->
                userHandler.setUserAttribute(mUser, UserAttribute.NAME, name));
    }

    public void setPreference(User.Preference preference, String value) {
        attempt(() -> userHandler.setUserAttribute(mUser, fromPreference(preference), value));
    }

    public void setPreference(User.Preference preference, boolean value) {
        attempt(() -> userHandler.setUserAttribute(mUser, fromPreference(preference), value));
    }

    public void setSkillCategory(SkillCategory skill) {
        setPreference(User.Preference.SKILL_CATEGORY, skill.toString());
    }

    private UserAttribute fromPreference(User.Preference preference) {
        switch (preference) {
            case LANGUAGE:
                return UserAttribute.LANGUAGE;
            case NOTIFICATIONS:
                return UserAttribute.NOTIFICATIONS;
            case SKILL_CATEGORY:
                return UserAttribute.SKILL_CATEGORY;
            default:
                return UserAttribute.UNDEFINED;
        }
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
