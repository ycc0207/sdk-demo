package com.epgis.mapsdk.activities.anim;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;

/**
 * Created by yangsimin on 2019/3/25.
 */

public class AnimatedMarkerActivity extends AppCompatActivity implements AegisMap.OnMapClickListener {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private LatLng currentPosition = new LatLng(24.4870400000, 118.1810890000);
    private GeoJsonSource geoJsonSource;
    private ValueAnimator animator;
    private AegisMap mAegisMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_basemap);
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
                        style.addImage(("marker_icon"), BitmapFactory.decodeResource(
                                getResources(), R.drawable.marker_icon_default));

                        style.addSource(geoJsonSource);

                        style.addLayer(new SymbolLayer("layer-id", "source-id")
                                .withProperties(
                                        PropertyFactory.iconImage("marker_icon"),
                                        PropertyFactory.iconIgnorePlacement(true),
                                        PropertyFactory.iconAllowOverlap(true)
                                ));


                        mAegisMap.addOnMapClickListener(AnimatedMarkerActivity.this);
                    }
                });
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        // When the user clicks on the map, we want to animate the marker to that
        // location.
        if (animator != null && animator.isStarted()) {
            currentPosition = (LatLng) animator.getAnimatedValue();
            animator.cancel();
        }

        animator = ObjectAnimator
                .ofObject(latLngEvaluator, currentPosition, point)
                .setDuration(2000);
        animator.addUpdateListener(animatorUpdateListener);
        animator.start();

        currentPosition = point;
        return true;
    }

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                    geoJsonSource.setGeoJson(Point.fromLngLat(animatedPosition.getLongitude(), animatedPosition.getLatitude()));
                }
            };

    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {

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
        if (animator != null) {
            animator.cancel();
        }
        if (mAegisMap != null) {
            mAegisMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }
}
