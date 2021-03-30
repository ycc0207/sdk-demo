package com.epgis.mapsdk.activities.style;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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
import com.epgis.mapsdk.style.layers.Layer;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.exponential;
import static com.epgis.mapsdk.style.expressions.Expression.interpolate;
import static com.epgis.mapsdk.style.expressions.Expression.stop;
import static com.epgis.mapsdk.style.expressions.Expression.zoom;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconImage;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconSize;

/**
 * 手动刷新图层元素
 * <p>
 * Created by yangsimin on 2019/3/25.
 */

public class SymbolLayerChangeActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

    private Style style;

    private SymbolLayer symbolLayer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_symbol_change);
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
                        SymbolLayerChangeActivity.this.style = style;
                        createSymbolLayer();
                    }
                });

            }
        });
    }





    private void createSymbolLayer() {
        List<Feature> markerCoordinates = new ArrayList<>();
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLatLng(24.4870400000,118.1810890000)));
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLatLng(24.4880300000,118.1720830000)));
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLatLng(24.4860450000,118.1910800000)));

        //添加图层数据
        style.addSource(new GeoJsonSource("marker-source",
                FeatureCollection.fromFeatures(markerCoordinates)));



        // 添加marker图标到地图
        style.addImage("marker-image-default", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.marker_icon_default));
        // 添加marker图标到地图
        style.addImage("marker-image-blue", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.blue_marker));

        symbolLayer = new SymbolLayer("marker-layer", "marker-source")
                .withProperties(
                        iconImage("marker-image-default")
                );

        // 添加图层
        style.addLayer(symbolLayer);
    }

    public void onIconChange(View view) {
        if(symbolLayer != null){
            //符号图层可通过withProperties或者setProperties方法更新图层元素
            symbolLayer.withProperties(
                    iconImage("marker-image-blue")
            );
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
