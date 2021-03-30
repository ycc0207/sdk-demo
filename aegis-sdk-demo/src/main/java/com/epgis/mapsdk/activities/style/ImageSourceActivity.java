package com.epgis.mapsdk.activities.style;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.geometry.LatLngQuad;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.RasterLayer;
import com.epgis.mapsdk.style.sources.ImageSource;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class ImageSourceActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

    private static final String ID_IMAGE_SOURCE = "animated_image_source";
    private static final String ID_IMAGE_LAYER = "animated_image_layer";

    private boolean flag;

    private Style style;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_imagesource);
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
                        ImageSourceActivity.this.style = style;
                        LatLngQuad quad = new LatLngQuad(
                                new LatLng(24.4870400000, 118.1810890000),
                                new LatLng(24.4870400000, 118.1610890000),
                                new LatLng(24.4670400000, 118.1610890000),
                                new LatLng(24.4670400000, 118.1810890000)
                        );
                        // 添加图片数据源到地图
                        style.addSource(new ImageSource(ID_IMAGE_SOURCE, quad, R.drawable.miami_beach));
                        style.addLayer(new RasterLayer(ID_IMAGE_LAYER, ID_IMAGE_SOURCE));
                    }
                });
            }
        });
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    ((ImageSource) style.getSource(ID_IMAGE_SOURCE)).setImage(R.drawable.miami_beach);
                } else {
                    ((ImageSource) style.getSource(ID_IMAGE_SOURCE)).setImage(R.drawable.miami_beach_black);
                }
                flag = !flag;
            }
        });
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
