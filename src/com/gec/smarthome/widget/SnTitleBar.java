package com.gec.smarthome.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * a custom TitleBar
 * 
 * @author Sig
 * @version 1.1
 * 
 */
public class SnTitleBar extends LinearLayout {
	private LayoutInflater mLayoutInflater = null;
	private LinearLayout.LayoutParams mLpWW = null;
	private LinearLayout mLogoViewLayout = null;
	private ImageView mLogoView = null;
	private LinearLayout mTitleViewLayout = null;
	private TextView mTitleView = null;
	private LinearLayout mRightLayout = null;
	private ImageView mOverflowView = null;

	/**
	 * @param context
	 *            the context
	 */
	public SnTitleBar(Context context) {
		super(context);
		initTitleBar(context);
	}

	/**
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public SnTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTitleBar(context);
	}

	/**
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyleAttr
	 *            the defStyleAttr
	 */
	public SnTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initTitleBar(context);
	}

	/**
	 * @param context
	 *            the context
	 */
	@SuppressLint("RtlHardcoded")
	public void initTitleBar(Context context) {
		mLayoutInflater = LayoutInflater.from(context);
		setOrientation(HORIZONTAL);
		setBackgroundColor(Color.parseColor("#1E90FF"));
		// context.getResources().getColor(android.R.color.holo_blue_bright);
		mLpWW = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLpWW.gravity = Gravity.RIGHT;
		// left layout
		mLogoViewLayout = new LinearLayout(context);
		mLogoViewLayout.setOrientation(HORIZONTAL);
		mLogoViewLayout.setPadding(20, 0, 10, 0);
		// left layout params
		LinearLayout.LayoutParams logoViewLayoutLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		logoViewLayoutLp.gravity = Gravity.LEFT | Gravity.CENTER;
		// left view
		mLogoView = new ImageView(context);
		mLogoView.setVisibility(View.GONE);
		mLogoViewLayout.addView(mLogoView, logoViewLayoutLp);
		// title layout
		mTitleViewLayout = new LinearLayout(context);
		mTitleViewLayout.setOrientation(HORIZONTAL);
		mTitleViewLayout.setPadding(0, 0, 0, 0);
		// title layout params
		LinearLayout.LayoutParams titleViewLayoutLp = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
		titleViewLayoutLp.gravity = Gravity.CENTER_VERTICAL;
		// title view
		mTitleView = new TextView(context);
		mTitleView.setGravity(Gravity.CENTER_HORIZONTAL);
		mTitleView.setTextColor(Color.rgb(255, 255, 255));
		mTitleView.setTextSize(22);
		mTitleView.setSingleLine();
		mTitleViewLayout.addView(mTitleView, titleViewLayoutLp);
		// right layout
		mRightLayout = new LinearLayout(context);
		mRightLayout.setOrientation(HORIZONTAL);
		mRightLayout.setPadding(0, 0, 20, 0);
		// right layout params
		LinearLayout.LayoutParams rightLayoutLp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		rightLayoutLp.gravity = Gravity.RIGHT;
		// overflow view
		mOverflowView = new ImageView(context);
		mOverflowView.setVisibility(View.GONE);
		mRightLayout.addView(mOverflowView, rightLayoutLp);

		addView(mLogoViewLayout, logoViewLayoutLp);
		addView(mTitleViewLayout, titleViewLayoutLp);
		addView(mRightLayout, rightLayoutLp);
	}

	/**
	 * @param drawable
	 *            the drawable
	 */
	@SuppressWarnings("deprecation")
	public void setTitleBarBgDrawable(Drawable drawable) {
		setBackgroundDrawable(drawable);
	}

	/**
	 * @param resId
	 *            the resId
	 */
	public void setTitleBarBg(int resId) {
		setBackgroundResource(resId);
	}

	/**
	 * @param color
	 *            the color
	 */
	public void setTitleBarBgColor(int color) {
		setBackgroundColor(color);
	}

	/**
	 * @param drawable
	 *            the drawable
	 */
	@SuppressWarnings("deprecation")
	public void setLogo(Drawable drawable) {
		mLogoView.setVisibility(View.VISIBLE);
		mLogoView.setBackgroundDrawable(drawable);
	}

	/**
	 * @param resId
	 *            the resId
	 */
	public void setLogo(int resId) {
		mLogoView.setVisibility(View.VISIBLE);
		mLogoView.setBackgroundResource(resId);
	}

	/**
	 * @return the mLogoView
	 */
	public ImageView getLogo() {
		return mLogoView;
	}

	/**
	 * @param text
	 *            the text
	 */
	public void setTitleText(String text) {
		mTitleView.setText(text);
	}

	/**
	 * @param resId
	 *            the resId
	 */
	public void setTitleText(int resId) {
		mTitleView.setText(resId);
	}

	/**
	 * @param size
	 *            the size
	 */
	public void setTitleSize(float size) {
		mTitleView.setTextSize(size);
	}

	/**
	 * @param bold
	 *            the bold
	 */
	public void setTitleTextBold(boolean bold) {
		TextPaint paint = mTitleView.getPaint();
		if (bold) {
			paint.setFakeBoldText(true);
		} else {
			paint.setFakeBoldText(false);
		}
	}

	/**
	 * @param color
	 *            the color
	 */
	public void setTitleTextColor(int color) {
		mTitleView.setTextColor(color);
	}

	/**
	 * @param drawable
	 *            the drawable
	 */
	@SuppressWarnings("deprecation")
	public void setOverflow(Drawable drawable) {
		mOverflowView.setVisibility(View.VISIBLE);
		mOverflowView.setBackgroundDrawable(drawable);
	}

	/**
	 * @param resId
	 *            the resId
	 */
	public void setOverflow(int resId) {
		mOverflowView.setVisibility(View.VISIBLE);
		mOverflowView.setBackgroundResource(resId);
	}

	/**
	 * clear right view
	 */
	public void clearRightView() {
		mRightLayout.removeAllViews();
	}

	/**
	 * @param rightView
	 *            the rightView
	 */
	public void addRightView(View rightView) {
		mRightLayout.addView(rightView, mLpWW);
	}

	/**
	 * @param resId
	 *            the resId
	 */
	public void addRightView(int resId) {
		mRightLayout.addView(mLayoutInflater.inflate(resId, null), mLpWW);
	}

	/**
	 * @param listener
	 *            the listener
	 */
	public void setOnLogoClickListener(OnClickListener listener) {
		mLogoView.setOnClickListener(listener);
	}

	/**
	 * @param listener
	 *            the listener
	 */
	public void setOnTtileClickListener(OnClickListener listener) {
		mTitleView.setOnClickListener(listener);
	}

	/**
	 * @param listener
	 *            the listener
	 */
	public void setOnOverflowClickListener(OnClickListener listener) {
		mOverflowView.setOnClickListener(listener);
	}

	/**
	 * @param left
	 *            the left
	 */
	@SuppressLint("RtlHardcoded")
	public void setTitleAlignLeft(boolean left) {
		if (left) {
			mTitleView.setGravity(Gravity.CENTER_VERTICAL);
		} else {
			mTitleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		}
	}
}
