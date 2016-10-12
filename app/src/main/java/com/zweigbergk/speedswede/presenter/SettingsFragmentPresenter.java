package com.zweigbergk.speedswede.presenter;

import android.content.Context;

import com.zweigbergk.speedswede.interactor.SettingsFragmentInteractor;

public class SettingsFragmentPresenter {
    private SettingsFragmentInteractor mInteractor;

    public SettingsFragmentPresenter(){
        mInteractor = new SettingsFragmentInteractor();

    }

    public void onListPreferenceSelected(String string, Context context){
        mInteractor.changeLanguage(string, context);
    }

}
