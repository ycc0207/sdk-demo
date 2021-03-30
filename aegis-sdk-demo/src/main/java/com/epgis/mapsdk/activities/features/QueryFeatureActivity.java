package com.epgis.mapsdk.activities.features;

import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.annotations.IconFactory;
import com.epgis.mapsdk.annotations.Marker;
import com.epgis.mapsdk.annotations.MarkerOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.camera.CameraUpdateFactory;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.google.gson.JsonElement;

import java.util.Iterator;
import java.util.List;


/**
 * 点击地图poi点，marker标记，并显示名称
 * <p>
 * Created by yangsimin on 2019/5/27.
 */
public class QueryFeatureActivity extends AppCompatActivity implements OnMapReadyCallback,
        AegisMap.OnMapClickListener {

    private MapView mapView;
    private AegisMap aegisMap;
    private LatLng center = new LatLng(24.4870400000, 118.1810890000);

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
        QueryFeatureActivity.this.aegisMap = aegisMap;


        aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                // 移图到指定位置
                aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(11D).build());
                aegisMap.addOnMapClickListener(QueryFeatureActivity.this);
            }
        });
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        final PointF pixel = aegisMap.getProjection().toScreenLocation(point);
        List<Feature> features = aegisMap.queryRenderedFeatures(pixel);

        removeAllMarker();
        if (features != null) {
            Iterator iterator = features.iterator();
            while (iterator.hasNext()) {
                Feature feature = (Feature) iterator.next();
                if (feature.geometry() instanceof Point) {
                    JsonElement jsonElement = feature.getProperty("_name");
                    if (jsonElement == null || jsonElement.getAsString().isEmpty()) {
                        if (feature.getProperty("NAME") == null || feature.getProperty("NAME").getAsString().isEmpty()) {
                            continue;
                        }
                    }
                    LatLng latLng = new LatLng(((Point) feature.geometry()).latitude(), ((Point) feature.geometry()).longitude());


                    String feaName;
                    if (feature.getProperty("_name") == null) {
                        feaName = feature.getProperty("NAME").getAsString();
                    } else {
                        feaName = feature.getProperty("_name").getAsString();
                    }

                    MarkerOptions mMarkerOptions = new MarkerOptions()
                            .position(latLng)
                            .setSnippet(feaName)
                            .icon(IconFactory.getInstance(this).fromResource(R.drawable.select_point));
                    Marker marker = aegisMap.addMarker(mMarkerOptions);
                    //居中定位
                    aegisMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    aegisMap.selectMarker(marker);
                }
            }
        }
        return true;
    }

    /**
     * 删除所有marker
     */
    protected void removeAllMarker() {
        if (aegisMap == null) {
            return;
        }
        List<Marker> markers = aegisMap.getMarkers();
        if (markers != null && markers.size() > 0) {
            for (Marker marker : markers) {
                aegisMap.removeMarker(marker);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aegisMap != null) {
            aegisMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
