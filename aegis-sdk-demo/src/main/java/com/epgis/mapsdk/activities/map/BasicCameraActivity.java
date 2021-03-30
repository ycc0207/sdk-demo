package com.epgis.mapsdk.activities.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.camera.CameraUpdateFactory;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class BasicCameraActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象

    private final LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private final LatLng target = new LatLng(24.6870400000, 118.1810890000);

    private boolean bStarted = false;
    private boolean bCenterChange = false;
    private boolean bTiltChange = false;
    private boolean bBearingChange = false;

    private Button button;

    private AegisMap.OnCameraMoveStartedListener onCameraMoveStartedListener = reason -> {
        Toast.makeText(this, "开始移图", Toast.LENGTH_SHORT).show();
    };

    private AegisMap.OnCameraMoveListener onCameraMoveListener = () -> {
        Toast.makeText(this, "移图进行中", Toast.LENGTH_SHORT).show();
    };

    private AegisMap.OnCameraMoveCanceledListener onCameraMoveCanceledListener = () -> {
        Toast.makeText(this, "移图过程被取消", Toast.LENGTH_SHORT).show();
    };

    private AegisMap.OnCameraIdleListener onCameraIdleListener = () -> {
        Toast.makeText(this, "结束移图", Toast.LENGTH_SHORT).show();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_basic_camera);
        button = findViewById(R.id.btn_camera_listener);
        // 地图视图对象获取
        mapView = findViewById(R.id.mapView);
        // 用于地图恢复状态使用
        mapView.onCreate(savedInstanceState);
        // 异步回调地图视图交互对象
        mapView.getMapAsync(aegisMap -> {
            // 移图到指定位置
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());
            // 设置地图显示样式
            aegisMap.setStyle(Style.STREETS);
        });
    }

    /**
     * 设置地图中心点,不带动画设置
     *
     * @param view
     */
    public void onSetMapCenter(View view) {
        if (!bCenterChange) {
            mapView.getMapAsync(aegisMap -> aegisMap.setCameraPosition(new CameraPosition.Builder().target(target).build()));
        } else {
            mapView.getMapAsync(aegisMap -> aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).build()));
        }
        bCenterChange = !bCenterChange;
    }

    /**
     * 地图俯仰角切换，目前设置最大仰角为60度
     *
     * @param view
     */
    public void onSetMapTilt(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (!bTiltChange) {
                // 设置为60度仰角
                aegisMap.animateCamera(CameraUpdateFactory.tiltTo(60D));
            } else {
                // 设置为0度仰角
                aegisMap.animateCamera(CameraUpdateFactory.tiltTo(0D));
            }
            bTiltChange = !bTiltChange;
        });
    }

    /**
     * 设置地图旋转角度
     *
     * @param view
     */
    public void onSetMapBearing(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (!bBearingChange) {
                aegisMap.animateCamera(CameraUpdateFactory.bearingTo(45D));
            } else {
                aegisMap.animateCamera(CameraUpdateFactory.bearingTo(0D));
            }
            bBearingChange = !bBearingChange;
        });
    }

    /**
     * 相机监听开闭回调
     *
     * @param view
     */
    public void onCameraListener(View view) {
        mapView.getMapAsync((AegisMap aegisMap) -> {
            if (!bStarted) {
                // 添加地图相机回调
                aegisMap.addOnCameraMoveStartedListener(onCameraMoveStartedListener);
                aegisMap.addOnCameraMoveListener(onCameraMoveListener);
                aegisMap.addOnCameraMoveCancelListener(onCameraMoveCanceledListener);
                aegisMap.addOnCameraIdleListener(onCameraIdleListener);
                button.setText("相机监听(开)");
            } else {
                // 移除地图相机回调
                aegisMap.removeOnCameraMoveStartedListener(onCameraMoveStartedListener);
                aegisMap.removeOnCameraMoveListener(onCameraMoveListener);
                aegisMap.removeOnCameraMoveCancelListener(onCameraMoveCanceledListener);
                aegisMap.removeOnCameraIdleListener(onCameraIdleListener);
                button.setText("相机监听(关)");
            }
            bStarted = !bStarted;
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
