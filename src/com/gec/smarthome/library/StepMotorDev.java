package com.gec.smarthome.library;

/**
 * @version 1.1
 * @author Sig
 */
public class StepMotorDev {
	private static StepMotorDev mStepMotorDev = null;

	static {
		System.loadLibrary("step_motor");
	}

	private StepMotorDev() {
	}

	public static StepMotorDev getInstance() {
		if (mStepMotorDev == null) {
			mStepMotorDev = new StepMotorDev();
		}
		return mStepMotorDev;
	}

	public native void openStepMotor();

	/**
	 * @param pos
	 *            1：顺时针 3：逆时针
	 */
	public native void ctrlStepMotor(int pos);

	public native void closeStepMotor();
}
