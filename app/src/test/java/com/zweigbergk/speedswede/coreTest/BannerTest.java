package com.zweigbergk.speedswede.coreTest;

import com.zweigbergk.speedswede.core.Banner;

import org.junit.Before;
import org.junit.Test;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by kevin on 29/09/2016.
 */

public class BannerTest {
    private Banner mBanner;
    private String mStrangerID;
    private String mUID;


    @Before
    public void initialize(){
        mBanner = new Banner();
        mStrangerID = "strangerID";
        mUID = "uID";
    }

    @Test
    public void addRemoveBanTest(){
        mBanner.addBan(mUID,mUID,mStrangerID);
        assertTrue(mBanner.getBanList().contains(mStrangerID));
        mBanner.removeBan(mStrangerID);
        assertFalse(mBanner.getBanList().contains(mStrangerID));
    }

    @Test
    public void setGetTest(){
        List<String> banList = new ArrayList<>();
        mBanner.setBanList(banList);
        assertTrue(mBanner.getBanList().equals(banList));
    }

    @Test
    public void isBannedTest(){
        mBanner.addBan(mUID,mUID,mStrangerID);
        assertTrue(mBanner.isBanned(mStrangerID));
    }
}
