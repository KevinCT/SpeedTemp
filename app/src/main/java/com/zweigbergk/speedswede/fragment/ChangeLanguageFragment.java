package com.zweigbergk.speedswede.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.local.LanguageChanger;

import static com.zweigbergk.speedswede.Constants.ARABIC;
import static com.zweigbergk.speedswede.Constants.DARI;
import static com.zweigbergk.speedswede.Constants.ENGLISH;
import static com.zweigbergk.speedswede.Constants.SWEDISH;
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

        view.findViewById(R.id.fragment_change_language_swedish).setOnClickListener(v -> changeLanguage(SWEDISH));
        view.findViewById(R.id.fragment_change_language_english).setOnClickListener(v -> changeLanguage(ENGLISH));
        view.findViewById(R.id.fragment_change_language_turkish).setOnClickListener(v -> changeLanguage(TURKISH));
        view.findViewById(R.id.fragment_change_language_dari).setOnClickListener(v -> changeLanguage(DARI));
        view.findViewById(R.id.fragment_change_language_arabic).setOnClickListener(v -> changeLanguage(ARABIC));

        return view;
    }

    private void changeLanguage(String languageCode) {
        LanguageChanger.changeLanguage(languageCode, getContext());
        finish();
    }

    private void finish() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

}