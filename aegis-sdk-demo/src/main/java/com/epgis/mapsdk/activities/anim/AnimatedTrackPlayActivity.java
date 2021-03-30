package com.epgis.mapsdk.activities.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
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
import com.epgis.mapsdk.utils.AegisUtils;
import com.epgis.base.utils.RotationAngleUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.get;
import static com.epgis.mapsdk.style.expressions.Expression.rgb;

/**
 * Created by yangsimin on 2020/3/13.
 */

public class AnimatedTrackPlayActivity extends AppCompatActivity {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private LatLng currentPosition = new LatLng(24.4870400000, 118.1810890000);
    private GeoJsonSource geoJsonSource;  //marker图层sourse
    private GeoJsonSource geoJsonLineSource; //走过的实线图层sourse
    private List<LatLng> list = new LinkedList<>();
    private List<LatLng> lineList = new LinkedList<>();//走过的实线图层的点
    private long time = 5000;

    private void addList() {
        list.add(new LatLng(24.472924, 118.184211));
        list.add(new LatLng(24.480158, 118.185659));
        list.add(new LatLng(24.482326, 118.184232));
        list.add(new LatLng(24.485454, 118.180879));
        list.add(new LatLng(24.487698, 118.179936));
        list.add(new LatLng(24.48943, 118.180338));
//        Collections.reverse(list);

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
                        addMarkerLayer(style);
                    }
                });
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            findViewById(R.id.btn_trace_pause_resume).setVisibility(View.VISIBLE);
        }
    }

    private SymbolLayer symbolLayer;

    /**
     * 添加marker移动图层
     *
     * @param style
     */
    private void addMarkerLayer(@NonNull Style style) {
        style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                getResources(), R.drawable.marker_icon_default));
        style.addSource(geoJsonSource);
        symbolLayer = new SymbolLayer("layer-id", "source-id")
                .withProperties(
                        PropertyFactory.iconImage("marker_icon"),
                        PropertyFactory.iconIgnorePlacement(true),
                        PropertyFactory.iconAllowOverlap(true)
                );
        style.addLayer(symbolLayer);
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
        playTrace();
    }

    AnimatorSet animatorSet = new AnimatorSet();


    private void playTrace() {
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
                    .setDuration(new Double(dis/disAll * time).longValue());
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
                    currentPosition = animatedPosition;
                    geoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));


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

            //调整icon的转向，如果不需要实时调整icon的转向则注释这两行代码
            float angel = RotationAngleUtils.getRotationAngle(startValue, endValue);
            symbolLayer.setProperties(PropertyFactory.iconRotate(angel));

            return latLng;
        }
    };

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
