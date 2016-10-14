package com.zweigbergk.speedswede.interactor;

import android.content.Context;
import android.util.Log;

import com.zweigbergk.speedswede.core.local.LanguageChanger;

public class SettingsFragmentInteractor {

    public SettingsFragmentInteractor(){

    }
    public void changeLanguage(boolean languageChanged){
        LanguageChanger.languageChanged(true);
    }

}
