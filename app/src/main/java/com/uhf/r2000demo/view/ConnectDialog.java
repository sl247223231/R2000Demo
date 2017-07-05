package com.uhf.r2000demo.view;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.uhf.r2000demo.R;
import com.uhf.r2000demo.mevents.MsgEvent;
import com.uhf.r2000demo.utils.Configs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ConnectDialog extends Dialog {
	private TextView working;

	public ConnectDialog(Context context) {
		super(context);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.connectdialog);
		working = (TextView) findViewById(R.id.working);
		EventBus.getDefault().register(this);

	}


	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(MsgEvent mEvent) {
		String type = mEvent.getType();
		String data = (String) mEvent.getMsg();
		switch (type) {
			case Configs.CONNECT_FAILED:
				// 初始化设备失败
				working.setText(data);
				dismiss();
				break;
			case Configs.CONNECT_WORKING:
				// 正在初始化设备
				working.setText(data);
				break;
			case Configs.CONNECT_SUCCESS:
				// 初始化设备成功
				working.setText(data);
				dismiss();
				break;
			case Configs.CONNECT_OFFING:
				// 正在断开设备连接
				working.setText(data);
				break;
			case Configs.CONNECT_OFF:
				// 断开连接成功
				working.setText(data);
				dismiss();
				break;

		}
	}

	

	@Override
	public void dismiss() {
		super.dismiss();
		if (EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().unregister(this);
		}
	}

}
