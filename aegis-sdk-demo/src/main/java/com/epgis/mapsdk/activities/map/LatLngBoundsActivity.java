package com.epgis.mapsdk.activities.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.annotations.MarkerOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.camera.CameraUpdateFactory;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.geometry.LatLngBounds;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class LatLngBoundsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private MapView mapView;
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private boolean bInited = false;
    private CheckBox cbBoundsForTarget;

    private static final List<LatLng> latLngList = new ArrayList<LatLng>() {
        {
            add(new LatLng(25.4870400000, 118.1410890000));
            add(new LatLng(25.4470400000, 118.1510890000));
            add(new LatLng(25.4570400000, 118.1610890000));
            add(new LatLng(25.4670400000, 118.2010890000));
            add(new LatLng(25.4970400000, 118.5810890000));
            add(new LatLng(25.4270400000, 118.4810890000));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_latlngbounds);

        mapView = findViewById(R.id.mapView);    // 地图视图对象获取
        mapView.onCreate(savedInstanceState);    // 用于地图恢复状态使用
        // 异步回调地图视图交互对象
        mapView.getMapAsync(aegisMap -> {
            // 移图到指定位置
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());
            // 设置地图显示样式
            aegisMap.setStyle(Style.STREETS);
        });

        cbBoundsForTarget = findViewById(R.id.cb_camera_bounds_for_target);
        cbBoundsForTarget.setOnCheckedChangeListener(this);
    }

    /**
     * 根据经纬度范围获取相机位置
     *
     * @param view
     */
    public void onNewLatLngBounds(View view) {
        mapView.getMapAsync((AegisMap aegisMap) -> {
            if (!bInited) {
                Iterator<LatLng> iterator = latLngList.iterator();
                while (iterator.hasNext()) {
                    aegisMap.addMarker(new MarkerOptions().position(iterator.next()));
                }
                bInited = true;
            }
            aegisMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().includes(latLngList).build(), 50), 500);
        });
    }

    /**
     * 根据经纬度范围获取新的相机位置对象
     *
     * @param view
     */
    public void onNewCameraPosition(View view) {
        mapView.getMapAsync(aegisMap -> {
            CameraPosition cameraPosition = aegisMap.getCameraForLatLngBounds(new LatLngBounds.Builder().includes(latLngList).build(), 60, 60);
            aegisMap.setCameraPosition(cameraPosition);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_camera_bounds_for_target) {
            if (isChecked) {
                // 将相机的中心点范围限制在了该区域外包矩形框内，也就是地图target坐标只能在该框内变化
                mapView.getMapAsync(aegisMap -> {
                    aegisMap.setLatLngBoundsForCameraTarget(new LatLngBounds.Builder().includes(latLngList).build());
                });
            } else {
                // 设置为null取消中心点范围的限制
                mapView.getMapAsync(aegisMap -> {
                    aegisMap.setLatLngBoundsForCameraTarget(null);
                });
            }
        }
    }
}
