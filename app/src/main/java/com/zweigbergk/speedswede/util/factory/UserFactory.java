package com.zweigbergk.speedswede.util.factory;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.zweigbergk.speedswede.Constants;
import com.zweigbergk.speedswede.core.MatchSkill;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;

import java.util.HashMap;
import java.util.Map;

import com.zweigbergk.speedswede.util.BooleanPreference;
import com.zweigbergk.speedswede.util.Lists;
import com.zweigbergk.speedswede.util.PreferenceValue;
import com.zweigbergk.speedswede.util.Stringify;
import com.zweigbergk.speedswede.util.async.Promise;
import com.zweigbergk.speedswede.util.async.PromiseNeed;
import com.zweigbergk.speedswede.util.StringPref;
import com.zweigbergk.speedswede.util.methodwrapper.EntryAssertion;

import static com.zweigbergk.speedswede.Constants.LANGUAGE;
import static com.zweigbergk.speedswede.Constants.NOTIFICATIONS;
import static com.zweigbergk.speedswede.Constants.SKILL;
import static com.zweigbergk.speedswede.Constants.USAGE;
import static com.zweigbergk.speedswede.Constants.preference;

public class UserFactory {
    private static final String TAG = UserFactory.class.getSimpleName().toUpperCase();

    public static Promise<User> buildUser(Promise<User> builder, DataSnapshot dataSnapshot) {
        builder.setResultForm(reconstructionBlueprint);

        Log.d(TAG, "In buildUser()");

        builder.requires(PromiseNeed.NAME, PromiseNeed.ID, PromiseNeed.NOTIFICATIONS,
                PromiseNeed.LANGUAGE, PromiseNeed.SKILL);

        builder.addItem(PromiseNeed.NAME,
                dataSnapshot.child(Constants.DISPLAY_NAME).getValue());

        builder.addItem(PromiseNeed.ID,
                dataSnapshot.child(Constants.USER_ID).getValue());

        builder.addItem(PromiseNeed.NOTIFICATIONS,
                dataSnapshot.child(preference(NOTIFICATIONS)).getValue());

        builder.addItem(PromiseNeed.LANGUAGE,
                dataSnapshot.child(preference(LANGUAGE)).getValue());

        builder.addItem(PromiseNeed.SKILL, dataSnapshot.child(SKILL).getValue());

        return builder;
    }

    private static final Promise.Result<User> reconstructionBlueprint = items -> {
        String name = items.getString(PromiseNeed.NAME);
        String id = items.getString(PromiseNeed.ID);

        Map<User.Preference, PreferenceValue> preferences = new HashMap<>();
        preferences.put(User.Preference.NOTIFICATIONS, new BooleanPreference(items.getBoolean(PromiseNeed.NOTIFICATIONS)));
        preferences.put(User.Preference.LANGUAGE, new StringPref(items.getString(PromiseNeed.LANGUAGE)));
        MatchSkill skill = MatchSkill.valueOf(MatchSkill.class, items.getString(PromiseNeed.SKILL));
        Log.d(TAG, Stringify.curlyFormat("Skill: {skill}", skill.getValue()));

        EntryAssertion<User.Preference, PreferenceValue> isNull = e -> e.getValue() == null;
        Map<User.Preference, PreferenceValue> nonNullPrefs = Lists.reject(preferences, isNull);

        User user = new UserProfile(name, id).withPreferences(nonNullPrefs);
        user.setOwnSkill(skill);
        return user;
    };
}
