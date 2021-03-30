package com.epgis.create;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.FeatureCollection;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.location.AndroidLocationEngine;
import com.epgis.mapsdk.annotations.Polygon;
import com.epgis.mapsdk.annotations.PolygonOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.plugins.location.LocationLayerPlugin;
import com.epgis.mapsdk.style.layers.CircleLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.rgb;
import static com.epgis.mapsdk.style.layers.PropertyFactory.circleColor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.circleRadius;

/**
 * @author fcy
 * 地图创建
 * 2020-10-27
 */
public class CreateActivity extends Activity implements View.OnClickListener,AegisMap.OnMapClickListener{

    private MapView mapView;
    private AegisMap mAegisMap;
    private LocationLayerPlugin locationLayerPlugin;
    private Style style;
    //默认初始化点
    private LatLng targetPos = new LatLng(32.074443, 118.803284);
    /**
     * 初始化View
     */
    private void initViews() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack));
        }
        ImageView goBack = findViewById(R.id.iv_goback);
        goBack.setOnClickListener(this);
        mapView = findViewById(R.id.create_mapview);  // 地图视图对象获取

        mapView.onCreate(savedInstanceState);  // 用于地图恢复状态使用
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull AegisMap aegisMap) {
                mAegisMap = aegisMap;
                aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        /*List<LatLng> latLngs=new ArrayList<>();
                        latLngs.add(new LatLng(31.957919,118.909926));
                        latLngs.add(new LatLng(31.557919,118.809926));
                        latLngs.add(new LatLng(31.757919,118.709926));
                        addPolygon(latLngs);*/
                    }
                });
                // 设置相机位置
                aegisMap.setCameraPosition(new CameraPosition.Builder().target(targetPos).zoom(11.0f).build());

                locationLayerPlugin = new LocationLayerPlugin(mapView, mAegisMap);
                locationLayerPlugin.setLocationLayerVisibility(true);
            }
        });
    }


    private void createCircleLayer() {
        List<Feature> markerCoordinates = new ArrayList<>();
        markerCoordinates.add(Feature.fromGeometry(Point.fromLatLng(118.1720830000,22)));
        markerCoordinates.add(Feature.fromGeometry(Point.fromLngLat(118.1720830000, 24.4880300000)));
        markerCoordinates.add(Feature.fromGeometry(Point.fromLngLat(118.1910800000, 24.4860450000)));

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

    public Polygon addPolygon(List<LatLng> latLngs) {
        PolygonOptions options = new PolygonOptions();
        Iterator<LatLng> iterator = latLngs.iterator();
        while (iterator.hasNext()) {
            options.add(iterator.next());
        }
        options.fillColor(0xffffff00);
        options.alpha(0.5f);
        Polygon polygon = mAegisMap.addPolygon(options);
        // 区域面对象监听
        mAegisMap.setOnPolygonClickListener(new AegisMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(@NonNull Polygon polygon) {
                // 实现面对象监听
            }
        });
        return polygon;
    }
    public void onStyleStreets(View view) {
        mAegisMap.setStyle(Style.STREETS);
    }

    public void onStyleStreetsRaster512(View view) {
        mAegisMap.setStyle(Style.STREETS_RASTER512);
    }

    public void onStyleStreetsDark(View view) {
        mAegisMap.setStyle(Style.STREETS_DARK);
    }

    public void onStyleSatellite512(View view) {
        mAegisMap.setStyle(Style.SATELLITE512);
    }

    public void onStyle1(View view){ mAegisMap.setStyle(Style.STREETS_LIGHT);}
    public void onStyle2(View view){ mAegisMap.setStyle(Style.STREETS_RASTER256);}
    public void onStyle3(View view){ mAegisMap.setStyle(Style.SATELLITE256);}
    public void onStyle4(View view){ mAegisMap.setStyle(Style.ROAD_APP);}
    public void onStyle5(View view){ mAegisMap.setStyle(Style.HILL_SHADE);}
    public void onStyle6(View view){ mAegisMap.setStyle(Style.HILL_SHADE_LAYER);}
    // 必须实现，视图开始创建
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    // 必须实现
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    // 必须实现
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    // 必须实现
    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    // 存储相机状态信息、调试状态、样式地址
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    // 低内存状态
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    // 必须实现
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_goback:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng latLng) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {

            });
        };
        return true;
    }
}