package com.uhf.r2000demo.fragment;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.uhf.constants.Constants.Result;
import com.uhf.r2000demo.R;
import com.uhf.r2000demo.adapter.Inventory_lv_Adapter;
import com.uhf.r2000demo.adapter.Inventory_lv_Adapter.MyClickListener;
import com.uhf.r2000demo.MyApplication;
import com.uhf.r2000demo.utils.Configs;
import com.uhf.r2000demo.view.InventoryDialog;
import com.uhf.structures.DynamicQParms;
import com.uhf.structures.St_Inv_Data;
import com.uhf.structures.TagGroup;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Inventory_fm extends BaseFragment implements OnClickListener {


    @BindView(R.id.state)
    TextView mState;
    @BindView(R.id.tagNum_tv)
    TextView mTagNumTv;
    @BindView(R.id.speed_tv)
    TextView mSpeedTv;
    @BindView(R.id.total_tv)
    TextView mTotalTv;
    @BindView(R.id.totalTime)
    TextView mTotalTime;
    @BindView(R.id.lv_checking)
    ListView mLvChecking;

    private static MyHandler mHandler = null;
    private static int num_256 = 256;
    private SoundPool soundPool;
    private SparseIntArray musicId;
    private List<String[]> mList;
    private long startCheckingTime;
    private int tagTempTotalCount;
    private int tagTotalCount;
    private Inventory_lv_Adapter cLv_Adapter;
    private boolean isChecking = false;
    private static Map<String, String> map;
    private St_Inv_Data[] stInvData;
    public boolean isClear;
    private int cancelOperation = -2;
    public boolean cancel_Task = false;
    private boolean cancel_Exception = false;

    @Override

    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override

    public void initView() {

        tagTotalCount = 0;
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 5);
        musicId = new SparseIntArray();
        musicId.put(1, soundPool.load(context, R.raw.success, 1));
        musicId.put(2, soundPool.load(context, R.raw.fail, 1));
        initData();

    }

    public void initData() {

        map = new HashMap<>();
        stInvData = new St_Inv_Data[1024];
        mList = new ArrayList<>();
        cLv_Adapter = new Inventory_lv_Adapter(mList, mListener, context);
        mLvChecking.setAdapter(cLv_Adapter);
        mHandler = new MyHandler(this);
        mState.setText("   盘点未开始");


    }


    @OnClick({R.id.inventory_start_btn, R.id.inventory_stop_btn, R.id.inventory_clear_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inventory_start_btn:
                cancel_Task = false;
                if (cancelOperation == -2) {
                    mHandler.sendEmptyMessage(Configs.INVENTORY_START * num_256);
                    mHandler.sendEmptyMessage(Configs.INVENTORY_CLEAR * num_256);
                    cancelOperation = -1;
                } else if (cancelOperation == 0) {
                    mHandler.sendEmptyMessage(Configs.INVENTORY_CLEAR * num_256);
                    mHandler.sendEmptyMessage(Configs.INVENTORY_START * num_256);
                } else {
                    MyApplication.getToast().showShortToast(context, "正在停止，请稍后再试");
                }

                break;
            case R.id.inventory_stop_btn:
                cancel_Task = true;
                mHandler.sendEmptyMessage(Configs.INVENTORY_STOP * num_256);
                cancelOperation = MyApplication.getLinkage().CancelOperation();

                break;
            case R.id.inventory_clear_btn:
                mHandler.sendEmptyMessage(Configs.INVENTORY_CLEAR * num_256);
                break;
        }
    }

    private static class MyHandler extends Handler {

        private final WeakReference<Inventory_fm> fm;

        MyHandler(Inventory_fm fragment) {

            fm = new WeakReference<>(fragment);

        }

        @Override

        public void handleMessage(Message msg) {

            final Inventory_fm inventory_fm = fm.get();

            switch ((msg.what / num_256)) {

                case Configs.FLUSH_DATA:

                    if (!inventory_fm.cancel_Task)

                    {
                        inventory_fm.UpdateListDataInsert(msg.arg1, msg.arg2, (msg.what & 0x0f), String.valueOf(msg.obj));
                    }

                    break;

                case Configs.INVENTORY_REFRESH:

                    if (!inventory_fm.cancel_Task)

                    {
                        inventory_fm.UpdateRateCount();
                    }

                    break;

                case Configs.INVENTORY_START:

                    if (!inventory_fm.isChecking) {
                        inventory_fm.isChecking = true;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MyApplication.getLinkage().Inventory(2000, 0, 0);
                            }

                        }).start();


                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                inventory_fm.ReceiveDataThread();
                            }
                        }).start();


                    } else {
                        MyApplication.getToast().showShortToast(inventory_fm.context, "正在盘点");
                    }
                    inventory_fm.mState.setText("   正在盘点");
                    break;

                case Configs.INVENTORY_STOP:

                    inventory_fm.isChecking = false;

                    inventory_fm.mState.setText("   盘点停止");

                    break;

                case Configs.INVENTORY_CLEAR:

                    inventory_fm.isClear = true;

                    inventory_fm.clear();

                    break;

            }

        }


    }


    private void clear() {

        if (!mList.isEmpty()) {

            mList.clear();

        }

        map.clear();

        tagTotalCount = 0;

        mSpeedTv.setText("0");

        mTagNumTv.setText("0");

        mTotalTv.setText("0");

        mTotalTime.setText("0");

    }


    private synchronized void UpdateListDataInsert(int found, int epcLen, int RssiLen, String data) {


        String epcData = data.substring(0, epcLen);

        String tidData = data.substring(epcLen, (data.length() - RssiLen));

        String RSSI = data.substring((data.length() - RssiLen), data.length());

        if (found == 1) {

            for (int i = 0; i < mList.size(); i++) {
                if (epcData.equals(mList.get(i)[0])) {

                    String t = mList.get(i)[3];

                    if (TextUtils.isEmpty(t)) {

                        t = "0";

                    }

                    t = String.valueOf(Integer.valueOf(t) + 1);

                    mList.get(i)[3] = t;

                    break;

                }

            }

            if (!mList.isEmpty()) {

                tagTotalCount++;

                tagTempTotalCount++;

            }

        } else {

            tagTotalCount++;

            tagTempTotalCount++;

            String[] stringBuffer = new String[4];

            stringBuffer[0] = epcData;

            stringBuffer[1] = tidData;

            stringBuffer[2] = RSSI;

            stringBuffer[3] = "1";

            mList.add(stringBuffer);

        }

    }

    private synchronized void UpdateRateCount() {

        long m_lEndTime = System.currentTimeMillis();

        double Rate = Math.ceil((tagTempTotalCount * 1.0) * 1000 / (m_lEndTime - startCheckingTime));

        long total_time_count = m_lEndTime - startCheckingTime;

        soundPool.play(musicId.get(1), 1, 1, 0, 0, 1);

        cLv_Adapter.notifyDataSetChanged();

        mSpeedTv.setText(String.valueOf(Rate));

        mTagNumTv.setText(String.valueOf(mList.size()));

        mTotalTv.setText(String.valueOf(tagTotalCount));

        mTotalTime.setText(String.valueOf(total_time_count));

    }

    private void ReceiveDataThread() {

        try {

            if (!cancel_Task && (!cancel_Exception)) {
                startCheckingTime = System.currentTimeMillis();

                tagTempTotalCount = 0;

                while (!cancel_Task) {

                    if (isClear) {

                        isClear = false;
                        continue;

                    }


                    int num = MyApplication.getLinkage().GetInvData(stInvData, 1);
                    if ((num > 0) && (stInvData != null)) {
                        String strEPCTemp = "";

                        String strTIDTemp = "";

                        String strRSSITemp = "";


                        for (int i = 0; i < num; i++) {

                            if (stInvData[i].nLength > 0 && stInvData[i].nLength < 66) {

                                strEPCTemp = MyApplication.getLinkage().b2hexs(stInvData[i].INV_Data,

                                        stInvData[i].nLength);

                                strRSSITemp = String.valueOf(stInvData[i].RSSI);

                            }

                            if (stInvData[i].tidLength > 0 && stInvData[i].tidLength < 66) {

                                strTIDTemp = MyApplication.getLinkage().b2hexs(stInvData[i].TID_Data,

                                        stInvData[i].tidLength);

                            }

                            if (TextUtils.isEmpty(strEPCTemp)) {
                                continue;
                            }

                            int flag;

                            if (map.containsKey(strEPCTemp)) {

                                flag = 1;

                            } else {

                                flag = 0;

                                map.put(strEPCTemp, strEPCTemp);

                            }
                            String epcAndTid = strEPCTemp + strTIDTemp + strRSSITemp;


                            int whatAndRSSILen = (Configs.FLUSH_DATA * num_256) + strRSSITemp.length();

                            Configs.send(mHandler, whatAndRSSILen, flag, strEPCTemp.length(), epcAndTid);

                        }

                        Configs.send(mHandler, Configs.INVENTORY_REFRESH * num_256);

                    }

                    Thread.sleep(50);

                }

            }

        } catch (Exception e) {

            cancel_Exception = true;

        }

    }

    /**
     * 实现类，响应按钮点击事件
     */
    private MyClickListener mListener = new MyClickListener() {
        @Override
        public void myOnClick(int position, View v) {

            InventoryDialog fragment = (InventoryDialog) getActivity().getFragmentManager().findFragmentByTag(
                    "inventoryDialog");
            if (fragment == null) {
                Bundle bundle = new Bundle();
                bundle.putString("label_epc", mList.get(position)[0]);
                bundle.putString("label_tid", mList.get(position)[1]);
                fragment = InventoryDialog.newInstance(bundle);
            }
            fragment.show(getActivity().getFragmentManager(), "inventoryDialog");
        }
    };

}