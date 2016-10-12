package com.zweigbergk.speedswede.util;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;

import java.util.HashMap;
import java.util.Map;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.util.methodwrapper.EntryAssertion;

import static com.zweigbergk.speedswede.Constants.LANGUAGE;
import static com.zweigbergk.speedswede.Constants.NOTIFICATIONS;
import static com.zweigbergk.speedswede.Constants.USAGE;
import static com.zweigbergk.speedswede.Constants.preference;

public class UserFactory {
    public static ProductBuilder<User> buildUser(ProductBuilder<User> builder, DataSnapshot dataSnapshot) {
        builder.setBlueprint(reconstructionBlueprint);

        builder.attachLocks(ProductLock.NAME, ProductLock.ID, ProductLock.NOTIFICATIONS,
                ProductLock.LANGUAGE, ProductLock.USAGE);

        builder.addItem(ProductLock.NAME,
                dataSnapshot.child(Constants.DISPLAY_NAME).getValue());

        builder.addItem(ProductLock.ID,
                dataSnapshot.child(Constants.USER_ID).getValue());

        builder.addItem(ProductLock.NOTIFICATIONS,
                dataSnapshot.child(preference(NOTIFICATIONS)).getValue());

        builder.addItem(ProductLock.LANGUAGE,
                dataSnapshot.child(preference(LANGUAGE)).getValue());

//        builder.addItem(ProductLock.SWEDISH_SKILL,
//                dataSnapshot.child(preference(SWEDISH_SKILL)).getValue());
//
//        builder.addItem(ProductLock.STRANGER_SWEDISH_SKILL,
//                dataSnapshot.child(preference(STRANGER_SWEDISH_SKILL)).getValue());

        builder.addItem(ProductLock.USAGE,
                dataSnapshot.child(preference(USAGE)).getValue());

        return builder;
    }

    private static final ProductBuilder.Blueprint<User> reconstructionBlueprint = items -> {
        String name = items.getString(ProductLock.NAME);
        String id = items.getString(ProductLock.ID);

        Map<User.Preference, PreferenceValue> preferences = new HashMap<>();
        preferences.put(User.Preference.NOTIFICATIONS, new BooleanPreference(items.getBoolean(ProductLock.NOTIFICATIONS)));
        preferences.put(User.Preference.LANGUAGE, new StringPref(items.getString(ProductLock.LANGUAGE)));
        preferences.put(User.Preference.USAGE, new StringPref(items.getString(ProductLock.USAGE)));
//        preferences.put(User.Preference.SWEDISH_SKILL, new LongPref(items.getLong(ProductLock.SWEDISH_SKILL)));
//        preferences.put(User.Preference.STRANGER_SWEDISH_SKILL, new LongPref(items.getLong(ProductLock.STRANGER_SWEDISH_SKILL)));

        EntryAssertion<User.Preference, PreferenceValue> isNull = e -> e.getValue() == null;
        Map<User.Preference, PreferenceValue> nonNullPrefs = Lists.reject(preferences, isNull);

        return new UserProfile(name, id).withPreferences(nonNullPrefs);
    };
}
