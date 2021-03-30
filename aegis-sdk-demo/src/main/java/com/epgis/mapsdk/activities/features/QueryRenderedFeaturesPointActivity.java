package com.epgis.mapsdk.activities.features;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.annotations.Marker;
import com.epgis.mapsdk.annotations.MarkerOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;

import java.util.Iterator;
import java.util.List;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class QueryRenderedFeaturesPointActivity extends AppCompatActivity implements AegisMap.OnCameraIdleListener, MapView.OnDidFinishRenderingMapListener {

    private static final String TAG = QueryRenderedFeaturesPointActivity.class.getSimpleName();

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_query_rendered_point);

        mapView = findViewById(R.id.mapView);    // 地图视图对象获取
        mapView.onCreate(savedInstanceState);   // 用于地图恢复状态使用
        mapView.getMapAsync(aegisMap -> {    // 异步回调地图视图交互对象
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build()); // 移图到指定位置
            aegisMap.setStyle(Style.STREETS);   // 设置地图显示样式
            aegisMap.addOnCameraIdleListener(this); // 移动结束时才会回调的接口
        });
        mapView.addOnDidFinishRenderingMapListener(this);
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

    /**
     * 该接口会在手势移动结束的时候回调一次
     */
    @Override
    public void onCameraIdle() {
        mapView.post(() -> mapView.getMapAsync(aegisMap -> {
            PointF pointF = new PointF(aegisMap.getWidth() / 2, aegisMap.getHeight() / 2);
            List<Feature> features = aegisMap.queryRenderedFeatures(pointF);
            Log.d(TAG, pointF.toString());

            Iterator<Feature> iterator = features.iterator();
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                Log.d(TAG, feature.toJson());

                if (feature.geometry() instanceof Point) {
                    if (feature.getProperty("NAME") != null) {
                        Toast.makeText(this, feature.getProperty("NAME").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }));
    }

    /**
     * 该接口会在地图渲染完完整第一帧的时候回调，且只会回调一次，除非发生样式切换
     *
     * @param fully true则表示渲染完成
     */
    @Override
    public void onDidFinishRenderingMap(boolean fully) {
        mapView.post(() -> mapView.getMapAsync(aegisMap -> {
            PointF pointF = new PointF(aegisMap.getWidth() / 2, aegisMap.getHeight() / 2);
            List<Feature> features = aegisMap.queryRenderedFeatures(pointF);
            Log.d(TAG, pointF.toString());

            Iterator<Feature> iterator = features.iterator();
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                Log.d(TAG, feature.toJson());
            }
        }));
    }
}
