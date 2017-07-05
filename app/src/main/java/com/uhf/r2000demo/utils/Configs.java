package com.uhf.r2000demo.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import com.uhf.r2000demo.R;


public class Configs {


	//MainActivity
	public static final int CONNECT = 0;// 连接页面页码
	public static final int INVENTORY = 1;// 盘点页面页码
	public static final int SETTING = 2;// 设置页面页码
	public static final int POWER = 3;// 功率页面页码
	public static final int RW = 4;// 读写页面页码
	public static final int PERMISSION = 5;// 权限页面页码
	public static final int LOCK = 6;// 锁定页面页码

	//连接
	public static String companyPower;
	public static String serial;


	public static final String CONNECT_OFF = "connect_off";// 断开连接
	public static final String IS_EXIT = "is_exit";// 退出
	public static final String UN_CONNECT = "un_connect";// 退出
	public static final String CONNECT_SUCCESS = "connect_success";// 初始化成功
	public static final String CONNECT_FAILED = "connect_failed";// 初始化失败
	public static final String VERSION = "version";// 初始化失败


	// 提示框
	public static final String CONNECT_OFFING = "connect_offing";// 正在断开连接
	public static final String CONNECT_WORKING = "connect_working";// 正在初始化设备

	//盘点
	public static final int FLUSH_DATA = 6;// 刷新新数据
	public static final int INVENTORY_REFRESH = 1;// 提示刷新
	public static final int INVENTORY_START = 222;// 盘点开始
	public static final int INVENTORY_STOP = 333;// 盘点停止
	public static final int INVENTORY_CLEAR = 555;// 盘点数据清除
	
	//频率
	public static final String SET_FREQUENCY_AREA = "set_frequency_area";// 设置频率区域


	//功率
	public static final String SAVE_POWER_VALUE = "save_power_value";// 功率值保存
	


	//锁定
	public static int flag = 0;

	//InventoryDialog
	public static final String TO_READ_WRITE = "to_read_write";
	public static final String TO_LOCK_KILL = "to_lock_kill";
	public static final String LABEL_INFO_RW = "label_info_rw";
	public static final String LABEL_INFO_LK = "label_info_lk";







	public static void send(Handler handler, int what) {
		Message msg = new Message();
		msg.what = what;
		handler.sendMessage(msg);
	}

	public static void send(Handler handler, int what, int arg1, int arg2, Object obj) {
		Message msg = new Message();
		msg.what = what;
		msg.arg1 = arg1;
		msg.arg2 = arg2;
		msg.obj = obj;
		handler.sendMessage(msg);
	}

	/**
	 * 判断十六进制
	 */
	public static boolean IsHex(String str) {
		boolean b = false;

		char[] c = str.toUpperCase().toCharArray();
		for (char aC : c) {
			if ((aC >= '0' && aC <= '9') || (aC >= 'A' && aC <= 'F')) {
				b = true;
			} else {
				b = false;
				break;
			}
		}
		return b;
	}


	/**
	 * 失败提示音
	 *
	 * @param context
	 */
	public static void callAlarmAsFailure(Context context) {
		if (context == null) {
			return;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE); // 播放提示音
		int max = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
		int current = audioManager
				.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		MediaPlayer player = MediaPlayer.create(context, R.raw.fail);
		player.setVolume((float) current / (float) max, (float) current
				/ (float) max); // 设置提示音量
		player.start();// 播放提示音
	}

	/**
	 * 成功提示音
	 *
	 * @param context
	 */
	public static void callAlarmAsSuccess(Context context) {
		if (context == null) {
			return;
		}
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE); // 播放提示音
		int max = audioManager
				.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);
		int current = audioManager
				.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
		MediaPlayer player = MediaPlayer.create(context, R.raw.success);
		player.setVolume((float) current / (float) max, (float) current
				/ (float) max); // 设置提示音量
		player.start();// 播放提示音
	}


}
