package com.gec.smarthome.library;

import android.os.Handler;
import android.os.Message;

/**
 * @author Sig
 * @version 1.1
 */
public class RfidDev {
	private Handler mHandler = null;

	static {
		System.loadLibrary("rfid");
	}

	public RfidDev(Handler handler) {
		mHandler = handler;
	}

	public native void openRfid();

	public void setOnDataListener(String cardId) {
		Message msg = new Message();
		msg.obj = cardId;
		mHandler.sendMessage(msg);
	}

	public native void startGetData();

	public native void stopGetData();

	public native void closeRfid();

}
