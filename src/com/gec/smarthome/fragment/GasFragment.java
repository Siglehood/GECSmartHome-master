package com.gec.smarthome.fragment;

import com.gec.smarthome.R;
import com.gec.smarthome.library.GasDev;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Gas Module
 * 
 * @author Sig
 * @version 1.1
 */
public class GasFragment extends Fragment {
	private static GasFragment mGasFragment = null;
	
	private View mView = null;
	private ProgressBar mWarnProgBar = null;
	private ProgressBar mNormProgBar = null;
	private TextView mContent = null;
	private GasDev mGasDev = null;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@SuppressWarnings("deprecation")
		@Override
		public boolean handleMessage(Message msg) {
			int data = msg.arg1;
			if (data != 0) {
				mNormProgBar.setVisibility(View.INVISIBLE);
				mWarnProgBar.setVisibility(View.VISIBLE);
				mContent.setText(R.string.gas_error);
				mContent.setTextColor(getResources().getColor(R.color.color7));
			} else {
				mWarnProgBar.setVisibility(View.INVISIBLE);
				mNormProgBar.setVisibility(View.VISIBLE);
				mContent.setText(R.string.gas_normal);
				mContent.setTextColor(getResources().getColor(android.R.color.black));
			}
			return false;
		}
	});

	/**
	 * 默认构造方法
	 */
	private GasFragment() {
	}

	/**
	 * 获取单例
	 * 
	 * @return the mGasFragment
	 */
	public static GasFragment getInstance() {
		if (mGasFragment == null)
			mGasFragment = new GasFragment();
		return mGasFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_gas, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	@Override
	public void onStop() {
		mGasDev.closeGas();
		super.onStop();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		mWarnProgBar = (ProgressBar) mView.findViewById(R.id.pb_warn);
		mNormProgBar = (ProgressBar) mView.findViewById(R.id.pb_normal);
		mContent = (TextView) mView.findViewById(R.id.tv_gas_content);

		mGasDev = new GasDev(mHandler);
		mGasDev.openGas();
		mGasDev.startGetData();
	}
}
