package com.zweigbergk.speedswede.interactor;

import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.database.DatabaseHandler;

public class BanInteractor  {
    private Banner mBanner;

    public static final String BANS = "bans";

    public BanInteractor(){
        /*String userId = DatabaseHandler.getActiveUserId();
        if (DatabaseHandler.getBans(userId) != null) {
            mBanner = DatabaseHandler.getBans(userId);
        } else {
            mBanner = new Banner();
        }*/
    }

    public void addBan(String firstUser, String secondUser){
        mBanner.addBan(DatabaseHandler.getActiveUserId(), firstUser, secondUser);
        DatabaseHandler.sendObject(BANS, mBanner);

    }

    public void removeBan(String strangerID){
        if (mBanner.isBanned(strangerID)) {
            mBanner.removeBan(strangerID);
        }
        DatabaseHandler.sendObject(BANS, mBanner);

    }
}
