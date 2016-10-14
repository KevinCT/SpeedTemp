package com.zweigbergk.speedswede.util.factory;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;

import com.zweigbergk.speedswede.util.PreferenceValue;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.async.PromiseNeed;
import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.collection.Map;

import static com.zweigbergk.speedswede.Constants.LANGUAGE;
import static com.zweigbergk.speedswede.Constants.NOTIFICATIONS;
import static com.zweigbergk.speedswede.Constants.SKILL_CATEGORY;
import static com.zweigbergk.speedswede.Constants.preference;

import static com.zweigbergk.speedswede.util.PreferenceValue.*;
import static com.zweigbergk.speedswede.core.User.Preference;

public class UserFactory {
    private static final String TAG = UserFactory.class.getSimpleName().toUpperCase();

    public static Promise<User> serializeUser(Promise<User> promise, DataSnapshot dataSnapshot) {
        promise.setResultForm(reconstructionBlueprint);

        Log.d(TAG, "In serializeUser()");

        promise.requires(PromiseNeed.NAME, PromiseNeed.ID, PromiseNeed.NOTIFICATIONS,
                PromiseNeed.LANGUAGE, PromiseNeed.SKILL_CATEGORY);

        promise.addItem(PromiseNeed.NAME,
                dataSnapshot.child(Constants.DISPLAY_NAME).getValue());

        promise.addItem(PromiseNeed.ID,
                dataSnapshot.child(Constants.USER_ID).getValue());

        promise.addItem(PromiseNeed.NOTIFICATIONS,
                dataSnapshot.child(preference(NOTIFICATIONS)).getValue());

        promise.addItem(PromiseNeed.LANGUAGE,
                dataSnapshot.child(preference(LANGUAGE)).getValue());

        promise.addItem(PromiseNeed.SKILL_CATEGORY,
                dataSnapshot.child(preference(SKILL_CATEGORY)).getValue());

        return promise;
    }

    private static final Promise.Result<User> reconstructionBlueprint = items -> {
        String name = items.getString(PromiseNeed.NAME);
        String id = items.getString(PromiseNeed.ID);

        Map<Preference, PreferenceValue> preferences = new HashMap<>();
        preferences.put(Preference.NOTIFICATIONS, new BooleanValue(items.getBoolean(PromiseNeed.NOTIFICATIONS)));
        preferences.put(Preference.LANGUAGE, new StringValue(items.getString(PromiseNeed.LANGUAGE)));
        preferences.put(Preference.SKILL_CATEGORY, new StringValue(items.getString(PromiseNeed.SKILL_CATEGORY)));

        User user = new UserProfile(name, id).withPreferences(preferences.nonNull());
        return user;

    };
}
