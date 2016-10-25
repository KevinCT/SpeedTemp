package com.zweigbergk.speedswede.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.google.firebase.database.FirebaseDatabase;
import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.view.MainView;

public class MainActivity extends AppCompatActivity implements MainView {
    public static final String TAG = MainActivity.class.getSimpleName().toUpperCase();

    private static final boolean LOGOUT_ON_STARTUP = false;
    private static boolean calledAlready = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        if (!calledAlready) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            DatabaseHandler.registerConnectionHandling();
            calledAlready = true;
        }

        setUpContent();

        if (LOGOUT_ON_STARTUP) {
            DatabaseHandler.logout();
        }

        startLoginActivity();

        LanguageChanger.onCreate(this);
    }

    private void setUpContent() {
    }

    private void startLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
