package com.zweigbergk.speedswede.fragment;


import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.local.LanguageChanger;

import android.content.res.Configuration;
import java.util.Locale;

import static com.zweigbergk.speedswede.Constants.SWEDISH;
import static com.zweigbergk.speedswede.Constants.ENGLISH;

public class ChangeLanguageFragment extends Fragment {


    public ChangeLanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_language, container, false);

        final ImageButton buttonSwedish = (ImageButton) view.findViewById(R.id.button_swedish);

        Drawable flagSweden = Drawable.createFromPath("@drawable/sweden.png");

        buttonSwedish.setBackground(flagSweden);

        view.findViewById(R.id.changeSwedishBtn).setOnClickListener(v -> pro(SWEDISH));
        view.findViewById(R.id.changeEnglishBtn).setOnClickListener(v -> pro(ENGLISH));

        return view;
    }

    private void changeToLanguage(String language) {
        LanguageChanger.changeLanguage(language, getContext());
        getActivity().recreate();
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void pro(String languageCode) {
        LanguageChanger.changeLanguage(languageCode, getContext());
        Resources resources = getContext().getResources();

        // Change locale settings in the app.
        DisplayMetrics metrics = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.locale = new Locale(languageCode.toLowerCase());
        resources.updateConfiguration(config, metrics);

        finish();
    }

    private void finish() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

}
