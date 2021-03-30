package com.epgis.navisdk.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.commons.Constants;
import com.epgis.commons.geojson.LineString;
import com.epgis.commons.geojson.Point;
import com.epgis.commons.geojson.Polygon;
import com.epgis.commons.geojson.utils.PolylineUtils;
import com.epgis.epgisapp.R;
import com.epgis.location.AndroidLocationEngine;
import com.epgis.location.LocationEngine;
import com.epgis.location.LocationEngineListener;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.camera.CameraUpdateFactory;
//import com.epgis.mapsdk.constants.Style;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.geometry.LatLngBounds;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.style.expressions.Expression;
import com.epgis.mapsdk.style.layers.FillExtrusionLayer;
import com.epgis.mapsdk.style.layers.Layer;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.sources.GeoJsonSource;
import com.epgis.navisdk.core.turf.TurfConstants;
import com.epgis.navisdk.core.turf.TurfMeasurement;
import com.epgis.navisdk.ui.AegisNaviExpand;
import com.epgis.navisdk.ui.model.POI;
import com.epgis.mapsdk.maps.Style;
import com.epgis.navisdk.core.utils.DriveSpUtil;
import com.epgis.navisdk.ui.AegisNaviOverlayManager;
import com.epgis.navisdk.ui.AegisNavi;
import com.epgis.navisdk.ui.listeners.OnCalculateRouteListener;
import com.epgis.navisdk.ui.listeners.OnRouteSelectionListener;
import com.epgis.navisdk.ui.model.RouteErrorCode;
import com.epgis.navisdk.ui.model.RouteType;
import com.epgis.navisdk.ui.utils.CarRouteTask;
import com.minemap.navicore.NavigationLogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.epgis.commons.Constants.PRECISION_5;
import static com.epgis.mapsdk.style.layers.PropertyFactory.fillExtrusionBase;
import static com.epgis.mapsdk.style.layers.PropertyFactory.fillExtrusionColor;
import static com.epgis.mapsdk.style.layers.PropertyFactory.fillExtrusionHeight;
import static com.epgis.mapsdk.style.layers.PropertyFactory.fillExtrusionOpacity;
//import static com.epgis.navisdk.core.RouteConstants.THIRTY;
//import static com.epgis.navisdk.core.RouteConstants.TWO_POINTS;
import static com.epgis.navisdk.ui.Constants.INTENT_IS_NIGHT_MODE;
import static com.epgis.navisdk.ui.Constants.INTENT_IS_SIMULATE_NAVI;

public class RouteActivity extends Activity implements OnCalculateRouteListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener, OnMapReadyCallback, LocationEngineListener, MapView.OnDidFinishLoadingStyleListener, OnRouteSelectionListener {

    private static final String TAG = RouteActivity.class.getSimpleName();


    private LatLng currentLocation = new LatLng(32.074443, 118.803284);

    // 路线规划规则
    CheckBox duobiyongdu, avoid_fee, nothighspeed, highspeed;

    RadioButton radioCar, radioBus, radioFoot, radioBike;

    // 导航按钮
    private Button mNaviButton;
    private Button mDetailButton;
    private Button mSimuNaviButton;

    private Button mBt01,mBt02;

    // 模拟导航按钮
    private TextView mTestNaviButton;// 模拟导航按钮

    // 起点名称
    private EditText mStartNameView;
    // 终点名称
    private EditText mEndNameView;

    // 定义地图视图对象
    private MapView mMapView;
    // 定义地图管理对象
    private AegisMap mAegisMap;

    // 测试起点
    private String mStartName = "南京大学(鼓楼校区)";
    // 测试终点
    private String mEndName = "东南大学(九龙湖校区)";

    private String mExchangeName;

    private int curIndex = 0;

    Point  startP = Point.fromLngLat(118.78371,32.05314);//自定义起点南京大学
    Point endP = Point.fromLngLat(118.82195, 31.88329);//自定义的钟点东南大学

    Point exChangePoi = null;

    // 定位引擎对象
    private LocationEngine locationEngine;
    POI startpoi, endpoi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_route);

        //1、初始化地图组件mapview
        initView();

        //2、初始化地图定位组件
        initLocationEngine();

        //3、地图创建与设置
        mMapView.onCreate(savedInstanceState);
        mMapView.addOnDidFinishLoadingStyleListener(this);
        mMapView.getMapAsync(this);

        //4、初始化导航
        AegisNavi.getInstance().initNavi(this);
        //5、设置监听
        AegisNavi.getInstance().addCalculateRouteListener(this);

        initData(mStartName, mEndName);

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

        Log.d(TAG, "onDestroy()");
        //移除导航的监听
        AegisNavi.getInstance().removeCalculateRouteListener(this);
        AegisNavi.getInstance().removeRouteSelectListener(mAegisMap, this);

        AegisNaviOverlayManager.getInstance().removeRouteOnMap(mAegisMap);

        mMapView.onDestroy();
    }

    private void initData(String startName, String endName) {
        // 测试样例
        mStartNameView.setText(startName);
        mEndNameView.setText(endName);
        startpoi = new POI();
        startpoi.setLongitude(startP.longitude());
        startpoi.setLatitude(startP.latitude());
        startpoi.setName(mStartNameView.getText().toString());

        endpoi = new POI();
        endpoi.setLongitude(endP.longitude());
        endpoi.setLatitude(endP.latitude());
        endpoi.setName(mEndNameView.getText().toString());

    }


    /**
     * 6、调用相应的算路调用接口
     *
     * @param routeType
     */
    public void requestRoute(RouteType routeType) {

        switch (routeType) {
            case CAR://驾车

//                AegisNavi.getInstance().strategyConvert()
                AegisNavi.getInstance().calculateDriveRoute(startpoi, endpoi, null, null);
                break;
            case BUS://公交
                AegisNavi.getInstance().calculateBusRoute(startpoi, endpoi);
                break;
            case ONFOOT://步行
                AegisNavi.getInstance().calculateFootRoute(startpoi, endpoi);
                break;
            case BIKE://骑行
                AegisNavi.getInstance().calculateBikeRoute(startpoi, endpoi);
                break;
            default:
                break;
        }
    }

    /**
     * 导航偏好策略的设置(躲避拥堵，避免收费，不走高速，高速优先)
     */
    private void saveRoutingPreference() {
        String newMethod = CarRouteTask.getRouteMethodBySelection(duobiyongdu.isChecked(),
                avoid_fee.isChecked(), nothighspeed.isChecked(), highspeed.isChecked());
        DriveSpUtil.setRoutePrefer(newMethod);
    }

    private View mBottomView;

    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        if (locationEngine == null) {
            locationEngine = AndroidLocationEngine.getInstance(this);
            locationEngine.addLocationEngineListener(this);
            if (locationEngine.getLastLocation() != null) {
                Location location = locationEngine.getLastLocation();
            }
        } else {
            AndroidLocationEngine.getInstance(this).startLocation();
        }
    }

    /**
     * 初始化UI控件
     */
    public void initView() {

        mMapView = findViewById(R.id.mymapView);

        mBottomView = findViewById(R.id.btn_navi_action);

        mDetailButton = findViewById(R.id.btn_route_detail);
        mDetailButton.setOnClickListener(this);
        mSimuNaviButton = findViewById(R.id.btn_simunavi);
        mSimuNaviButton.setOnClickListener(this);
        mNaviButton = findViewById(R.id.btn_startnavi);
        mNaviButton.setOnClickListener(this);

        mBt01 = findViewById(R.id.bt_01);
        mBt01.setOnClickListener(this);

        mBt02 = findViewById(R.id.bt_02);
        mBt02.setOnClickListener(this);

        // 路线规划规则
        duobiyongdu = (CheckBox) findViewById(R.id.checkbox_duobiyongdu);
        avoid_fee = (CheckBox) findViewById(R.id.checkbox_avoid_fee);
        nothighspeed = (CheckBox) findViewById(R.id.checkbox_nothighspeed);
        highspeed = (CheckBox) findViewById(R.id.checkbox_highspeed);

        //交通方式选择

        duobiyongdu.setOnCheckedChangeListener(this);
        avoid_fee.setOnCheckedChangeListener(this);
        nothighspeed.setOnCheckedChangeListener(this);
        highspeed.setOnCheckedChangeListener(this);

        //
        radioCar = findViewById(R.id.radio_car);
        radioBus = findViewById(R.id.radio_bus);
        radioFoot = findViewById(R.id.radio_foot);
        radioBike = findViewById(R.id.radio_bike);

        radioCar.setChecked(true);
        // 起点名称
        mStartNameView = (EditText) findViewById(R.id.et_start_place);
        // 终点名称
        mEndNameView = (EditText) findViewById(R.id.et_end_place);

        // 开始设置起点
        mStartNameView.setOnClickListener(this);
        // 开始设置终点
        mEndNameView.setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);

        findViewById(R.id.btn_exchange).setOnClickListener(this);

        hideBottomView();
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.btn_search) {

            if (TextUtils.isEmpty(mStartNameView.getText().toString()) || TextUtils.isEmpty(mEndNameView.getText().toString())) {
                Toast.makeText(this, "请先输入起终点信息", Toast.LENGTH_SHORT).show();
                return;
            }

            if (radioCar.isChecked()) {
                requestRoute(RouteType.CAR);
            } else if (radioBus.isChecked()) {
                requestRoute(RouteType.BUS);
            } else if (radioFoot.isChecked()) {
                requestRoute(RouteType.ONFOOT);
            } else if (radioBike.isChecked()) {
                requestRoute(RouteType.BIKE);
            }

        } else if (mDetailButton == view) {
            goToRouteDetail();
        } else if (mSimuNaviButton == view) {
            launchNavigationWithRoute(true);
        } else if (mNaviButton == view) {
            launchNavigationWithRoute(false);
        } else if (view.getId() == R.id.btn_exchange) {
            exChangePoi = endP;
            endP = startP;
            startP = exChangePoi;
            mExchangeName = mEndName;
            mEndName = mStartName;
            mStartName = mExchangeName;
            initData(mStartName, mEndName);

//            testline();
        } else if(view.getId() == R.id.bt_01){

        } else if(view.getId() == R.id.bt_02){
        }
    }

    @Override
    public void onMapReady(AegisMap aegisMap) {

        mAegisMap = aegisMap;
        mAegisMap.setStyle(Style.STREETS);
        mAegisMap.setCameraPosition(new CameraPosition.Builder().target(currentLocation).zoom(12.0f).build());
        AegisNavi.getInstance().addRouteSelectListener(mAegisMap, this);

    }

    private void showBottomView() {
        mBottomView.setVisibility(View.VISIBLE);
    }

    private void hideBottomView() {
        mBottomView.setVisibility(View.GONE);
    }

    private void animateCameraBbox(LatLngBounds bounds, int animationTime, int[] padding) {
        mAegisMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds,
                padding[0], padding[1], padding[2], padding[3]), animationTime);
    }

    public void goToRouteDetail() {
        Intent intent = new Intent(this, RouteDetailActivity.class);
        Bundle bundle = new Bundle();
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void launchNavigationWithRoute(boolean shouldSimulateRoute) {
        //
        Intent navigationActivity = new Intent(this, CustomNaviActivity.class);
        //跳转到sdk 自带的导航界面
//        Intent navigationActivity = new Intent(this,NavigationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(INTENT_IS_SIMULATE_NAVI, shouldSimulateRoute);
        bundle.putBoolean(INTENT_IS_NIGHT_MODE, false);
        navigationActivity.putExtras(bundle);
        startActivity(navigationActivity);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean check) {
        if (compoundButton == duobiyongdu) {
            saveRoutingPreference();
        } else if (compoundButton == avoid_fee) {
            if (check) {
                highspeed.setChecked(false);
            }
            saveRoutingPreference();
        } else if (compoundButton == nothighspeed) {

            if (check) {
                highspeed.setChecked(false);
            }
            saveRoutingPreference();
        } else if (compoundButton == highspeed) {
            if (check) {
                avoid_fee.setChecked(false);
                nothighspeed.setChecked(false);
            }
            saveRoutingPreference();
        }
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onLocationError(int i) {

    }

    /**
     * 算路成功的回调
     *
     * @param o
     * @param routeType
     */
    @Override
    public void onCalculateRouteSuccess(Object o, RouteType routeType) {
        Log.d(TAG, "onCalculateSuccess()  routetype = " + routeType.getName());
        if (mAegisMap != null) {
            Toast.makeText(this, "路线规划成功 ", Toast.LENGTH_SHORT).show();

            if (routeType == RouteType.BUS) {
                Intent busactivity = new Intent(this, BusResultActivity.class);
                startActivity(busactivity);

            } else {
                AegisNaviOverlayManager.getInstance().addRouteMarkerToMap(mAegisMap);
                AegisNaviOverlayManager.getInstance().addRoutesToMap(mAegisMap);
                showBottomView();
            }
        }
    }

    /**
     * 算路失败的回调
     */
    @Override
    public void onCalculateRouteFailure(RouteErrorCode code, String msg, RouteType type) {
        Log.d(TAG, "onCalculateFailure()  routetype = " + type.getName());
        hideBottomView();
    }

    @Override
    public void onDidFinishLoadingStyle() {

    }

    @Override
    public void onRouteSelected(int index) {
        Log.d(TAG, "onRouteSelected() index = " + index);
        if (curIndex != index) {
            AegisNavi.getInstance().setRouteSelect(mAegisMap, index);
            curIndex = index;
        }
    }

}
