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

import static com.zweigbergk.speedswede.Constants.ARABIC;
import static com.zweigbergk.speedswede.Constants.DARI;
import static com.zweigbergk.speedswede.Constants.SWEDISH;
import static com.zweigbergk.speedswede.Constants.ENGLISH;
import static com.zweigbergk.speedswede.Constants.TURKISH;

public class ChangeLanguageFragment extends Fragment {


    public ChangeLanguageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_language, container, false);

        view.findViewById(R.id.fragment_change_language_swedish).setOnClickListener(v -> pro(SWEDISH));
        view.findViewById(R.id.fragment_change_language_english).setOnClickListener(v -> pro(ENGLISH));
        view.findViewById(R.id.fragment_change_language_turkish).setOnClickListener(v -> pro(TURKISH));
        view.findViewById(R.id.fragment_change_language_dari).setOnClickListener(v -> pro(DARI));
        view.findViewById(R.id.fragment_change_language_arabic).setOnClickListener(v -> pro(ARABIC));

        return view;
    }

    private void pro(String languageCode) {
        LanguageChanger.changeLanguage(languageCode, getContext());
        finish();
    }

    private void finish() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

}
