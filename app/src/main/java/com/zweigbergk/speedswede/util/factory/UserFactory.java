package com.zweigbergk.speedswede.util.factory;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;

import com.zweigbergk.speedswede.util.PreferenceValue;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.collection.Collections;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.collection.List;
import com.zweigbergk.speedswede.util.collection.Map;

import static com.zweigbergk.speedswede.Constants.DISPLAY_NAME;
import static com.zweigbergk.speedswede.Constants.FIRST_LOGIN;
import static com.zweigbergk.speedswede.Constants.PREFERENCES;
import static com.zweigbergk.speedswede.Constants.USER_ID;
import static com.zweigbergk.speedswede.core.User.Preference;

public class UserFactory {
    private static final String TAG = UserFactory.class.getSimpleName().toUpperCase();

    public static User deserializeUser(DataSnapshot dataSnapshot) {
        String name = dataSnapshot.child(DISPLAY_NAME).getValue().toString();
        String id = dataSnapshot.child(USER_ID).getValue().toString();
        Object firstLoginObj = dataSnapshot.child(FIRST_LOGIN).getValue();
        boolean firstLogin = firstLoginObj != null && (boolean) firstLoginObj;

        //First get a list of the preference key: value pairs
        List<DataSnapshot> preferences = Collections.asList(dataSnapshot.child(PREFERENCES)
                .getChildren()
                .iterator());

        //Extract the keys
        List<Preference> preferenceKeys = preferences.map(DataSnapshot::getKey)
                .map(Preference::fromString);

        //Extract the values
        List<PreferenceValue> preferenceValues = preferences
                .map(snapshot -> PreferenceValue.cast(snapshot.getValue()));

        //Create a preference map
        Map<Preference, PreferenceValue> preferenceMap = HashMap
                .create(preferenceKeys, preferenceValues)
                .nonNull();

        User user = new UserProfile(name, id).withPreferences(preferenceMap);
        user.setFirstLogin(firstLogin);

        preferenceMap.foreach(entry -> Log.d(TAG, Stringify.curlyFormat("Here's a preference entry from the preferencemap! Key: {key}, Value: {value}",
                entry.getKey().toString(), entry.getValue().getValue().toString())));

        return user;
    }
}
