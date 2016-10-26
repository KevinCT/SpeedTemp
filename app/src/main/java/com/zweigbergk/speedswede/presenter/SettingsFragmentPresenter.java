package com.zweigbergk.speedswede.presenter;

import com.zweigbergk.speedswede.interactor.SettingsFragmentInteractor;

public class SettingsFragmentPresenter {
    private SettingsFragmentInteractor mInteractor;

    public SettingsFragmentPresenter(){
        mInteractor = new SettingsFragmentInteractor();

    }

    public void onDialogPreferenceSelected(){
        mInteractor.changeLanguage();
    }

}