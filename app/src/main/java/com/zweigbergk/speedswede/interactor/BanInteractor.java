package com.zweigbergk.speedswede.interactor;

import android.provider.ContactsContract;

import com.zweigbergk.speedswede.core.Banner;
import com.zweigbergk.speedswede.database.DatabaseHandler;

/**
 * Created by Kevin on 2016-10-03.
 */

public class BanInteractor  {
    private Banner mBanner;

    public BanInteractor(){
        if(DatabaseHandler.INSTANCE.getBans(DatabaseHandler.INSTANCE.getActiveUserId())!=null) {
            mBanner = DatabaseHandler.INSTANCE.getBans(DatabaseHandler.INSTANCE.getActiveUserId());
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
        mBanner.removeBan();
        mBanner.removeBan(strangerID);
        DatabaseHandler.INSTANCE.sendObject("bans",mBanner);

    }




}
