package com.uhf.r2000demo.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.uhf.constants.Constants.MemoryBank;
import com.uhf.constants.Constants.Result;
import com.uhf.r2000demo.R;
import com.uhf.r2000demo.adapter.Spinner_Adapter;
import com.uhf.r2000demo.MyApplication;
import com.uhf.r2000demo.mevents.MsgEvent;
import com.uhf.r2000demo.utils.Configs;
import com.uhf.r2000demo.utils.maskController;
import com.uhf.structures.ReadParms;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Rw_fm extends BaseFragment implements OnClickListener {
	@BindView(R.id.chose_area) Spinner mChoseArea;// 选择区域
	@BindView(R.id.start_address) EditText mStartAddress;// 起始地址
	@BindView(R.id.chose_len) EditText mChoseLen;// 选择长度
	@BindView(R.id.visit_word) EditText mVisitWord;// 访问密码
	@BindView(R.id.isMask) CheckBox mIsMask;
	@BindView(R.id.mask) EditText mMask;
	@BindView(R.id.read_et) EditText mReadEt;
	@BindView(R.id.read_epc_et) EditText mReadEpcEt;
	@BindView(R.id.write_et) EditText mWriteEt;

	private static String TAG = "RW_FM";
	private static String READ_OK = "read_ok";
	private static String READ_OK_epc = "READ_OK_epc";

	private static String WRITE_ERROR = "write_error";
	private static String READ_ERROR = "read_error";
	private boolean isRegister;

	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_rw, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void initView() {

		mChoseArea.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.chose_area), context));


		mIsMask.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Configs.flag = 2;
				} else {
					Configs.flag = 0;
				}
			}
		});
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser) {
			EventBus.getDefault().register(this);
			isRegister = true;
		} else {
			if (isRegister) {
				EventBus.getDefault().unregister(this);
				isRegister = false;
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(MsgEvent mEvent) {
		String type = mEvent.getType();
		String data = (String) mEvent.getMsg();
		if (type.equals(TAG)) {
			MyApplication.getToast().showShortToast(context, data);
		} else if (type.equals(READ_OK)) {
			mReadEt.setText(data);
			MyApplication.getToast().showShortToast(context, "读取成功");

		} else if (type.equals(READ_OK_epc)) {
			mReadEpcEt.setText(data);


		} else if (type.equals(READ_ERROR)) {
			mReadEt.setText("");
			mReadEpcEt.setText("");
			if (data.equals("-19993")) {
				MyApplication.getToast().showShortToast(context, "无标签");
			} else {
				MyApplication.getToast().showShortToast(context, "读取标签失败");
			}
		} else if (type.equals(WRITE_ERROR)) {
			if (data.equals("-19993")) {
				MyApplication.getToast().showShortToast(context, "无标签");
			} else {
				MyApplication.getToast().showShortToast(context, "写入标签失败");
			}
		}else if (type.equals(Configs.LABEL_INFO_RW)) {
			mMask.setText(data);
		}
	}

	@OnClick({R.id.setMask, R.id.read, R.id.write})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.read:
				read();
				break;
			case R.id.write:
				write();
				break;
			case R.id.setMask:
				String trim = mMask.getText().toString().trim();
				if (!TextUtils.isEmpty(trim)) {
					byte[] writeByte = maskController.toByteArray(trim);
					int result = maskController.SetMask(MyApplication.getLinkage(), writeByte, trim.length());
					if (result != 0) {
						EventBus.getDefault().post(new MsgEvent(TAG, "设置掩码失败"));
					} else {
						EventBus.getDefault().post(new MsgEvent(TAG, "设置掩码成功"));
					}
				} else {
					EventBus.getDefault().post(new MsgEvent(TAG, "掩码不能为空"));
				}

				break;

			default:
				break;
		}
	}

	/*
	 * 读标签函数
	 */
	private void read() {
		ReadParms result = new ReadParms();
		int bank = 0;
		String pwd = mVisitWord.getText().toString().trim().replace(" ", "");

		if (pwd == null || pwd.equals("")) {
			EventBus.getDefault().post(new MsgEvent(TAG, "访问密码不能为空"));
			Configs.callAlarmAsFailure(context);
			return;
		}
		if (!Configs.IsHex(pwd)) {
			EventBus.getDefault().post(new MsgEvent(TAG, "密码输入错误"));
			Configs.callAlarmAsFailure(context);
			return;
		}
		int accessPassword = Integer.parseInt(mVisitWord.getText().toString().trim().replace(" ", ""), 16);
		int address = Integer.parseInt(mStartAddress.getText().toString().trim().replace(" ", ""));
		int len = Integer.parseInt(mChoseLen.getText().toString().trim().replace(" ", ""));
		int id = (int) mChoseArea.getSelectedItemId();

		switch (id) {
			case 0:
				bank = MemoryBank.EPC.getValue();
				break;
			case 1:
				bank = MemoryBank.TID.getValue();
				break;
			case 2:
				bank = MemoryBank.USER.getValue();
				break;
			case 3:
				bank = MemoryBank.RESERVED.getValue();
				break;
			default:
				break;
		}


		int Read_status = MyApplication.getLinkage().Radio_ReadTag(len, address, bank, accessPassword, result,
				Configs.flag);
		if (Read_status == Result.RFID_STATUS_OK.getValue()) {
			String total_Data = MyApplication.getLinkage().c2hexs(result.ReadData, result.DATAlen + result.EPClen);
			String data = total_Data.substring(0, result.DATAlen * 4);

			String epc = total_Data.substring(result.DATAlen * 4);

			EventBus.getDefault().post(new MsgEvent(READ_OK, data));
			EventBus.getDefault().post(new MsgEvent(READ_OK_epc, epc));
			Configs.callAlarmAsSuccess(context);

		} else {
			EventBus.getDefault().post(new MsgEvent(READ_ERROR, String.valueOf(Read_status)));
			Configs.callAlarmAsFailure(context);
		}
	}

	/*
	 * 写标签函数
	 */

	private void write() {

		int bank = 0;

		String value = mWriteEt.getText().toString().trim().replace(" ", "");
		String textBoxAddressOffset = mStartAddress.getText().toString().trim().replace(" ", "");
		String textBoxReadWriteLength = mChoseLen.getText().toString().trim().replace(" ", "");
		String txtpwd = mVisitWord.getText().toString().trim().replace(" ", "");
		if ((value == null || value.equals("")) || (textBoxAddressOffset == null || textBoxAddressOffset.equals(""))
				|| (textBoxReadWriteLength == null || textBoxReadWriteLength.equals(""))
				|| (txtpwd == null || txtpwd.equals(""))) {
			EventBus.getDefault().post(new MsgEvent(TAG, "值不能为空"));
			Configs.callAlarmAsFailure(context);
			return;
		}
		if (!Configs.IsHex(value) || !Configs.IsHex(textBoxAddressOffset) || !Configs.IsHex(textBoxReadWriteLength)
				|| !Configs.IsHex(txtpwd)) {
			EventBus.getDefault().post(new MsgEvent(TAG, "输入的值有误"));
			Configs.callAlarmAsFailure(context);
			return;
		}
		int offset = Integer.parseInt(textBoxAddressOffset);
		int count = Integer.parseInt(textBoxReadWriteLength);
		int pwd = Integer.parseInt(txtpwd, 16);
		int id = (int) mChoseArea.getSelectedItemId();
		switch (id) {
			case 0:
				bank = MemoryBank.EPC.getValue();
				break;
			case 1:
				bank = MemoryBank.TID.getValue();
				break;
			case 2:
				bank = MemoryBank.USER.getValue();
				break;
			case 3:
				bank = MemoryBank.RESERVED.getValue();
				break;
			default:
				break;
		}
		if (id == 1) {
			EventBus.getDefault().post(new MsgEvent(TAG, "TID不可写入"));
			Configs.callAlarmAsFailure(context);
			return;
		}

		if ((value.length() % 4) != 0) {
			EventBus.getDefault().post(new MsgEvent(TAG, "写入长度有误"));
			Configs.callAlarmAsFailure(context);
			return;
		}
		if ((id == 0 && offset == 0)) {
			EventBus.getDefault().post(new MsgEvent(TAG, "起始地址有误"));
			Configs.callAlarmAsFailure(context);
			return;
		}
		if ((value.length() / 4) != count) {
			EventBus.getDefault().post(new MsgEvent(TAG, "写入长度有误"));
			Configs.callAlarmAsFailure(context);
			return;
		}
		char[] WriteText = MyApplication.getLinkage().s2char(value);
		int result = MyApplication.getLinkage().Radio_WriteTag(count, offset, bank, pwd, WriteText, Configs.flag);
		if (result == Result.RFID_STATUS_OK.getValue()) {
			EventBus.getDefault().post(new MsgEvent(TAG, "写入成功"));
			Configs.callAlarmAsSuccess(context);
		} else {
			EventBus.getDefault().post(new MsgEvent(WRITE_ERROR, "" + result));
			Configs.callAlarmAsFailure(context);
		}
	}


}
