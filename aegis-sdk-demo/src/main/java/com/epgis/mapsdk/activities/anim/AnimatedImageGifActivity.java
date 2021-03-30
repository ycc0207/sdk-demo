package com.epgis.mapsdk.activities.anim;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;


import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.geometry.LatLngQuad;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.RasterLayer;
import com.epgis.mapsdk.style.sources.ImageSource;

import java.io.InputStream;

/**
 * Add an animated image (GIF) anywhere on the map
 */
public class AnimatedImageGifActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String ID_IMAGE_SOURCE = "animated_image_source";
    private static final String ID_IMAGE_LAYER = "animated_image_layer";
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);
    private MapView mapView;
    private Handler handler;
    private Runnable runnable;

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
        aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // Use the RefreshImageRunnable class and runnable to quickly display images for a GIF/video UI experience
                InputStream gifInputStream = getResources().openRawResource(R.raw.waving_bear);
                runnable = new RefreshImageRunnable(style, Movie.decodeStream(gifInputStream), handler = new Handler());
                handler.postDelayed(runnable, 100);
            }
        });
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
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
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

    private static class RefreshImageRunnable implements Runnable {

        private ImageSource imageSource;
        private Style style;
        private Movie movie;
        private Handler handler;
        private long movieStart;
        private Bitmap bitmap;
        private Canvas canvas;

        RefreshImageRunnable(Style style, Movie movie, Handler handler) {
            this.style = style;
            this.movie = movie;
            this.handler = handler;
            bitmap = Bitmap.createBitmap(movie.width(), movie.height(), Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
        }

        @Override
        public void run() {
            long now = android.os.SystemClock.uptimeMillis();
            if (movieStart == 0) {
                movieStart = now;
            }

            int dur = movie.duration();
            if (dur == 0) {
                dur = 1000;
            }

            movie.setTime((int) ((now - movieStart) % dur));
            movie.draw(canvas, 0, 0);

            if (imageSource == null) {
                // Set the bounds/size of the gif. Then create an image source object with a unique id,
                // the bounds, and drawable image
                imageSource = new ImageSource(ID_IMAGE_SOURCE,
                        new LatLngQuad(

                                new LatLng(24.4870400000, 118.1810890000),
                                new LatLng(24.4870400000, 118.1610890000),
                                new LatLng(24.4670400000, 118.1610890000),
                                new LatLng(24.4670400000, 118.1810890000)),
                        bitmap);

                // Add the source to the map
                style.addSource(imageSource);

                // Create an raster layer with a unique id and the image source created above. Then add the layer to the map.
                style.addLayer(new RasterLayer(ID_IMAGE_LAYER, ID_IMAGE_SOURCE));
            }

            imageSource.setImage(bitmap);
            handler.postDelayed(this, 50);
        }
    }
}
