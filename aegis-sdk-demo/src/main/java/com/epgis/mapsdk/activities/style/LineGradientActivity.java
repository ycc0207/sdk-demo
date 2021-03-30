package com.epgis.mapsdk.activities.style;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.FeatureCollection;
import com.epgis.commons.geojson.LineString;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.expressions.Expression;
import com.epgis.mapsdk.style.layers.LineLayer;
import com.epgis.mapsdk.style.layers.Property;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonOptions;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.interpolate;
import static com.epgis.mapsdk.style.expressions.Expression.lineProgress;
import static com.epgis.mapsdk.style.expressions.Expression.linear;
import static com.epgis.mapsdk.style.expressions.Expression.rgb;
import static com.epgis.mapsdk.style.expressions.Expression.stop;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineCap;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineColor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineGradient;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineJoin;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineWidth;

/**
 * 添加线渐变图层示例
 * <p>
 * Created by yangsimin on 2019/6/4.
 */

public class LineGradientActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

    private Style style;

    private List<Point> routeCoordinates;

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
                        LineGradientActivity.this.style = style;
                        createLineLayer();
                    }
                });

            }
        });
    }


    private void createLineLayer() {

        initCoordinates();
        LineString lineString = LineString.fromLngLats(routeCoordinates);

        FeatureCollection featureCollection = FeatureCollection.fromFeature(Feature.fromGeometry(lineString));

        style.addSource(new GeoJsonSource("line-source", featureCollection,
                new GeoJsonOptions().withLineMetrics(true)));

        // The layer properties for our line. This is where we set the gradient colors, set the
        // line width, etc
        style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                lineCap(Property.LINE_CAP_ROUND),
                lineJoin(Property.LINE_JOIN_ROUND),
                lineWidth(14f),
                //路线颜色渐变
                lineGradient(interpolate(
                        linear(), lineProgress(),
                        stop(0f, rgb(6, 1, 255)), // blue
                        stop(0.1f, rgb(59, 118, 227)), // royal blue
                        stop(0.3f, rgb(7, 238, 251)), // cyan
                        stop(0.5f, rgb(0, 255, 42)), // lime
                        stop(0.7f, rgb(255, 252, 0)), // yellow
                        stop(1f, rgb(255, 30, 0)) // red
                ))));
    }

    private void initCoordinates() {
        routeCoordinates = new ArrayList<>();
        routeCoordinates.add(Point.fromLngLat(118.184211, 24.472924));
        routeCoordinates.add(Point.fromLngLat(118.185659, 24.480158));
        routeCoordinates.add(Point.fromLngLat(118.184232, 24.482326));
        routeCoordinates.add(Point.fromLngLat(118.180879, 24.485454));
        routeCoordinates.add(Point.fromLngLat(118.179936, 24.487698));
        routeCoordinates.add(Point.fromLngLat(118.180338, 24.48943));
        routeCoordinates.add(Point.fromLngLat(118.18264, 24.492528));
        routeCoordinates.add(Point.fromLngLat(118.17696, 24.498424));
        routeCoordinates.add(Point.fromLngLat(118.172309, 24.49937));
        routeCoordinates.add(Point.fromLngLat(118.170056, 24.490945));
        routeCoordinates.add(Point.fromLngLat(118.167645, 24.491779));
        routeCoordinates.add(Point.fromLngLat(118.166946, 24.492645));
        routeCoordinates.add(Point.fromLngLat(118.166942, 24.495502));
        routeCoordinates.add(Point.fromLngLat(118.168054, 24.497449));
        routeCoordinates.add(Point.fromLngLat(118.16806, 24.502088));
        routeCoordinates.add(Point.fromLngLat(118.17364, 24.502108));
        routeCoordinates.add(Point.fromLngLat(118.173643, 24.509926));
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
