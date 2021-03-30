package com.epgis.mapsdk.activities.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.camera.CameraUpdateFactory;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.log.Logger;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.Style;

/**
 * Created by huangsiwen on 2019/3/28.
 */

public class MapListenerActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final String TAG = MapListenerActivity.class.getSimpleName();

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private CheckBox cbSetListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mapsdk_activity_maplistener);

        mapView = findViewById(R.id.mapView);    // 地图视图对象获取
        initMapListeners();                        // 初始化地图监听
        mapView.onCreate(savedInstanceState);   // 用于地图恢复状态使用

        // 异步回调地图视图交互对象
        mapView.getMapAsync(aegisMap -> {
            aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());    // 移图到指定位置
            aegisMap.setStyle(Style.STREETS);    // 设置地图显示样式
        });

        // 初始化控件对象
        initViews();
        // 初始化控件监听
        initViewListeners();
    }

    private void initViews() {
        cbSetListener = findViewById(R.id.cb_set_listener);
    }

    private void initViewListeners() {
        cbSetListener.setOnCheckedChangeListener(this);
    }

    /**
     * 初始化地图监听对象
     * <p>
     * 移除监听采用{@link MapView#removeOnDidFinishRenderingFrameListener(MapView.OnDidFinishRenderingFrameListener)} 等等移除接口移除
     * 不需要监听场景记得清除回调，防止一直回调影响效率和效果
     * </p>
     */
    private void initMapListeners() {
        // 相机区域将发生变化时回调该接口
        mapView.addOnCameraWillChangeListener(animated -> {
            Logger.d(TAG, "OnCameraWillChangeListener Called, animated: " + animated);
        });

        // 相机区域正在变化时回调该接口
        mapView.addOnCameraIsChangingListener(() -> {
            Logger.d(TAG, "OnCameraIsChangingListener Called");
            Toast.makeText(this, "相机正在改变中...", Toast.LENGTH_SHORT).show();
        });

        // 相机区域完成变化时回调该接口
        mapView.addOnCameraDidChangeListener(animated -> {
            Logger.d(TAG, "OnCameraDidChangeListener Called, animated: " + animated);
        });

        // 将开始加载地图时回调该接口，样式设置时回调该接口，只会执行一次，除非再切换样式
        mapView.addOnWillStartLoadingMapListener(() -> {
            Logger.d(TAG, "OnWillStartLoadingMapListener Called");
        });

        // 将开始渲染地图的第一帧，只会执行一次
        mapView.addOnWillStartRenderingMapListener(() -> {
            Logger.d(TAG, "OnWillStartRenderingMapListener Called");
        });

        // 地图每帧开始渲染时回调，每帧都会回调一次
        mapView.addOnWillStartRenderingFrameListener(() -> {
            Logger.d(TAG, "OnWillStartRenderingFrameListener Called");
        });

        // 地图每帧渲染完都会回调一次
        mapView.addOnDidFinishRenderingFrameListener(fully -> {
            Logger.d(TAG, "OnDidFinishRenderingFrameListener Called");
        });

        // 地图第一次完整帧结束时回调该接口，只会回调一次，除非样式重置
        mapView.addOnDidFinishRenderingMapListener(fully -> {
            Logger.d(TAG, "OnDidFinishRenderingMapListener Called, Render State: " + fully);
        });

        // 地图加载完回调该接口，时机和addOnDidFinishRenderingMapListener一致
        mapView.addOnDidFinishLoadingMapListener(() -> {
            Logger.d(TAG, "OnDidFinishLoadingMapListener Called");
        });

        // 样式加载完成回调，只会在样式设置解析完回调
        mapView.addOnDidFinishLoadingStyleListener(() -> {
            Logger.d(TAG, "OnDidFinishLoadingStyleListener Called");
        });

        // 地图完成一次完整帧渲染时回调该接口
        mapView.addOnDidBecomeIdleListener(() -> {
            Logger.d(TAG, "OnDidBecomeIdleListener Called");
        });

        // 样式加载失败时回调该接口
        mapView.addOnDidFailLoadingMapListener(errorMessage -> {
            Logger.d(TAG, "OnDidFailLoadingMapListener Called， errorMessage： " + errorMessage);
        });

        // 数据源改变时回调该接口
        mapView.addOnSourceChangedListener(id -> {
            Logger.d(TAG, "OnSourceChangedListener Called， source id： " + id);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.cb_set_listener) {
            // 动画操作是为了展示相机正在改变的回调，OnCameraIsChangingListener该回调在发生动画时才会被回调，否则只会调用开始动画和结束动画两个回调
            mapView.getMapAsync(aegisMap -> {
                if (isChecked) {
                    aegisMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(0, 0), 5), 5000);
                } else {
                    aegisMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 12), 5000);
                }
            });
        }
    }
}
