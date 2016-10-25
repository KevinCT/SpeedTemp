package com.zweigbergk.speedswede.interactor;

import com.zweigbergk.speedswede.core.local.LanguageChanger;

public class SettingsFragmentInteractor {

    public SettingsFragmentInteractor() {

    }

    public void changeLanguage() {
        LanguageChanger.languageChanged(true);
    }

}