package com.epgis.navisdk.demo;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.epgis.epgisapp.R;
import com.epgis.navisdk.ui.AegisNavi;
import com.epgis.navisdk.ui.AegisNaviViewOptions;
import com.epgis.navisdk.ui.NavigationView;
import com.epgis.navisdk.ui.listeners.OnAegisNaviListener;
import com.epgis.navisdk.ui.listeners.OnAegisNaviViewListener;
import com.epgis.navisdk.ui.model.NaviInfo;
import com.epgis.navisdk.ui.model.RouteErrorCode;
import com.epgis.navisdk.ui.model.RouteType;

import static com.epgis.navisdk.ui.Constants.INTENT_IS_SIMULATE_NAVI;

/**
 * 自定义导航界面
 */
public class CustomNaviActivity extends Activity implements OnAegisNaviViewListener, OnAegisNaviListener, View.OnClickListener {

    private static final String TAG = CustomNaviActivity.class.getSimpleName();

    private NavigationView navigationView;
    private boolean isRunning = false;
    private boolean shouldSimulate = true;//true是模拟导航,false是gps导航

    private Button mBtStartVocie, mBtStopVoice, mBtnRefresh;

    private boolean isNightMode = false;
    private boolean isTrafficLayerEnabled = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        //屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_navi);
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                shouldSimulate = bundle.getBoolean(INTENT_IS_SIMULATE_NAVI,false);
//                isNightMode = bundle.getBoolean(INTENT_IS_NIGHT_MODE,false);
            }
        }

        mBtnRefresh = findViewById(R.id.btn_refresh);
        mBtnRefresh.setOnClickListener(this);

        navigationView = findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        navigationView.setAegisNaviViewListener(this);

        //1.修改配置
        AegisNaviViewOptions options = navigationView.getViewOptions();
        options.setNaviNight(isNightMode);
        options.setVisbileNaviPreview(true);
        options.setVisbileSettingView(true);
        options.setIsOpenTraffic(true);
        options.setVisbileSettingView(true);
        options.setVisbileTrafficView(false);
        //options.setSettingEnabled(true);
        navigationView.setViewOptions(options);

        //2、设置导航界面监听
        AegisNavi.getInstance().addAegisNaviListener(this);

        //3、调用开始导航
//        AegisNavi.getInstance().startNavi(true);
        AegisNavi.getInstance().startNavi(shouldSimulate);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //5、销毁导航相关

        AegisNavi.getInstance().removeAegisNaviListener(this);
        if (navigationView != null) {
            navigationView.setAegisNaviViewListener(null);
            navigationView.onDestroy();
        }
    }

    //4、增加地图视图的生命周期
    @Override
    protected void onStart() {
        super.onStart();
        if (navigationView != null) {
            navigationView.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navigationView != null) {
            navigationView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (navigationView != null) {
            navigationView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (navigationView != null) {
            navigationView.onStop();
        }
    }

    @Override
    public void onBackPressed() {
        onNaviExit();
    }


    @Override
    public void onNaviExit() {

        AegisNavi.getInstance().endNavigation();
        finish();
    }

    @Override
    public void onNaviMapMode(boolean b) {

    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onArriveDestination() {//到达目的地回调
        Log.d(TAG, "onArriveDestination() ");

        if (shouldSimulate) {
            finish();
        }
    }

    @Override
    public void onGetNavigationText(String s) {//播报内容回调
        Log.d(TAG, "onGetNavigationText() s= " + s);
    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {//导航信息回调
        Log.d(TAG, "onNaviInfoUpdate() " + naviInfo.getNextRoadName() + " , " + naviInfo.getCurrentSpeed());
    }

    @Override
    public void onLocationChanged(int i, Location location) {//位置更新通知
        Log.d(TAG, "onLocationChange() i= " + i);
    }

    @Override
    public void onClick(View view) {
        if (view == mBtnRefresh) {
            AegisNavi.getInstance().routeRefresh();
        }
    }

    @Override
    public void onInitNaviSuccess(RouteType type) {

    }

    @Override
    public void onInitNaviFailure(int code, String msg) {

    }

    @Override
    public void onCalculateRouteSuccess(Object response, RouteType type) {
        Toast.makeText(this, "路线规划成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCalculateRouteFailure(RouteErrorCode code, String msg, RouteType type) {

    }
}
