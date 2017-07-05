
package com.uhf.r2000demo.utils;


import com.uhf.r2000demo.MyApplication;

public class ModuleManager {
	//全局的串口句柄，底层通过句柄操作模块

	public static int initLibSO() {
		DeviceControl DevCtr = new DeviceControl(Configs.companyPower);
		//初始化模块，为模块上电，为保证上电成功，建议先下电，在上电
		DevCtr.PowerOffDevice();
		DevCtr.PowerOnDevice();
		int result = MyApplication.getLinkage().open_serial(Configs.serial);
		
		 MyApplication.getLinkage().Radio_Initialization();
		MyApplication.getLinkage().Radio_UpgradeProgram();
		return result;
	}

	public static void destroyLibSO() {
		//关闭串口
		MyApplication.getLinkage().close_serial();
		DeviceControl DevCtr = new DeviceControl(Configs.companyPower);
		//下电
		DevCtr.PowerOffDevice();
	}



}

