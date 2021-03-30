package com.epgis.mapsdk.activities.style;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
import com.epgis.mapsdk.style.layers.Property;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;
import com.epgis.mapsdk.style.sources.Source;

import java.util.ArrayList;
import java.util.List;

import static com.epgis.mapsdk.style.layers.PropertyFactory.visibility;

/**
 * 添加符号图层示例
 * <p>
 * Created by yangsimin on 2019/3/25.
 */

public class ShowAndHideLayerActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

    private Style style;
    private Button btnShowLayer, btnCreateLayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_show_hide_layer);
        btnShowLayer = findViewById(R.id.btn_show_layer);
        btnCreateLayer = findViewById(R.id.btn_create_layer);
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
                        ShowAndHideLayerActivity.this.style = style;
                        createSymbolLayer();
                    }
                });

            }
        });

        //显示和隐藏图层
        btnShowLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleLayer();
            }
        });

        //创建和删除图层
        btnCreateLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAndDeleteLayer();
            }
        });
    }


    /**
     * 创建图层
     */
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
                FeatureCollection.fromFeatures(markerCoordinates)));

        // 添加marker图标到地图
        style.addImage("my-marker-image", BitmapFactory.decodeResource(
                this.getResources(), R.drawable.marker_icon_default));

        // 添加图层
        style.addLayer(new SymbolLayer("marker-layer", "marker-source")
                .withProperties(PropertyFactory.iconImage("my-marker-image")));

        //创建图层可以设置，选择创建在某个图层之上或者之下
//		style.addLayerAbove(null, null);
//		style.addLayerBelow(null, null);
    }

    /**
     * 隐藏和显示图层
     */
    private void toggleLayer() {
        Layer layer = style.getLayer("marker-layer");
        if (layer != null) {
            if (Property.VISIBLE.equals(layer.getVisibility().getValue())) {
                layer.setProperties(visibility(Property.NONE));
                btnShowLayer.setText("显示图层");
            } else {
                layer.setProperties(visibility(Property.VISIBLE));
                btnShowLayer.setText("隐藏图层");
            }
        }
    }

    /**
     * 创建和删除图层
     */
    private void createAndDeleteLayer() {
        Layer layer = style.getLayer("marker-layer");
        if (layer != null) {
            removeMapLayer();
            btnCreateLayer.setText("创建图层");
        } else {
            createSymbolLayer();
            btnCreateLayer.setText("删除图层");
        }
    }

    /**
     * 删除图层
     */
    private void removeMapLayer() {
        if (style != null) {
            Layer layer = style.getLayer("marker-layer");
            Source source = style.getSource("marker-source");
            if (source != null
                    && layer != null) {
                style.removeLayer(layer);
                style.removeSource(source);
            }
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
