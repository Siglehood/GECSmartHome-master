package com.gec.smarthome.fragment;

import com.gec.smarthome.R;
import com.gec.smarthome.library.BeepDev;
import com.gec.smarthome.widget.SnEditText;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Pwn Module
 * 
 * @author Sig
 * @version 1.1
 */
public class BeepFragment extends Fragment {
	private static BeepFragment mPwmFragment = null;

	private View mView = null;
	private SnEditText mFreqEet = null;
	private BeepDev mBeepDev = null;

	/**
	 * 默认构造方法
	 */
	private BeepFragment() {
	}

	/**
	 * 获取单例
	 * 
	 * @return the mPwmFragment
	 */
	public static BeepFragment getInstance() {
		if (mPwmFragment == null)
			mPwmFragment = new BeepFragment();
		return mPwmFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_pwm, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	@Override
	public void onStop() {
		mBeepDev.closePwm();
		super.onStop();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		mFreqEet = (SnEditText) mView.findViewById(R.id.eet_frequency);

		// 开始
		mView.findViewById(R.id.btn_start).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String input = mFreqEet.getText().toString().trim();
				if (TextUtils.isEmpty(input)) {
					Toast.makeText(getActivity(), R.string.pwminput, Toast.LENGTH_SHORT).show();
					return;
				}
				mBeepDev.ctrlPwm(0);
			}
		});

		// 停止
		mView.findViewById(R.id.btn_stop).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String input = mFreqEet.getText().toString().trim();
				if (!TextUtils.isEmpty(input))
					mBeepDev.ctrlPwm(1);
			}
		});
		
		mBeepDev = BeepDev.geInstance();
		mBeepDev.openPwm();
	}
}
