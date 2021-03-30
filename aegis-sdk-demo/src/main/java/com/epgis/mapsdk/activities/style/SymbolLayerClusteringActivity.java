package com.epgis.mapsdk.activities.style;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.epgis.mapsdk.style.expressions.Expression;
import com.epgis.mapsdk.style.layers.CircleLayer;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonOptions;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.get;
import static com.epgis.mapsdk.style.expressions.Expression.gt;
import static com.epgis.mapsdk.style.expressions.Expression.step;
import static com.epgis.mapsdk.style.expressions.Expression.stop;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textColor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textField;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textFont;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textSize;

/**
 * 添加符号聚类图层示例
 * <p>
 * Created by yangsimin on 2019/5/30.
 */

public class SymbolLayerClusteringActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

    // 聚类点图层ID
    public static final String ClusterSymbolLayerID = "ClusterSymbolLayerID";
    private static final String ClusterSymbolLayerNumID = "ClusterSymbolLayerNumID";

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
                        SymbolLayerClusteringActivity.this.style = style;
                        createSymbolLayer();
                    }
                });

            }
        });
    }


    private void createSymbolLayer() {
        List<Feature> markerCoordinates = new ArrayList<>();
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLngLat(118.1810890000, 24.4870400000)));
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLngLat(118.1720830000, 24.4880300000)));
        markerCoordinates.add(Feature.fromGeometry(
                Point.fromLngLat(118.1910800000, 24.4860450000)));

        //添加图层数据
        style.addSource(new GeoJsonSource("marker-source",
                FeatureCollection.fromFeatures(markerCoordinates), new GeoJsonOptions()
                .withCluster(true)
                .withClusterMaxZoom(14)
                .withClusterRadius(50)));

        // 添加marker图标到地图
        style.addImage("my-marker-image", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.marker_icon_default));

        // 添加图层
        style.addLayer(new SymbolLayer("marker-layer", "marker-source")
                .withProperties(PropertyFactory.iconImage("my-marker-image")));


        //添加聚类图层
        CircleLayer circleLayer = new CircleLayer(ClusterSymbolLayerID, "marker-source");
        circleLayer.setProperties(
                PropertyFactory.circleColor(
                        step(Expression.get("point_count"), Expression.color(ContextCompat.getColor(this, R.color.flutter_10)),
                                stop(0, Expression.color(ContextCompat.getColor(this, R.color.flutter_10))),
                                stop(20, Expression.color(ContextCompat.getColor(this, R.color.flutter_20))),
                                stop(100, Expression.color(ContextCompat.getColor(this, R.color.flutter_30)))
                        )
                ),
                PropertyFactory.circleRadius(20.0f));
        circleLayer.setFilter(gt(get("point_count"), 1));
        style.addLayerAbove(circleLayer, "marker-layer");

        SymbolLayer textLayer = new SymbolLayer(ClusterSymbolLayerNumID, "marker-source");
        textLayer.setProperties(
                textField("{point_count}"),
                textSize(12f),
                textColor(Color.WHITE),
                textFont(new String[]{"Microsoft YaHei Regular"})
        );
        style.addLayerAbove(textLayer, ClusterSymbolLayerID);
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
