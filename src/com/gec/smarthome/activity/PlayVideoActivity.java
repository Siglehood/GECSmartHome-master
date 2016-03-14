package com.gec.smarthome.activity;

import java.io.File;

import com.gec.smarthome.R;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.VideoView;

/**
 * 视频播放界面
 * 
 * @author Sig
 * @version 1.1
 */
public class PlayVideoActivity extends Activity {
	private VideoView mVioView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_play_video);
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mVioView = (VideoView) findViewById(R.id.video_view);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		mVioView.setLayoutParams(layoutParams);

		// LayoutParams layoutParams = new LayoutParams(1920, 1080);
		// layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		// mVioView.setLayoutParams(layoutParams);

		mVioView.setVideoURI(
				Uri.parse("android.resource://" + getPackageName() + File.separator + R.raw.robotica_1080));
		mVioView.setMediaController(new MediaController(this));
		mVioView.start();

		mVioView.setOnCompletionListener(new OnCompletionListener() {

			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.start();
			}
		});
	}
}
