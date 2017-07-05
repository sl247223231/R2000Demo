package com.uhf.r2000demo.runnable;


import android.content.Context;
import android.util.Log;

import com.uhf.r2000demo.MainActivity;
import com.uhf.r2000demo.mevents.MsgEvent;
import com.uhf.r2000demo.utils.Configs;
import com.uhf.r2000demo.utils.ModuleManager;
import com.uhf.r2000demo.view.ConnectDialog;

import org.greenrobot.eventbus.EventBus;


public class ConnectThread implements Runnable {
	private boolean flags;

	public ConnectThread(boolean flags, Context context ) {
		super();
		this.flags = flags;
		ConnectDialog aDialog = new ConnectDialog(context);
		if (context != null && (!aDialog.isShowing())) {
			aDialog.show();
		}
	}


	@Override
	public void run() {
		if (flags) {
			EventBus.getDefault().post(new MsgEvent(Configs.CONNECT_WORKING, "正在初始化设备..."));
			int result = ModuleManager.initLibSO();
			Log.i("result","---result----"+result);
			if (result == 0) {
				MainActivity.success_state = true;
				EventBus.getDefault().post(new MsgEvent(Configs.CONNECT_SUCCESS, "设备已连接"));
			} else if (result==-2){
				MainActivity.success_state = false;
				EventBus.getDefault().post(new MsgEvent(Configs.CONNECT_FAILED, "打开串口失败"));
			}else {
				MainActivity.success_state = false;
				EventBus.getDefault().post(new MsgEvent(Configs.CONNECT_FAILED, "设备连接失败"));
			}
		}
		if (!flags) {
			EventBus.getDefault().post(new MsgEvent(Configs.CONNECT_OFFING, "正在断开设备..."));
			MainActivity.success_state = false;
			ModuleManager.destroyLibSO();
			EventBus.getDefault().post(new MsgEvent(Configs.CONNECT_OFF, "断开成功"));
			EventBus.getDefault().post(new MsgEvent(Configs.IS_EXIT, "退出"));
		}
	}


	


}
