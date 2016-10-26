package com.zweigbergk.speedswede.util.factory;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;

import com.zweigbergk.speedswede.mock.ISnapshot;
import com.zweigbergk.speedswede.mock.SnapshotExtension;
import com.zweigbergk.speedswede.util.PreferenceWrapper;
import com.zweigbergk.speedswede.util.collection.Collections;
import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import com.zweigbergk.speedswede.util.collection.MapExtension;


import static com.zweigbergk.speedswede.Constants.DISPLAY_NAME;
import static com.zweigbergk.speedswede.Constants.FIRST_LOGIN;
import static com.zweigbergk.speedswede.Constants.PREFERENCES;
import static com.zweigbergk.speedswede.Constants.UNDEFINED;
import static com.zweigbergk.speedswede.Constants.USER_ID;
import static com.zweigbergk.speedswede.core.User.Preference;

import static com.zweigbergk.speedswede.util.collection.MapExtension.MapEntry;

public class UserFactory {

    public static User deserializeUser(ISnapshot dataSnapshot) {
        ISnapshot sName = dataSnapshot.child(DISPLAY_NAME);
        String name = sName.exists() ? sName.getValue().toString() : UNDEFINED;

        ISnapshot sId = dataSnapshot.child(USER_ID);
        String id = sId.exists() ? sId.getValue().toString() : UNDEFINED;

        ISnapshot sFirstLogin = dataSnapshot.child(FIRST_LOGIN);
        boolean firstLogin = sFirstLogin.exists() && toBoolean(sFirstLogin.getValue());

        //Retrieve the preferences
        ISnapshot sPreferences = dataSnapshot.child(PREFERENCES);

        MapExtension<Preference, PreferenceWrapper> preferenceMap = sPreferences.exists() ?
                Collections.asList(dataSnapshot.child(PREFERENCES)
                .getChildren()
                .iterator())
                .toMap(snapshot ->
                new MapEntry<>(
                        Preference.fromString(snapshot.getKey()),
                        PreferenceWrapper.cast(snapshot.getValue())
                )).nonNull()
                : new HashMapExtension<>();


        User user = new UserProfile(name, id).withPreferences(preferenceMap);
        user.setFirstLogin(firstLogin);

        return user;
    }

    public static User deserializeUser(DataSnapshot snapshot) {
        return deserializeUser(new SnapshotExtension(snapshot));
    }

    private static boolean toBoolean(Object object) {
        return object.getClass().equals(Boolean.class) && (boolean) object;
    }
}
