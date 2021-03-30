package com.epgis.mapsdk.activities.style;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.expressions.Expression;
import com.epgis.mapsdk.style.layers.Layer;
import com.epgis.mapsdk.style.layers.LineLayer;
import com.epgis.mapsdk.style.layers.Property;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.io.InputStream;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.get;
import static com.epgis.mapsdk.style.expressions.Expression.linear;
import static com.epgis.mapsdk.style.expressions.Expression.rgb;
import static com.epgis.mapsdk.style.expressions.Expression.stop;
import static com.epgis.mapsdk.style.expressions.Expression.zoom;

/**
 * 线图层示例，根据比例尺设置线颜色，设置图层的最小和最大可见比例尺
 * <p>
 * Created by yangsimin on 2019/6/6.
 */

public class LineZoomLayerActivity extends AppCompatActivity {

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
                        LineZoomLayerActivity.this.style = style;
                        createLineLayer();
                    }
                });

            }
        });
    }


    private void createLineLayer() {

        style.addSource(new GeoJsonSource("line-source",
                loadGeoJsonFromAsset("small_line.geojson")));
        Layer layer = new LineLayer("linelayer", "line-source").withProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                //根据比例尺设置线的颜色
                PropertyFactory.lineColor(
                        Expression.interpolate(
                                linear(), zoom(),
                                stop(11.99, rgb(247, 69, 93)), //比例尺500米以下红色
                                stop(12, rgb(51, 201, 235)) //比例尺500米以上蓝色
                        )
                )
        );
        layer.setMinZoom(11f); //图层可见的最小比例尺200米
        layer.setMaxZoom(13f); //图层可见的最大比例尺1公里
        style.addLayer(layer);
    }

    private String loadGeoJsonFromAsset(String filename) {
        try {
            InputStream is = getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
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
