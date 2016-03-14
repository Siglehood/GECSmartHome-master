package com.gec.smarthome.activity;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.ab.util.AbFileUtil;
import com.ab.util.AbImageUtil;
import com.ab.view.cropimage.CropImage;
import com.ab.view.cropimage.CropImageView;
import com.gec.smarthome.R;

/**
 * 相片裁剪界面
 * 
 * @author Sig
 * @version 1.1
 */
public class CropImageActivity extends Activity implements OnClickListener {
	private static final String TAG = "CropImageActivity";
	private static final boolean D = false;

	private CropImageView mImageView = null;
	private Bitmap mBitmap = null;
	private CropImage mCrop = null;
	private Button mSave = null;
	private Button mCancel = null;
	private Button rotateLeft = null;
	private Button rotateRight = null;
	private String mPath = "CropImageActivity";
	public int screenWidth = 0;
	public int screenHeight = 0;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_crop_image);
		init();
	}

	@Override
	protected void onStop() {
		if (mBitmap != null) {
			mBitmap = null;
		}
		super.onStop();
	}

	/**
	 * 初始化数据
	 */
	private void init() {
		getWindowWH();
		mPath = getIntent().getStringExtra("PATH");
		if (D)
			Log.d(TAG, "mPath = getIntent().getStringExtra(\"PATH\");" + mPath);
		initView();
		// 相册中原来的图片
		File mFile = new File(mPath);
		try {
			mBitmap = AbFileUtil.getBitmapFromSD(mFile, AbImageUtil.SCALEIMG, 500, 500);
			if (mBitmap == null) {
				Toast.makeText(this, getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
				finish();
			} else {
				resetImageView(mBitmap);
			}
		} catch (Exception e) {
			Toast.makeText(this, getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	/**
	 * 获取屏幕的宽和高
	 */
	private void getWindowWH() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		mImageView = (CropImageView) findViewById(R.id.crop_image);
		mSave = (Button) this.findViewById(R.id.okBtn);
		mCancel = (Button) this.findViewById(R.id.cancelBtn);
		rotateLeft = (Button) this.findViewById(R.id.rotateLeft);
		rotateRight = (Button) this.findViewById(R.id.rotateRight);
		mSave.setOnClickListener(this);
		mCancel.setOnClickListener(this);
		rotateLeft.setOnClickListener(this);
		rotateRight.setOnClickListener(this);
	}

	/**
	 * 重置ImageView
	 * 
	 * @param b
	 *            the bitmap
	 */
	private void resetImageView(Bitmap b) {
		mImageView.clear();
		mImageView.setImageBitmap(b);
		mImageView.setImageBitmapResetBase(b, true);
		mCrop = new CropImage(this, mImageView, mHandler);
		mCrop.crop(b);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.cancelBtn:
			finish();
			break;
		case R.id.okBtn:
			String path = mCrop.saveToLocal(mCrop.cropAndSave());
			if (D)
				Log.d(TAG, "String path = mCrop.saveToLocal(mCrop.cropAndSave());" + path);
			Intent intent = new Intent();
			intent.putExtra("PATH", path);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.rotateLeft:
			mCrop.startRotate(270.f);
			break;
		case R.id.rotateRight:
			mCrop.startRotate(90.f);
			break;
		default:
			break;
		}
	}
}
