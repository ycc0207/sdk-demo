package com.epgis.mapsdk.activities.style;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.FeatureCollection;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.CircleLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.rgb;
import static com.epgis.mapsdk.style.layers.PropertyFactory.circleColor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.circleRadius;
import static com.epgis.mapsdk.style.layers.PropertyFactory.circleStrokeColor;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class CircleLayerActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private Style style;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_basemap);
        // 地图视图对象获取
        mapView = findViewById(R.id.mapView);
        // 用于地图恢复状态使用
        mapView.onCreate(savedInstanceState);
        // 异步回调地图视图交互对象
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull AegisMap aegisMap) {
                // 移图到指定位置
                aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());
                // 设置地图显示样式
                aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        CircleLayerActivity.this.style = style;
                        createCircleLayer();
                    }
                });
            }
        });
    }

    private void createCircleLayer() {
        List<Feature> markerCoordinates = new ArrayList<>();
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLngLat(118.1810890000, 24.4870400000)));
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLngLat(118.1720830000, 24.4880300000)));
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLngLat(118.1910800000, 24.4860450000)));

        //添加图层数据
        style.addSource(new GeoJsonSource("circle-source",
                FeatureCollection.fromFeatures(markerCoordinates)));
        CircleLayer circles = new CircleLayer("cluster-layer", "circle-source");
        circles.setProperties(
                circleColor(rgb(247, 69, 93)),
                circleRadius(18f)
        );
        style.addLayer(circles);
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
