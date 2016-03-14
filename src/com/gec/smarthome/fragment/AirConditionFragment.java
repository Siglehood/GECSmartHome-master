package com.gec.smarthome.fragment;

import com.gec.smarthome.R;
import com.gec.smarthome.library.DcMotorDev;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * AirCondition Module
 * 
 * @author Sig
 * @version 1.1
 */
public class AirConditionFragment extends Fragment {
	private static AirConditionFragment mAirConditionFragment = null;

	private View mView = null;
	private DcMotorDev mDcMotorDev = null;

	/**
	 * 默认构造方法
	 */
	private AirConditionFragment() {
	}

	/**
	 * 获取单例
	 * 
	 * @return the mAirConditionFragment
	 */
	public static AirConditionFragment getInstance() {
		if (mAirConditionFragment == null)
			mAirConditionFragment = new AirConditionFragment();
		return mAirConditionFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_air_condition, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	@Override
	public void onStop() {
		mDcMotorDev.ctrlDcMotor(0);
		mDcMotorDev.closeDcMotor();
		super.onStop();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		// 左转
		mView.findViewById(R.id.btn_left).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDcMotorDev.ctrlDcMotor(4);
			}
		});

		// 停止
		mView.findViewById(R.id.btn_stop).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDcMotorDev.ctrlDcMotor(0);
			}
		});

		// 右转
		mView.findViewById(R.id.btn_right).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDcMotorDev.ctrlDcMotor(1);
			}
		});
		
		mDcMotorDev = DcMotorDev.geInstance();
		mDcMotorDev.openDcMotor();
	}
}
