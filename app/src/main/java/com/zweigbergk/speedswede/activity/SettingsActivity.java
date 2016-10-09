package com.zweigbergk.speedswede.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.UserReference;
import com.zweigbergk.speedswede.fragment.SettingsFragment;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                backButton();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void backButton() {
        this.finish();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("DEBUG", "Local preferences changed: "+key+", "+sharedPreferences.getAll().get(key));

        UserReference user = DatabaseHandler.get(DatabaseHandler.getActiveUser());
        switch(key) {
            // TODO: change these two skill preferences to sliders and not booleans -->
            case "pref_my_skill":
                long mySkill = 0;
                if (sharedPreferences.getBoolean(key, false)) {
                    mySkill = 100;
                }
                user.setPreference(User.Preference.SWEDISH_SKILL, mySkill);
                break;
            case "pref_match_skill":
                long matchSkill = 0;
                if (sharedPreferences.getBoolean(key, false)) {
                    matchSkill = 100;
                }
                user.setPreference(User.Preference.STRANGER_SWEDISH_SKILL, matchSkill);
                break;
            // TODO: <--
            case "pref_app_language":
                user.setPreference(User.Preference.LANGUAGE, sharedPreferences.getString(key, "en"));
                break;
            case "pref_notifications":
                user.setPreference(User.Preference.NOTIFICATIONS, sharedPreferences.getBoolean(key, true));
                break;
            default:
        }
    }
}
