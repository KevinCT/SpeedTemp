package com.zweigbergk.speedswede.presenter;

import com.zweigbergk.speedswede.interactor.BanInteractor;

/**
 * Created by Kevin on 2016-10-09.
 */

public class ChatFragmentPresenter {
    private BanInteractor mBanInteractor;

    public ChatFragmentPresenter(){
        mBanInteractor = new BanInteractor();
    }

    public void onBanClicked(String firstUser, String secondUser){
        mBanInteractor.addBan(firstUser, secondUser);
    }
}
