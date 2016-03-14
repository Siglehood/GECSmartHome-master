package com.gec.smarthome.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.ab.util.AbAnimationUtil;
import com.ab.view.slidingmenu.SlidingMenu;
import com.ab.view.slidingmenu.SlidingMenu.CanvasTransformer;
import com.gec.smarthome.R;
import com.gec.smarthome.fragment.MainFragment;
import com.gec.smarthome.fragment.MainMenuFragment;
import com.gec.smarthome.widget.SnTitleBar;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.widget.Toast;

/**
 * 主界面
 * 
 * @author Sig
 * @version 1.1
 */
public class MainActivity extends FragmentActivity {
	private com.ab.view.slidingmenu.SlidingMenu mMenu = null;
	private Fragment mContent = null;
	private MainMenuFragment mMainMenuFragment = null;
	private boolean isExit = false;

	private static Interpolator mInterp = new Interpolator() {

		@Override
		public float getInterpolation(float t) {
			t -= 1.0f;
			return t * t * t + 1.0f;
		}
	};

	public SnTitleBar mSnTitleBar = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setContentView(R.layout.activity_main);
		initTitleBar();
		initData();
		initView(savedInstanceState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	@Override
	public void onBackPressed() {
		if (mMenu.isMenuShowing()) {
			mMenu.showContent();
		} else {
			if (!isExit) {
				Toast.makeText(this, R.string.exit_confirm, Toast.LENGTH_SHORT).show();
				isExit = true;
				new Timer().schedule(new TimerTask() {

					@Override
					public void run() {
						isExit = false;
					}
				}, 2000);

			} else {
				finish();
			}
		}
	}

	/**
	 * 切换Fragment
	 * 
	 * @param fragment
	 */
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		mMenu.showContent();
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mSnTitleBar = (SnTitleBar) findViewById(R.id.sn_title_bar);
		mSnTitleBar.setTitleText(R.string.app_name);
		mSnTitleBar.setTitleSize(30.0f);
		mSnTitleBar.setLogo(R.drawable.sel_ic_back);
		mSnTitleBar.setTitleAlignLeft(true);
		mSnTitleBar.setOnLogoClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AbAnimationUtil.playRotateAnimation(mSnTitleBar.getLogo(), 1000, 0, Animation.RESTART);
				if (mMenu.isMenuShowing()) {
					mMenu.showContent();
				} else {
					mMenu.showMenu();
				}
			}
		});
	}

	/**
	 * 初始化view
	 * 
	 * @param savedInstanceState
	 *            the savedInstanceState
	 */
	private void initView(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		}
		if (mContent == null) {
			mContent = new MainFragment();
		}
		// 主视图的Fragment添加
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent).commit();
		mMainMenuFragment = new MainMenuFragment();
		initMenu();
		setAnim();
	}

	/**
	 * SlidingMenu的配置
	 */
	private void initMenu() {
		mMenu = new SlidingMenu(this);
		mMenu.setMode(SlidingMenu.LEFT);
		mMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mMenu.setShadowWidthRes(R.dimen.shadow_width);
		mMenu.setShadowDrawable(R.drawable.menu_shadow);
		mMenu.setBehindOffsetRes(R.dimen.menu_offset);
		mMenu.setFadeDegree(0.35f);
		mMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		// mMenu视图的Fragment添加
		mMenu.setMenu(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, mMainMenuFragment).commit();
	}

	/**
	 * 动画配置
	 */
	private void setAnim() {
		mMenu.setBehindCanvasTransformer(new CanvasTransformer() {

			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				// 将画布默认的黑背景替换掉
				// canvas.drawColor(getResources().getColor(R.color.gray_dark));

				// sliding up
				canvas.translate(0, canvas.getHeight() * (1 - mInterp.getInterpolation(percentOpen)));

				// scale
				// canvas.scale(percentOpen, 1, 0, 0);

				// zoom
				// float scale = (float) (percentOpen * 0.25 + 0.75);
				// canvas.scale(scale, scale, canvas.getWidth() / 2,
				// canvas.getHeight() / 2);
			}
		});
	}

	/**
	 * 初始化root数据
	 */
	private void initData() {
		File[] devFile = new File[6];
		devFile[0] = new File("/dev/Led");
		devFile[1] = new File("/dev/dc_motor");
		devFile[2] = new File("/dev/stepmotor");
		devFile[3] = new File("/dev/gec_gas_drv");
		devFile[4] = new File("/dev/humidity");
		devFile[5] = new File("/dev/beep");
		for (int i = 0; i < devFile.length; i++) {
			if (!devFile[i].exists())
				return;

			if (!new File("/system/bin/su").exists())
				return;

			if (!devFile[i].canRead() || !devFile[i].canWrite()) {
				Process process;
				try {
					process = Runtime.getRuntime().exec("su");
					DataOutputStream os = new DataOutputStream(process.getOutputStream());
					String cmd = "chmod 777 " + devFile[i].getAbsolutePath() + "\n" + "exit\n";
					os.writeBytes(cmd);
					os.close();
				} catch (IOException e) {
					throw new RuntimeException("输入输出异常", e);
				}
			}
		}
	}
}
