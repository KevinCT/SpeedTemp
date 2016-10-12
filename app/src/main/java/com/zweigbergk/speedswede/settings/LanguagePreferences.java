package com.zweigbergk.speedswede.settings;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.local.LanguageChanger;

import java.util.Locale;

import static android.support.design.R.styleable.View;
import static com.zweigbergk.speedswede.Constants.ARABIC;
import static com.zweigbergk.speedswede.Constants.DARI;
import static com.zweigbergk.speedswede.Constants.ENGLISH;
import static com.zweigbergk.speedswede.Constants.SWEDISH;
import static com.zweigbergk.speedswede.Constants.TURKISH;

public class LanguagePreferences extends DialogPreference {
    public LanguagePreferences(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        setPersistent(false);

        setDialogLayoutResource(R.layout.fragment_change_language);


    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        view.findViewById(R.id.fragment_change_language_swedish).setOnClickListener(v -> pro(SWEDISH));
        view.findViewById(R.id.fragment_change_language_english).setOnClickListener(v -> pro(ENGLISH));
        view.findViewById(R.id.fragment_change_language_turkish).setOnClickListener(v -> pro(TURKISH));
        view.findViewById(R.id.fragment_change_language_dari).setOnClickListener(v -> pro(DARI));
        view.findViewById(R.id.fragment_change_language_arabic).setOnClickListener(v -> pro(ARABIC));
    }

    private void pro(String languageCode) {
        LanguageChanger.changeLanguage(languageCode, getContext());
        Resources resources = getContext().getResources();

        // Change locale settings in the app.
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(languageCode.toLowerCase());
        resources.updateConfiguration(config, metrics);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
    }
}
