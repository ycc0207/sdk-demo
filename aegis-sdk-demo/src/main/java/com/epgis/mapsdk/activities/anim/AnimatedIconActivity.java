package com.epgis.mapsdk.activities.anim;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.FeatureCollection;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.SymbolLayer;
import com.epgis.mapsdk.style.sources.GeoJsonSource;
import com.epgis.mapsdk.utils.BitmapUtils;

import static com.epgis.mapsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconImage;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconOffset;
import static com.epgis.mapsdk.style.layers.PropertyFactory.iconTranslate;

/**
 * 介绍地图图钉的动画，举了4种动画例子，更多动画可以通过自定义Interpolator实现
 * <p>
 * Created by yangsimin on 2019/5/21.
 */

public class AnimatedIconActivity extends AppCompatActivity implements
        MapView.OnDidFinishRenderingMapListener {

    private MapView mapView;    // 地图视图对象
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

    private static final String ICON_ID = "red-pin-icon-id";

    private static final float DEFAULT_DESIRED_ICON_OFFSET = -16;
    private static final float STARTING_DROP_HEIGHT = -60;
    private static final long DROP_SPEED_MILLISECONDS = 700;
    private static final String SYMBOL_LAYER_ID = "symbol-layer-id";
    private SymbolLayer pinSymbolLayer;
    private Style style;
    private TimeInterpolator currentSelectedTimeInterpolator = new BounceInterpolator();
    private ValueAnimator animator;
    private boolean firstRunThrough = true;
    private boolean animationHasStarted;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_icon_anim);
        // 地图视图对象获取
        mapView = findViewById(R.id.mapView);
        // 用于地图恢复状态使用
        mapView.onCreate(savedInstanceState);
        // 异步回调地图视图交互对象
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull AegisMap aegisMap) {
                // 移图到指定位置
                aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());
                // 设置地图显示样式
                aegisMap.setStyle(new Style.Builder().fromUrl(Style.STREETS)
                                // Add GeoJsonSource with random Features to the map.
                                .withSource(new GeoJsonSource("source-id",
                                        FeatureCollection.fromFeatures(new Feature[]{
                                                Feature.fromGeometry(Point.fromLngLat(center.getLongitude()
                                                        , center.getLatitude()))
                                        })
                                ))
                                .withImage(ICON_ID, BitmapUtils.getBitmapFromDrawable(
                                        getResources().getDrawable(R.drawable.map_marker_push_pin_pink))), new Style.OnStyleLoaded() {
                            @Override
                            public void onStyleLoaded(@NonNull Style style) {
                                AnimatedIconActivity.this.style = style;
                                mapView.addOnDidFinishRenderingMapListener(AnimatedIconActivity.this);
                            }
                        }
                );
            }
        });
    }


    /**
     * 地图渲染完后开始动画
     *
     * @param fully true则表示渲染完成
     */
    @Override
    public void onDidFinishRenderingMap(boolean fully) {
        initAnimation(currentSelectedTimeInterpolator);
        initInterpolatorButtons();
    }

    /**
     * 初始化并开始动画
     *
     * @param desiredTimeInterpolator
     */
    private void initAnimation(TimeInterpolator desiredTimeInterpolator) {
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(STARTING_DROP_HEIGHT, 0);
        animator.setDuration(DROP_SPEED_MILLISECONDS);
        animator.setInterpolator(desiredTimeInterpolator);
        animator.setStartDelay(500);
        animator.start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (!animationHasStarted) {
                    initSymbolLayer();
                    animationHasStarted = true;
                }
                pinSymbolLayer.setProperties(iconTranslate(new Float[]{0f, (Float) valueAnimator.getAnimatedValue()}));
            }
        });
    }

    /**
     * 添加图层到地图
     */
    private void initSymbolLayer() {
        pinSymbolLayer = new SymbolLayer(SYMBOL_LAYER_ID,
                "source-id");
        pinSymbolLayer.setProperties(
                iconImage(ICON_ID),
                iconIgnorePlacement(true),
                iconAllowOverlap(true),
                iconOffset(new Float[]{0f, DEFAULT_DESIRED_ICON_OFFSET}));
        style.addLayer(pinSymbolLayer);
    }

    /**
     * 初始化动画按钮
     */
    private void initInterpolatorButtons() {

        Button bounceInterpolatorFab = findViewById(R.id.button1);
        bounceInterpolatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstRunThrough = false;
                currentSelectedTimeInterpolator = new BounceInterpolator();
                resetIcons();
            }
        });

        Button linearInterpolatorFab = findViewById(R.id.button2);
        linearInterpolatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectedTimeInterpolator = new OvershootInterpolator();
                firstRunThrough = false;
                resetIcons();
            }
        });

        Button accelerateInterpolatorFab = findViewById(R.id.button3);
        accelerateInterpolatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectedTimeInterpolator = new AnticipateInterpolator();
                firstRunThrough = false;
                resetIcons();
            }
        });

        Button decelerateInterpolatorFab = findViewById(R.id.button4);
        decelerateInterpolatorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectedTimeInterpolator = new DecelerateInterpolator();
                firstRunThrough = false;
                resetIcons();
            }
        });
    }

    private void resetIcons() {
        if (!firstRunThrough) {
            animationHasStarted = false;
            style.removeLayer(SYMBOL_LAYER_ID);
            initAnimation(currentSelectedTimeInterpolator);
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
        mapView.onDestroy();
    }
}
