package com.zweigbergk.speedswede.presenter;

import com.zweigbergk.speedswede.MainActivity;
import com.zweigbergk.speedswede.interactor.MainInteractor;
import com.zweigbergk.speedswede.view.MainView;

public class MainPresenter implements MainActivity.ViewListener {

    private MainView mView;
    private MainInteractor mInteractor;

    public MainPresenter(MainView view) {
        mView = view;
        mInteractor = new MainInteractor();
    }

//    @Override
//    public void onLoginClick() {
//        mView.updateButtonText("Ny text");
//    }
}
