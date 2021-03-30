package com.epgis.navisdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.navisdk.ui.AegisNavi;
import com.epgis.navisdk.ui.AegisNaviOverlayManager;

public class BusMapActivity extends Activity implements OnMapReadyCallback, MapView.OnDidFinishLoadingStyleListener, View.OnClickListener {

    private static String TAG = BusMapActivity.class.getSimpleName();

    private LatLng currentLocation = new LatLng(24.5346308296, 118.1352996826);

    private Button mBtTest;

    // 定义地图视图对象
    private MapView mMapView;
    // 定义地图管理对象
    private AegisMap mAegisMap;

    private int curBusIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_bus_map);

        //1、初始化地图组件mapview
        initView();

        if (getIntent() != null) {
            curBusIndex = getIntent().getIntExtra("postion", 0);
        }

        Log.d(TAG, "onCreate() curBusIndex=" + curBusIndex);

        //3、地图创建与设置
        mMapView.onCreate(savedInstanceState);
        mMapView.addOnDidFinishLoadingStyleListener(this);
        mMapView.getMapAsync(this);

//        initData(mStartName, mEndName);
    }

    /**
     * 初始化UI控件
     */
    public void initView() {

//        Toast.makeText(this, "initView", Toast.LENGTH_SHORT).show();

        findViewById(R.id.title_btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        TextView titleView = (TextView) findViewById(R.id.title_text_name);

        titleView.setText("公交路线查看");

        mBtTest = findViewById(R.id.bt_test);
        mBtTest.setOnClickListener(this);

        mMapView = findViewById(R.id.mymapView);
    }


    @Override
    public void onDidFinishLoadingStyle() {

//        curBusIndex = 1;
        showBusRoute(curBusIndex);
    }

    @Override
    public void onMapReady(@NonNull AegisMap aegisMap) {

        mAegisMap = aegisMap;
        mAegisMap.setStyle(Style.STREETS);
        mAegisMap.setCameraPosition(new CameraPosition.Builder().target(currentLocation).zoom(12.0f).build());


        AegisNaviOverlayManager.getInstance().addRouteMarkerToMap(mAegisMap);
    }


    /**
     * 在地图上显示公交路线
     *
     * @param position
     */
    private void showBusRoute(int position) {
        AegisNaviOverlayManager.getInstance().addBusRouteToMap(mAegisMap, position);

        //绘制公交站点

    }


    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onClick(View view) {
//        curBusIndex = 1;
        showBusRoute(curBusIndex);
    }
}
