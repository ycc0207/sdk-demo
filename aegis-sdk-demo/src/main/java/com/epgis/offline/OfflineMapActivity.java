package com.epgis.offline;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.epgis.epgisapp.R;
import com.epgis.offline.fragment.BaseActivity;
import com.epgis.offline.fragment.OfflineMapFragment;

/**
 * Created by Lynn on 2019/3/15.
 */
public class OfflineMapActivity extends BaseActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_offline_activity);
        getSupportFragmentManager()    //
                .beginTransaction()
                .add(R.id.fragment_container, new OfflineMapFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();
    }

    @Override
    protected int fragmentLayoutId() {
        return 0;
    }
}