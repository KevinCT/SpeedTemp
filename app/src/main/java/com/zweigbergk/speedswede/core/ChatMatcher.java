package com.zweigbergk.speedswede.core;

import android.util.Log;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.async.Statement;
import com.zweigbergk.speedswede.util.collection.ArrayListExtension;
import com.zweigbergk.speedswede.util.collection.ListExtension;

import com.zweigbergk.speedswede.util.collection.HashMapExtension;
import com.zweigbergk.speedswede.util.collection.MapExtension;
import java.util.Timer;
import java.util.TimerTask;

public enum ChatMatcher {
    INSTANCE;

    private static final String TAG = ChatMatcher.class.getSimpleName().toUpperCase();

    private ListExtension<User> mUsersInPool;

    private boolean loopIsActive = false;

    ChatMatcher() {
        mUsersInPool = new ArrayListExtension<>();
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

    private void handleUserAdded(User user) {
        User activeUser = DatabaseHandler.getActiveUser();

        Statement activeUserBlockedPromised = DatabaseHandler.getReference(user).hasBlocked(activeUser);
        Statement strangerBlockedPromised = DatabaseHandler.getReference(activeUser).hasBlocked(user);

        activeUserBlockedPromised.or(strangerBlockedPromised).onFalse(() -> {
            if (!mUsersInPool.contains(user)) {
                mUsersInPool.add(user);
                match();
            }
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

    private void match() {
        Log.d(TAG, "Users in pool: " + mUsersInPool.size());
        if (mUsersInPool.size() > 1) {
            // TODO: Change to a more sofisticated matching algorithm in future. Maybe match depending on personal best in benchpress?
            ListExtension<User> matchedUsers = mUsersInPool.first(2);

            DatabaseHandler.getPool().removeUser(matchedUsers.get(0));
            DatabaseHandler.getPool().removeUser(matchedUsers.get(1));

            Chat chat = new Chat(matchedUsers.get(0), matchedUsers.get(1));
            Log.d(TAG, chat.getName());
            DatabaseHandler.getReference(chat).push();
        }
    }



//    public ListExtension<User> sofisticatedMatch() {
//        User activeUser = DatabaseHandler.getActiveUser();
//
//        for(User secondUser : mUsersInPool) {
//            if(activeUser.getUid() != secondUser.getUid()) {
//                return checkIfMatch(activeUser, secondUser);
//            }
//        }
//        return null;
//    }

    private void nextLevelMatch() {
        MapExtension<String, ListExtension<User>> listMap = seperatePools();
        ListExtension<User> learners = listMap.get("learners");
        ListExtension<User> mentors = listMap.get("mentors");
        ListExtension<User> chatters = listMap.get("chatters");
        matchLearners(learners, mentors);
        matchChatters(chatters);
    }

    private MapExtension<String, ListExtension<User>> seperatePools() {
        ListExtension<User> learners = new ArrayListExtension<>();
        ListExtension<User> mentors = new ArrayListExtension<>();
        ListExtension<User> chatters = new ArrayListExtension<>();
        MapExtension<String, ListExtension<User>> listMap = new HashMapExtension<>();

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

    private void matchLearners(ListExtension<User> learners, ListExtension<User> mentors) {
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

    private void matchChatters(ListExtension<User> userList) {
        if(userList.size() > 1) {
            ListExtension<User> matchedUsers = new ArrayListExtension<>();
            matchedUsers.add(userList.get(0));
            matchedUsers.add(userList.get(1));

            DatabaseHandler.getPool().removeUser(matchedUsers.get(0));
            DatabaseHandler.getPool().removeUser(matchedUsers.get(1));

            Chat chat = new Chat(matchedUsers.get(0), matchedUsers.get(1));
            Log.d("FILTHYCASUALS: ", chat.getName() + "");
            DatabaseHandler.getReference(chat).push();
        }
    }

//    public ListExtension<User> checkIfMatch(User activeUser, User secondUser) {
//        ListExtension<User> matchedUsers = new ArrayListExtension<>();
//        if(activeUser.getmMatchSkill() == secondUser.getOwnSkill()) {
//            ListExtension<User> skillGroup = new ArrayListExtension<>();
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
//    public ListExtension<User> checkIfMatch(User userSecond) {
//        int userSecondMin = userSecond.getMatchInterval()[0];
//        int userSecondMax = userSecond.getMatchInterval()[1];
//        int userFirstRating = DatabaseHandler.getActiveUser().getOwnRating();
//        ListExtension<User> matchedUsers = new ArrayListExtension<>();
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
//    public ListExtension<User> checkIfDifferentUsers(User userSecond) {
//        if(userSecond.getUid() != DatabaseHandler.getActiveUser().getUid()) {
//            return checkIfMatch(userSecond);
//        }
//        return null;
//    }
//
//    public ListExtension<User> advancedMatch() {
//        if(mUsersInPool.size() > 1) {
//            for(User user : mUsersInPool) {
//                return checkIfDifferentUsers(user);
//            }
//        }
//        return null;
//    }
}
