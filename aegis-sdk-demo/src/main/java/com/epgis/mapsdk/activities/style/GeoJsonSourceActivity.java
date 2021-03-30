package com.epgis.mapsdk.activities.style;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.FillLayer;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by huangsiwen on 2019/3/23.
 */

public class GeoJsonSourceActivity extends AppCompatActivity {

    private static final String SourceID = "GeoJsonSourceID-0";
    private static final String LayerID = "GeoJsonLayerID-1";

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

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
                        addPolygonGeoJsonSource(style);
                    }
                });
            }
        });
    }

    // 添加GeoJson数据源图层，当前添加一块矩形面
    private void addPolygonGeoJsonSource(Style style) {
        String buffer = getJsonFromAssets("small_poylgon.geojson");
        if (buffer != null) {
            GeoJsonSource source = new GeoJsonSource(SourceID, buffer);
            style.addSource(source);
            FillLayer layer = new FillLayer(LayerID, source.getId());
            layer.setProperties(
                    PropertyFactory.fillColor("rgb(255, 0, 0)"),
                    PropertyFactory.fillOpacity(0.5f)
            );
            style.addLayer(layer);
        }
    }

    private String getJsonFromAssets(String name) {
        try {
            InputStream is = mapView.getContext().getResources().getAssets().open(name);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
