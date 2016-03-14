package com.gec.smarthome.fragment;

import java.util.ArrayList;
import java.util.List;

import com.gec.smarthome.R;
import com.gec.smarthome.activity.MainActivity;
import com.gec.smarthome.activity.PlayAudioActivity;
import com.gec.smarthome.activity.PlayVideoActivity;
import com.gec.smarthome.activity.RecordVideoActivity;
import com.gec.smarthome.activity.TakePhotoActivity;
import com.gec.smarthome.adapter.LeftMenuAdapter;
import com.gec.smarthome.bean.MenuBean;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;

/**
 * SlidingMenu Module
 * 
 * @author Sig
 * @version 1.1
 */
public class MainMenuFragment extends Fragment {
	private MainActivity mContext = null;
	private View mView = null;
	private ExpandableListView mMenuElv = null;
	private List<String> mGroupName = null;
	private List<List<MenuBean>> mList = null;
	private List<MenuBean> mSubList1 = null;
	private List<MenuBean> mSubList2 = null;
	private List<MenuBean> mSubList3 = null;
	private List<MenuBean> mSubList4 = null;
	private List<MenuBean> mSubList5 = null;
	private LeftMenuAdapter mAdapter = null;
	private OnChangeViewListener mOnChangeViewListener = null;
	private TitleChangedTask mTitleChangedTask = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mContext = (MainActivity) getActivity();
		mView = inflater.inflate(R.layout.fragment_main_menu, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
		initView();
	}

	public interface OnChangeViewListener {
		public void onChangeView(int groupPosition, int childPosition);
	}

	/**
	 * @param onChangeViewListener
	 *            the onChangeViewListener
	 */
	public void setOnChangeViewListener(OnChangeViewListener onChangeViewListener) {
		mOnChangeViewListener = onChangeViewListener;
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		mMenuElv = (ExpandableListView) mView.findViewById(R.id.elv_menu);
		mGroupName = new ArrayList<String>();
		mList = new ArrayList<List<MenuBean>>();
		mSubList1 = new ArrayList<MenuBean>();
		mSubList2 = new ArrayList<MenuBean>();
		mSubList3 = new ArrayList<MenuBean>();
		mSubList4 = new ArrayList<MenuBean>();
		mSubList5 = new ArrayList<MenuBean>();
		mAdapter = new LeftMenuAdapter(mContext, mGroupName, mList);
		mMenuElv.setAdapter(mAdapter);
		initMenu();
		mMenuElv.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				return true;
			}
		});
		mMenuElv.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition,
					long id) {
				if (mOnChangeViewListener != null) {
					mOnChangeViewListener.onChangeView(groupPosition, childPosition);
				}
				return false;
			}
		});
	}

	/**
	 * 初始化Menu列表
	 */
	private void initMenu() {
		mGroupName.clear();
		mList.clear();
		mSubList1.clear();
		mSubList2.clear();
		mSubList3.clear();
		mSubList4.clear();
		mSubList5.clear();
		mGroupName.add(getString(R.string.control_appliances));
		mGroupName.add(getString(R.string.monitor_data));
		mGroupName.add(getString(R.string.communication));
		mGroupName.add(getString(R.string.mutli_media));
		// 家电控制
		MenuBean mb1 = new MenuBean();
		mb1.setIconId(R.drawable.led_3);
		mb1.setName(getString(R.string.led));
		mSubList1.add(mb1);
		mb1 = new MenuBean();
		mb1.setIconId(R.drawable.motor_1);
		mb1.setName(getString(R.string.air_condition));
		mSubList1.add(mb1);
		mb1 = new MenuBean();
		mb1.setIconId(R.drawable.stepmotor_1);
		mb1.setName(getString(R.string.curtain));
		mSubList1.add(mb1);
		mb1 = new MenuBean();
		mb1.setIconId(R.drawable.pwm_1);
		mb1.setName(getString(R.string.pwm));
		mSubList1.add(mb1);
		// 数据监测
		MenuBean mb2 = new MenuBean();
		mb2.setIconId(R.drawable.gas_1);
		mb2.setName(getString(R.string.gas));
		mSubList2.add(mb2);
		mb2 = new MenuBean();
		mb2.setIconId(R.drawable.humidity);
		mb2.setName(getString(R.string.humidity));
		mSubList2.add(mb2);
		// 通信
		MenuBean mb3 = new MenuBean();
		mb3.setIconId(R.drawable.rfid_1);
		mb3.setName(getString(R.string.rfid));
		mSubList3.add(mb3);
		mb3 = new MenuBean();
		mb3.setIconId(R.drawable.phone_1);
		mb3.setName(getString(R.string.phone));
		mSubList3.add(mb3);
		// 多媒体
		MenuBean mb4 = new MenuBean();
		mb4.setIconId(R.drawable.recordvideo_1);
		mb4.setName(getString(R.string.captureimages));
		mSubList4.add(mb4);
		mb4 = new MenuBean();
		mb4.setIconId(R.drawable.recordvideo_1);
		mb4.setName(getString(R.string.recordvideo));
		mSubList4.add(mb4);
		mb4 = new MenuBean();
		mb4.setIconId(R.drawable.mediaplaysound_1);
		mb4.setName(getString(R.string.mediaplaysound));
		mSubList4.add(mb4);
		mb4 = new MenuBean();
		mb4.setIconId(R.drawable.mediaplay_2);
		mb4.setName(getString(R.string.mediaplay));
		mSubList4.add(mb4);

		mList.add(mSubList1);
		mList.add(mSubList2);
		mList.add(mSubList3);
		mList.add(mSubList4);
		for (int i = 0; i < mGroupName.size(); i++)
			mMenuElv.expandGroup(i);
		mAdapter.notifyDataSetChanged();

		setOnChangeViewListener(new OnChangeViewListener() {

			@Override
			public void onChangeView(int groupPosition, int childPosition) {
				Fragment mContent = null;
				mTitleChangedTask = new TitleChangedTask();
				switch (groupPosition) {
				case 0:
					// 灯光控制
					if (childPosition == 0) {
						mTitleChangedTask.execute(0);
						mContent = LedFragment.getInstance();
					}
					// 空调控制
					else if (childPosition == 1) {
						mTitleChangedTask.execute(1);
						mContent = AirConditionFragment.getInstance();
					}
					// 窗帘控制
					else if (childPosition == 2) {
						mTitleChangedTask.execute(2);
						mContent = CurtainFragment.getInstance();
					}
					// 蜂鸣器控制
					else if (childPosition == 3) {
						mTitleChangedTask.execute(3);
						mContent = BeepFragment.getInstance();
					}
					break;
				case 1:
					// 温湿度监测
					if (childPosition == 0) {
						mTitleChangedTask.execute(4);
						mContent = GasFragment.getInstance();
					}
					// 烟雾监测
					else if (childPosition == 1) {
						mTitleChangedTask.execute(5);
						mContent = HumitureFragment.getInstance();
					}
					break;
				case 2:
					// 读卡
					if (childPosition == 0) {
						mTitleChangedTask.execute(6);
						mContent = CardFragment.getInstance();
					}
					// 电话
					else if (childPosition == 1) {
						mTitleChangedTask.execute(7);
						mContent = MobileFragment.getInstance();
					}
					break;
				case 3:
					// 照相
					if (childPosition == 0) {
						startActivity(new Intent(getActivity(), TakePhotoActivity.class));
					}
					// 录像
					else if (childPosition == 1) {
						startActivity(new Intent(getActivity(), RecordVideoActivity.class));
					}
					// 音乐播放
					else if (childPosition == 2) {
						startActivity(new Intent(getActivity(), PlayAudioActivity.class));
					}
					// 视频播放
					else if (childPosition == 3) {
						startActivity(new Intent(getActivity(), PlayVideoActivity.class));
					}
					break;
				default:
					break;
				}
				if (mContent != null)
					switchFragment(mContent);
			}
		});
	}

	private class TitleChangedTask extends AsyncTask<Integer, Void, String> {

		@Override
		protected String doInBackground(Integer... params) {
			return getResources().getStringArray(R.array.titles)[params[0]];
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mContext.mSnTitleBar.setTitleText(result);
		}
	}

	/**
	 * 切换Fragment
	 * 
	 * @param fragment
	 *            the fragment
	 */
	private void switchFragment(Fragment fragment) {
		if (mContext == null) {
			return;
		}
		if (mContext instanceof MainActivity) {
			mContext.switchContent(fragment);
		}
	}
}
