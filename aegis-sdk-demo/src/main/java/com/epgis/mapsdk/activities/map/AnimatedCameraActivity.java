package com.epgis.mapsdk.activities.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.camera.CameraUpdateFactory;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.log.Logger;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.AegisMap.CancelableCallback;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

/**
 * Created by huangsiwen on 2019/3/25.
 */

public class AnimatedCameraActivity extends AppCompatActivity {

    private static final String TAG = AnimatedCameraActivity.class.getSimpleName();

    private MapView mapView;    // 地图视图对象
    private final LatLng defaultCenter = new LatLng(24.4870400000, 118.1810890000);
    private final LatLng targetCenter = new LatLng(24.4870400000, 118.0010890000);
    private boolean bChanged = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_animated_camera);

        mapView = findViewById(R.id.mapView); // 地图视图对象获取
        mapView.onCreate(savedInstanceState); // 用于地图恢复状态使用
        // 异步回调地图视图交互对象
        mapView.getMapAsync(aegisMap -> {
            // 移图到指定位置
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(defaultCenter).zoom(12D).build());
            // 设置地图显示样式
            aegisMap.setStyle(Style.STREETS);
        });
    }

    /**
     * 立即移动相机，即直接从A点移动到B点，没有动画效果
     *
     * @param view
     */
    public void onMoveCamera(View view) {
        mapView.getMapAsync((AegisMap aegisMap) -> {
            if (!bChanged) {
                aegisMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetCenter, 12.0f));
            } else {
                aegisMap.moveCamera(CameraUpdateFactory.newLatLng(defaultCenter), new CancelableCallback() {
                    @Override
                    public void onCancel() {
                        // 因为移动相机方式是立即发生，不会发生动画过程，取消动画不会被回调
                        Toast.makeText(AnimatedCameraActivity.this, "移动相机不会调用到", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(AnimatedCameraActivity.this, "移动相机成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            bChanged = !bChanged;
        });
    }

    /**
     * 线性化移动相机，即从A点平滑线性过渡到B点，带动画效果
     * <p>
     * 动画都可以添加被动取消监听和主动完成监听，可以自定义回调实现，完成指定功能
     * </p>
     *
     * @param view
     */
    public void onEaseCamera(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (!bChanged) {
                aegisMap.easeCamera(CameraUpdateFactory.newLatLngZoom(targetCenter, 12.0f), 3500, new CancelableCallback() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(AnimatedCameraActivity.this, "A->B线性跟随动画被取消", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(AnimatedCameraActivity.this, "A->B线性跟随动画正常完成", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // 第三个参数表示是否做线性插值处理，一般默认为true，这边只是为了展示不同
                aegisMap.easeCamera(CameraUpdateFactory.newLatLng(defaultCenter), 500, false, new CancelableCallback() {
                    @Override
                    public void onCancel() {
                        Toast.makeText(AnimatedCameraActivity.this, "B->A线性跟随动画被取消", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(AnimatedCameraActivity.this, "B->A线性跟随动画正常完成", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            bChanged = !bChanged;
        });
    }

    /**
     * 喷气式移动相机，即从A点弧形平滑过渡到B点，带动画效果
     *
     * @param view
     */
    public void onAnimateCamera(View view) {
        mapView.getMapAsync(aegisMap -> {
            if (!bChanged) {
                aegisMap.animateCamera(CameraUpdateFactory.newLatLngZoom(targetCenter, 12.0f), 1500);
            } else {
                aegisMap.animateCamera(CameraUpdateFactory.newLatLng(defaultCenter), 500);
            }
            bChanged = !bChanged;
        });
    }

    /**
     * 取消相机动画操作
     * <p>
     * 这是通过API的方式取消动画，这边只是举例取消回调的过程；
     * 一般当动画发生的过程中，发生新的动画或者手势操作等会回调{@link CancelableCallback#onCancel()}
     * </p>
     *
     * @param view
     */
    public void onCancelAnimation(View view) {
        mapView.getMapAsync(aegisMap -> {
            aegisMap.cancelTransitions();
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
