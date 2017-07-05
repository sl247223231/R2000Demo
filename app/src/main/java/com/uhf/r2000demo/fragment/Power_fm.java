package com.uhf.r2000demo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.uhf.constants.Constants;
import com.uhf.constants.Constants.Result;
import com.uhf.r2000demo.MainActivity;
import com.uhf.r2000demo.R;
import com.uhf.r2000demo.adapter.Spinner_Adapter;
import com.uhf.r2000demo.MyApplication;
import com.uhf.r2000demo.mevents.MsgEvent;
import com.uhf.r2000demo.utils.Configs;
import com.uhf.structures.Rfid_Value;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Power_fm extends BaseFragment implements OnSeekBarChangeListener {
    @BindView(R.id.chose_power)
    SeekBar mChosePower;
    @BindView(R.id.seekbar_value)
    TextView mSeekBarValue;
    @BindView(R.id.current_power)
    TextView mCurrentPower;
    @BindView(R.id.power_link)
    Spinner mPowerLink;
    @BindView(R.id.frequency_area)
    Spinner frequencyArea;// 区域选择器

    private int iu32;

    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_power, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        mPowerLink.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.power_link), context));
        frequencyArea.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.frequency_area), context));
        mChosePower.setMax(20);
        mChosePower.setOnSeekBarChangeListener(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        getDefaultConfig();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent mEvent) {
        String type = mEvent.getType();
        String data = (String) mEvent.getMsg();
        if (type.equals(Configs.SAVE_POWER_VALUE)) {
            mSeekBarValue.setText(String.valueOf(iu32));
            mCurrentPower.setText(String.valueOf(iu32));
            MyApplication.getToast().showShortToast(context, data);
            Configs.callAlarmAsSuccess(context);
        } else if (type.equals(Configs.SET_FREQUENCY_AREA)) {
            MyApplication.getToast().showShortToast(context, data);
            Configs.callAlarmAsSuccess(context);
        }
    }

    /**
     * getDefaultConfig @Description: 获取链接，天线功率的值 @参 数: @return void @throws
     */
    public void getDefaultConfig() {
        if (!MainActivity.success_state) {
            return;
        }
        do {

            // 频段
            Rfid_Value rfid_value = new Rfid_Value();
            int result = MyApplication.getLinkage().Radio_MacGetRegion(rfid_value);
            if (result == Result.RFID_STATUS_OK.getValue()) {
                if (rfid_value.value == 0) {
                    frequencyArea.setSelection(0);
                } else if (rfid_value.value == 1) {
                    frequencyArea.setSelection(1);
                } else if (rfid_value.value == 2) {
                    frequencyArea.setSelection(2);
                } else if (rfid_value.value == 3) {
                    frequencyArea.setSelection(3);
                }
            } else {
                break;
            }

            // 链接
            int status = MyApplication.getLinkage().Radio_GetCurrentLinkProfile(rfid_value);
            if (status == Result.RFID_STATUS_OK.getValue()) {
                if (rfid_value.value == 0) {
                    mPowerLink.setSelection(0);
                } else if (rfid_value.value == 1) {
                    mPowerLink.setSelection(1);
                } else if (rfid_value.value == 2) {
                    mPowerLink.setSelection(2);
                } else if (rfid_value.value == 3) {
                    mPowerLink.setSelection(3);
                }
            } else {
                break;
            }
            // 天线功率
            status = MyApplication.getLinkage().Radio_GetAntennaPower(rfid_value);
            if (status == Result.RFID_STATUS_OK.getValue()) {
                mChosePower.setProgress((rfid_value.value) / 10 - 10);
                mSeekBarValue.setText(String.valueOf(mChosePower.getProgress() + 10));
                mCurrentPower.setText(String.valueOf(mChosePower.getProgress() + 10));
            }
        } while (false);
    }

    @OnClick({R.id.set_power, R.id.set_link, R.id.set_frequency_area})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_power:
                if (!MainActivity.success_state) {
                    return;
                }
                int status;
                iu32 = mChosePower.getProgress() + 10;
                status = MyApplication.getLinkage().Radio_SetAntennaPower(iu32 * 10);

                if (status == Result.RFID_STATUS_OK.getValue()) {

                    EventBus.getDefault().post(new MsgEvent(Configs.SAVE_POWER_VALUE, "功率修改成功"));
                } else {

                    MyApplication.getToast().showShortToast(context, "常规错误");
                    Configs.callAlarmAsFailure(context);
                }

                break;
            case R.id.set_link:
                if (!MainActivity.success_state) {
                    return;
                }
                int status1 = MyApplication.getLinkage().Radio_SetCurrentLinkProfile((int) mPowerLink.getSelectedItemId());

                if (status1 == Result.RFID_STATUS_OK.getValue()) {
                    MyApplication.getToast().showShortToast(context, "设置链接成功");
                    Configs.callAlarmAsSuccess(context);

                } else {
                    MyApplication.getToast().showShortToast(context, "常规错误");
                    Configs.callAlarmAsFailure(context);
                }
                break;
            case R.id.set_frequency_area:
                Constants.RFID_18K6C_COUNTRY_REGION region = null;
                int uRegionIndex = (int) frequencyArea.getSelectedItemId();
                switch (uRegionIndex) {
                    case 0:
                        region = Constants.RFID_18K6C_COUNTRY_REGION.China840_845;
                        break;
                    case 1:
                        region = Constants.RFID_18K6C_COUNTRY_REGION.China920_925;
                        break;
                    case 2:
                        region = Constants.RFID_18K6C_COUNTRY_REGION.Open_Area902_928;
                        break;
                    case 3:
                        region = Constants.RFID_18K6C_COUNTRY_REGION.Europe_Area;
                        break;
                }
                int result = MyApplication.getLinkage()
                        .Radio_MacSetRegion(region);
                if (result == Result.RFID_STATUS_OK.getValue()) {
                    MyApplication.getToast().showShortToast(context, "设置区域成功");
                    Configs.callAlarmAsSuccess(context);
                } else {
                    MyApplication.getToast().showShortToast(context, "常规错误");
                    Configs.callAlarmAsFailure(context);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mSeekBarValue.setText("" + (progress + 10));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


}
