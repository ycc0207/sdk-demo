package com.epgis.mapsdk.activities.style;

import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import com.epgis.mapsdk.style.expressions.Expression;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.epgis.mapsdk.style.layers.Property.ICON_ANCHOR_BOTTOM;
import static com.epgis.mapsdk.style.layers.Property.TEXT_ANCHOR_TOP;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconAnchor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconImage;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textAnchor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textColor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textField;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textFont;
import static com.epgis.mapsdk.style.layers.PropertyFactory.textSize;

/**
 * 添加符号图层（包括图片和文字）示例
 * <p>
 * Created by yangsimin on 2019/3/25.
 */

public class SymbolTextLayerActivity extends AppCompatActivity {

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
                        SymbolTextLayerActivity.this.style = style;
                        createSymbolLayer();
                    }
                });

            }
        });
    }


    private void createSymbolLayer() {


        style.addImage("sunny", BitmapFactory.decodeResource(getResources(), R.drawable.sunny));
        style.addImage("cloudy", BitmapFactory.decodeResource(getResources(), R.drawable.cloudy));


        //添加图层数据
        style.addSource(new GeoJsonSource("marker-source",
                loadGeoJsonFromAsset("symbol_points.geojson")));


        // 添加图层
        style.addLayer(new SymbolLayer("marker-layer", "marker-source")
                .withProperties(iconImage(Expression.match(
                        Expression.get("symbol"),
                        Expression.literal("晴天"), Expression.literal("sunny"),
                        Expression.literal("多云"), Expression.literal("cloudy"),
                        Expression.literal("cloudy"))),
                        textField("{text}"),
                        iconAnchor(ICON_ANCHOR_BOTTOM),
                        textAnchor(TEXT_ANCHOR_TOP),
                        textColor(Color.BLACK),
                        textSize(12f),
                        textFont(new String[]{"Microsoft YaHei Regular"})));
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
