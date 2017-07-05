package com.uhf.r2000demo.view;

import android.content.Context;
import android.widget.Toast;

public class BaseToast {
	private Toast toast = null;

	/**
	 * Description: 显示短时间Toast信息
	 *
	 * @param context
	 *            上下文
	 * @param msg
	 *            显示的消息内容
	 */
	public void showShortToast(Context context, String msg) {
		if (toast == null) {
			toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
			toast.show();
		} else {
			toast.setText(msg);
			toast.show();
		}

	}

}
