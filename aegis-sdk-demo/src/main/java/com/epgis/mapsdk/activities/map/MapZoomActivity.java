package com.epgis.mapsdk.activities.map;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.camera.CameraUpdateFactory;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class MapZoomActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_mapzoom);
        // 地图视图对象获取
        mapView = findViewById(R.id.mapView);
        // 用于地图恢复状态使用
        mapView.onCreate(savedInstanceState);
        // 异步回调地图视图交互对象
        mapView.getMapAsync(aegisMap -> {
            // 移图到指定位置
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());
            // 设置地图显示样式
            aegisMap.setStyle(Style.STREETS);
        });
    }

    /**
     * 地图放大一级，带相机动画设置
     *
     * @param view
     */
    public void onMapZoomIn(View view) {
        mapView.getMapAsync(aegisMap -> aegisMap.animateCamera(CameraUpdateFactory.zoomIn(), 200));
    }

    /**
     * 地图放大一级，带相机动画设置
     *
     * @param view
     */
    public void onMapZoomOut(View view) {
        mapView.getMapAsync(aegisMap -> aegisMap.animateCamera(CameraUpdateFactory.zoomOut(), 200));
    }

    /**
     * 地图放大指定级别，带相机动画设置
     *
     * @param view
     */
    public void onMapZoomTo(View view) {
        mapView.getMapAsync(aegisMap -> aegisMap.animateCamera(CameraUpdateFactory.zoomTo(0D), 200));
    }

    /**
     * 地图在当前级别基础上加上N级，带相机动画设置
     *
     * @param view
     */
    public void onMapZoomBy(View view) {
        mapView.getMapAsync(aegisMap -> aegisMap.animateCamera(CameraUpdateFactory.zoomBy(4D), 200));
    }

    /**
     * 地图在当前级别基础上加上N级并且设置缩放中心点，带相机动画设置
     *
     * @param view
     */
    public void onMapZoomByFocus(View view) {
        mapView.getMapAsync(aegisMap -> aegisMap.animateCamera(CameraUpdateFactory.zoomBy(4D, new Point(0, 0)), 200));
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
