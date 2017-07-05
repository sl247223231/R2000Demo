package com.uhf.r2000demo.view;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.uhf.r2000demo.R;
import com.uhf.r2000demo.mevents.MsgEvent;
import com.uhf.r2000demo.utils.Configs;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InventoryDialog extends DialogFragment {


	@BindView(R.id.label_epc) TextView mLabelEpc;
	@BindView(R.id.label_tid) TextView mLabelTid;
	@BindView(R.id.label_tid_title) TextView mLabelTidTitle;
	private String epcData;
	private String tidData;

	@Nullable @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().setCanceledOnTouchOutside(false);
		View mView = inflater.inflate(R.layout.inventory_dialog, container, false);
		Bundle bundle = getArguments();
		epcData = bundle.getString("label_epc");
		tidData = bundle.getString("label_tid");
		ButterKnife.bind(this, mView);
		return mView;
	}


	public static InventoryDialog newInstance(Bundle bundle) {
		InventoryDialog mDialog = new InventoryDialog();
		mDialog.setArguments(bundle);
		return mDialog;
	}

	@Override public void onStart() {
		super.onStart();
		final Window window = getDialog().getWindow();
		assert window != null;
		WindowManager.LayoutParams wlp = window.getAttributes();
		wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
		wlp.height = 300;
		window.setAttributes(wlp);
		if (TextUtils.isEmpty(tidData)){
			mLabelTidTitle.setVisibility(View.INVISIBLE);
		}else {
			mLabelTidTitle.setVisibility(View.VISIBLE);
		}
		mLabelEpc.setText(epcData);
		mLabelTid.setText(tidData);
	}


	@OnClick({R.id.to_read_write, R.id.to_lock_kill, R.id.cancel})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.to_read_write:
				EventBus.getDefault().post(new MsgEvent(Configs.TO_READ_WRITE, "3"));
				EventBus.getDefault().post(new MsgEvent(Configs.LABEL_INFO_RW, epcData));
				dismiss();
				break;
			case R.id.to_lock_kill:
				EventBus.getDefault().post(new MsgEvent(Configs.TO_LOCK_KILL, "5"));
				EventBus.getDefault().post(new MsgEvent(Configs.LABEL_INFO_LK, epcData));
				dismiss();
				break;
			case R.id.cancel:
				dismiss();
				break;
		}
	}
}
