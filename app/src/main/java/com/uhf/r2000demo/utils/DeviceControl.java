package com.uhf.r2000demo.utils;

import com.uhf.r2000demo.MyApplication;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *上电操作
 */
class DeviceControl {
	private String Company = "";

	DeviceControl(String company) {
		Company = company;
	}

	void PowerOnDevice() {
		try {
			switch (Company) {
				case "RM_LG43":
					WriteFile("/proc/usb_dc_en", "on");
					WriteFile("/proc/vbat_en", "on");
					WriteFile("/proc/gpio127_ctl", "on");
					break;
				case "RM_ZD07":
					WriteFile("/sys/devices/platform/c110sysfs/rfid_power", "1");
					break;
				case "RM_KT45":
					WriteFile("/sys/class/misc/mtgpio/pin", "-wdout64 1");
					break;
				case "RM_KT45Q":
					WriteFile("/sys/class/misc/mtgpio/pin", "-wdout94 1");
					break;
				case "RM_XT07":
					WriteFile("/sys/devices/soc.0/xt_dev.68/xt_dc_in_en", "1");
					// 芯片上电
					WriteFile("/sys/devices/soc.0/xt_dev.68/xt_vbat_out_en", "1");
					// 切换超高频串口
					WriteFile("/sys/devices/soc.0/xt_dev.68/xt_gpio_112", "0");
					break;
				case "RM_KT80":
					WriteFile("/sys/class/misc/mtgpio/pin", "-wdout 119 1");
					break;
				default:
					break;
			}

			Thread.sleep(300);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		int a = MyApplication.getLinkage().Radio_RetrieveAttache();

		MyApplication.getLinkage().Radio_ConnectTo();
	}

	void PowerOffDevice() {
		try {
			switch (Company) {
				case "RM_LG43":

					WriteFile("/proc/usb_dc_en", "off");

					WriteFile("/proc/vbat_en", "off");
					WriteFile("/proc/gpio127_ctl", "off");
					break;
				case "RM_ZD07":
					WriteFile("/sys/devices/platform/c110sysfs/rfid_power", "0");
					break;
				case "RM_KT45":
					WriteFile("/sys/class/misc/mtgpio/pin", "-wdout64 0");
					break;
				case "RM_KT45Q":
					WriteFile("/sys/class/misc/mtgpio/pin", "-wdout94 0");
					break;
				case "RM_XT07":
					WriteFile("/sys/devices/soc.0/xt_dev.68/xt_dc_in_en", "0");
					//芯片下电
					WriteFile("/sys/devices/soc.0/xt_dev.68/xt_vbat_out_en", "0");
					break;
				case "RM_KT80":
					WriteFile("/sys/class/misc/mtgpio/pin", "-wdout 119 0");
					break;
				default:
					break;
			}
		} catch (IOException  e) {
			e.printStackTrace();
		}
	}

	private void WriteFile(String path, String value) throws IOException {
		File DeviceName = new File(path);
		if (DeviceName.exists()) {
			BufferedWriter CtrlFile = new BufferedWriter(new FileWriter(DeviceName, false));
			CtrlFile.write(value);
			CtrlFile.flush();
			CtrlFile.close();
		}
	}
}