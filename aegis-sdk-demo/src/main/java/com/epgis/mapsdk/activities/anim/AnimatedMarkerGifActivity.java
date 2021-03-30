package com.epgis.mapsdk.activities.anim;

import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

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
import com.epgis.mapsdk.utils.GifImageDecoder;

import java.io.IOException;

/**
 * Add an animated image (GIF) anywhere on the map
 */
public class AnimatedMarkerGifActivity extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private LatLng target = new LatLng(24.4970400000, 118.1910890000);
    private MapView mapView;
    private Handler handler = new Handler();
    private AegisMap mAegisMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mapsdk_activity_basemap);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull final AegisMap aegisMap) {
        aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());
        mAegisMap = aegisMap;
        aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                markerGif_1();

                markerGif_2();
            }
        });
    }

    Marker marker = null;
    MarkerOptions markerOptions;

    /**
     * 方式1：通过marker自带的接口添加gif，重新setIcon后gif取消
     */
    private void markerGif_1(){
        MarkerOptions markerOptions = new MarkerOptions().position(target);
        Marker myMarker = mAegisMap.addMarker(markerOptions);
        myMarker.setGifInputStream(getResources().openRawResource(R.raw.timg));

//        Matrix matrix = new Matrix();
//        matrix.postScale(2f,2f); //长和宽放大缩小的比例
//        matrix.postRotate(30f); //gif旋转
//        myMarker.setGifInputStream(getResources().openRawResource(R.raw.timg), matrix);
    }

    /**
     * 方式2：自己解析gif为bitmap后，通过定时setIcon，来展示gif
     */
    private void markerGif_2(){
        markerOptions = new MarkerOptions().position(center);
        marker = mAegisMap.addMarker(markerOptions);
        showGif();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    GifImageDecoder gifDecoder;

    private void showGif(){
        gifDecoder = new GifImageDecoder();
        try {
            gifDecoder.read(this.getResources().openRawResource(R.raw.timg));
            size = gifDecoder.getFrameCount();
            handler.post(new GifRunnable());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    int index = 0;
    int size = 0;

    private class GifRunnable implements Runnable{
        @Override
        public void run() {
            marker.setIcon(IconFactory.getInstance(AnimatedMarkerGifActivity.this).fromBitmap(gifDecoder.getFrame(index)));
            mAegisMap.updateMarker(marker);

            index = ++index % size;
            handler.postDelayed(this, 100);
        }
    }
}
