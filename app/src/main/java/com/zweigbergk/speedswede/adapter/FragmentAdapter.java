package com.zweigbergk.speedswede.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.zweigbergk.speedswede.util.collection.List;

public class FragmentAdapter extends FragmentPagerAdapter {

	public static int pos = 0;

	private List<Fragment> myFragments;

	public FragmentAdapter(FragmentManager fm, List<Fragment> myFrags) {
		super(fm);
		myFragments = myFrags;
	}

	@Override
	public Fragment getItem(int position) {

		return myFragments.get(position);

	}

	@Override
	public int getCount() {

		return myFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {

		setPos(position);

		String pageTitle = "";

		switch(pos)
		{
			case 0:
				pageTitle = "page 1";
				break;
			case 1:
				pageTitle = "page 2";
				break;
			case 2:
				pageTitle = "page 3";
				break;
			case 3:
				pageTitle = "page 4";
				break;
			case 4:
				pageTitle = "page 5";
				break;
			case 5:
				pageTitle = "page 6";
				break;
			case 6:
				pageTitle = "page 7";
				break;
		}
		return pageTitle;
	}

	public static int getPos() {
		return pos;
	}

	public static void setPos(int pos) {
		FragmentAdapter.pos = pos;
	}
}