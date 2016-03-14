package com.gec.smarthome.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.gec.smarthome.R;

/**
 * 欢迎界面
 * 
 * @author Sig
 * @version 1.1
 */
public class LaunchActivity extends Activity {

	private Context mContext = null;
	private RelativeLayout mLaunchLayout = null;
	private Animation mFadeIn = null;
	private Animation mFadeInScale = null;
	private Animation mFadeOut = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去掉标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_launch);
		mContext = getApplicationContext();
		init();
		setListener();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (KeyEvent.KEYCODE_BACK == keyCode) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		mLaunchLayout = (RelativeLayout) findViewById(R.id.launch);
		initAnim();
		mLaunchLayout.startAnimation(mFadeIn);
	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		mFadeIn = AnimationUtils.loadAnimation(mContext, R.anim.welcome_fade_in);
		mFadeInScale = AnimationUtils.loadAnimation(mContext, R.anim.welcome_fade_in_scale);
		mFadeOut = AnimationUtils.loadAnimation(mContext, R.anim.welcome_fade_out);
	}

	/**
	 * 设置动画监听器
	 */
	private void setListener() {
		setFadeInListener();
		setFadeInScaleListener();
		setFadeOutScaleListener();
	}

	/**
	 * 淡入动画
	 */
	private void setFadeInListener() {
		mFadeIn.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mLaunchLayout.startAnimation(mFadeInScale);
			}
		});
	}

	/**
	 * 比例放大动画
	 */
	private void setFadeInScaleListener() {
		mFadeInScale.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mLaunchLayout.startAnimation(mFadeOut);
			}
		});
	}

	/**
	 * 淡出动画
	 */
	private void setFadeOutScaleListener() {
		mFadeOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				startActivity(new Intent(mContext, MainActivity.class));
				finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
	}
}
