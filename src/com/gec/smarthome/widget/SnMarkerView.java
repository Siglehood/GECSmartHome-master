package com.gec.smarthome.widget;

import android.content.Context;
import android.widget.TextView;

import com.gec.smarthome.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.Utils;

/**
 * custom implementation of the MarkerView
 *
 * @author Sig
 * @version 1.1
 *
 */
public class SnMarkerView extends MarkerView {
	private TextView mContent;

	/**
	 * @param context
	 *            the context
	 * @param layoutRes
	 *            the layoutRes
	 */
	public SnMarkerView(Context context, int layoutRes) {
		super(context, layoutRes);
		mContent = (TextView) findViewById(R.id.tv_content);
	}

	@Override
	public void refreshContent(Entry e, int dataSetIndex) {
		if (e instanceof CandleEntry) {
			CandleEntry ce = (CandleEntry) e;
			mContent.setText("" + Utils.formatNumber(ce.getHigh(), 0, true));
		} else {
			mContent.setText("" + Utils.formatNumber(e.getVal(), 0, true));
		}
	}

	@Override
	public int getXOffset() {
		// this will center the marker-view horizontally
		return -(getWidth() / 2);
	}

	@Override
	public int getYOffset() {
		// this will cause the marker-view to be above the selected value
		return -getHeight();
	}
}
