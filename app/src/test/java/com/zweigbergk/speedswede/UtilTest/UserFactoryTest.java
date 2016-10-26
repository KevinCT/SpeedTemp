package com.zweigbergk.speedswede.UtilTest;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.mock.ISnapshot;
import com.zweigbergk.speedswede.mock.SnapshotMock;
import com.zweigbergk.speedswede.util.factory.UserFactory;

import org.junit.Before;
import org.junit.Test;

import static com.zweigbergk.speedswede.Constants.DISPLAY_NAME;
import static com.zweigbergk.speedswede.Constants.FIRST_LOGIN;
import static com.zweigbergk.speedswede.Constants.NOTIFICATIONS;
import static com.zweigbergk.speedswede.Constants.PREFERENCES;
import static com.zweigbergk.speedswede.Constants.USER_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserFactoryTest {

    private ISnapshot wellBehavedSnapshot, errorSnapshot;

    private static final String TEST_NAME = "Mr. Robot";
    private static final String TEST_UID = "uid_236631";
    private static final boolean TEST_NOTIFICATIONS = true;

    @Before
    public void reset() {
        wellBehavedSnapshot = new SnapshotMock();
        wellBehavedSnapshot.child(DISPLAY_NAME).setValue(TEST_NAME);
        wellBehavedSnapshot.child(USER_ID).setValue("uid_236631");
        wellBehavedSnapshot.child(FIRST_LOGIN).setValue(true);
        wellBehavedSnapshot.child(PREFERENCES).child(NOTIFICATIONS).setValue(TEST_NOTIFICATIONS);

        errorSnapshot = new SnapshotMock();
        errorSnapshot.child(DISPLAY_NAME).setValue(null);
        errorSnapshot.child(USER_ID).setValue(28);
        errorSnapshot.child(FIRST_LOGIN).setValue("oj");
        errorSnapshot.child(PREFERENCES).child(NOTIFICATIONS).setValue(51.7f);
    }

    @Test
    public void deserializeUser() {
        User correctUser = UserFactory.deserializeUser(wellBehavedSnapshot);
        assertTrue(correctUser.getDisplayName().equals(TEST_NAME)
                        && correctUser.getUid().equals(TEST_UID)
                        && correctUser.isFirstLogin()
                        && correctUser.getPreference(User.Preference.NOTIFICATIONS).getValue().equals(TEST_NOTIFICATIONS));

        User errorUser = UserFactory.deserializeUser(errorSnapshot);
        assertTrue(errorUser.getDisplayName() != null
                    && errorUser.getUid() != null
                    && !errorUser.isFirstLogin()
                    && errorUser.getPreferences() != null);

        System.out.println(errorUser);
    }
}
