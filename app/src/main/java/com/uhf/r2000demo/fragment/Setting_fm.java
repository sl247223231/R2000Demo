package com.uhf.r2000demo.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

import com.uhf.r2000demo.MainActivity;
import com.uhf.r2000demo.R;
import com.uhf.r2000demo.adapter.Spinner_Adapter;
import com.uhf.r2000demo.MyApplication;
import com.uhf.r2000demo.mevents.MsgEvent;
import com.uhf.r2000demo.utils.Configs;
import com.uhf.structures.DynamicQParms;
import com.uhf.structures.TagGroup;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class Setting_fm extends BaseFragment {

    @BindView(R.id.check_content)
    Spinner mCheckContent;
    @BindView(R.id.offset)
    EditText mOffset;
    @BindView(R.id.tagLen)
    EditText mTagLen;
    @BindView(R.id.isQuickInventory)
    CheckBox mIsQuickInventory;


    @Override
    protected View initLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initView() {
        mCheckContent.setAdapter(new Spinner_Adapter(getResources().getStringArray(R.array.check_content), context));

        mIsQuickInventory.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TagGroup group = new TagGroup();
                    MyApplication.getLinkage().Radio_SetCurrentSingulationAlgorithm(1);
                    MyApplication.getLinkage().Radio_GetQueryTagGroup(group);
                    group.session = 1;
                    MyApplication.getLinkage().Radio_SetQueryTagGroup(group);
                    DynamicQParms dynamic = new DynamicQParms();
                    dynamic.minQValue = 0;
                    dynamic.maxQValue = 15;
                    dynamic.retryCount = 0;
                    dynamic.startQValue = 8;
                    dynamic.thresholdMultiplier = 40;
                    dynamic.toggleTarget = 1;
                    MyApplication.getLinkage().Radio_SetSingulationAlgorithmDyParameters(dynamic);
                } else {
                    TagGroup group = new TagGroup();
                    MyApplication.getLinkage().Radio_SetCurrentSingulationAlgorithm(1);
                    MyApplication.getLinkage().Radio_GetQueryTagGroup(group);
                    group.session = 2;
                    MyApplication.getLinkage().Radio_SetQueryTagGroup(group);
                    DynamicQParms dynamic = new DynamicQParms();
                    dynamic.minQValue = 0;

                    dynamic.maxQValue = 15;

                    dynamic.retryCount = 0;

                    dynamic.startQValue = 4;

                    dynamic.thresholdMultiplier = 4;

                    dynamic.toggleTarget = 1;
                    MyApplication.getLinkage().Radio_SetSingulationAlgorithmDyParameters(dynamic);
                }
            }
        });


    }


    @OnClick(R.id.check_content_save)
    public void onClick(View v) {
        if (!MainActivity.success_state) {
            return;
        }
        if (mCheckContent.getSelectedItemId() == 0) {
            MyApplication.getLinkage().InventoryPar(1, 0, 0);
        } else if (mCheckContent.getSelectedItemId() == 1) {
            MyApplication.getLinkage().InventoryPar(2, Integer.parseInt(mOffset.getText().toString()),
                    Integer.parseInt(mTagLen.getText().toString()));
        } else if (mCheckContent.getSelectedItemId() == 2) {
            MyApplication.getLinkage().InventoryPar(3, Integer.parseInt(mOffset.getText().toString()),
                    Integer.parseInt(mTagLen.getText().toString()));
        }
        MyApplication.getToast().showShortToast(context, "设置成功");
    }


}
