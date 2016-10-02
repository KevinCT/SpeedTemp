package com.zweigbergk.speedswede.adapter;

/**
 * Class may be unnecessary, might have to delete
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zweigbergk.speedswede.core.Chat;

import java.util.ArrayList;

public class ChatListFragmentAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mChatFragments = new ArrayList<>();

    public ChatListFragmentAdapter(FragmentManager fragmentManager, ArrayList<Fragment> chatFragments) {
        super(fragmentManager);
        mChatFragments = chatFragments;
    }

    public ChatListFragmentAdapter(FragmentManager fragmentManager, Fragment chat) {
        super(fragmentManager);
        mChatFragments.add(chat);
    }

    @Override
    public Fragment getItem(int position) {
        return mChatFragments.get(position);
    }

    @Override
    public int getCount() {
        return mChatFragments.size();
    }

    public void addChat(Fragment chat) {
        mChatFragments.add(chat);
    }
}
