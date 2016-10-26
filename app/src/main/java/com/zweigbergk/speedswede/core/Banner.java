package com.zweigbergk.speedswede.core;

import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;

public class Banner {
    private ListExtension<String> banList;

    public Banner(){
        banList = new ArrayListExtension<>();
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

    public ListExtension<String> getBanList(){
        return banList;
    }

    public void setBanList(ListExtension<String> banList){
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
