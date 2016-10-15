package com.zweigbergk.speedswede.core;

import android.util.Log;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.collection.ArrayList;
import com.zweigbergk.speedswede.util.collection.List;

import com.zweigbergk.speedswede.util.collection.HashMap;
import com.zweigbergk.speedswede.util.collection.Map;
import java.util.Timer;
import java.util.TimerTask;

public enum ChatMatcher {
    INSTANCE;

    public static final String TAG = ChatMatcher.class.getSimpleName().toUpperCase();

    private List<User> mUsersInPool;

    private boolean loopIsActive = false;

    ChatMatcher() {
        mUsersInPool = new ArrayList<>();
    }

    public void handleUser(DataChange<User> dataChange) {
        Log.d(TAG, "handleUser: " + dataChange.getItem().getDisplayName());
        User user = dataChange.getItem();
        DatabaseEvent event = dataChange.getEvent();

        switch (event) {
            case ADDED:
                Log.d(TAG, "addUserLocally: " + dataChange.getItem().getDisplayName());
                handleUserAdded(user);
                break;
            case REMOVED:
                removeUserLocally(user);
                break;
            default:
                break;
        }
    }

    /**
     * IF activeUser has not blocked user, and user has not blocked activeUser, this will
     * add user to the local user pool (mUsersInPool) and then run match().
     * @param user
     */
    private void handleUserAdded(User user) {
        User activeUser = DatabaseHandler.getActiveUser();

        Statement activeUserBlockedPromised = DatabaseHandler.getReference(user).hasBlocked(activeUser);
        Statement strangerBlockedPromised = DatabaseHandler.getReference(activeUser).hasBlocked(user);

        activeUserBlockedPromised.or(strangerBlockedPromised).onFalse(() -> {
            mUsersInPool.add(user);
            match();
        });
    }

    /** Removes user from the local pool of users */
    private void removeUserLocally(User user){
        mUsersInPool.remove(user);
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
            List<User> matchedUsers = mUsersInPool.first(2);

            DatabaseHandler.getPool().removeUser(matchedUsers.get(0));
            DatabaseHandler.getPool().removeUser(matchedUsers.get(1));

            Chat chat = new Chat(matchedUsers.get(0), matchedUsers.get(1));
            Log.d(TAG, chat.getName());
            DatabaseHandler.getReference(chat).push();
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


//    public List<User> sofisticatedMatch() {
//        User activeUser = DatabaseHandler.getActiveUser();
//
//        for(User secondUser : mUsersInPool) {
//            if(activeUser.getUid() != secondUser.getUid()) {
//                return checkIfMatch(activeUser, secondUser);
//            }
//        }
//        return null;
//    }

    public void nextLevelMatch() {
        Map<String, List<User>> listMap = seperatePools();
        List<User> learners = listMap.get("learners");
        List<User> mentors = listMap.get("mentors");
        List<User> chatters = listMap.get("chatters");
        matchLearners(learners, mentors);
        matchChatters(chatters);
    }

    public Map<String, List<User>> seperatePools() {
        List<User> learners = new ArrayList<>();
        List<User> mentors = new ArrayList<>();
        List<User> chatters = new ArrayList<>();
        Map<String, List<User>> listMap = new HashMap<>();

        for(User user : mUsersInPool) {
            switch(user.getSkillCategory()) {
                case STUDENT:
                    learners.add(user);
                    break;
                case CHATTER:
                    chatters.add(user);
                    break;
                case MENTOR:
                    mentors.add(user);
                    break;
                default:
                    break;
            }
        }

        listMap.put("learners", learners);
        listMap.put("mentors", mentors);
        listMap.put("chatters", chatters);
        return listMap;
    }

    public void matchLearners(List<User> learners, List<User> mentors) {
        if(learners.size() > 0 && mentors.size() > 0) {
            User firstBeginner = learners.get(0);
            for (User user : learners) {
                if (firstBeginner.getTimeInQueue() > user.getTimeInQueue()) {
                    firstBeginner = user;
                }
            }
            User firstMentor = mentors.get(0);
            for (User user : mentors) {
                if (firstMentor.getTimeInQueue() > user.getTimeInQueue()) {
                    firstMentor = user;
                }
            }

            DatabaseHandler.getPool().removeUser(firstBeginner);
            DatabaseHandler.getPool().removeUser(firstMentor);

            Chat chat = new Chat(firstBeginner, firstMentor);
            Log.d("MAFAKALEARNERS", chat.getName() + "");
            DatabaseHandler.getReference(chat).push();
        }
    }

    public void matchChatters(List<User> userList) {
        if(userList.size() > 1) {
            List<User> matchedUsers = new ArrayList<>();
            matchedUsers.add(userList.get(0));
            matchedUsers.add(userList.get(1));

            DatabaseHandler.getPool().removeUser(matchedUsers.get(0));
            DatabaseHandler.getPool().removeUser(matchedUsers.get(1));

            Chat chat = new Chat(matchedUsers.get(0), matchedUsers.get(1));
            Log.d("FILTHYCASUALS: ", chat.getName() + "");
            DatabaseHandler.getReference(chat).push();
        }
    }

//    public List<User> checkIfMatch(User activeUser, User secondUser) {
//        List<User> matchedUsers = new ArrayList<>();
//        if(activeUser.getmMatchSkill() == secondUser.getOwnSkill()) {
//            List<User> skillGroup = new ArrayList<>();
//            skillGroup.add(secondUser);
//            boolean firstTime = true;
//            long temp = 0;
//            User bestMatch = null;
//            for(User matchedUser : skillGroup) {
//                if(firstTime) {
//                    temp = matchedUser.getTimeInQueue();
//                    bestMatch = matchedUser;
//                    firstTime = false;
//                }
//                if(temp > matchedUser.getTimeInQueue()) {
//                    temp = matchedUser.getTimeInQueue();
//                    bestMatch = matchedUser;
//                }
//            }
//            matchedUsers.add(DatabaseHandler.getActiveUser());
//            matchedUsers.add(bestMatch);
//            Log.d("FELIXMATCH", " matched: " + bestMatch);
//            return matchedUsers;
//        }
//        return null;
//    }

    public void matchingLoop() {
        if(!loopIsActive) {
            loopIsActive = true;
            Timer timer = new Timer();

            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    nextLevelMatch();
                }
            }, 10 * 1000, 10 * 1000);
        }
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
