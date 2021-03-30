package com.epgis.mapsdk.activities.annotations;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.annotations.Polyline;
import com.epgis.mapsdk.annotations.PolylineOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.camera.CameraUpdateFactory;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.geometry.LatLngBounds;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

import java.util.Arrays;

/**
 * Created by huangsiwen on 2019/3/24.
 */

public class PolylineActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private Polyline polyline;    // 线对象
    private boolean isAdded;    // 线是否添加
    private LatLng target = new LatLng(24.4870400000, 118.1810890000); // 指定传入火星坐标系坐标

    private LatLng[] latLngs = new LatLng[]{new LatLng(24.4870400000, 118.1810890000),  // 不指定类型默认为思极坐标
            new LatLng(24.4070400000, 118.1010890000),
            new LatLng(24.4070400000, 118.2610890000)
    };

    private LatLng[] latLngs2 = new LatLng[]{new LatLng(25.4870400000, 118.1810890000),  // 不指定类型默认为思极坐标
            new LatLng(25.5070400000, 118.1010890000),
            new LatLng(25.4070400000, 118.2610890000)
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_polyline);
        // 地图视图对象获取
        mapView = findViewById(R.id.mapView);
        // 用于地图恢复状态使用
        mapView.onCreate(savedInstanceState);
        // 异步回调地图视图交互对象
        mapView.getMapAsync(aegisMap -> {
            // 移图到指定位置
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(target).zoom(12D).build());
            // 设置地图显示样式
            aegisMap.setStyle(Style.STREETS);
        });
    }

    /**
     * 点击按钮添加线对象回调
     *
     * @param view
     */
    public void onAddPolyline(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                if (!isAdded) {
                    // 配置线选项
                    PolylineOptions options = new PolylineOptions();
                    options.add(latLngs);
                    options.color(0xFF00FF00);
                    options.alpha(0.5f);
                    options.width(10.0f);
                    polyline = aegisMap.addPolyline(options);
                    // 按范围计算相机指定比例尺及中心点
                    aegisMap.easeCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().includes(Arrays.asList(latLngs)).build(), 20), 500);
                    isAdded = !isAdded;
                }
            });
        }
    }

    /**
     * 点击按钮移除线对象回调
     *
     * @param view
     */
    public void onRemovePolyline(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                if (polyline != null && isAdded) {
                    aegisMap.removePolyline(polyline);
                    isAdded = !isAdded;
                }
            });
        }
    }

    private PolylineOptions dashOptions;  // 虚线对象选项

    /**
     * 添加虚线绘制接口
     *
     * @param view
     */
    public void onAddDashPolyline(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (dashOptions == null) {
                // 配置线选项
                dashOptions = new PolylineOptions();
                dashOptions.add(latLngs2);
                dashOptions.color(0xFFFF0000);
                dashOptions.alpha(0.5f);
                dashOptions.width(5.0f);
                dashOptions.dashArray(new float[]{2, 2});
                aegisMap.addPolyline(dashOptions);
            }
            // 按范围计算相机指定比例尺及中心点
            aegisMap.animateCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds.Builder().includes(Arrays.asList(latLngs2)).build(), 20), 500);
        });
    }

    /**
     * 点击按钮移除所有标注回调
     *
     * @param view
     */
    public void onRemovePolylines(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                aegisMap.removeAnnotations();
                dashOptions = null; // 移除虚线对象
                isAdded = false;    // 重置标识
            });
        }
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
