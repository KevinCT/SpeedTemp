package com.zweigbergk.speedswede.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.MatchSkill;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.UserReference;
import com.zweigbergk.speedswede.fragment.SettingsFragment;

import static com.zweigbergk.speedswede.core.User.Preference;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle(R.string.activity_settings_title);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("DEBUG", "Local preferences changed: "+key+", "+sharedPreferences.getAll().get(key));

        UserReference user = DatabaseHandler.get(DatabaseHandler.getActiveUser());
        switch(key) {
            case "pref_usage":
                user.setPreference(Preference.USAGE, sharedPreferences.getString(key, "learn"));
                switch(sharedPreferences.getString(key, "learn")) {
                    case "learn":
                        DatabaseHandler.getActiveUser().setOwnSkill(MatchSkill.BEGINNER);
                        break;
                    case "mentor":
                        DatabaseHandler.getActiveUser().setOwnSkill(MatchSkill.SKILLED);
                        break;
                    case "chat":
                        DatabaseHandler.getActiveUser().setOwnSkill(MatchSkill.INTERMEDIATE);
                        break;
                    default:
                        break;
                }
                break;
            case "pref_app_language":
                user.setPreference(Preference.LANGUAGE, sharedPreferences.getString(key, "en"));
                break;
            case "pref_notifications":
                user.setPreference(Preference.NOTIFICATIONS, sharedPreferences.getBoolean(key, true));
                break;
            default:
        }
    }

    public void backButton() {
        this.finish();
    }
}
