package com.gec.smarthome.fragment;

import com.gec.smarthome.R;
import com.gec.smarthome.library.LedDev;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Led Module
 * 
 * @author Sig
 * @version 1.1
 */
public class LedFragment extends Fragment implements OnCheckedChangeListener {
	private static Fragment mLedFragment = null;

	private View mView = null;
	private CheckBox[] mChBoxes = null;
	private LedDev mLedDev = null;

	private int[] leds = new int[] { 1, 2, 3, 4 };

	/**
	 * 默认构造方法
	 */
	private LedFragment() {
	}

	/**
	 * 获取单例
	 * 
	 * @return the mLedFragment
	 */
	public static Fragment getInstance() {
		if (mLedFragment == null)
			mLedFragment = new LedFragment();
		return mLedFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_led, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	@Override
	public void onStop() {
		closeAllLed();
		mLedDev.closeLed();
		super.onStop();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.cb_led1:
			ledStatusChanged(isChecked, mChBoxes[0], leds[0]);
			break;
		case R.id.cb_led2:
			ledStatusChanged(isChecked, mChBoxes[1], leds[1]);
			break;
		case R.id.cb_led3:
			ledStatusChanged(isChecked, mChBoxes[2], leds[2]);
			break;
		case R.id.cb_led4:
			ledStatusChanged(isChecked, mChBoxes[3], leds[3]);
			break;
		default:
			break;
		}
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		mChBoxes = new CheckBox[4];
		mChBoxes[0] = (CheckBox) mView.findViewById(R.id.cb_led1);
		mChBoxes[1] = (CheckBox) mView.findViewById(R.id.cb_led2);
		mChBoxes[2] = (CheckBox) mView.findViewById(R.id.cb_led3);
		mChBoxes[3] = (CheckBox) mView.findViewById(R.id.cb_led4);

		mChBoxes[0].setOnCheckedChangeListener(this);
		mChBoxes[1].setOnCheckedChangeListener(this);
		mChBoxes[2].setOnCheckedChangeListener(this);
		mChBoxes[3].setOnCheckedChangeListener(this);

		mLedDev = LedDev.geInstance();
		mLedDev.openLed();
	}

	/**
	 * LED状态改变
	 * 
	 * @param isChecked
	 * @param chBox
	 * @param ledPos
	 */
	private void ledStatusChanged(boolean isChecked, CheckBox chBox, int ledPos) {
		if (isChecked) {
			mLedDev.ctrlLed(ledPos, 1);
			ledStyleChanged(chBox, R.drawable.btn_led_press);
		} else {
			mLedDev.ctrlLed(ledPos, 0);
			ledStyleChanged(chBox, R.drawable.btn_led_normal);
		}
	}

	/**
	 * LED样式改变
	 * 
	 * @param chBox
	 * @param id
	 */
	private void ledStyleChanged(CheckBox chBox, int id) {
		@SuppressWarnings("deprecation")
		Drawable topDrawable = this.getResources().getDrawable(id);
		topDrawable.setBounds(0, 0, topDrawable.getMinimumWidth(), topDrawable.getMinimumHeight());
		chBox.setCompoundDrawables(null, topDrawable, null, null);
	}

	/**
	 * 关闭所有LED
	 */
	private void closeAllLed() {
		for (int i = 0; i < leds.length; i++)
			mLedDev.ctrlLed(leds[i], 0);
	}
}
