package com.zweigbergk.speedswede.util;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;

public class TestFactory {

    public final static String USER_1_NAME = "Sir";
    public final static String USER_2_NAME = "Lord";

    public final static String USER_1_ID = "uid_user1";
    public final static String USER_2_ID = "uid_user2";

    public static User mockUser(String name, String uid) {
        return new UserProfile(name, uid);
    }
}
