package com.gec.smarthome.activity;

import com.gec.smarthome.R;
import com.gec.smarthome.widget.SnHorizontalProgressBar;
import com.gec.smarthome.widget.SnTitleBar;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 音乐播放界面
 * 
 * @author Sig
 * @version 1.1
 */
public class PlayAudioActivity extends Activity {
	private SnTitleBar mSnTitleBar = null;
	private MediaPlayer mMPlayer = null;
	private RelativeLayout rLayout = null;
	private SnHorizontalProgressBar mSnProgBar = null;
	private TextView mSnProgVal = null;
	private Button mStartBtn = null;
	private Button mStopBtn = null;
	private Button mPauseBtn = null;
	private Button mContBtn = null;
	private Handler mHandler = null;
	private boolean isPlaying = true;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_play_audio);
		initTitleBar();
		initView();
		audioCtrl();
		updUI();
	}

	@Override
	protected void onPause() {
		if (mMPlayer != null)
			mMPlayer.release();
		isPlaying = false;
		super.onPause();
	}

	/**
	 * 初始化标题栏
	 */
	private void initTitleBar() {
		mSnTitleBar = (SnTitleBar) this.findViewById(R.id.sn_title_bar);
		mSnTitleBar.setTitleText(R.string.mediaplaysound);
		mSnTitleBar.setTitleSize(30.0f);
		mSnTitleBar.setLogo(R.drawable.sel_ic_back);
		mSnTitleBar.setTitleAlignLeft(true);
		mSnTitleBar.setOnLogoClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		rLayout = (RelativeLayout) this.findViewById(R.id.r_layout);
		mSnProgBar = (SnHorizontalProgressBar) this.findViewById(R.id.progress_horizontal);
		mSnProgVal = (TextView) this.findViewById(R.id.tv_progress_val);
		mSnProgVal.setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf"));
		mStartBtn = (Button) this.findViewById(R.id.start);
		mStopBtn = (Button) this.findViewById(R.id.stop);
		mPauseBtn = (Button) this.findViewById(R.id.pause);
		mContBtn = (Button) this.findViewById(R.id.continues);
	}

	/**
	 * 初始化控制
	 */
	private void audioCtrl() {
		audioStart();
		audioStop();
		audioPause();
		audioCont();
	}

	/**
	 * 开始播放
	 */
	private void audioStart() {
		mStartBtn.setEnabled(true);
		mStopBtn.setEnabled(false);
		mPauseBtn.setEnabled(false);
		mContBtn.setEnabled(false);

		mStartBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rLayout.setVisibility(View.VISIBLE);
				mStartBtn.setEnabled(false);
				mStopBtn.setEnabled(true);
				mPauseBtn.setEnabled(true);
				mContBtn.setEnabled(true);
				// 创建MediaPlayer，Mp3资源从资源库中获取
				mMPlayer = MediaPlayer.create(PlayAudioActivity.this, R.raw.android);
				// 设置进度条最大进度
				mSnProgBar.setMax(mMPlayer.getDuration());
				// 设置当前进度
				mSnProgBar.setProgress(-1);
				// 发消息给Handler处理UI
				mHandler.sendEmptyMessage(1);
				// 开始播放
				mMPlayer.start();
				// 设置当前为播放状态
				isPlaying = true;

				// 监听mp3播放完毕
				mMPlayer.setOnCompletionListener(new OnCompletionListener() {

					@Override
					public void onCompletion(MediaPlayer mediaPlayer) {
						// 设置为不播放状态
						isPlaying = false;
						// 播放完毕释放资源
						mMPlayer.release();
						mStartBtn.setEnabled(true);
						mStopBtn.setEnabled(false);
						mPauseBtn.setEnabled(false);
						mContBtn.setEnabled(false);
					}
				});
			}
		});
	}

	/**
	 * 停止播放
	 */
	private void audioStop() {
		mStopBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				rLayout.setVisibility(View.INVISIBLE);
				mStartBtn.setEnabled(true);
				mStopBtn.setEnabled(false);
				mPauseBtn.setEnabled(false);
				mContBtn.setEnabled(false);
				// 判断mediaPlayer不为null且在播放状态
				if (mMPlayer != null && mMPlayer.isPlaying()) {
					// 停止播放
					mMPlayer.stop();
					isPlaying = false;
				}
			}
		});
	}

	/**
	 * 暂停播放
	 */
	private void audioPause() {
		mPauseBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMPlayer != null && mMPlayer.isPlaying()) {
					// 暂停播放
					mMPlayer.pause();
				}
			}
		});
	}

	/**
	 * 继续播放（针对于暂停播放而言）
	 */
	private void audioCont() {
		mContBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMPlayer != null) {
					// 由暂停状态的重新播放
					mMPlayer.start();
				}
			}
		});
	}

	/**
	 * 更新UI
	 */
	private void updUI() {
		mHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {

				switch (msg.what) {
				case 1:
					try {
						// 睡眠1秒钟
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (isPlaying) {
						int i = (int) ((mMPlayer.getCurrentPosition() * 1.0f / mSnProgBar.getMax()) * 100);
						mSnProgVal.setText(i + "%");
						// 设置当前播放位置
						mSnProgBar.setProgress(mMPlayer.getCurrentPosition());
						// 发送堆栈消息与case 1循环更新UI
						mHandler.sendEmptyMessage(1);
					} else {
						mSnProgVal.setText("");
						// 如果处于停止状态则进度条进度归0
						mSnProgBar.setProgress(0);
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
	}
}
