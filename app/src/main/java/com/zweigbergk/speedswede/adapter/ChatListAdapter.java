package com.zweigbergk.speedswede.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ChatListAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mChatFragments = new ArrayList<>();

    public ChatListAdapter(FragmentManager fragmentManager, ArrayList<Fragment> chatFragments) {
        super(fragmentManager);
        mChatFragments = chatFragments;
    }

    public ChatListAdapter(FragmentManager fragmentManager, Fragment fragment) {
        super(fragmentManager);
        mChatFragments.add(fragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mChatFragments.get(position);
    }

    @Override
    public int getCount() {
        return mChatFragments.size();
    }

    public void addFragment(Fragment fragment) {
        mChatFragments.add(fragment);
    }
}
