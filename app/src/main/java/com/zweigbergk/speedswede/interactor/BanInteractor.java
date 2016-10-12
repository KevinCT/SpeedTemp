package com.zweigbergk.speedswede.interactor;

import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.database.DatabaseHandler;

public class BanInteractor  {
    private Banner mBanner;

    public BanInteractor(){
        String userId = DatabaseHandler.getActiveUserId();
        if (DatabaseHandler.getBans(userId) != null) {
            mBanner = DatabaseHandler.getBans(userId);
        } else {
            mBanner = new Banner();
        }
    }

    public void addBan(String firstUser, String secondUser){
        mBanner.addBan(DatabaseHandler.getActiveUserId(),firstUser,secondUser);
        DatabaseHandler.sendObject("bans",mBanner);

    }

    public void removeBan(String strangerID){
        mBanner.removeBan(strangerID);
        DatabaseHandler.sendObject("bans",mBanner);

    }
}
