package com.epgis.mapsdk.activities.traffic;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.plugins.traffic.TrafficLayerPlugin;
import com.epgis.mapsdk.plugins.traffic.TrafficMapLayer;

import java.io.File;

/**
 * Created by huangsiwen on 2019/4/3.
 */

public class MapTrafficActivity extends AppCompatActivity implements AegisMap.OnCameraIdleListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = MapTrafficActivity.class.getSimpleName();

    private MapView mapView;    // 地图视图对象
    private AegisMap aegisMap;  // 地图交互对象
    private LatLng center = new LatLng(39.71833213348579, 116.12778465816723);
    private CheckBox cbDebug, trafficOpen;
    private TrafficLayerPlugin trafficLayerPlugin;
    private TextView mapStyle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_maptraffic);

        // 每次近来都清除缓存
        clearAppCache();

        // 初始化控件
        initViews();

        mapView = findViewById(R.id.mapView);  // 地图视图对象获取
        mapView.onCreate(savedInstanceState);  // 用于地图恢复状态使用
        mapView.getMapAsync(aegisMap -> {      // 异步回调地图视图交互对象
            MapTrafficActivity.this.aegisMap = aegisMap;
            // 移图到指定位置
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(11D).build());
            // 设置地图显示样式
            aegisMap.setStyle(Style.STREETS);
            // 初始化地图图层
            initMapLayer();
            // 初始化地图监听
            initListeners();
        });
    }

    /**
     * 初始化地图图层
     */
    private void initMapLayer() {
        if (aegisMap != null) {
            // 定义图层插件管理对象
            trafficLayerPlugin = new TrafficLayerPlugin(mapView, aegisMap);
            // 初始设置路况显示
            trafficLayerPlugin.setLayerVisibility(true);
        }
    }

    private int trafficMode = TrafficMapLayer.TRAFFIC_MODE_DAY;

    Handler handler = new Handler();

    /**
     * 初始化控件
     */
    private void initViews() {
        cbDebug = findViewById(R.id.cb_map_debug);
        trafficOpen = findViewById(R.id.traffic_switch);
        mapStyle = findViewById(R.id.map_style);
        mapStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (trafficMode){
                    case TrafficMapLayer.TRAFFIC_MODE_DAY:
                        trafficMode = TrafficMapLayer.TRAFFIC_MODE_NIGHT;
                        aegisMap.setStyle(Style.STREETS_DARK);
                        trafficLayerPlugin.setTrafficMode(TrafficMapLayer.TRAFFIC_MODE_NIGHT);
                        mapStyle.setText("黑夜模式");
                        break;
                    case TrafficMapLayer.TRAFFIC_MODE_NIGHT:
                        trafficMode = TrafficMapLayer.TRAFFIC_MODE_SATELLITE;
                        aegisMap.setStyle(Style.SATELLITE512);
                        trafficLayerPlugin.setTrafficMode(TrafficMapLayer.TRAFFIC_MODE_SATELLITE);

                        mapStyle.setText("影像模式");
                        break;
                    case TrafficMapLayer.TRAFFIC_MODE_SATELLITE:
                        trafficMode = TrafficMapLayer.TRAFFIC_MODE_DAY;
                        aegisMap.setStyle(Style.STREETS);
                        mapStyle.setText("白天模式");
                        trafficLayerPlugin.setTrafficMode(TrafficMapLayer.TRAFFIC_MODE_DAY);
                        break;
                }
            }
        });
    }

    /**
     * 初始化地图监听
     */
    private void initListeners() {
        if (aegisMap != null) {
            aegisMap.addOnCameraIdleListener(this);
        }

        // 设置复用控件监听
        cbDebug.setOnCheckedChangeListener(this);
        trafficOpen.setOnCheckedChangeListener(this);
    }

    /**
     * 清除数据缓存，主要是地图数据缓存
     */
    private void clearAppCache() {
        File directory = this.getFilesDir();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
        Toast.makeText(this, "正在清除地图缓存", Toast.LENGTH_SHORT).show();
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
    public void onCameraIdle() {
        if (aegisMap != null) {
            Log.d(TAG, "地图中心位置: " + aegisMap.getCameraPosition().target);
            Toast.makeText(this, "地图中心坐标：[ " + aegisMap.getCameraPosition().target.getLatitude()
                    + ", " + aegisMap.getCameraPosition().target.getLongitude() + " ]", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_map_debug) {
            mapView.getMapAsync(aegisMap -> {
                if (isChecked) {
                    aegisMap.setDebugActive(true);
                } else {
                    aegisMap.setDebugActive(false);
                }
            });
        }else if(buttonView.getId() == R.id.traffic_switch){
            mapView.getMapAsync(aegisMap -> {
                if (isChecked) {
                    trafficLayerPlugin.setLayerVisibility(true);
                } else {
                    trafficLayerPlugin.setLayerVisibility(false);
                }
            });
        }
    }
}
