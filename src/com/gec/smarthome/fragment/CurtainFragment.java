package com.gec.smarthome.fragment;

import com.gec.smarthome.R;
import com.gec.smarthome.library.StepMotorDev;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

/**
 * Curtain Module
 * 
 * @author Sig
 * @version 1.1
 */
public class CurtainFragment extends Fragment {
	private static CurtainFragment mCurtainFragment = null;

	private View mView = null;
	private StepMotorDev mStepMotorDev = null;

	/**
	 * 默认构造方法
	 */
	private CurtainFragment() {
	}

	/**
	 * 获取单例
	 * 
	 * @return the mCurtainFragment
	 */
	public static CurtainFragment getInstance() {
		if (mCurtainFragment == null)
			mCurtainFragment = new CurtainFragment();
		return mCurtainFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_curtain, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	@Override
	public void onStop() {
		mStepMotorDev.closeStepMotor();
		super.onStop();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		// 顺时针
		mView.findViewById(R.id.btn_forward).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mStepMotorDev.ctrlStepMotor(1);
			}
		});

		// 逆时针
		mView.findViewById(R.id.btn_backward).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mStepMotorDev.ctrlStepMotor(3);
			}
		});

		mStepMotorDev = StepMotorDev.getInstance();
		mStepMotorDev.openStepMotor();
	}
}
