package com.zweigbergk.speedswede.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.activity.SettingsActivity;
import com.zweigbergk.speedswede.presenter.SettingsFragmentPresenter;
import com.zweigbergk.speedswede.settings.LanguagePreferences;

public class SettingsFragment extends PreferenceFragment {
    private static final String TAG = SettingsActivity.class.getSimpleName().toUpperCase();


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
        mListener = (sharedPreferences, key) -> {
            Preference preference = findPreference(key);
            //Make sure it only checks when Languagepreference is open
            if (preference instanceof LanguagePreferences) {
                mPresenter.onDialogPreferenceSelected(true);
                getActivity().recreate();
            }

        };
    }
}