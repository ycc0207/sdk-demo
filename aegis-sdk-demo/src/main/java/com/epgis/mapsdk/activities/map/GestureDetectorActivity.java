package com.epgis.mapsdk.activities.map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.epgis.android.gestures.MoveGestureDetector;
import com.epgis.android.gestures.RotateGestureDetector;
import com.epgis.android.gestures.ShoveGestureDetector;
import com.epgis.android.gestures.StandardScaleGestureDetector;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class GestureDetectorActivity extends AppCompatActivity {

    private MapView mapView;        // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private boolean bGestureDetector = false;

    private Button btnGestureDetector;
    private Button btnBearingLocked;
    private Button btnZoomLocked;
    private Button btnTiltLocked;
    private Button btnScrollLocked;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_gesture_detector);

        btnGestureDetector = findViewById(R.id.btn_gesture_detector);
        btnBearingLocked = findViewById(R.id.btn_gesture_bearing_locked);
        btnZoomLocked = findViewById(R.id.btn_gesture_zoom_locked);
        btnTiltLocked = findViewById(R.id.btn_gesture_tilt_locked);
        btnScrollLocked = findViewById(R.id.btn_gesture_scroll_locked);

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

    // 手势移图操作
    private final AegisMap.OnMoveListener onMoveListener = new AegisMap.OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "开始移图", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMove(@NonNull MoveGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "移图中...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "结束移图", Toast.LENGTH_SHORT).show();
        }
    };

    // 手势旋转操作
    private final AegisMap.OnRotateListener onRotateListener = new AegisMap.OnRotateListener() {
        @Override
        public void onRotateBegin(@NonNull RotateGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "开始旋转", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRotate(@NonNull RotateGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "旋转中...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRotateEnd(@NonNull RotateGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "结束旋转...", Toast.LENGTH_SHORT).show();
        }
    };

    // 手势缩放操作
    private final AegisMap.OnScaleListener onScaleListener = new AegisMap.OnScaleListener() {
        @Override
        public void onScaleBegin(@NonNull StandardScaleGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "开始缩放", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onScale(@NonNull StandardScaleGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "缩放中...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onScaleEnd(@NonNull StandardScaleGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "结束缩放...", Toast.LENGTH_SHORT).show();
        }
    };

    // 手势推拉操作
    private final AegisMap.OnShoveListener onShoveListener = new AegisMap.OnShoveListener() {
        @Override
        public void onShoveBegin(@NonNull ShoveGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "开始推拉...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onShove(@NonNull ShoveGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "推拉中...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onShoveEnd(@NonNull ShoveGestureDetector detector) {
            Toast.makeText(GestureDetectorActivity.this, "结束推拉...", Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 添加手势探测回调
     *
     * @param view
     */
    public void onGestureListener(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (!bGestureDetector) {
                // 添加手势监听
                aegisMap.addOnMoveListener(onMoveListener);
                aegisMap.addOnRotateListener(onRotateListener);
                aegisMap.addOnScaleListener(onScaleListener);
                aegisMap.addOnShoveListener(onShoveListener);
                btnGestureDetector.setText("手势探测(开)");
            } else {
                // 移除手势监听
                aegisMap.removeOnMoveListener(onMoveListener);
                aegisMap.removeOnRotateListener(onRotateListener);
                aegisMap.removeOnScaleListener(onScaleListener);
                aegisMap.removeOnShoveListener(onShoveListener);
                btnGestureDetector.setText("手势探测(关)");
            }
            bGestureDetector = !bGestureDetector;
        });
    }

    /**
     * 锁定手势旋转回调，双指以某一指作为旋转轴旋转
     *
     * @param view
     */
    public void onBearingLocked(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (aegisMap.getUiSettings().isRotateGesturesEnabled()) {
                aegisMap.getUiSettings().setRotateGesturesEnabled(false);
                btnBearingLocked.setText("锁定旋转(开)");
            } else {
                aegisMap.getUiSettings().setRotateGesturesEnabled(true);
                btnBearingLocked.setText("锁定旋转(关)");
            }
        });
    }

    /**
     * 锁定手势缩放回调，手势缩放通过单点放大，双指点击缩小，双手拟合缩放方式实现
     *
     * @param view
     */
    public void onZoomLocked(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (aegisMap.getUiSettings().isZoomGesturesEnabled()) {
                aegisMap.getUiSettings().setZoomGesturesEnabled(false);
                btnZoomLocked.setText("锁定缩放(开)");
            } else {
                aegisMap.getUiSettings().setZoomGesturesEnabled(true);
                btnZoomLocked.setText("锁定缩放(关)");
            }
        });
    }

    /**
     * 锁定手势推拉回调，俯仰角切换通过双指推拉实现
     *
     * @param view
     */
    public void onTiltLocked(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (aegisMap.getUiSettings().isTiltGesturesEnabled()) {
                aegisMap.getUiSettings().setTiltGesturesEnabled(false);
                btnTiltLocked.setText("锁定俯仰(开)");
            } else {
                aegisMap.getUiSettings().setTiltGesturesEnabled(true);
                btnTiltLocked.setText("锁定俯仰(关)");
            }
        });
    }

    /**
     * 锁定手势移图回调
     *
     * @param view
     */
    public void onScrollLocked(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (aegisMap.getUiSettings().isScrollGesturesEnabled()) {
                aegisMap.getUiSettings().setScrollGesturesEnabled(false);
                btnScrollLocked.setText("锁定移图(开)");
            } else {
                aegisMap.getUiSettings().setScrollGesturesEnabled(true);
                btnScrollLocked.setText("锁定移图(关)");
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
