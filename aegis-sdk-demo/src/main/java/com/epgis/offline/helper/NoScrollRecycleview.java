package com.epgis.offline.helper;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;

/**
 * Created by zhiwei on 2020/2/16.
 */

public class NoScrollRecycleview extends LinearLayoutManager {

	private boolean isScrollEnabled = true;

	public NoScrollRecycleview(Context context) {
		super(context);
	}

	public NoScrollRecycleview(Context context, int orientation, boolean reverseLayout) {
		super(context, orientation, reverseLayout);
	}

	public NoScrollRecycleview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	public void setScrollEnabled(boolean flag) {
		this.isScrollEnabled = flag;
	}

	@Override
	public boolean canScrollVertically() {
		return isScrollEnabled && super.canScrollVertically();
	}
}
