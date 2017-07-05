package com.uhf.r2000demo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.uhf.r2000demo.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Inventory_lv_Adapter extends BaseAdapter {
	private List<String[]> mList;
	private Context mContext;
	private MyClickListener mListener;

	public Inventory_lv_Adapter(List<String[]> mList,MyClickListener listener, Context mContext) {
		super();
		this.mList = mList;
		this.mContext = mContext;
		this.mListener=listener;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_lv_inventory, parent, false);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		}
		vh = (ViewHolder) convertView.getTag();
		vh.mCodeName.setText(mList.get(position)[0]);
		vh.mCodeNameTid.setText(mList.get(position)[1]);
		vh.mCodeRssi.setText(mList.get(position)[2]);
		vh.mCodeNum.setText(mList.get(position)[3]);
		vh.mCodeName.setOnClickListener(mListener);
		vh.mCodeName.setTag(position);
		vh.mCodeNameTid.setOnClickListener(mListener);
		vh.mCodeNameTid.setTag(position);
		return convertView;
	}


	  class ViewHolder {
		@BindView(R.id.code_name) TextView mCodeName;
		@BindView(R.id.code_name_tid) TextView mCodeNameTid;
		@BindView(R.id.code_rssi) TextView mCodeRssi;
		@BindView(R.id.code_num) TextView mCodeNum;

		ViewHolder(View view) {
			ButterKnife.bind(this, view);
		}
	}

	/**
	 * 用于回调的抽象类
	 */
	public static abstract class MyClickListener implements View.OnClickListener {
		/**
		 * 基类的onClick方法
		 */
		@Override public void onClick(View v) {
			myOnClick((Integer) v.getTag(), v);
		}

		public abstract void myOnClick(int position, View v);
	}

	
}
