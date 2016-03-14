package com.gec.smarthome.activity;

import java.io.File;
import java.util.ArrayList;

import com.ab.activity.AbActivity;
import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.gec.smarthome.R;
import com.gec.smarthome.adapter.ImageShowAdapter;
import com.gec.smarthome.widget.SnTitleBar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 照相界面
 * 
 * @author Sig
 * @version 1.1
 */
public class TakePhotoActivity extends AbActivity {
	/** 用来标识请求照相功能的Activity */
	private static final int CAMERA_WITH_DATA = 3023;
	/** 用来标识请求裁剪图片后的Activity */
	private static final int CAMERA_CROP_DATA = 3022;

	private SnTitleBar mSnTitleBar = null;
	/** 照相机拍照得到的图片 */
	private File mCurrentPhotoFile = null;
	private String mFileName = null;
	private GridView mGridView = null;
	private ImageShowAdapter mImagePathAdapter = null;
	private ArrayList<String> mPhotoList = null;

	private int selectIndex = 0;
	private int camIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_take_photo);
		initTitleBar();
		mPhotoList = new ArrayList<String>();
		// 默认
		mPhotoList.add(String.valueOf(R.drawable.cam_photo));
		mGridView = (GridView) findViewById(R.id.myGrid);
		mImagePathAdapter = new ImageShowAdapter(this, mPhotoList, 150, 150);
		mGridView.setAdapter(mImagePathAdapter);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectIndex = position;
				if (selectIndex == camIndex) {
					doPickPhotoAction();
				}
			}
		});

		findViewById(R.id.btn_action_take).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doPickPhotoAction();
			}
		});
	}

	private void initTitleBar() {
		mSnTitleBar = (SnTitleBar) findViewById(R.id.sn_title_bar);
		mSnTitleBar.setTitleText(R.string.captureimages);
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
	 * 从照相机获取
	 */
	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		// 判断是否有SD卡,如果有SD卡存入SD卡再说，没有SD卡直接转换为图片
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			doTakePhoto();
		} else {
			AbToastUtil.showToast(this, getString(R.string.no_sdcard));
		}
	}

	/**
	 * 拍照获取图片
	 */
	protected void doTakePhoto() {
		try {
			mFileName = System.currentTimeMillis() + ".jpg";
			mCurrentPhotoFile = new File(Environment.getExternalStorageDirectory(), mFileName);
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPhotoFile));
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (Exception e) {
			AbToastUtil.showToast(this, getString(R.string.no_camera));
		}
	}

	/**
	 * 描述：因为调用了Camera和Galley所以要判断他们各自的返回情况, 他们启动时是这样的startActivityForResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CAMERA_WITH_DATA:
			AbLogUtil.d(TakePhotoActivity.class, "将要进行裁剪的图片的路径是 = " + mCurrentPhotoFile.getPath());
			String currentFilePath2 = mCurrentPhotoFile.getPath();
			Intent intent2 = new Intent(this, CropImageActivity.class);
			intent2.putExtra("PATH", currentFilePath2);
			startActivityForResult(intent2, CAMERA_CROP_DATA);
			break;
		case CAMERA_CROP_DATA:
			String path = mIntent.getStringExtra("PATH");
			AbLogUtil.d(TakePhotoActivity.class, "裁剪后得到的图片的路径是 = " + path);
			mImagePathAdapter.addItem(mImagePathAdapter.getCount() - 1, path);
			camIndex++;
			break;
		}
	}
}
