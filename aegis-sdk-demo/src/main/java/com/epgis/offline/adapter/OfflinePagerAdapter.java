package com.epgis.offline.adapter;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Lynn on 2019/3/15.
 */
public class OfflinePagerAdapter extends PagerAdapter {
    private View mOfflineAllCity;
    private View mOfflineDowloadedCity;

    private ViewPager mContentViewPager;

    public OfflinePagerAdapter(ViewPager contentViewPager, View offlineAllCity, View offlineDowloadedCity) {
        mContentViewPager = contentViewPager;
        this.mOfflineAllCity = offlineAllCity;
        this.mOfflineDowloadedCity = offlineDowloadedCity;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        if (arg1 == 0) {
            mContentViewPager.removeView(mOfflineAllCity);
        } else {
            mContentViewPager.removeView(mOfflineDowloadedCity);
        }

    }

    @Override
    public void finishUpdate(View arg0) {

    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {

        if (arg1 == 0) {
            mContentViewPager.addView(mOfflineAllCity);
            return mOfflineAllCity;
        } else {
            mContentViewPager.addView(mOfflineDowloadedCity);
            return mOfflineDowloadedCity;
        }

    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == (arg1);
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {

    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {

    }
}
