package com.zweigbergk.speedswede.util;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import static com.zweigbergk.speedswede.Constants.LANGUAGE;
import static com.zweigbergk.speedswede.Constants.NOTIFICATIONS;
import static com.zweigbergk.speedswede.Constants.STRANGER_SWEDISH_SKILL;
import static com.zweigbergk.speedswede.Constants.SWEDISH_SKILL;
import static com.zweigbergk.speedswede.Constants.preference;

public class UserFactory {
    public static ProductBuilder<User> buildUser(ProductBuilder<User> builder, DataSnapshot dataSnapshot) {
        builder.setBlueprint(reconstructionBlueprint);

        builder.attachLocks(ProductLock.NAME, ProductLock.ID, ProductLock.NOTIFICATIONS,
                ProductLock.LANGUAGE, ProductLock.SWEDISH_SKILL, ProductLock.STRANGER_SWEDISH_SKILL);

        builder.addItem(ProductLock.NAME,
                dataSnapshot.child(Constants.DISPLAY_NAME).getValue());

        builder.addItem(ProductLock.ID,
                dataSnapshot.child(Constants.USER_ID).getValue());

        builder.addItem(ProductLock.NOTIFICATIONS,
                dataSnapshot.child(preference(NOTIFICATIONS)).getValue());

        builder.addItem(ProductLock.LANGUAGE,
                dataSnapshot.child(preference(LANGUAGE)).getValue());

        builder.addItem(ProductLock.SWEDISH_SKILL,
                dataSnapshot.child(preference(SWEDISH_SKILL)).getValue());

        builder.addItem(ProductLock.STRANGER_SWEDISH_SKILL,
                dataSnapshot.child(preference(STRANGER_SWEDISH_SKILL)).getValue());

        return builder;
    }

    private static final ProductBuilder.Blueprint<User> reconstructionBlueprint = items -> {
        String name = items.getString(ProductLock.NAME);
        String id = items.getString(ProductLock.ID);

        Map<User.Preference, Object> preferences = new HashMap<>();
        preferences.put(User.Preference.NOTIFICATIONS, items.getBoolean(ProductLock.NOTIFICATIONS));
        preferences.put(User.Preference.LANGUAGE, items.getString(ProductLock.LANGUAGE));
        preferences.put(User.Preference.SWEDISH_SKILL, items.getLong(ProductLock.SWEDISH_SKILL));
        preferences.put(User.Preference.STRANGER_SWEDISH_SKILL, items.getLong(ProductLock.STRANGER_SWEDISH_SKILL));

        return new UserProfile(name, id).withPreferences(preferences);
    };
}
