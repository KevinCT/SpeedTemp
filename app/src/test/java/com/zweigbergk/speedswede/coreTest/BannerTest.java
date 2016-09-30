package com.zweigbergk.speedswede.coreTest;

import com.zweigbergk.speedswede.core.Banner;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Created by kevin on 29/09/2016.
 */

public class BannerTest {
    Banner banner;

    @Before
    public void initialize(){
        this.banner = new Banner();
    }

    @Test
    public void addBanTest(){
        String strangerID = "strangerID";
        banner.addBan(strangerID);
        assertTrue(banner.getBanList().contains(strangerID));
    }

    @Test
    public void removeBanTest(){
        String strangerID = "strangerID";
        banner.removeBan(strangerID);
        assertFalse(banner.getBanList().contains(strangerID));
    }

    @Test
    public void setGetTest(){
        List<String> banList = new ArrayList<>();
        banner.setBanList(banList);
        assertTrue(banner.getBanList().equals(banList));
    }
}
