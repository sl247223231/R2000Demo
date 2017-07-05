package com.uhf.r2000demo;

import android.app.Application;
import android.content.Context;

import com.uhf.linkage.Linkage;
import com.uhf.r2000demo.view.BaseToast;

public class MyApplication extends Application {

    private static Linkage link;
    private static BaseToast toast;



    @Override
    public void onCreate() {
        super.onCreate();
        link = new Linkage();
        toast = new BaseToast();
    }


    public static Linkage getLinkage() {
        if (link == null) {
            link = new Linkage();
        }
        return link;
    }


    public static BaseToast getToast() {
        if (toast == null) {
            toast = new BaseToast();
        }
        return toast;
    }






}
