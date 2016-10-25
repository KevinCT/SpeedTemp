package com.zweigbergk.speedswede.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.local.LanguageChanger;
import static com.zweigbergk.speedswede.Constants.ARABIC;
import static com.zweigbergk.speedswede.Constants.DARI;
import static com.zweigbergk.speedswede.Constants.ENGLISH;
import static com.zweigbergk.speedswede.Constants.SWEDISH;
import static com.zweigbergk.speedswede.Constants.TURKISH;

public class LanguagePreferences extends DialogPreference {
    private static final String TAG  = "LanguagePreferences";

    public LanguagePreferences(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        setPersistent(false);
        setKey("language");

        setDialogLayoutResource(R.layout.fragment_change_language);

    }

    @Override
    protected void onAttachedToActivity() {
        super.onAttachedToActivity();
        setSummary(R.string.chosen_language);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        setSummary(R.string.chosen_language);
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        Log.d(TAG, view.toString());

//        int[] fragmentIds = new int[R.id.fragment_change_language_swedish, R.id.fragment_change_language_english,
//                R.id.fragment_change_language_turkish,
//                R.id.fragment_change_language_dari,
//                R.id.fragment_change_language_arabic];

        Integer s = R.id.fragment_change_language_swedish;
//        String[] languages = { SWEDISH, ENGLISH };
//        for (int i = 0; i < fragmentIds.length; i++) {
//            view.findViewById(fragmentIds[i]).setOnClickListener(v -> changeLanguage(languages[i]));
//        }

        view.findViewById(s).setOnClickListener(v -> changeLanguage(SWEDISH));
        view.findViewById(R.id.fragment_change_language_english).setOnClickListener(v -> changeLanguage(ENGLISH));
        view.findViewById(R.id.fragment_change_language_turkish).setOnClickListener(v -> changeLanguage(TURKISH));
        view.findViewById(R.id.fragment_change_language_dari).setOnClickListener(v -> changeLanguage(DARI));
        view.findViewById(R.id.fragment_change_language_arabic).setOnClickListener(v -> changeLanguage(ARABIC));

    }

    private void changeLanguage(String languageCode) {
        LanguageChanger.changeLanguage(languageCode, getContext());
        setSummary(R.string.chosen_language);
        getDialog().dismiss();
    }
}