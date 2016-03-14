package com.gec.smarthome.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

/**
 * a custom horizontal ProgressBar
 *
 * @author Sig
 * @version 1.1
 *
 */
public class SnHorizontalProgressBar extends View {
	private int progress = 0;
	private int max = 100;
	/** 绘制轨迹 */
	private Paint pathPaint = null;
	/** 绘制填充 */
	private Paint mFillPaint = null;
	/** 路径宽度 */
	private int pathWidth = 35;
	private int width = 0;
	private int height = 0;
	/** 灰色轨迹 */
	private int pathColor = 0xFFF0EEDF;
	/** 灰色轨迹边 */
	private int pathBorderColor = 0xFFD2D1C4;
	/** 梯度渐变的填充颜色 */
	private int[] fillColors = new int[] { 0xFF3DF346, 0xFF02C016 };
	/** 指定了光源的方向和环境光强度来添加浮雕效果 */
	private EmbossMaskFilter mEmboss = null;
	/** 设置光源的方向 */
	float[] direction = new float[] { 1, 1, 1 };
	/** 设置环境光亮度 */
	float light = 0.4f;
	/** 选择要应用的反射等级 */
	float specular = 6;
	/** 向 mask应用一定级别的模糊 */
	float blur = 3.5f;
	/** 指定了一个模糊的样式和半径来处理 Paint 的边缘 */
	private BlurMaskFilter mBlur = null;
	/** 监听器 */
	private OnProgressListener mOnProgressListener = null;
	/** view重绘的标记 */
	private boolean reset = false;

	/**
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public SnHorizontalProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		pathPaint = new Paint();
		// 设置是否抗锯齿
		pathPaint.setAntiAlias(true);
		// 帮助消除锯齿
		pathPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		// 设置中空的样式
		pathPaint.setStyle(Paint.Style.FILL);
		pathPaint.setDither(true);
		// pathPaint.setStrokeJoin(Paint.Join.ROUND);
		mFillPaint = new Paint();
		// 设置是否抗锯齿
		mFillPaint.setAntiAlias(true);
		// 帮助消除锯齿
		mFillPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		// 设置中空的样式
		mFillPaint.setStyle(Paint.Style.FILL);
		mFillPaint.setDither(true);
		// mFillPaint.setStrokeJoin(Paint.Join.ROUND);
		mEmboss = new EmbossMaskFilter(direction, light, specular, blur);
		mBlur = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);
	}

	/**
	 * @return the progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * @param progress
	 *            the progress
	 */
	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
		if (mOnProgressListener != null) {
			if (max <= progress) {
				mOnProgressListener.onComplete();
			} else {
				mOnProgressListener.onProgress(progress);
			}
		}
	}

	/**
	 * @return the max
	 */
	public int getMax() {
		return max;
	}

	/**
	 * @param max
	 *            the max
	 */
	public void setMax(int max) {
		this.max = max;
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (reset) {
			canvas.drawColor(Color.TRANSPARENT);
			reset = false;
		}
		width = getMeasuredWidth();
		height = getMeasuredHeight();
		// 设置画笔颜色
		pathPaint.setColor(pathColor);
		// 设置画笔宽度
		pathPaint.setStrokeWidth(pathWidth);
		// 添加浮雕效果
		pathPaint.setMaskFilter(mEmboss);
		canvas.drawRect(0, 0, width, height, pathPaint);
		pathPaint.setColor(pathBorderColor);
		canvas.drawRect(0, 0, width, height, pathPaint);
		LinearGradient linearGradient = new LinearGradient(0, 0, width, height, fillColors[0], fillColors[1],
				TileMode.CLAMP);
		mFillPaint.setShader(linearGradient);
		// 模糊效果
		mFillPaint.setMaskFilter(mBlur);
		// 设置线的类型,边是圆的
		mFillPaint.setStrokeCap(Paint.Cap.ROUND);
		mFillPaint.setStrokeWidth(pathWidth);
		canvas.drawRect(0, 0, ((float) progress / max) * width, height, mFillPaint);
	}

	/**
	 * @return the mOnProgressListener
	 */
	public OnProgressListener getOnProgressListener() {
		return mOnProgressListener;
	}

	/**
	 * @param onProgressListener
	 *            the onProgressListener
	 */
	public void setOnProgressListener(OnProgressListener onProgressListener) {
		mOnProgressListener = onProgressListener;
	}

	/**
	 * 重置进度
	 */
	public void reset() {
		reset = true;
		progress = 0;
		invalidate();
	}

	/**
	 * 获取颜色
	 * 
	 * @return the fillColors
	 */
	public int[] getFillColors() {
		return fillColors;
	}

	/**
	 * 设置颜色
	 * 
	 * @param fillColors
	 *            the fillColors
	 */
	public void setFillColors(int[] fillColors) {
		this.fillColors = fillColors;
		invalidate();
	}

	/**
	 * 获取背景色
	 * 
	 * @return the pathColor
	 */
	public int getPathColor() {
		return pathColor;
	}

	/**
	 * 设置背景色
	 */
	public void setPathColor(int pathColor) {
		this.pathColor = pathColor;
		invalidate();
	}

	/**
	 * 获取背景边框色
	 * 
	 * @return the pathBorderColor
	 */
	public int getPathBorderColor() {
		return pathBorderColor;
	}

	/**
	 * 设置背景边框色
	 * 
	 * @return
	 */
	public void setPathBorderColor(int pathBorderColor) {
		this.pathBorderColor = pathBorderColor;
		invalidate();
	}

	/**
	 * 进度监听器
	 */
	public interface OnProgressListener {
		/**
		 * @param progress
		 *            the progress
		 */
		public void onProgress(int progress);

		public void onComplete();
	}
}
