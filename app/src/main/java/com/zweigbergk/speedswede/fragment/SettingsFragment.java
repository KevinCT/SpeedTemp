package com.zweigbergk.speedswede.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.zweigbergk.speedswede.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
