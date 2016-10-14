package com.zweigbergk.speedswede.core;

import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;

public class Banner {
    private List<String> banList;

    public Banner(){
        banList = new ArrayList<>();
    }

    public Banner(List<String> banList) {
        this.banList = banList;
    }

    public void addBan(String uID, String firstUser, String secondUser){
        banList.add(getStrangerID(uID,firstUser,secondUser));

    }

    public void removeBan(String strangerID){
        banList.remove(strangerID);

    }

    public boolean isBanned(String strangerID){
        return banList.contains(strangerID);

    }

    public List<String> getBanList(){
        return banList;
    }

    public void setBanList(List<String> banList){
        this.banList=banList;
    }

    private String getStrangerID(String uID, String firstUser, String secondUser){
        if(uID.equals(firstUser)){
            return secondUser;
        }
        else
            return firstUser;
    }
}
