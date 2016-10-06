package com.zweigbergk.speedswede.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.local.LanguageChanger;

public class SettingsFragment extends PreferenceFragment {
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        initListener();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(mListener);

    }

    private void initListener(){
        mListener = (sharedPreferences, s) -> {
            Preference preference = findPreference(s);
            //Make sure it only checks when listpreference is open
            if (preference instanceof ListPreference) {
                String language = ((ListPreference) preference).getValue().toString();

                switch (language) {
                    case "sv":
                        LanguageChanger.changeLanguage("sv", getActivity().getBaseContext());
                        break;
                    case "en":
                        LanguageChanger.changeLanguage("en", getActivity().getBaseContext());
                        break;
                    case "ar":
                        LanguageChanger.changeLanguage("ar", getActivity().getBaseContext());
                        break;
                    default:
                        LanguageChanger.changeLanguage("default",getActivity().getBaseContext());
                        break;

                }
                LanguageChanger.languageChanged(true);
                getActivity().recreate();
            }

        };

    }
}
