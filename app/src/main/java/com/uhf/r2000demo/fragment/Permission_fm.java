package com.uhf.r2000demo.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.uhf.constants.Constants.Result;
import com.uhf.r2000demo.MainActivity;
import com.uhf.r2000demo.R;
import com.uhf.r2000demo.adapter.Spinner_Adapter;
import com.uhf.r2000demo.MyApplication;
import com.uhf.r2000demo.mevents.MsgEvent;
import com.uhf.r2000demo.utils.Configs;
import com.uhf.structures.ReadParms;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Permission_fm extends BaseFragment  {
	private static String TAG = "PERMISSION_FM";
	@BindView(R.id.destroy_power) TextView mDestroyPower;// 销毁密码
	@BindView(R.id.access_password) TextView mAccessPassword;// 访问密码
	@BindView(R.id.input_password) EditText mInputPassword;// 再次输入密码
	@BindView(R.id.password_mode) Spinner mPasswordMode;// 密码类型
	@BindView(R.id.new_password) EditText mNewPassword;// 新密码


	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_permission, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void initView() {
		mPasswordMode.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.password_mode), context));
	}


	@OnClick({R.id.read_password, R.id.change_password})
	public void onClick(View view) {

		switch (view.getId()) {
			case R.id.read_password:
				readPassword();
				break;
			case R.id.change_password:
				changePassword();
				break;
			default:
				break;
		}
	}

	/**
	 * readPassword @Description: 读取密码 return void @throws
	 */
	private void readPassword() {
		if (!MainActivity.success_state) {
			return;
		}
		do {
			String value;

			String pwd = mInputPassword.getText().toString().trim().replace(" ", "");

			if (pwd == null || TextUtils.isEmpty(pwd)) {
				MyApplication.getToast().showShortToast(context, "访问密码不能为空");
				Configs.callAlarmAsFailure(context);
				return;
			}
			if (!Configs.IsHex(pwd)) {
				MyApplication.getToast().showShortToast(context, "授权密码格式不对");
				Configs.callAlarmAsFailure(context);
				return;
			}

			long cs_InputPassword = Long.parseLong(pwd);
			int cTemp;
			ReadParms rp = new ReadParms();
			cTemp = MyApplication.getLinkage().Radio_ReadTag(4, 0, 0, cs_InputPassword, rp, Configs.flag);
			if (cTemp == Result.RFID_STATUS_OK.getValue()) {
				value = MyApplication.getLinkage().c2hexs(rp.ReadData, rp.ReadData.length);
				if (value.length() == 16) {
					EventBus.getDefault().post(new MsgEvent(TAG, "获取密码成功"));
					String cs_DestroyPassword = value.substring(0, 8);
					String cs_AccessPassword = value.substring(8, 16);
					mAccessPassword.setText(cs_AccessPassword);
					mDestroyPower.setText(cs_DestroyPassword);
					Configs.callAlarmAsSuccess(context);
				} else {
					EventBus.getDefault().post(new MsgEvent(TAG, "获取密码失败"));
					Configs.callAlarmAsFailure(context);
				}
			} else {
				EventBus.getDefault().post(new MsgEvent(TAG, cTemp + "常规错误"));
			}
		} while (false);

	}

	/**
	 * readPassword @Description: 修改密码 @参 数: @return void @throws
	 */
	private void changePassword() {
		if (!MainActivity.success_state) {
			return;
		}
		int status = Result.RFID_STATUS_OK.getValue();
		int ui_Offset = (int) mPasswordMode.getSelectedItemId();
		String cs_ReadyToChangePassword = mNewPassword.getText().toString();
		String powerPwd = "00000000";
		do {
			if (TextUtils.isEmpty(cs_ReadyToChangePassword)) {
				EventBus.getDefault().post(new MsgEvent(TAG, "值不能为空"));
				Configs.callAlarmAsFailure(context);
				break;
			}
			if (!Configs.IsHex(cs_ReadyToChangePassword) || !Configs.IsHex(powerPwd)) {
				EventBus.getDefault().post(new MsgEvent(TAG, "密码格式有误"));
				Configs.callAlarmAsFailure(context);
				break;
			}
			if ((cs_ReadyToChangePassword.length() % 4) != 0 || cs_ReadyToChangePassword.length() / 4 != 2) {
				Result.ACTION_RFID_18K6C_TAG_WRITE_ERROR_DATA.getValue();
				EventBus.getDefault().post(new MsgEvent(TAG, "密码格式或长度错误"));
				Configs.callAlarmAsFailure(context);
				break;
			}
			int cs_InputPassword = Integer.parseInt(powerPwd);
			char[] writeData = MyApplication.getLinkage().s2char(cs_ReadyToChangePassword);

			if (Result.ACTION_RFID_18K6C_TAG_WRITE_ERROR_DATA.getValue() == status) {
				break;
			}
			status = MyApplication.getLinkage().Radio_WriteTag(2, (ui_Offset * 2), 0, cs_InputPassword, writeData,
					Configs.flag);
			if (Result.RFID_STATUS_OK.getValue() == status) {
				if (ui_Offset == 0) {
					EventBus.getDefault().post(new MsgEvent(TAG, "修改销毁密码成功"));
				} else {
					EventBus.getDefault().post(new MsgEvent(TAG, "修改访问密码成功"));
				}
				Configs.callAlarmAsSuccess(context);
			} else {
				if (ui_Offset == 0) {
					EventBus.getDefault().post(new MsgEvent(TAG, status + "常规错误"));
				} else {
					EventBus.getDefault().post(new MsgEvent(TAG, status + "常规错误"));
				}
				Configs.callAlarmAsFailure(context);
			}
		} while (false);
	}


	@Override
	public void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}


}
