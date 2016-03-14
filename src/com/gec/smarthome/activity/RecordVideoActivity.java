package com.gec.smarthome.activity;

import java.io.File;
import java.io.IOException;

import com.gec.smarthome.R;

import android.app.Activity;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 录像界面
 * 
 * @author Sig
 * @version 1.1
 */
@SuppressWarnings("deprecation")
public class RecordVideoActivity extends Activity implements SurfaceHolder.Callback {
	private static final String TAG = "RecordVideoActivity";
	private static final boolean D = false;

	private ImageButton mRcdBtn = null;
	private ImageButton mStopRcdBtn = null;
	private ImageView mRcdStatus = null;
	private SurfaceView mSurView = null;
	private SurfaceHolder mSurHolder = null;
	private MediaRecorder mMRcd = null;
	private Camera mCam = null;
	private File myFile = null;
	private boolean isRecord = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_record_video);
		init();
	}

	@Override
	protected void onDestroy() {
		if (mCam != null) {
			stopRecord();
			closeCamera();
			mCam = null;
		}
		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		openCamera();
		startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}

	/**
	 * 初始化
	 */
	private void init() {
		if (D)
			Log.d(TAG, "init start");
		// 录制状态指示图标，绿色为空闲状态，红色为录制状态
		mRcdStatus = (ImageView) this.findViewById(R.id.imgV_state);
		mSurView = (SurfaceView) this.findViewById(R.id.myPreview);
		mSurHolder = mSurView.getHolder();
		// setType 在安卓4.0.3版已设为由程序自动控制,在安卓2.3版仍然要设置。
		// mSurHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 设定SurfaceHolder的回调函数
		mSurHolder.addCallback(this);
		mRcdBtn = (ImageButton) this.findViewById(R.id.btn_record);
		mStopRcdBtn = (ImageButton) this.findViewById(R.id.btn_stopRecord);
		mRcdBtn.setEnabled(true);
		mStopRcdBtn.setEnabled(false);
		// 按钮添加监听
		mRcdBtn.setOnClickListener(mClickListener);
		mStopRcdBtn.setOnClickListener(mClickListener);
		mMRcd = new MediaRecorder();
		if (D)
			Log.d(TAG, "init complete");
	}

	/**
	 * 获取camera资源，设置camera参数
	 */
	private void openCamera() {
		try {
			if (D)
				Log.d(TAG, "openCamera start");
			// 打开camera
			mCam = Camera.open();
			// camera旋转90度
			mCam.setDisplayOrientation(90);
			// 获取参数，设置参数
			Camera.Parameters mParameters = mCam.getParameters();
			// 设置预览分辨率
			mParameters.setPreviewSize(640, 480);
			// 设置场景
			mParameters.setSceneMode(Camera.Parameters.SCENE_MODE_FIREWORKS);
			// 设置白平衡
			mParameters.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_FLUORESCENT);
			// 设置预览侦率 直接影响录制的侦率
			// 在安卓4.0版本中这个参数已经由系统自己设定，但是安卓2.3版仍然要手动设置
			// mParameters.setPreviewFrameRate(25);
			// 将设定好的参数传入
			mCam.setParameters(mParameters);
			mCam.setPreviewDisplay(mSurHolder);
			if (D)
				Log.d(TAG, "openCamera complete");
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(RecordVideoActivity.this, R.string.no_camera, Toast.LENGTH_SHORT).show();
			if (D)
				Log.d(TAG, "openCamera error");
		}
	}

	/**
	 * 开始预览控制
	 */
	private void startPreview() {
		if (D)
			Log.d(TAG, "startPreview start");
		// 先使camera处于停止预览状态
		stopPreview();
		// 开始预览
		mCam.startPreview();
		// camera释放资源控制，为recorder获取资源，进行录制做准备
		mCam.unlock();
		if (D)
			Log.d(TAG, "startPreview complete");
	}

	/**
	 * 停止预览
	 */
	private void stopPreview() {
		mCam.stopPreview();
		if (D)
			Log.d(TAG, "stopPreview complete");
	}

	/**
	 * 关闭camera，释放资源
	 */
	private void closeCamera() {
		if (mCam != null) {
			try {
				if (D)
					Log.d(TAG, "closeCamera start");
				if (D)
					Log.d(TAG, "camera reconnect start");
				// 使camera重新获取资源的控制
				mCam.reconnect();
				if (D)
					Log.d(TAG, "camera reconnect complete");
				// 停止预览，释放资源
				stopPreview();
				mCam.release();
				if (D)
					Log.d(TAG, "closeCamera complete");
			} catch (IOException e) {
				e.printStackTrace();
				if (D)
					Log.d(TAG, "closeCamera IOException");
			} catch (RuntimeException e) {
				e.printStackTrace();
				if (D)
					Log.d(TAG, "closeCamera RuntimeException");
			}
		}
	}

	/**
	 * 开始录制视频
	 */
	private void startRecord() {
		if (D)
			Log.d(TAG, "startRecord start");
		mStopRcdBtn.setEnabled(true);
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Toast.makeText(RecordVideoActivity.this, R.string.no_sdcard, Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			myFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".mp4");
			// 以下开始进入录制状态，设置状态为true和状态标志为红色
			isRecord = true;
			mRcdStatus.setImageResource(R.drawable.red_1);
			// 为了快速在预览和录制之间转换，故使用此函数
			// recorder.setCamera()一般于recorder.setVideoSource()之前调用，否则会有异常
			mMRcd.setCamera(mCam);
			// 设置视频源为camera
			mMRcd.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// 设置音频源为mic
			// mMRcd.setAudioSource(MediaRecorder.AudioSource.MIC);
			// 设置输出文件的格式为mp4
			mMRcd.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			// 设置音频编码
			// mMRcd.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
			// 设置视频编码
			mMRcd.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
			// 最长录制5000秒钟
			mMRcd.setMaxDuration(5000000);
			// 512k/s
			mMRcd.setVideoEncodingBitRate(512000);
			// 输出文件的路径和名称
			mMRcd.setOutputFile(myFile.getAbsolutePath());
			mMRcd.setPreviewDisplay(mSurHolder.getSurface());
			// 准备，开始，视频录制
			mMRcd.prepare();
			mMRcd.start();
			Toast.makeText(this, R.string.start_record_video, Toast.LENGTH_SHORT).show();
			if (isRecord == true) {
				mRcdBtn.setEnabled(false);
				mStopRcdBtn.setEnabled(true);
			}
			if (D)
				Log.d(TAG, "startRecord complete");
		} catch (IllegalStateException e) {
			e.printStackTrace();
			if (D)
				Log.d(TAG, "startRecord IllegalStateException");
		} catch (IOException e) {
			e.printStackTrace();
			if (D)
				Log.d(TAG, "startRecord IOException");
		}
	}

	/**
	 * 停止录制
	 */
	private void stopRecord() {
		if (D)
			Log.d(TAG, "stopRecord start");
		// 判断是否是录制状态
		if (isRecord == true) {
			// 以下停止录制状态，设置状态为false和状态标志为绿色
			isRecord = false;
			mRcdBtn.setEnabled(true);
			mStopRcdBtn.setEnabled(false);
			mRcdStatus.setImageResource(R.drawable.green_1);
			// 设置myRecorder为空的状态，为下次录制做准备
			mMRcd.reset();
			if (D)
				Log.d(TAG, "stopRecord complete");
			Toast.makeText(this, R.string.save_record_video, Toast.LENGTH_SHORT).show();
		}
		if (D)
			Log.d(TAG, "stopRecord");
	}

	/**
	 * 点击事件
	 */
	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mRcdBtn) {
				startRecord();
			} else if (v == mStopRcdBtn) {
				stopRecord();
			}
		}
	};
}
