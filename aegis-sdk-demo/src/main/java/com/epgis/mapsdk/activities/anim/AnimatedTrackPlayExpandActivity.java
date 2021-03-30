package com.epgis.mapsdk.activities.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.LineString;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.annotations.IconFactory;
import com.epgis.mapsdk.annotations.Marker;
import com.epgis.mapsdk.annotations.MarkerOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.LineLayer;
import com.epgis.mapsdk.style.layers.Property;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;
import com.epgis.mapsdk.turf.TurfMeasurement;
import com.epgis.mapsdk.utils.AegisUtils;
import com.epgis.base.utils.RotationAngleUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.rgb;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconRotate;

/**
 * Created by yangsimin on 2020/3/13.
 */
public class AnimatedTrackPlayExpandActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private LatLng currentPosition = new LatLng(24.4870400000, 118.1810890000);
    private GeoJsonSource geoJsonSource;  //marker图层sourse
    private GeoJsonSource geoJsonLineSource; //走过的实线图层sourse
    private List<LatLng> list = new LinkedList<>();
    private List<LatLng> lineList = new LinkedList<>();//走过的实线图层的点
    private long DEFAULT_DURATION = 5000;

    private SymbolLayer carlayer;

    AnimatorSet animatorSet = new AnimatorSet();

    private int count = 0;
    private Marker marker;
    private AegisMap mAegisMap;

    private LatLng previousPoint;
    private LatLng curPoint;

    private void addList() {
        list.add(new LatLng(24.472924, 118.184211));
        list.add(new LatLng(24.480158, 118.185659));
        list.add(new LatLng(24.482326, 118.184232));
        list.add(new LatLng(24.485454, 118.180879));
        list.add(new LatLng(24.487698, 118.179936));
        list.add(new LatLng(24.48943, 118.180338));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addList();
        setContentView(R.layout.mapsdk_activity_trace_play);
        // 地图视图对象获取
        mapView = findViewById(R.id.mapView);
        // 用于地图恢复状态使用
        mapView.onCreate(savedInstanceState);
        // 异步回调地图视图交互对象
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull AegisMap aegisMap) {

                mAegisMap = aegisMap;

                geoJsonSource = new GeoJsonSource("source-id",
                        Feature.fromGeometry(Point.fromLngLat(currentPosition.getLongitude(),
                                currentPosition.getLatitude())));
                // 移图到指定位置
                aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());
                // 设置地图显示样式
                aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        addDashLineLayer(style);
                        addLineLayer(style);
                        addCarLayer(style);
                        addMarker();
                    }
                });
            }
        });

        findViewById(R.id.btn_trace_pause_resume).setVisibility(View.GONE);
    }

    /**
     * 绘制图钉和起泡
     */
    private void addMarker(){
        MarkerOptions mMarkerOptions = new MarkerOptions()
                .position(center)
                .setSnippet(count+"")
                .icon(IconFactory.getInstance(this).fromResource(R.drawable.blue_circle));
        marker = mAegisMap.addMarker(mMarkerOptions);
        mAegisMap.selectMarker(marker);
        mAegisMap.getUiSettings().setDeselectMarkersOnTap(false);  // 设置这个，防止起泡点击会消失,如果起泡移除，需要设为true
    }


    /**
     * 更新气泡提示语  (注：起泡前后内容长度尽量一致，还没对起泡做自适应处理)
     * @param str
     */
    private void updateMarkerText(String str){
        if(marker!=null){
            marker.setSnippet(str);
        }
    }

    /**
     * 添加marker移动图层
     *
     * @param style
     */
    private void addCarLayer(@NonNull Style style) {
        style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                getResources(), R.drawable.aegis_car_circle));
        style.addSource(geoJsonSource);
        carlayer = new SymbolLayer("layer-id", "source-id")
                .withProperties(
                        PropertyFactory.iconImage("marker_icon"),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                );
        style.addLayer(carlayer);
    }

    /**
     * 虚线全程图层
     *
     * @param style
     */
    private void addDashLineLayer(@NonNull Style style){
        List<Point> points = new ArrayList<>();
        for(LatLng latLng : list){
            points.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
        }
        LineString lineString = LineString.fromLngLats(points);
        style.addSource(new GeoJsonSource("line-source2", Feature.fromGeometry(lineString)));
        style.addLayer(new LineLayer("linelayer2", "line-source2").withProperties(
                PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}), //虚线，不需要虚线则删除这行代码
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineColor(rgb(51, 201, 235)
                )
        ));
    }

    /**
     * 实线走过路线的图层
     *
     * @param style
     */
    private void addLineLayer(@NonNull Style style){
        List<Point> points = new ArrayList<>();
        points.add(Point.fromLngLat(list.get(0).getLongitude(), list.get(0).getLatitude()));
        LineString lineString = LineString.fromLngLats(points);
        geoJsonLineSource = new GeoJsonSource("line-source3", Feature.fromGeometry(lineString));
        style.addSource(geoJsonLineSource);
        style.addLayer(new LineLayer("linelayer3", "line-source3").withProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(5f),
                PropertyFactory.lineColor(rgb(51, 201, 235)
                )
        ));
    }

    /**
     * 暂停/回放
     *
     * @param view
     */
    public void onTracePlayPause(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            if(animatorSet != null){
                if(animatorSet.isPaused()){
                    animatorSet.resume();
                }else {
                    animatorSet.pause();
                }
            }
        }
    }

    /**
     * 回放按钮点击
     *
     * @param view
     */
    public void onTracePlayStart(View view) {
        playTrace(list,DEFAULT_DURATION);
    }

    private void playTrace(List<LatLng> list , long duration) {
        float angel = RotationAngleUtils.getRotationAngle(list.get(0), list.get(1));
        Toast.makeText(this, "angel: " + angel, Toast.LENGTH_SHORT).show();

        lineList.clear();
        currentPosition = list.get(0);
        List<Animator> animList = new ArrayList<>();
        if (animatorSet != null && animatorSet.isStarted()) {
            animatorSet.cancel();
        }
        animatorSet = new AnimatorSet();
        if(list == null || list.size() < 2){
            return;
        }
        //获取总距离，为了计算速度
        double disAll = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            disAll = disAll + AegisUtils.calculateLineDistance(list.get(i), list.get(i + 1));
        }

        for (int i = 0; i < list.size() - 1; i++) {
            double dis = AegisUtils.calculateLineDistance(list.get(i), list.get(i + 1));
            ValueAnimator animator = ObjectAnimator
                    .ofObject(latLngEvaluator, list.get(i), list.get(i + 1))
                    .setDuration(new Double(dis/disAll * duration).longValue());
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animatorUpdateListener);
            animList.add(animator);
        }
        /**
         * 多个点的回放动画顺序执行
         */
        animatorSet.playSequentially(animList);
        animatorSet.start();
    }

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();

                    previousPoint = animatedPosition;

                    //更新车标
                    currentPosition = animatedPosition;
                    geoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));


                    //更新车头方向
                    float bearing = (float) getBearing(curPoint,previousPoint);
//                    Log.d(TAG,"onAnimationUpdate() bearing="+bearing);
                    carlayer.setProperties(iconRotate(bearing));
                    curPoint = copy(animatedPosition);


                    //更新起泡
                    count++;
                    marker.setPosition(animatedPosition);
                    updateMarkerText(count+"");

                    //更新走过的路
                    List<Point> points = new ArrayList<>();
                    for(LatLng latLng : list){
                        if(currentPosition.getLongitude() == latLng.getLongitude() &&
                            currentPosition.getLatitude() == latLng.getLatitude()){
                            lineList.add(latLng);
                        }
                    }
                    for(LatLng latLng : lineList){
                        points.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude()));
                    }
                    points.add(Point.fromLngLat(currentPosition.getLongitude(), currentPosition.getLatitude()));
                    LineString lineString = LineString.fromLngLats(points);
                    geoJsonLineSource.setGeoJson(Feature.fromGeometry(lineString));
                }
            };

    private final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };

    /**
     * 计算方向角
     * @param latLng1
     * @param latLng2
     * @return
     */
    private double getBearing(LatLng latLng1, @NonNull LatLng latLng2){
        if(latLng1!=null && latLng2!=null){
            Point point1 = Point.fromLngLat(latLng1.getLongitude(),latLng1.getLatitude());
            Point point2 = Point.fromLngLat(latLng2.getLongitude(),latLng2.getLatitude());
            double bearing = TurfMeasurement.bearing(point1, point2);
            return bearing ;
        }
        return 0;
    }

    public static <T> T copy(Parcelable input) {
        Parcel parcel = null;

        try {
            parcel = Parcel.obtain();
            parcel.writeParcelable(input, 0);

            parcel.setDataPosition(0);
            return parcel.readParcelable(input.getClass().getClassLoader());
        } finally {
            parcel.recycle();
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
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        mapView.onDestroy();
    }
}
