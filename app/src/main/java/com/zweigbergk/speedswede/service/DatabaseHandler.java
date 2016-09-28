package com.zweigbergk.speedswede.service;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.zweigbergk.speedswede.core.Message;
import com.zweigbergk.speedswede.core.User;
import com.zweigbergk.speedswede.core.UserProfile;
import com.zweigbergk.speedswede.service.eventListener.MessageListener;
import com.zweigbergk.speedswede.service.eventListener.UserPoolListener;
import com.zweigbergk.speedswede.util.Client;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public enum DatabaseHandler {
    INSTANCE;

    DatabaseHandler() { }

    public static final String CONVERSATION = "conversation";
    public static final String CHATS = "chats";
    public static final String POOL = "pool";
    public static final String USER_NAME = "displayName";
    public static final String UID = "uid";
    public static final String BANS = "bans";
    public static final String STRIKES = "strikes";
    private User mLoggedInUser;
   // private HashMap<String,List<User>> banMap;
    private List<User> mBanList = new ArrayList<>();

    private DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();

    // TODO: Implement fetchMatchingPool and registerPoolListener instead of getMatchingPool /Andreas
    private DatabaseReference fetchMatchingPool() {
        return mDatabaseReference.child(POOL);
    }

    public void registerPoolListener(Client<DataChange<User>> client) {
        DatabaseReference poolReference = fetchMatchingPool();
        poolReference.keepSynced(true);

        poolReference.addChildEventListener(new UserPoolListener(client));
    }

    public void getMatchingPool(Client<User> client) {
        List<String> userStrings = new LinkedList<>();

        mDatabaseReference.child(POOL).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = new UserProfile((String) dataSnapshot.child(USER_NAME).getValue(), (String) dataSnapshot.child(UID).getValue());
                client.supply(user);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addUserToPool(User user) {
        mDatabaseReference.child(POOL).child(user.getUid()).setValue(user);
    }


    public void removeUserFromPool(User user) {
        mDatabaseReference.child(POOL).child(user.getUid()).setValue(null);
    }

    public String getActiveUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                String uid = profile.getUid();
                if (uid != null)
                    return uid;
            }
        }

        return null;
    }

    public User getLoggedInUser() {
        if (mLoggedInUser == null) {
            String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
            String uid = getActiveUserId();
            mLoggedInUser = new UserProfile(name, uid);
        }

        return mLoggedInUser;
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void registerConversationListener(String chatUid, Client<DataChange<Message>> client) {
        DatabaseReference conversationReference = fetchChatConversationByUid(chatUid);
        conversationReference.keepSynced(true);

        conversationReference.addChildEventListener(new MessageListener(client));
    }

    private DatabaseReference fetchChatConversationByUid(String chatUid) {
        return mDatabaseReference.child(CHATS).child(chatUid).child(CONVERSATION);
    }

    public void postMessageToChat(String chatId, Message message) {
        mDatabaseReference.child(CHATS).child(chatId).child(CONVERSATION).push().setValue(message);
    }

    public void banUser(String uID, User stranger ){
        List<User> banList = getBanList(uID);
        banList.add(stranger);
        mDatabaseReference.child(BANS).child(uID).setValue(banList);
        mDatabaseReference.child("Global"+BANS).push().setValue(stranger.getUid());
    }


    public List<User> getBanList(String uID){
        mDatabaseReference.child(BANS).child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mBanList=(List<User>) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mBanList= new ArrayList<>();

            }

        });
        return mBanList;

    }

    public void removeBan(String uID, User stranger){
        List<User> banList = getBanList(uID);
        banList.remove(stranger);
        mDatabaseReference.child(BANS).child(uID).setValue(banList);

    }



}
