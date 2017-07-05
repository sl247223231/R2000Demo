package com.uhf.r2000demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.uhf.r2000demo.MainActivity;
import com.uhf.r2000demo.R;
import com.uhf.r2000demo.adapter.Spinner_Adapter;
import com.uhf.r2000demo.MyApplication;
import com.uhf.r2000demo.utils.Configs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.uhf.constants.Constants.RFID_18K6C_TAG_MEM_PERM;
import static com.uhf.constants.Constants.RFID_18K6C_TAG_PWD_PERM;
import static com.uhf.constants.Constants.Result;


public class Locking_fm extends BaseFragment{
	@BindView(R.id.access_password_locking) Spinner mAccessPasswordLocking;
	@BindView(R.id.destroy_password_locking) Spinner mDestroyPasswordLocking;
	@BindView(R.id.EPC_locking) Spinner mEPCLocking;
	@BindView(R.id.TID_locking) Spinner mTIDLocking;
	@BindView(R.id.USER_locking) Spinner mUSERLocking;
	@BindView(R.id.pss_locking) EditText mPssLocking;


	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_locking, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void initView() {

		mAccessPasswordLocking
				.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.password_locking), context));
		mDestroyPasswordLocking
				.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.password_locking), context));
		mEPCLocking.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.rw_locking), context));
		mTIDLocking.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.rw_locking), context));
		mUSERLocking.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.rw_locking), context));

	}

	@OnClick(R.id.execute_btn)
	public void onClick() {
		if (!MainActivity.success_state) {
			return;
		}
		long accesspassword = 0;
		String pwd = mPssLocking.getText().toString().trim().replace(" ", "");

		if (pwd == null || pwd.equals("")) {
			MyApplication.getToast().showShortToast(context, "访问密码不能为空");
			Configs.callAlarmAsFailure(context);
			return;
		}
		if (!Configs.IsHex(pwd)) {
			MyApplication.getToast().showShortToast(context, "密码输入错误");
			Configs.callAlarmAsFailure(context);
			return;
		}
		accesspassword = Long.parseLong(pwd, 16);
		int result = MyApplication.getLinkage().Radio_LockTag(accesspassword,
				GetPasswordChoicePermission(mAccessPasswordLocking),
				GetPasswordChoicePermission(mDestroyPasswordLocking), GetMemoryChoicePermission(mEPCLocking),
				GetMemoryChoicePermission(mTIDLocking), GetMemoryChoicePermission(mUSERLocking), Configs.flag);

		if (result == Result.RFID_STATUS_OK.getValue()) {
			MyApplication.getToast().showShortToast(context, "执行成功");
			Configs.callAlarmAsSuccess(context);
		} else {
			MyApplication.getToast().showShortToast(context, result + "常规错误");
			Configs.callAlarmAsFailure(context);
		}

	}

	private int GetPasswordChoicePermission(Spinner objComb) {
		switch ((int) objComb.getSelectedItemId()) {
			case 0:
				return RFID_18K6C_TAG_PWD_PERM.NO_CHANGE.getValue();
			case 1:
				return RFID_18K6C_TAG_PWD_PERM.ACCESSIBLE.getValue();
			case 2:
				return RFID_18K6C_TAG_PWD_PERM.ALWAYS_ACCESSIBLE.getValue();
			case 3:
				return RFID_18K6C_TAG_PWD_PERM.SECURED_ACCESSIBLE.getValue();
			case 4:
				return RFID_18K6C_TAG_PWD_PERM.ALWAYS_NOT_ACCESSIBLE.getValue();
			default:
				return RFID_18K6C_TAG_PWD_PERM.NO_CHANGE.getValue();
		}
	}

	private int GetMemoryChoicePermission(Spinner objComb) {
		switch ((int) objComb.getSelectedItemId()) {
			case 0:
				return RFID_18K6C_TAG_MEM_PERM.NO_CHANGE.getValue();
			case 1:
				return RFID_18K6C_TAG_MEM_PERM.WRITEABLE.getValue();
			case 2:
				return RFID_18K6C_TAG_MEM_PERM.ALWAYS_WRITEABLE.getValue();
			case 3:
				return RFID_18K6C_TAG_MEM_PERM.SECURED_WRITEABLE.getValue();
			case 4:
				return RFID_18K6C_TAG_MEM_PERM.ALWAYS_NOT_WRITEABLE.getValue();
			default:
				return RFID_18K6C_TAG_MEM_PERM.NO_CHANGE.getValue();
		}
	}

}
