package com.zweigbergk.speedswede.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.view.SettingsView;

public class SettingsActivity extends AppCompatActivity implements SettingsView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
}
