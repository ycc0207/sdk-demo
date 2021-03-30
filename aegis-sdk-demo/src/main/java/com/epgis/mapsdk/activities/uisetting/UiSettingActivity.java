package com.epgis.mapsdk.activities.uisetting;

import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

/**
 * Created by huangsiwen on 2019/3/26.
 */

public class UiSettingActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private RadioGroup rgSetFocal;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_uisetting);

        initView();

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

        rgSetFocal.setOnCheckedChangeListener(this);
    }

    private void initView() {
        rgSetFocal = findViewById(R.id.rg_setfocal);
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

    // 可以通过设置焦点坐标（屏幕坐标）来改变手势操作的焦点位置，一般手势操作会基于点击位置缩放或者两指中心
    // 缩放，通过改变用户焦点位置，可以指定缩放中心点
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mapView.getMapAsync(aegisMap -> {
            switch (checkedId) {
                case R.id.rb_set_left_top_focal:
                    // 设置为屏幕左上角
                    aegisMap.getUiSettings().setFocalPoint(new PointF(0f, 0f));
                    break;

                case R.id.rb_set_center_focal:
                    // 设置为屏幕中心点
                    float viewWidth = aegisMap.getWidth();
                    float viewHeight = aegisMap.getHeight();
                    aegisMap.getUiSettings().setFocalPoint(new PointF(viewWidth * 0.5f, viewHeight * 0.5f));
                    break;

                case R.id.rb_cancel_user_focal:
                    // 设置为null，将取消用户设置的焦点，恢复正常操作
                    aegisMap.getUiSettings().setFocalPoint(null);
                    break;

                default:
                    break;
            }
        });
    }
}
