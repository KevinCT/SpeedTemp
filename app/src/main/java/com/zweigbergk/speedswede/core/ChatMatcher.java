package com.zweigbergk.speedswede.core;

import android.util.Log;

import com.zweigbergk.speedswede.database.DataChange;
import com.zweigbergk.speedswede.database.DatabaseEvent;
import com.zweigbergk.speedswede.database.DatabaseHandler;
import com.zweigbergk.speedswede.database.firebase.DbChatHandler;
import com.zweigbergk.speedswede.database.firebase.DbUserHandler;
import com.zweigbergk.speedswede.util.Client;
import com.zweigbergk.speedswede.util.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ChatMatcher {
    INSTANCE;

    public static final String TAG = ChatMatcher.class.getSimpleName().toUpperCase();

    private List<User> mUserPool;

    private Map<DatabaseEvent, List<Client<User>>> clients;

    ChatMatcher() {
        mUserPool = new ArrayList<>();
        clients = new HashMap<>();

        for(DatabaseEvent event : DatabaseEvent.values()) {
            clients.put(event, new ArrayList<>());
        }
    }

    public void handleUser(DataChange<User> dataChange) {
        Log.d(TAG, "Caught user: " + dataChange.getItem().getDisplayName());
        User user = dataChange.getItem();
        DatabaseEvent event = dataChange.getEvent();

        switch (event) {
            case ADDED:
                addUserLocally(user);
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

        mUserPool.add(user);
        notifyListeners(DatabaseEvent.ADDED, user);
    }

    private boolean isBlocked(User user) {
        String activeUserId = DbUserHandler.INSTANCE.getActiveUserId();
        Banner banner = DatabaseHandler.INSTANCE.getBans(activeUserId);

        if (banner != null) {
            List<String> bannedIds = DatabaseHandler.INSTANCE.getBans(activeUserId).getBanList();

            return !bannedIds.contains(user.getUid());
        } else {
            return false;
        }
    }

    /** Removes user from the local pool of users */
    private void removeUserLocally(User user){
        mUserPool.remove(user);
        notifyListeners(DatabaseEvent.REMOVED, user);
    }

    /** Include user in the matching process */
    public void pushUser(User user) {
        DbUserHandler.INSTANCE.addUserToPool(user);
    }

    /** Remove user from the matching process */
    public void removeUser(User user) {
        DbUserHandler.INSTANCE.removeUserFromPool(user);
    }

    public boolean hasUserInPool(User user) {
        return mUserPool.contains(user);
    }

    public User getFirstInPool() {
        if(mUserPool.size() > 0) {
            return mUserPool.get(0);
        }
        return null;
    }

    public void addPoolClient(DatabaseEvent event, Client<User> callback) {
        clients.get(event).add(callback);
    }

    public void removePoolClient(DatabaseEvent event, Client<User> callback) {
        clients.get(event).remove(callback);
    }

    private void notifyListeners(DatabaseEvent event, User user) {
        List<Client<User>> clients = this.clients.get(event);
        for (Client<User> client : clients) {
            Log.d(TAG, "Supplying listener with a user");
            client.supply(user);
        }
    }

    public void match(Client<Chat> client) {
        Log.d("Users in pool: ", ""+mUserPool.size());
            if (mUserPool.size() > 1) {
                // TODO: Change to a more sofisticated matching algorithm in future. Maybe match depending on personal best in benchpress?
                List<User> matchedUsers = Lists.getFirstElements(mUserPool, 2);

                Lists.forEach(matchedUsers, DbUserHandler.INSTANCE::removeUserFromPool);

                Chat chat = new Chat(matchedUsers.get(0), matchedUsers.get(1));
                client.supply(chat);
                Log.d("CHATMATCHER: NAME: ", chat.getName() + "");
                DbChatHandler.INSTANCE.pushChat(chat);
            }
    }

    private List<String> getUserIdList(){
        List<String> userNameList = new ArrayList<>();
        for(int i=0;i<mUserPool.size();i++){
            userNameList.add(mUserPool.get(i).getUid());
        }
        return userNameList;
    }

    public void clear() {
        mUserPool.clear();
    }
}
