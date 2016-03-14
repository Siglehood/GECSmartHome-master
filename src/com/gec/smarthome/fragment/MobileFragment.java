package com.gec.smarthome.fragment;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import com.gec.smarthome.R;
import com.gec.smarthome.bean.ComBean;
import com.gec.smarthome.util.SerialHelper;
import com.gec.smarthome.widget.SnEditText;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * @author Sig
 * @version 1.1
 */
public class MobileFragment extends Fragment {
	private static final String TAG = MobileFragment.class.getSimpleName();

	private static MobileFragment mMobileFragment = null;
	private View mView = null;
	private SnEditText MmobileNumEet = null;
	// 4个串口
	private SerialControl mComA = null;
	// 刷新显示线程
	private DispQueueThread mDispQueue = null;
	private ProgressDialog mProgDialog = null;

	private boolean isRing = false;

	/**
	 * 默认构造方法
	 */
	private MobileFragment() {
	}

	/**
	 * 获取单例
	 * 
	 * @return the mMobileFragment
	 */
	public static MobileFragment getInstance() {
		if (mMobileFragment == null)
			mMobileFragment = new MobileFragment();
		return mMobileFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_mobile, container, false);
		return mView;
	}

	@Override
	public void onResume() {
		super.onResume();
		initView();
	}

	@Override
	public void onStop() {
		mComA.stopRead();
		CloseComPort(mComA);
		super.onStop();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		MmobileNumEet = (SnEditText) mView.findViewById(R.id.eet_mobile_number);
		
		mDispQueue = new DispQueueThread();
		mDispQueue.start();
		
		mComA = new SerialControl();
		mComA.setPort("/dev/ttySAC0");
		mComA.setBaudRate("9600");
		OpenComPort(mComA);
		
		getPrdouceId();
		
		isRing = false;
		mView.findViewById(R.id.btn_dialog).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 获取电话号码
				String mobile = MmobileNumEet.getText().toString().trim();
				if (mobile != null && !mobile.equals("") && mobile.length() == 11) {
					mComA.stopRead();
					String cmd = "AT+QAUDCH=0";
					Log.d(TAG, "1cmd=" + cmd);
					String result = mComA.sendAT(cmd);
					Log.d(TAG, "1result=" + result);
					cmd = "AT+QMIC=0,15";
					Log.d(TAG, "2cmd=" + cmd);
					result = mComA.sendAT(cmd);
					Log.d(TAG, "2result=" + result);
					cmd = "ATD" + mobile + ";";
					Log.d(TAG, "3cmd=" + cmd);
					result = mComA.sendAT(cmd);
					Log.d(TAG, "3result=" + result);
					mComA.startRead();
					callDialog();
				} else {
					ShowMessage(R.string.mobile);
				}
			}
		});

		final Handler myHandler = new Handler(new Handler.Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				// 如果该消息是本程序所发送的
				if (msg.what == 0x1233) {
					Log.d(TAG, "dialog start");
					reviceDialog();
				} else if (msg.what == 0x1234) {
					/*
					 * builder.create().cancel();
					 */
					if (mProgDialog != null) {
						mProgDialog.dismiss();
						mProgDialog = null;
					}
				}
				return false;
			}
		});

		// 定义一个计时器，让该计时器周期性地执行指定任务
		new Timer().schedule(new TimerTask() {
			public void run() {
				// 新启动的线程无法访问该Activity里的组件
				// 所以需要通过Handler发送信息
				if (isRing) {
					Message msg = new Message();
					msg.what = 0x1233;
					// 发送消息
					myHandler.sendMessage(msg);
				} else {
					Message msg = new Message();
					msg.what = 0x1234;
					// 发送消息
					myHandler.sendMessage(msg);
				}
			}
		}, 0, 800);
	}

	//
	/**
	 * 串口控制类
	 * 
	 * @author Sig
	 * @version 1.1
	 *
	 */
	private class SerialControl extends SerialHelper {

		public SerialControl() {
		}

		@Override
		protected void onDataReceived(final ComBean ComRecData) {
			// 数据接收量大或接收时弹出软键盘，界面会卡顿,可能和6410的显示性能有关
			// 直接刷新显示，接收数据量大时，卡顿明显，但接收与显示同步。
			// 用线程定时刷新显示可以获得较流畅的显示效果，但是接收数据速度快于显示速度时，显示会滞后。
			// 最终效果差不多-_-，线程定时刷新稍好一些。
			mDispQueue.AddQueue(ComRecData);// 线程定时刷新显示(推荐)
		}
	}

	private void OpenComPort(SerialHelper ComPort) {
		try {
			ComPort.open();
		} catch (SecurityException e) {
			ShowMessage(R.string.openComError);
		} catch (IOException e) {
			ShowMessage(R.string.openComError1);
		} catch (InvalidParameterException e) {
			ShowMessage(R.string.openComError2);
		}
	}

	/**
	 * 显示消息
	 * 
	 * @param sMsg
	 */
	private void ShowMessage(int sMsg) {
		Toast.makeText(getActivity(), sMsg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 刷新显示线程
	 * 
	 * @author Sig
	 * @version 1.1
	 */
	private class DispQueueThread extends Thread {
		private Queue<ComBean> QueueList = new LinkedList<ComBean>();

		@Override
		public void run() {
			while (!isInterrupted()) {
				final ComBean ComData;
				while ((ComData = QueueList.poll()) != null) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							DispRecData(ComData);
						}
					});
					try {
						Thread.sleep(100);// 显示性能高的话，可以把此数值调小
					} catch (InterruptedException e) {
						throw new RuntimeException("线程中断异常", e);
					}
					break;
				}
			}
		}

		public synchronized void AddQueue(ComBean ComData) {
			QueueList.add(ComData);
		}
	}

	/**
	 * 显示接收数据
	 * 
	 * @param ComRecData
	 */
	private void DispRecData(ComBean ComRecData) {
		if (ComRecData.bRec != null && !ComRecData.bRec.equals("")) {
			Log.d(TAG, ComRecData.bRec);
			if (ComRecData.bRec.indexOf("RING") != -1) {
				isRing = true;
			} else if (ComRecData.bRec.trim().indexOf("NO CARRIER") != -1) {
				isRing = false;
			}
		}
	}

	private void CloseComPort(SerialHelper ComPort) {
		if (ComPort != null) {
			ComPort.close();
		}
	}

	@SuppressWarnings("deprecation")
	private void reviceDialog() {
		if (mProgDialog == null) {
			// 创建ProgressDialog对象
			mProgDialog = new ProgressDialog(getActivity());
			// 设置进度条风格为圆形
			mProgDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.animated_rotate_loading));
			mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			// 设置进度条 提示信息
			mProgDialog.setMessage(getString(R.string.dia_msg));
			// 设置进度条图标
			// 设置进度条的进度条是否不确定
			mProgDialog.setIndeterminate(true);
			// 设置对话框是否可以按退回键取消
			mProgDialog.setCancelable(true);
			// 设置一个Button
			mProgDialog.setButton(getString(R.string.revice), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mProgDialog.dismiss();// 关闭对话框
					String cmd = "ATA";
					Log.d(TAG, "0cmd=" + cmd);
					String result = mComA.sendAT(cmd);
					Log.d(TAG, "0result=" + result);

				}
			});

			mProgDialog.setButton2(getString(R.string.refuse), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mProgDialog.dismiss();// 关闭对话框
					String cmd = "ATH";
					Log.d(TAG, "0cmd=" + cmd);
					String result = mComA.sendAT(cmd);
					Log.d(TAG, "0result=" + result);

				}
			});

			// 让显示对话框
			mProgDialog.show();
		}
	}

	@SuppressWarnings("deprecation")
	private void callDialog() {
		// 创建ProgressDialog对象
		ProgressDialog mProgDialog = new ProgressDialog(getActivity());
		// 设置进度条风格为圆形
		mProgDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.animated_rotate_loading));
		mProgDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 设置进度条 提示信息
		mProgDialog.setMessage(getString(R.string.call_msg));
		// 设置进度条图标
		// 设置进度条的进度条是否不确定
		mProgDialog.setIndeterminate(true);
		// 设置对话框是否可以按退回键取消
		mProgDialog.setCancelable(true);
		// 设置一个Button
		mProgDialog.setButton(getString(R.string.finish_call_msg), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();// 关闭对话框

				String cmd = "ATH";
				Log.d(TAG, "0cmd=" + cmd);
				String result = mComA.sendAT(cmd);
				Log.d(TAG, "0result=" + result);

			}
		});

		// 让显示对话框
		mProgDialog.show();
	}

	private void getPrdouceId() {
		String cmd = "AT+CGMM";
		Log.d(TAG, "0cmd=" + cmd);
		String result = mComA.sendAT(cmd);
		Log.d(TAG, "0result=" + result);
	}
}
