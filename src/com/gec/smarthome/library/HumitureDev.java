package com.gec.smarthome.library;

import com.gec.smarthome.bean.HumitureBean;

import android.os.Handler;
import android.os.Message;

/**
 * @author Sig
 * @version 1.1
 */
public class HumitureDev {
	private Handler mHandler = null;

	static {
		System.loadLibrary("humiture");
	}

	public HumitureDev(Handler handler) {
		mHandler = handler;
	}

	public native void openHumiture();

	public void setOnDataListener(int tempz, int tempx, int humidityz, int humidityx) {
		float temp = Float.parseFloat(new String(tempz + "." + tempx));
		float humidity = Float.parseFloat(new String(humidityz + "." + humidityx));
		HumitureBean humitureBean = new HumitureBean(temp, humidity);
		Message msg = Message.obtain();
		msg.obj = humitureBean;
		mHandler.sendMessage(msg);
	}

	public native void startGetData();

	public native void stopGetData();

	public native void closeHumiture();
}
