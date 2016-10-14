package com.zweigbergk.speedswede.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.local.LanguageChanger;

import java.util.Locale;

import static com.zweigbergk.speedswede.Constants.ARABIC;
import static com.zweigbergk.speedswede.Constants.DARI;
import static com.zweigbergk.speedswede.Constants.ENGLISH;
import static com.zweigbergk.speedswede.Constants.SWEDISH;
import static com.zweigbergk.speedswede.Constants.TURKISH;

public class LanguagePreferences extends DialogPreference {
    public static String TAG  = "LanguagePreferences";

    public LanguagePreferences(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        setPersistent(false);
        setKey("language");

        setDialogLayoutResource(R.layout.fragment_change_language);
        
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        Log.d(TAG, view.toString());

        view.findViewById(R.id.fragment_change_language_swedish).setOnClickListener(v -> changeLanguage(SWEDISH));
        view.findViewById(R.id.fragment_change_language_english).setOnClickListener(v -> changeLanguage(ENGLISH));
        view.findViewById(R.id.fragment_change_language_turkish).setOnClickListener(v -> changeLanguage(TURKISH));
        view.findViewById(R.id.fragment_change_language_dari).setOnClickListener(v -> changeLanguage(DARI));
        view.findViewById(R.id.fragment_change_language_arabic).setOnClickListener(v -> changeLanguage(ARABIC));

    }

    private void changeLanguage(String languageCode) {
        LanguageChanger.changeLanguage(languageCode, getContext());
        setSummary(languageCode);
        getDialog().dismiss();

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
    }
}
