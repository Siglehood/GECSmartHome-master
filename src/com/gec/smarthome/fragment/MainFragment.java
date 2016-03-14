package com.gec.smarthome.fragment;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.gec.smarthome.R;

/**
 * Main Module
 * 
 * @author Sig
 * @version 1.1
 */
public class MainFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		TextView tv = new TextView(getActivity());
		tv.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		tv.setGravity(Gravity.CENTER);
		tv.setText(getString(R.string.welcome));
		tv.setTextSize(40.0f);
		tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
		return tv;
	}
}
