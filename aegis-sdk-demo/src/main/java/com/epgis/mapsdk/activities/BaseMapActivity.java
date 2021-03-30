package com.epgis.mapsdk.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

/**
 * Created by huangsiwen on 2019/1/29.
 */

public abstract class BaseMapActivity extends AppCompatActivity {

    protected MapView mapView;        // 地图视图对象
    protected AegisMap aegisMap;    // 地图交互对象

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_basemap);

        mapView = findViewById(R.id.mapView);  // 地图视图对象获取
        mapView.onCreate(savedInstanceState);  // 用于地图恢复状态使用

        // 异步回调地图视图交互对象
        mapView.getMapAsync(aegisMap -> {
            BaseMapActivity.this.aegisMap = aegisMap;
            // 设置地图显示样式
            aegisMap.setStyle(Style.STREETS);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
