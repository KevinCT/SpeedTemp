package com.zweigbergk.speedswede.core;

import android.util.Log;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public enum ChatMatcher {
    INSTANCE;

    public static final String TAG = ChatMatcher.class.getSimpleName().toUpperCase();

    private List<User> mUsersInPool;

    ChatMatcher() {
        mUsersInPool = new ArrayList<>();
    }

    public void handleUser(DataChange<User> dataChange) {
        Log.d(TAG, "handleUser: " + dataChange.getItem().getDisplayName());
        User user = dataChange.getItem();
        DatabaseEvent event = dataChange.getEvent();

        switch (event) {
            case ADDED:
                addUserLocally(user);
                Log.d(TAG, "addUserLocally: " + dataChange.getItem().getDisplayName());
                break;
            case REMOVED:
                removeUserLocally(user);
                break;
            default:
                break;
        }
    }

    /** Adds user to the local pool of users. Does nothing if the incoming user is blocked by our
     * logged in user. */
    private void addUserLocally(User user) {
        if (isBlocked(user)) {
            return;
        }

        mUsersInPool.add(user);
        Log.d(TAG, "Added user. Poolsize: " + mUsersInPool.size());

        User activeUser = DatabaseHandler.getActiveUser();
        DatabaseHandler.getPool().contains(activeUser).onTrue(this::matchingLoop);
    }

    /** Removes user from the local pool of users */
    private void removeUserLocally(User user){
        mUsersInPool.remove(user);
    }

    private boolean isBlocked(User user) {
        String activeUserId = DatabaseHandler.getActiveUserId();
        Banner banner = DatabaseHandler.getBans(activeUserId);

        if (banner != null) {
            List<String> bannedIds = DatabaseHandler.getBans(activeUserId).getBanList();

            return bannedIds.contains(user.getUid());
        } else {
            return false;
        }
    }

    /** Include user in the matching process */
    public void pushUser(User user) {
        DatabaseHandler.getPool().push(user);
    }

    /** Remove user from the matching process */
    public void removeUser(User user) {
        DatabaseHandler.getPool().remove(user);
    }

    public void match() {
        Log.d(TAG, "Users in pool: " + mUsersInPool.size());
        if (mUsersInPool.size() > 1) {
            // TODO: Change to a more sofisticated matching algorithm in future. Maybe match depending on personal best in benchpress?
            //List<User> matchedUsers = Lists.getFirstElements(mUsersInPool, 2);
            List<User> matchedUsers = sofisticatedMatch();

            Lists.forEach(matchedUsers, DatabaseHandler.getPool()::remove);

            Chat chat = new Chat(matchedUsers.get(0), matchedUsers.get(1));
            Log.d("CHATMATCHER: NAME: ", chat.getName() + "");
            DatabaseHandler.get(chat).push();
        }
    }

    private List<String> getUserIdList(){
        List<String> userNameList = new ArrayList<>();
        for(int i=0;i<mUsersInPool.size();i++){
            userNameList.add(mUsersInPool.get(i).getUid());
        }
        return userNameList;
    }

    public void clear() {
        mUsersInPool.clear();
    }

    public List<User> sofisticatedMatch() {
        User activeUser = DatabaseHandler.getActiveUser();

        for(User secondUser : mUsersInPool) {
            if(activeUser.getUid() != secondUser.getUid()) {
                return checkIfMatch(activeUser, secondUser);
            }
        }
        return null;
    }

    public List<User> checkIfMatch(User activeUser, User secondUser) {
        List<User> matchedUsers = new ArrayList<>();
        if(activeUser.getMatchSkill() == secondUser.getOwnSkill()) {
            matchedUsers.add(DatabaseHandler.getActiveUser());
            matchedUsers.add(secondUser);
            Log.d("FELIXMATCH", " : we got a match brah");
            return matchedUsers;
        }
        return null;
    }

    public void matchingLoop() {
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                match();
            }
        }, 10*1000, 10*1000);
    }
//
//    public List<User> checkIfMatch(User userSecond) {
//        int userSecondMin = userSecond.getMatchInterval()[0];
//        int userSecondMax = userSecond.getMatchInterval()[1];
//        int userFirstRating = DatabaseHandler.getActiveUser().getOwnRating();
//        List<User> matchedUsers = new ArrayList<>();
//
//        if(userFirstRating >= userSecondMin && userFirstRating <= userSecondMax) {
//            matchedUsers.add(DatabaseHandler.getActiveUser());
//            matchedUsers.add(userSecond);
//            Log.d("FELIXMATCH", " : we got a match brah");
//            return matchedUsers;
//        }
//        return null;
//    }
//
//    public List<User> checkIfDifferentUsers(User userSecond) {
//        if(userSecond.getUid() != DatabaseHandler.getActiveUser().getUid()) {
//            return checkIfMatch(userSecond);
//        }
//        return null;
//    }
//
//    public List<User> advancedMatch() {
//        if(mUsersInPool.size() > 1) {
//            for(User user : mUsersInPool) {
//                return checkIfDifferentUsers(user);
//            }
//        }
//        return null;
//    }
}
