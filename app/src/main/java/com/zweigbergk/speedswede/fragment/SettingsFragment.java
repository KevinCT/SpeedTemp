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
import com.zweigbergk.speedswede.presenter.SettingsFragmentPresenter;

public class SettingsFragment extends PreferenceFragment {
    private SharedPreferences.OnSharedPreferenceChangeListener mListener;
    private SettingsFragmentPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        initListener();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.registerOnSharedPreferenceChangeListener(mListener);
        mPresenter = new SettingsFragmentPresenter();
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
                mPresenter.onListPreferenceSelected(language, getActivity().getBaseContext());

                LanguageChanger.languageChanged(true);
                getActivity().recreate();
            }

        };

    }
}
