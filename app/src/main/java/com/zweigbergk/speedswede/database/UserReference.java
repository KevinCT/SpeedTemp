package com.zweigbergk.speedswede.database;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.SkillCategory;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.util.PreferenceWrapper;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.methodwrapper.Executable;

import static com.zweigbergk.speedswede.util.PreferenceWrapper.StringWrapper;
import static com.zweigbergk.speedswede.util.PreferenceWrapper.BooleanWrapper;

public class UserReference {

    public enum UserAttribute {
        NAME(Constants.DISPLAY_NAME), ID(Constants.USER_ID),
        NOTIFICATIONS(Constants.makePath(Constants.PREFERENCES, Constants.NOTIFICATIONS)),
        LANGUAGE(Constants.makePath(Constants.PREFERENCES, Constants.LANGUAGE)),
        SKILL_CATEGORY(Constants.makePath(Constants.PREFERENCES, Constants.SKILL_CATEGORY)),
        FIRST_LOGIN(Constants.FIRST_LOGIN),
        TIME_IN_QUEUE(Constants.TIME_IN_QUEUE),
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


    public Promise<User> pull() {
        return userHandler.pullUser(mUser.getUid());
    }

    public void setPreference(User.Preference preference, String value) {
        updateActiveUserPreference(preference, new StringWrapper(value));
        attempt(() -> userHandler.setUserAttribute(mUser, fromPreference(preference), value));
    }

    private void updateActiveUserPreference(User.Preference preference, PreferenceWrapper wrapper) {
        if (mUser.equals(activeUser())) {
            activeUser().setPreference(preference, wrapper);
        }
    }

    void setPreference(User.Preference preference, PreferenceWrapper wrapper) {
        updateActiveUserPreference(preference, wrapper);
        attempt(() -> userHandler.setUserAttribute(mUser, fromPreference(preference), wrapper.getValue()));
    }

    public void setNotifications(boolean value) {
        updateActiveUserPreference(User.Preference.NOTIFICATIONS, new BooleanWrapper(value));
        attempt(() -> userHandler.setUserAttribute(
                mUser,
                fromPreference(User.Preference.NOTIFICATIONS),
                value));
    }

    public void setSkillCategory(SkillCategory skill) {
        setPreference(User.Preference.SKILL_CATEGORY, skill.toString());
    }

    public void setFirstLogin(boolean value) {
        activeUser().setFirstLogin(value);
        attempt(() -> userHandler.setUserAttribute(mUser, UserAttribute.FIRST_LOGIN, value));
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

    public void setUserAttribute(UserAttribute attribute, Object value) {
        attempt(() -> userHandler.setUserAttribute(mUser, attribute, value));
    }

    public Statement hasBlocked(User user) {
        return userHandler.hasBlockedUser(mUser, user);
    }

    public void block(User user) {
        userHandler.blockUser(mUser, user);
    }

    public void setId(String id) {
        attempt(() -> userHandler.setUserAttribute(mUser, UserAttribute.ID, id));
    }

    private User activeUser() {
        return DatabaseHandler.getActiveUser();
    }

    private void attempt(Executable executable) {
        ifStillValid().onTrue(executable);
    }

    private Statement ifStillValid() {
        return userHandler.exists(mUser);
    }


}
