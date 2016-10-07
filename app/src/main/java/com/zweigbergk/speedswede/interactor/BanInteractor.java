package com.zweigbergk.speedswede.interactor;

import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.database.DatabaseHandler;

public class BanInteractor  {
    private Banner mBanner;

    public BanInteractor(){
        String userId = DatabaseHandler.getInstance().getActiveUserId();
        if(DatabaseHandler.INSTANCE.getBans(userId)!=null) {
            mBanner = DatabaseHandler.INSTANCE.getBans(userId);
        }
        else {
            mBanner = new Banner();
        }
    }

    public void addBan(String uID, String firstUser, String secondUser){
        mBanner.addBan(uID,firstUser,secondUser);
        DatabaseHandler.INSTANCE.sendObject("bans",mBanner);

    }

    public void removeBan(String strangerID){
        mBanner.removeBan(strangerID);
        DatabaseHandler.INSTANCE.sendObject("bans",mBanner);

    }




}
