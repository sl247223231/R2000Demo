package com.uhf.r2000demo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class Main_vp_adapter extends FragmentPagerAdapter {
	private List<Fragment> mList;


	public Main_vp_adapter(FragmentManager fm, List<Fragment> list) {
		super(fm);
		this.mList = list;
	}

	@Override
	public Fragment getItem(int position) {
		return mList.get(position);
	}

	@Override
	public int getCount() {
		return mList.size();
	}


}
