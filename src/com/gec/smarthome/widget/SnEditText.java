package com.gec.smarthome.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.gec.smarthome.R;

/**
 * a custom EditText with a delete button
 * 
 * @author Sig
 * @version 1.1
 *
 */
public class SnEditText extends EditText {

	/**
	 * @param context
	 *            the context
	 */
	public SnEditText(Context context) {
		super(context);
	}

	/**
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public SnEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            thedefStyle
	 */
	public SnEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		Drawable[] drawable = getCompoundDrawables();
		if (!"".equals(getText().toString().trim()) && hasFocus()) {
			setCompoundDrawablesWithIntrinsicBounds(drawable[0], drawable[1],
					getContext().getResources().getDrawable(R.drawable.ic_clear), drawable[3]);
		} else {
			setCompoundDrawablesWithIntrinsicBounds(drawable[0], drawable[1], null, drawable[3]);
		}
		invalidate();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		Drawable[] drawable = getCompoundDrawables();
		if (!"".equals(getText().toString().trim()) && focused) {
			setCompoundDrawablesWithIntrinsicBounds(drawable[0], drawable[1],
					getContext().getResources().getDrawable(R.drawable.ic_clear), drawable[3]);
		} else {
			setCompoundDrawablesWithIntrinsicBounds(drawable[0], drawable[1], null, drawable[3]);
		}
		invalidate();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean re = super.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			Drawable[] drawable = getCompoundDrawables();
			if (drawable[2] != null) {
				int left = getWidth() - getPaddingRight() - drawable[2].getIntrinsicWidth();
				int right = getWidth() - getPaddingRight();
				int top = getPaddingTop();
				int bottom = getHeight() - getPaddingBottom();
				if (event.getX() < right && event.getX() > left && event.getY() > top && event.getY() < bottom) {
					setText("");
					invalidate();
				}
			}
			break;
		}
		return re;
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
		// 把光标定位到文本末尾
		CharSequence text2 = getText();
		if (text2 instanceof Spannable) {
			Spannable spanText = (Spannable) text2;
			Selection.setSelection(spanText, text2.length());
		}
	}
}
