package com.gec.smarthome.library;

/**
 * @author Sig
 * @version 1.1
 */
public class DcMotorDev {
	private static DcMotorDev mDcMotorDev = null;

	static {
		System.loadLibrary("dc_motor");
	}

	private DcMotorDev() {

	}

	public static DcMotorDev geInstance() {
		if (mDcMotorDev == null)
			mDcMotorDev = new DcMotorDev();
		return mDcMotorDev;
	}

	public native void openDcMotor();

	/**
	 * @param pos 4：左转 0：停止 1：右转
	 */
	public native void ctrlDcMotor(int pos);

	public native void closeDcMotor();
}
