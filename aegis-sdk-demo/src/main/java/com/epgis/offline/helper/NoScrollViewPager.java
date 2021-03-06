package com.epgis.offline.helper;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhiwei on 2020/2/16.
 */

public class NoScrollViewPager extends ViewPager {
	private boolean scrollable = false;

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPager(Context context) {
		super(context);
	}

	public void setScrollable(boolean scrollable) {
		this.scrollable = scrollable;
	}

	public boolean isScrollable() {
		return scrollable;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (scrollable) {
			return super.onTouchEvent(ev);
		} else {
			return false;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (scrollable) {
			return super.onInterceptTouchEvent(ev);
		} else {
			return false;
		}
	}
}
