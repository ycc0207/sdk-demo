package com.epgis.heatmap;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.HeatmapLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

import java.io.InputStream;

import static com.epgis.mapsdk.style.expressions.Expression.get;
import static com.epgis.mapsdk.style.expressions.Expression.heatmapDensity;
import static com.epgis.mapsdk.style.expressions.Expression.interpolate;
import static com.epgis.mapsdk.style.expressions.Expression.linear;
import static com.epgis.mapsdk.style.expressions.Expression.literal;
import static com.epgis.mapsdk.style.expressions.Expression.rgb;
import static com.epgis.mapsdk.style.expressions.Expression.rgba;
import static com.epgis.mapsdk.style.expressions.Expression.stop;
import static com.epgis.mapsdk.style.expressions.Expression.zoom;
import static com.epgis.mapsdk.style.layers.PropertyFactory.heatmapColor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.heatmapIntensity;
import static com.epgis.mapsdk.style.layers.PropertyFactory.heatmapOpacity;
import static com.epgis.mapsdk.style.layers.PropertyFactory.heatmapRadius;
import static com.epgis.mapsdk.style.layers.PropertyFactory.heatmapWeight;

/**
 * 热力图图层示例
 * @author fcy
 * 2020-10-28
 */

public class HeatMapActivity extends AppCompatActivity implements View.OnClickListener{

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(32.441960213339437, 118.806995638756251);

    private static final String HEATMAP_ID = "heatmap_layer_id";
    private static final String HEATMAP_SOURCE = "heatmap_layer_source";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heatmap);
        ImageView goBack = findViewById(R.id.iv_goback);
        goBack.setOnClickListener(this);
        // 地图视图对象获取
        mapView = findViewById(R.id.mapView);
        // 用于地图恢复状态使用
        mapView.onCreate(savedInstanceState);
        // 异步回调地图视图交互对象
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull AegisMap aegisMap) {
                // 移图到指定位置
                aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(14).build());
                // 设置地图显示样式
                aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        addEarthquakeSource(style);
                        addHeatmapLayer(style);
                    }
                });
            }
        });
    }


    private void addEarthquakeSource(@NonNull Style loadedMapStyle) {
        try {
            loadedMapStyle.addSource(new GeoJsonSource(HEATMAP_SOURCE, loadGeoJsonFromAsset("heatmap.geojson")));
        } catch (Exception e) {
        }
    }

    private void addHeatmapLayer(@NonNull Style loadedMapStyle) {
        HeatmapLayer layer = new HeatmapLayer(HEATMAP_ID, HEATMAP_SOURCE);
        layer.setProperties(

                /**
                 * 设置热力图的颜色，热力图是颜色在强度在给定的两个范围进颜色记性线性变化
                 */
                heatmapColor(
                        interpolate(
                                linear(), heatmapDensity(),
                                literal(0), rgba(255, 0, 0, 0),
                                literal(0.1), rgba(0, 30, 255, .6),
                                literal(0.2), rgba(7, 208, 255, .6),
                                literal(0.3), rgb(44,201,70),
                                literal(0.4), rgb(213,251,12),
                                literal(0.6), rgb(224,78,78),
                                literal(0.8), rgb(243,57,0),
                                literal(1), rgba(243, 57, 0, .6),
                                literal(1.5), rgba(243, 57, 0, .8)
                        )
                ),

                /**
                 * 获取配一个要素的mag属性的值来设置每一个点对热力图强度的贡献，
                 * 事例表示当mag的数值在0~6之间的时候对热力图强度贡献为从0到1进行线性贡献，
                 * 大于6表示贡献为1，小于0表示无贡献
                 */
                heatmapWeight(
                        interpolate(
                                linear(), get("mag"),
                                stop(0, 0),
                                stop(6, 1.5)
                        )
                ),

                /**
                 * 根据地图的缩放级别类设置热力图的强度，当缩放级别在0~9之间进行线性变化的时候，
                 * 热力图的强度从1~3进行线性变化，小于0是 强度为0，大于9时强度为3
                 */
                heatmapIntensity(
                        interpolate(
                                linear(), zoom(),
                                stop(0, 1),
                                stop(17, 1)
                        )
                ),

                /**
                 * 设置在不同缩放级别的时候更改热力图计算的半径
                 */
                heatmapRadius(
                        interpolate(
                                linear(), zoom(),
                                stop(0, 8),
                                stop(17, 50)
                        )
                ),

                /**
                 * 设置热力图的透明度，是整体图形的透明度，在zoom处于7~15间将热力图逐渐的透明，在zoom大于15的时候完全透明
                 */
                heatmapOpacity(
                        interpolate(
                                linear(), zoom(),
                                stop(5, 0.8),
                                stop(17, 0.8)
                        )
                )
        );

        loadedMapStyle.addLayer(layer);
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
}
