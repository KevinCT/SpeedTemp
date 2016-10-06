package com.zweigbergk.speedswede.interactor;

import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.DbUserHandler;

/**
 * Created by Kevin on 2016-10-03.
 */

public class BanInteractor  {
    private Banner mBanner;

    public BanInteractor(){
        if(DatabaseHandler.INSTANCE.getBans(DbUserHandler.INSTANCE.getLoggedInUserId())!=null) {
            mBanner = DatabaseHandler.INSTANCE.getBans(DbUserHandler.INSTANCE.getLoggedInUserId());
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
