package com.uhf.r2000demo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.uhf.r2000demo.adapter.Main_vp_adapter;
import com.uhf.r2000demo.fragment.Connect_fm;
import com.uhf.r2000demo.fragment.Inventory_fm;
import com.uhf.r2000demo.fragment.Locking_fm;
import com.uhf.r2000demo.fragment.Permission_fm;
import com.uhf.r2000demo.fragment.Power_fm;
import com.uhf.r2000demo.fragment.Rw_fm;
import com.uhf.r2000demo.fragment.Setting_fm;
import com.uhf.r2000demo.runnable.ConnectThread;
import com.uhf.r2000demo.mevents.MsgEvent;
import com.uhf.r2000demo.utils.Configs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener
{

    public static boolean success_state = false;// 模块初始化标记

    @BindView(R.id.deviceState)
    TextView mDeviceState;//模块连接状态显示文本
    @BindView(R.id.connect_rb)
    RadioButton mConnectRb;//连接
    @BindView(R.id.inventory_rb)
    RadioButton mInventoryRb;//盘点
    @BindView(R.id.setting_rb)
    RadioButton mSettingRb;//设置
    @BindView(R.id.power_rb)
    RadioButton mPowerRb;//功率
    @BindView(R.id.rw_rb)
    RadioButton mRwRb;//读写
    @BindView(R.id.permission_rb)
    RadioButton mPermissionRb;//权限
    @BindView(R.id.locking_rb)
    RadioButton mLockingRb;//锁定标签
    @BindView(R.id.main_vp)
    ViewPager mMainVp;//容器


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
    }


    private void initData()
    {
        EventBus.getDefault().register(this);
        mDeviceState.setText("设备未连接");
        // 填充ViewPager容器
        List<Fragment> list = new ArrayList<>();

        list.add(new Connect_fm());
        list.add(new Inventory_fm());
        list.add(new Setting_fm());
        list.add(new Power_fm());
        list.add(new Rw_fm());
        list.add(new Permission_fm());
        list.add(new Locking_fm());
        mMainVp.setAdapter(new Main_vp_adapter(getSupportFragmentManager(), list));
        mMainVp.addOnPageChangeListener(this);
        mMainVp.setOffscreenPageLimit(6);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MsgEvent mEvent)
    {
        String type = mEvent.getType();
        String msg = (String) mEvent.getMsg();
        switch (type)
        {
            case Configs.CONNECT_SUCCESS:
                mDeviceState.setText(msg);
                break;
            case Configs.CONNECT_FAILED:
                mDeviceState.setText(msg);
                break;
            case Configs.CONNECT_OFF:
                mDeviceState.setText(msg);
                break;
            case Configs.IS_EXIT:
                finish();
                Process.killProcess(Process.myPid());
                break;
            case Configs.UN_CONNECT:
                new Thread(new ConnectThread(false, MainActivity.this)).start();
                break;
            case Configs.TO_READ_WRITE:
                mMainVp.setCurrentItem(Configs.RW, false);
                break;
            case Configs.TO_LOCK_KILL:
                mMainVp.setCurrentItem(Configs.LOCK, false);
                break;
        }

    }


    @Override
    public void onPageScrollStateChanged(int arg0)
    {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2)
    {
    }


    public void onPageSelected(int id)
    {

        switch (id)
        {
            case Configs.CONNECT:
                mConnectRb.setChecked(true);
                break;
            case Configs.INVENTORY:
                mInventoryRb.setChecked(true);
                break;
            case Configs.SETTING:
                mSettingRb.setChecked(true);
                break;
            case Configs.POWER:
                mPowerRb.setChecked(true);
                break;
            case Configs.RW:
                mRwRb.setChecked(true);
                break;
            case Configs.PERMISSION:
                mPermissionRb.setChecked(true);
                break;
            case Configs.LOCK:
                mLockingRb.setChecked(true);
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.locking_rb, R.id.connect_rb, R.id.setting_rb, R.id.inventory_rb, R.id.power_rb, R.id.rw_rb,
             R.id.permission_rb})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.connect_rb:
                mMainVp.setCurrentItem(Configs.CONNECT, false);
                break;
            case R.id.inventory_rb:
                mMainVp.setCurrentItem(Configs.INVENTORY, false);
                break;
            case R.id.setting_rb:
                mMainVp.setCurrentItem(Configs.SETTING, false);
                break;
            case R.id.power_rb:
                mMainVp.setCurrentItem(Configs.POWER, false);
                break;
            case R.id.rw_rb:
                mMainVp.setCurrentItem(Configs.RW, false);
                break;
            case R.id.permission_rb:
                mMainVp.setCurrentItem(Configs.PERMISSION, false);
                break;
            case R.id.locking_rb:
                mMainVp.setCurrentItem(Configs.LOCK, false);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            // 创建退出对话框
            AlertDialog.Builder isExit = new AlertDialog.Builder(this);
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗");
            // 添加选择按钮并注册监听
            isExit.setPositiveButton("确定", new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    EventBus.getDefault().post(new MsgEvent(Configs.UN_CONNECT, ""));
                }
            });
            isExit.setNegativeButton("取消", new DialogInterface.OnClickListener()
            {

                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                }
            });
            // 显示对话框
            isExit.show();
        }
        return false;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
        {
            EventBus.getDefault().unregister(this);
        }
    }

}