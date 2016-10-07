package com.zweigbergk.speedswede.core;

import android.util.Log;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.util.Lists;

import java.util.ArrayList;
import java.util.List;

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

        User activeUser = DatabaseHandler.getInstance().getActiveUser();
        DatabaseHandler.getPool().ifContains(activeUser).then(this::match);
    }

    /** Removes user from the local pool of users */
    private void removeUserLocally(User user){
        mUsersInPool.remove(user);
    }

    private boolean isBlocked(User user) {
        String activeUserId = DatabaseHandler.getInstance().getActiveUserId();
        Banner banner = DatabaseHandler.INSTANCE.getBans(activeUserId);

        if (banner != null) {
            List<String> bannedIds = DatabaseHandler.INSTANCE.getBans(activeUserId).getBanList();

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
            List<User> matchedUsers = Lists.getFirstElements(mUsersInPool, 2);

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
}
