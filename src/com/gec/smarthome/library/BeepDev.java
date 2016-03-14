package com.gec.smarthome.library;

/**
 * @author Sig
 * @version 1.1
 */
public class BeepDev {
	private static BeepDev mBeepDev = null;

	static {
		System.loadLibrary("beep");
	}

	private BeepDev() {

	}

	public static BeepDev geInstance() {
		if (mBeepDev == null)
			mBeepDev = new BeepDev();
		return mBeepDev;
	}

	public native void openPwm();

	/**
	 * @param pos
	 *            0：开始 1：停止
	 */
	public native void ctrlPwm(int pos);

	public native void closePwm();
}
