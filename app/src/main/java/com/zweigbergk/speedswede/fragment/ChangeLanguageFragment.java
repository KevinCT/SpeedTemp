package com.zweigbergk.speedswede.fragment;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.zweigbergk.speedswede.R;
import com.zweigbergk.speedswede.core.local.LanguageChanger;

/**
 * A simple {@link Fragment} subclass.
 */
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

        view.findViewById(R.id.changeSwedishBtn).setOnClickListener(view1 -> {
            LanguageChanger.changeLanguage("sv", getContext());
            getActivity().recreate();;
            getFragmentManager().popBackStack();
        });

        view.findViewById(R.id.changeEnglishBtn).setOnClickListener(view1 -> {
            LanguageChanger.changeLanguage("en", getContext());
            getActivity().recreate();
            getFragmentManager().popBackStack();
        });
        return view;
    }

}
