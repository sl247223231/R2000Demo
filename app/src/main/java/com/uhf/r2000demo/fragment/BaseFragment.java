package com.uhf.r2000demo.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class BaseFragment extends Fragment {

	// 根部view
	private View rootView;
	protected Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (rootView == null) {
			rootView = initLayout(inflater, container, savedInstanceState);
		}
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		((ViewGroup) rootView.getParent()).removeView(rootView);
	}

	/**
	 * 子类实现初始化View操作
	 */
	protected abstract View initLayout(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState);

	/**
	 * 
	 *子类控件实现实例 
	 */
	public abstract void initView();


}
