package com.epgis.mapsdk.activities.annotations;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.annotations.IconFactory;
import com.epgis.mapsdk.annotations.Marker;
import com.epgis.mapsdk.annotations.MarkerOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

/**
 * Created by huangsiwen on 2019/3/24.
 */

public class MarkerActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private AegisMap aegisMap;
    private Marker marker;        // 图标对象
    private boolean isAdded;    // 图标是否添加
    private LatLng target = new LatLng(24.4870400000, 118.1810890000); // 指定传入火星坐标系坐标

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_marker);

        mapView = findViewById(R.id.mapView);    // 地图视图对象获取
        mapView.onCreate(savedInstanceState);   // 用于地图恢复状态使用
        mapView.getMapAsync(aegisMap -> {
            this.aegisMap = aegisMap;
            // 移图到指定位置
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(target).zoom(12D).build());
            // 设置地图显示样式
            aegisMap.setStyle(Style.STREETS);
            // 在地图当前位置添加图标，不指定icon使用默认的图标
            initDefaultIconMarker(aegisMap);

            aegisMap.setOnMarkerDragListener(new AegisMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDrag(Marker marker) {
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                }

                @Override
                public void onMarkerDragStart(Marker marker) {
                }
            });
        });

    }

    /**
     * 初始化默认的图标
     *
     * @param aegisMap 地图交互对象
     */
    private void initDefaultIconMarker(AegisMap aegisMap) {
        aegisMap.setInfoWindowAdapter(new AegisMap.InfoWindowAdapter() {
            @Nullable
            @Override
            public View getInfoWindow(@NonNull Marker marker) {
                return new Button(MarkerActivity.this);
            }
        });
        MarkerOptions markerOptions = new MarkerOptions().position(target);
        Marker marker = aegisMap.addMarker(markerOptions);
        marker.setDraggable(true);
    }


    /**
     * 点击按钮添加图标回调
     *
     * @param view
     */
    public void onAddMarker(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                if (!isAdded) {
                    LatLng latLng = new LatLng(24.4880400000, 118.1920890000);
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.icon(IconFactory.getInstance(MarkerActivity.this).fromResource(R.drawable.select_point));
                    // 默认传入坐标为思极坐标
                    markerOptions.position(latLng);
                    markerOptions.title("思极地图");
                    markerOptions.snippet("我被点击了");
                    marker = aegisMap.addMarker(markerOptions);
                    marker.setDraggable(true);
                    marker.setIcon(IconFactory.getInstance(MarkerActivity.this).fromResource(R.drawable.miami_beach));
                    aegisMap.updateMarker(marker);
                    marker.setIcon(IconFactory.getInstance(MarkerActivity.this).fromResource(R.drawable.aegis_marker_icon_default));
                    aegisMap.updateMarker(marker);
                    // 移动到当前点位置
                    aegisMap.setCameraPosition(new CameraPosition.Builder().target(latLng).build());
                    isAdded = true;
                }
            });
        }
    }

    /**
     * 点击按钮移除图标回调
     *
     * @param view
     */
    public void onRemoveMarker(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                if (marker != null && isAdded) {
                    aegisMap.removeMarker(marker);
                    isAdded = false;
                }
            });
        }
    }

    /**
     * 点击按钮移除所有标注回调
     *
     * @param view
     */
    public void onRemoveMarkers(View view) {
        if (mapView != null) {
            mapView.getMapAsync(aegisMap -> {
                aegisMap.removeAnnotations();
                isAdded = false;
            });
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
