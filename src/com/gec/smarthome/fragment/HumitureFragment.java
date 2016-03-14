package com.gec.smarthome.fragment;

import java.util.ArrayList;

import com.gec.smarthome.R;
import com.gec.smarthome.bean.HumitureBean;
import com.gec.smarthome.library.HumitureDev;
import com.gec.smarthome.widget.SnMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.LargeValueFormatter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Humidity Module
 * 
 * @author Sig
 * @version 1.1
 */
public class HumitureFragment extends Fragment {
	private static HumitureFragment mHumidityFragment = null;

	private View mView = null;
	private Button mStartBtn = null;
	private Button mStopBtn = null;
	private BarChart mChart = null;
	private Typeface mType = null;
	private HumitureDev mHumDev = null;

	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (isAdded()) {
				HumitureBean humitureBean = (HumitureBean) msg.obj;
				float temp = humitureBean.getTemp();
				float humidity = humitureBean.getHumidity();
				ArrayList<String> xVals = new ArrayList<String>();
				xVals.add(0 + "");

				ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
				ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

				yVals1.add(new BarEntry(new float[] { temp }, 0));
				yVals2.add(new BarEntry(new float[] { humidity }, 0));

				BarDataSet set1 = new BarDataSet(yVals1,
						getString(R.string.label_temperature) + "/" + getString(R.string.temperature_unit));
				set1.setColor(Color.rgb(104, 241, 175));
				set1.setValueTextSize(30.0f);

				BarDataSet set2 = new BarDataSet(yVals2,
						getString(R.string.label_humidity) + "/" + getString(R.string.humidity_unit));
				set2.setColor(Color.rgb(164, 228, 251));
				set2.setValueTextSize(30.0f);

				ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
				dataSets.add(set1);
				dataSets.add(set2);

				BarData data = new BarData(xVals, dataSets);
				data.setGroupSpace(80f);
				data.setValueTypeface(mType);

				mChart.setData(data);
				mChart.invalidate();
			}
			return false;
		}
	});

	/**
	 * 默认构造方法
	 */
	private HumitureFragment() {
	}

	/**
	 * 获取单例
	 * 
	 * @return the mHumidityFragment
	 */
	public static HumitureFragment getInstance() {
		if (mHumidityFragment == null)
			mHumidityFragment = new HumitureFragment();
		return mHumidityFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_humidity, container, false);
		return mView;
	}

	@Override
	public void onStart() {
		super.onStart();
		initChart();
		initView();
	}

	@Override
	public void onStop() {
		mHumDev.closeHumiture();
		super.onStop();
	}

	/**
	 * 初始化报表
	 */
	private void initChart() {
		mChart = (BarChart) mView.findViewById(R.id.chart1);
		mChart.animateXY(1500, 1500);
		mChart.setDrawBorders(false);
		mChart.setPinchZoom(false);
		mChart.setDrawBarShadow(false);
		mChart.setDrawGridBackground(false);
		mChart.setDescription(getString(R.string.barchar_desc));

		SnMarkerView mv = new SnMarkerView(getActivity(), R.layout.custom_marker_view);
		mChart.setMarkerView(mv);
		mType = Typeface.createFromAsset(getActivity().getAssets(), "Roboto-Medium.ttf");

		Legend l = mChart.getLegend();
		l.setPosition(LegendPosition.RIGHT_OF_CHART_INSIDE);
		l.setTypeface(mType);
		l.setYOffset(0f);
		l.setYEntrySpace(0f);
		l.setTextSize(30.0f);

		XAxis xAxis = mChart.getXAxis();
		xAxis.setTypeface(mType);

		YAxis yAxis = mChart.getAxisLeft();
		yAxis.setTypeface(mType);
		yAxis.setValueFormatter(new LargeValueFormatter());
		yAxis.setDrawGridLines(false);
		yAxis.setSpaceTop(30f);
		yAxis.setTextSize(30.0f);

		mChart.getAxisRight().setEnabled(false);

		ArrayList<String> xVals = new ArrayList<String>();
		xVals.add(0 + "");

		ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
		ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

		yVals1.add(new BarEntry(new float[] { 100.0f }, 0));
		yVals2.add(new BarEntry(new float[] { 100.0f }, 0));

		BarDataSet set1 = new BarDataSet(yVals1,
				getString(R.string.label_temperature) + "/" + getString(R.string.temperature_unit));
		set1.setColor(Color.rgb(104, 241, 175));
		set1.setValueTextSize(30.0f);

		BarDataSet set2 = new BarDataSet(yVals2,
				getString(R.string.label_humidity) + "/" + getString(R.string.humidity_unit));
		set2.setColor(Color.rgb(164, 228, 251));
		set2.setValueTextSize(30.0f);

		ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		dataSets.add(set1);
		dataSets.add(set2);

		BarData data = new BarData(xVals, dataSets);
		data.setGroupSpace(100.0f);
		data.setValueTypeface(mType);
		mChart.setData(data);
		mChart.invalidate();
	}

	/**
	 * 初始化view
	 */
	private void initView() {
		mStartBtn = (Button) mView.findViewById(R.id.btn_start);
		mStopBtn = (Button) mView.findViewById(R.id.btn_stop);

		// 开始
		mStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mStartBtn.setEnabled(false);
				mHumDev.startGetData();
			}
		});

		// 停止
		mStopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				mStartBtn.setEnabled(true);
				mHumDev.stopGetData();
			}
		});

		mHumDev = new HumitureDev(mHandler);
		mHumDev.openHumiture();
	}
}
