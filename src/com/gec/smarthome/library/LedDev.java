package com.gec.smarthome.library;

/**
 * @author Sig
 * @version 1.1
 */
public class LedDev {
	private static LedDev mLedDev = null;

	static {
		System.loadLibrary("led");
	}

	private LedDev() {

	}

	public static LedDev geInstance() {
		if (mLedDev == null)
			mLedDev = new LedDev();
		return mLedDev;
	}

	public native void openLed();

	/**
	 * @param pos
	 *            1：第一个LED 2：第一个LED 3：第一个LED 4：第四个LED
	 * @param status
	 *            0：关 1：开
	 */
	public native void ctrlLed(int pos, int status);

	public native void closeLed();
}
