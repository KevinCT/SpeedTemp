package com.zweigbergk.speedswede.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.SkillCategory;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.UserReference;
import com.zweigbergk.speedswede.fragment.SettingsFragment;

import java.util.Locale;

import static com.zweigbergk.speedswede.Constants.SETTINGS_FIRST_SETUP;
import static com.zweigbergk.speedswede.database.UserReference.UserAttribute;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = SettingsActivity.class.getSimpleName().toUpperCase(Locale.ENGLISH);

    private boolean isFirstTime = false;

    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnContinue = (Button) findViewById(R.id.preference_continue_btn_continue);

        handleIntent();

        getFragmentManager().beginTransaction().replace(R.id.activity_settings_fragment_container, new SettingsFragment()).commit();

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }


    private void handleIntent() {
        isFirstTime = getIntent().getBooleanExtra(SETTINGS_FIRST_SETUP, false);
        if (!isFirstTime) {
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            btnContinue.setVisibility(View.GONE);
        } else {
            btnContinue.setOnClickListener(v -> close());
        }
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
                close();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged()");

        User activeUser = DatabaseHandler.getActiveUser();
        UserReference user = DatabaseHandler.getReference(DatabaseHandler.getActiveUser());
        switch(key) {
            case "pref_usage":
                switch(sharedPreferences.getString(key, "learn")) {
                    case "learn":
                        user.setSkillCategory(SkillCategory.STUDENT);
                        DatabaseHandler.getReference(activeUser).setUserAttribute(UserAttribute.SKILL_CATEGORY, SkillCategory.STUDENT.toString());
                        break;
                    case "mentor":
                        DatabaseHandler.getReference(activeUser).setSkillCategory(SkillCategory.MENTOR);
                        DatabaseHandler.getReference(activeUser).setUserAttribute(UserAttribute.SKILL_CATEGORY, SkillCategory.MENTOR.toString());
                        break;
                    case "chat":
                        DatabaseHandler.getReference(activeUser).setSkillCategory(SkillCategory.CHATTER);
                        DatabaseHandler.getReference(activeUser).setUserAttribute(UserAttribute.SKILL_CATEGORY, SkillCategory.CHATTER.toString());
                        break;
                    default:
                        break;
                }
                break;
            case "pref_app_language":
                Log.d(TAG, "The string we're sending as language: " + sharedPreferences.getString(key, "en"));
                DatabaseHandler.getReference(activeUser).setUserAttribute(UserAttribute.LANGUAGE, sharedPreferences.getString(key, "en"));
                break;
            case "pref_notifications":
                DatabaseHandler.getReference(activeUser).setUserAttribute(UserAttribute.NOTIFICATIONS, sharedPreferences.getBoolean(key, true));
                break;
            default:
        }
    }

    private void close() {
        if (isFirstTime) {
            User activeUser = DatabaseHandler.getActiveUser();
            DatabaseHandler.getReference(activeUser).setFirstLogin(false);
            startActivity(new Intent(this, ChatActivity.class));
        }
        this.finish();
    }
}
