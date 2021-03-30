package com.epgis.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epgis.CheckPermissionsActivity;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.plugins.location.LocationLayerPlugin;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationActivity extends CheckPermissionsActivity implements View.OnClickListener, LocationEngineListener {

    private static final String TAG = LocationActivity.class.getSimpleName();

    private AndroidLocationEngine mSingleLocationEngine, mReplyLocationEngine;
    private TextView appLocInfo;
    private Context mContext;
    private Handler mHandler;

    private Location mLocation;
    private MapView mapView;
    private AegisMap mAegisMap;
    private LocationLayerPlugin locationLayerPlugin;

    //默认初始化点
    private LatLng targetPos = new LatLng(24.4883232500, 118.1779569800, LatLng.WGS84TYPE);
    private boolean singleMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_location);
        // 改变5.0以上版本状态栏颜色,设为黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack));
        }
        initViews();
        initData();
        // step-1: 初始化定位引擎
        // step-2: 发起定位
        // step-3: 监听回调
        // step-4: 结束定位
        // step-5: 销毁定位
    }

    /**
     * 初始化View
     */
    private void initViews() {
        Button singleLoc = findViewById(R.id.app_single_loc_btn);
        Button replyLoc = findViewById(R.id.app_reply_loc_btn);
        ImageView goBack = findViewById(R.id.iv_goback);
        appLocInfo = findViewById(R.id.app_loc_log_tv);
        mapView = findViewById(R.id.location_mapview);
        singleLoc.setOnClickListener(this);
        replyLoc.setOnClickListener(this);
        goBack.setOnClickListener(this);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(@NonNull AegisMap aegisMap) {
                mAegisMap = aegisMap;
                aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                    }
                });
                // 设置相机位置
                aegisMap.setCameraPosition(new CameraPosition.Builder().target(targetPos).zoom(12.0f).bearing(0).tilt(0).build());

                locationLayerPlugin = new LocationLayerPlugin(mapView, mAegisMap);
                locationLayerPlugin.setLocationLayerVisibility(true);
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 本Handler 只是用于消息的文本显示
        if (mHandler == null) mHandler = new MyHandler();
        mContext = this;
    }

    @Override
    public void onClick(View v) {
//        Log.d(TAG, "onClick");
        switch (v.getId()) {
            case R.id.app_single_loc_btn:
                initSingleLocationEngine();
                break;
            case R.id.app_reply_loc_btn:
                initReplyLocationEngine();
                break;
            case R.id.iv_goback:
                finish();
                break;
            default:
                break;
        }
    }

    /**
     * step-1: 初始化定位引擎(单次定位)
     */
    @SuppressLint("MissingPermission")
    private void initSingleLocationEngine() {
        Log.d(TAG, "initSingleLocationEngine");
        if (mReplyLocationEngine != null) {// 将其他模式的置为null，避免影响。如若没有创建多个Engine，无需此操作
            mReplyLocationEngine.destroy();
            mReplyLocationEngine = null;
        }
        if (mHandler != null) {
            mHandler.obtainMessage(1, "Log消息：开始定位中...").sendToTarget();
        }
        if (mSingleLocationEngine == null) {
            mSingleLocationEngine = AndroidLocationEngine.getInstance(mContext);
            mSingleLocationEngine.addLocationEngineListener(this);
            mSingleLocationEngine.startSingleLocation();//step-2 发起定位
            //如果之前有定位，则可以直接获取
            if (mSingleLocationEngine.getLastLocation() != null) {
                Location lastLocation = mSingleLocationEngine.getLastLocation();
                locationLayerPlugin.forceLocationUpdate(lastLocation);
                if (lastLocation != null) {
                    mLocation = lastLocation;
                    if (mHandler != null) {
                        mHandler.obtainMessage(3).sendToTarget();
                    }
                }
            }
        } else {
            mSingleLocationEngine.startSingleLocation();
        }
    }

    /**
     * step-1: 初始化定位引擎(连续定位)
     */
    @SuppressLint("MissingPermission")
    private void initReplyLocationEngine() {
        Log.d(TAG, "initReplyLocationEngine");
        if (mSingleLocationEngine != null) {// 将其他模式的置为null，避免影响。如若没有创建多个Engine，无需此操作
            mSingleLocationEngine.destroy();
            mSingleLocationEngine = null;
        }
        if (mHandler != null) {
            mHandler.obtainMessage(1, "Log消息：开始定位中...").sendToTarget();
        }
        if (mReplyLocationEngine == null) {
            mReplyLocationEngine = AndroidLocationEngine.getInstance(mContext);
            mReplyLocationEngine.addLocationEngineListener(this);
            mReplyLocationEngine.startContinuouslyLocation();//step-2 发起定位
            //如果之前有定位，则可以直接获取
            if (mReplyLocationEngine.getLastLocation() != null) {
                Location lastLocation = mReplyLocationEngine.getLastLocation();
                locationLayerPlugin.forceLocationUpdate(lastLocation);
                if (lastLocation != null) {
                    mLocation = lastLocation;
                    if (mHandler != null) {
                        mHandler.obtainMessage(3).sendToTarget();
                    }
                }
            }
        } else {
            mReplyLocationEngine.startContinuouslyLocation();
        }
    }

    /**
     * 定位连接回调
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onConnected() {
        Log.d(TAG, "onConnected");
        if (mHandler != null) {
            mHandler.obtainMessage(1, "Log消息：定位已连接，获取定位中...").sendToTarget();
        }
        if (mSingleLocationEngine != null) mSingleLocationEngine.requestLocationUpdates();
        if (mReplyLocationEngine != null) mReplyLocationEngine.requestLocationUpdates();
    }

    /**
     * step-3: 监听回调
     * 定位引擎 更新坐标时 回调
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        if (location != null) {
            mLocation = location;
            locationLayerPlugin.forceLocationUpdate(location);


            if (mHandler != null) {
                mHandler.obtainMessage(3).sendToTarget();
            }
        }
    }

    @Override
    public void onLocationError(int i) {
        Log.d(TAG, "onLocationError errorCode is " + i);
        if (i == 12) {
            if (mHandler != null) {
                mHandler.obtainMessage(1, "Log消息：缺少定位权限。\n" +
                        "请在设备的设置中开启app的定位权限\n（华为部分机型默认高精度定位模式需开启GPS，设置低电量定位模式则无需开启GPS）").sendToTarget();
            }
        } else {
            if (mHandler != null) {
                mHandler.obtainMessage(1, "Log消息：定位失败，失败代码：" + i).sendToTarget();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mContext != null) AndroidLocationEngine.getInstance(mContext).stopLocation();
        if (mapView != null) mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) mapView.onLowMemory();
    }

    /**
     * step-4: 结束定位
     */
    private void stopLoc() {
        AndroidLocationEngine.getInstance(mContext).stopLocation();
    }

    /**
     * step-5: 销毁定位
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mContext != null) AndroidLocationEngine.getInstance(mContext).destroy();
        if (mSingleLocationEngine != null)
            mSingleLocationEngine.removeLocationEngineListener(this);
        if (mReplyLocationEngine != null) mReplyLocationEngine.removeLocationEngineListener(this);
        if(locationLayerPlugin != null){
            locationLayerPlugin.onDestory();
        }
        if (mapView != null) mapView.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (appLocInfo != null) {
                        appLocInfo.setText(msg.obj == null ? "" : msg.obj.toString());
                    }
                    break;
                case 3:
                    if (mLocation != null) {
                        appLocInfo.setText(getLocInfo(mLocation).toString());
                        if (singleMode && mAegisMap != null)
                            mAegisMap.setCameraPosition(new CameraPosition.Builder().target(new LatLng(mLocation.getLatitude(), mLocation.getLongitude())).zoom(12.0f).bearing(0).tilt(0).build());
                    }
                    break;
                default:
                    break;
            }
        }

    }

    @NonNull
    private StringBuffer getLocInfo(Location location) {
        StringBuffer sb = new StringBuffer();
        if (mLocation != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            sb.append("定位成功\n\n");
            sb.append("定位时间：" + df.format(new Date()) + "\n");
            sb.append("经    度: " + location.getLongitude() + "\n");
            sb.append("纬    度: " + location.getLatitude() + "\n");
            sb.append("精    度: " + location.getAccuracy() + "米\n");
        } else {
            sb.append("location is null");
        }
        return sb;
    }
}