package com.gec.smarthome.library;

import android.os.Handler;
import android.os.Message;

/**
 * @author Sig
 * @version 1.1
 */
public class GasDev {
	private Handler mHandler = null;

	static {
		System.loadLibrary("gas");
	}

	public GasDev(Handler handler) {
		mHandler = handler;
	}

	public native void openGas();

	public void setOnDataListener(int value) {
		Message msg = Message.obtain();
		msg.arg1 = value;
		mHandler.sendMessage(msg);
	}

	public native void startGetData();

	public native void stopGetData();

	public native void closeGas();
}
