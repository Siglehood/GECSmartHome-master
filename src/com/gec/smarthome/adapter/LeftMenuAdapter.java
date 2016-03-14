package com.gec.smarthome.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gec.smarthome.R;
import com.gec.smarthome.bean.MenuBean;

/**
 * 适配器 侧滑菜单列表
 * 
 * @author Sig
 * @version 1.1
 */
public class LeftMenuAdapter extends BaseExpandableListAdapter {
	private Context mContext = null;
	private List<String> mGroupName = null;
	private List<List<MenuBean>> mList = null;
	private LayoutInflater mInflater = null;

	public LeftMenuAdapter(Context context, List<String> groupName, List<List<MenuBean>> list) {
		mContext = context;
		mGroupName = groupName;
		mList = list;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.main_menu_list_child, null);
			holder.mChildIcon = (ImageView) convertView.findViewById(R.id.desktop_list_child_icon);
			holder.mChildName = (TextView) convertView.findViewById(R.id.desktop_list_child_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MenuBean m = mList.get(groupPosition).get(childPosition);
		holder.mChildIcon.setImageResource(m.getIconId());
		holder.mChildName.setText(m.getName());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroupName.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mGroupName.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.main_menu_list_group, null);
			holder.mGroupName = (TextView) convertView.findViewById(R.id.desktop_list_group_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mGroupName.setText(mGroupName.get(groupPosition));
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private static class ViewHolder {
		private TextView mGroupName = null;
		private ImageView mChildIcon = null;
		private TextView mChildName = null;
	}
}
