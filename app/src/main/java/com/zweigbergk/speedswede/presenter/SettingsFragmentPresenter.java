package com.zweigbergk.speedswede.presenter;

import android.content.Context;

import com.zweigbergk.speedswede.interactor.SettingsFragmentInteractor;

/**
 * Created by Kevin on 2016-10-09.
 */

public class SettingsFragmentPresenter {
    private SettingsFragmentInteractor mInteractor;

    public SettingsFragmentPresenter(){
        mInteractor = new SettingsFragmentInteractor();

    }

    public void onListPreferenceSelected(String string, Context context){
        mInteractor.changeLanguage(string, context);
    }

}
