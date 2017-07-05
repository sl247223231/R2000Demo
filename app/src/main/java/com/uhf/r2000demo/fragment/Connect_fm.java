package com.uhf.r2000demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;

import com.uhf.r2000demo.R;
import com.uhf.r2000demo.runnable.ConnectThread;
import com.uhf.r2000demo.utils.Configs;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 连接页面：该页面选择手持机平台，串口，给模块上电并打开串口
 */
public class Connect_fm extends BaseFragment {

	@BindView(R.id.device_platform_sp) Spinner mDevicePlatform_Sp;
	@BindView(R.id.serial_command_et) EditText mSerialCommand_et;


	@Override
	protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_connect, container, false);
		ButterKnife.bind(this, rootView);
		return rootView;
	}

	@Override public void initView() {
		mDevicePlatform_Sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
					case 0:
						mSerialCommand_et.setText(R.string.RM_LG43);
						break;
					case 1:
						mSerialCommand_et.setText(R.string.RM_ZD07);
						break;
					case 2:
						mSerialCommand_et.setText(R.string.RM_KT45);
						break;
					case 3:
						mSerialCommand_et.setText(R.string.RM_KT45Q);
						break;
					case 4:
						mSerialCommand_et.setText(R.string.RM_XT07);
						break;
					case 5:
						mSerialCommand_et.setText(R.string.RM_KT80);
						break;
				}
			}

			@Override public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}




	@OnClick(R.id.connect_module_btn) 
	public void onClick() {
		Configs.companyPower = GetPlatform(mDevicePlatform_Sp);
		Configs.serial = mSerialCommand_et.getText().toString().trim();
		new Thread(new ConnectThread(true, getActivity())).start();
	}

	private String GetPlatform(Spinner spinner) {
		switch ((int) spinner.getSelectedItemId()) {
			case 0:
				return "RM_LG43";
			case 1:
				return "RM_ZD07";
			case 2:
				return "RM_KT45";
			case 3:
				return "RM_KT45Q";
			case 4:
				return "RM_XT07";
			case 5:
				return "RM_KT80";
			default:
				return "";
		}
	}

}
