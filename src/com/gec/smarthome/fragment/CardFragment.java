package com.gec.smarthome.fragment;

import com.gec.smarthome.R;
import com.gec.smarthome.library.RfidDev;
import com.gec.smarthome.widget.SnEditText;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.view.ViewGroup;

/**
 * Card Module
 * 
 * @author Sig
 * @version 1.1
 */
public class CardFragment extends Fragment {
	private static CardFragment mCardFragment = null;

	private View mView = null;
	private SnEditText mCardNumEet = null;
	private Button mStartCheckBtn = null;
	private Button mStopCheckBtn = null;
	private RfidDev mRfidDev = null;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			String rfidData = (String) msg.obj;
			mCardNumEet.setText(rfidData);
			return false;
		}
	});

	/**
	 * 默认构造方法
	 */
	private CardFragment() {
	}

	/**
	 * 获取单例
	 * 
	 * @return the mCardFragment
	 */
	public static CardFragment getInstance() {
		if (mCardFragment == null)
			mCardFragment = new CardFragment();
		return mCardFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_card, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	@Override
	public void onStop() {
		mRfidDev.closeRfid();
		super.onStop();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		mCardNumEet = (SnEditText) mView.findViewById(R.id.eet_card_num);
		mStartCheckBtn = (Button) mView.findViewById(R.id.btn_start_check);
		mStopCheckBtn = (Button) mView.findViewById(R.id.btn_stop_check);

		// 开始
		mStartCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mStartCheckBtn.setEnabled(false);
				mRfidDev.startGetData();
			}
		});

		// 停止
		mStopCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mStartCheckBtn.setEnabled(true);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						mCardNumEet.setText("0");
					}
				}, 2 * 1000);
				mRfidDev.stopGetData();
			}
		});

		mRfidDev = new RfidDev(mHandler);
		mRfidDev.openRfid();
	}
}
