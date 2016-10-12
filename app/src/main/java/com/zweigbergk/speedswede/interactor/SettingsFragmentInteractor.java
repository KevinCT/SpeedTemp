package com.zweigbergk.speedswede.interactor;

import android.content.Context;
import android.util.Log;

import com.zweigbergk.speedswede.core.local.LanguageChanger;

public class SettingsFragmentInteractor {

    public SettingsFragmentInteractor(){

    }
    //android specific code allowed?
    public void changeLanguage(String string, Context context){
        LanguageChanger.changeLanguage(string, context);
        LanguageChanger.languageChanged(true);
    }

}
