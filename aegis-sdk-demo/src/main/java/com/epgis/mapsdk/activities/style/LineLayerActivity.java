package com.epgis.mapsdk.activities.style;

import android.graphics.Color;
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
import com.epgis.mapsdk.style.layers.LineLayer;
import com.epgis.mapsdk.style.layers.Property;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.get;
import static com.epgis.mapsdk.style.expressions.Expression.interpolate;
import static com.epgis.mapsdk.style.expressions.Expression.rgb;
import static com.epgis.mapsdk.style.expressions.Expression.stop;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineCap;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineColor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineGradient;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineJoin;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineOpacity;
import static com.epgis.mapsdk.style.layers.PropertyFactory.lineWidth;

/**
 * 添加符号图层示例
 * <p>
 * Created by yangsimin on 2019/6/4.
 */

public class LineLayerActivity extends AppCompatActivity {

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
                        LineLayerActivity.this.style = style;
                        createLineLayer();
                    }
                });

            }
        });
    }


    private void createLineLayer() {

        style.addSource(new GeoJsonSource("line-source",
                loadGeoJsonFromAsset("small_line.geojson")));

        style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
                PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}), //虚线，不需要虚线则删除这行代码
//                PropertyFactory.lineOpacity(0.5f),  //设置透明度
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineColor(
                        Expression.match(get("color"), rgb(0, 0, 0),
                                stop("red", rgb(247, 69, 93)),
                                stop("blue", rgb(51, 201, 235)))
                )
        ));
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
