package com.epgis.mapsdk.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.annotations.Marker;
import com.epgis.mapsdk.annotations.MarkerOptions;
import com.epgis.mapsdk.annotations.Polygon;
import com.epgis.mapsdk.annotations.PolygonOptions;
import com.epgis.mapsdk.annotations.Polyline;
import com.epgis.mapsdk.annotations.PolylineOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SUZY on 2019/3/23.
 */

public class AnnotationActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private MapView mapView;
    private AegisMap aegisMap;

    private ImageView goBack;
    private CheckBox addPoint;
    private CheckBox addPolyline;
    private CheckBox addPolygon;

    private Marker maker;
    private Polyline polyline;
    private Polygon polygon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annotation);
        mapView = findViewById(R.id.mapView2);
        mapView.onCreate(savedInstanceState);
        goBack = findViewById(R.id.iv_goback);
        goBack.setOnClickListener(this);
        addPoint = findViewById(R.id.rb_add_point);
        addPoint.setOnCheckedChangeListener(this);
        addPolyline = findViewById(R.id.rb_add_polyline);
        addPolyline.setOnCheckedChangeListener(this);
        addPolygon = findViewById(R.id.rb_add_polygon);
        addPolygon.setOnCheckedChangeListener(this);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull AegisMap map) {
                aegisMap = map;
                aegisMap.setStyle(Style.STREETS);
                aegisMap.setCameraPosition(
                        new CameraPosition.Builder()
                                .target(new LatLng(24.4883232500, 118.1779569800, LatLng.SGTYPE))
                                .zoom(12.0f)
                                .bearing(0)
                                .tilt(0)
                                .build());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_goback:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (aegisMap != null) {
            if (buttonView.getId() == R.id.rb_add_point) {
                if (isChecked) {
                    //添加
                    aegisMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            maker = aegisMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(24.5083232500, 118.1456569800))
                                    .title("你点到我了")
                            );
                        }
                    });
                } else {
                    if (maker != null) {
                        aegisMap.removeAnnotation(maker);
                    }
                }
            } else if (buttonView.getId() == R.id.rb_add_polyline) {
                if (isChecked) {
                    //添加
                    aegisMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            List<LatLng> list = new ArrayList<>();
                            list.add(new LatLng(24.4483232500, 118.1479569800));
                            list.add(new LatLng(24.4783232500, 118.1809569800));
                            list.add(new LatLng(24.4983232500, 118.1809569800));
                            list.add(new LatLng(24.5283232500, 118.1579569800));
                            polyline = aegisMap.addPolyline(new PolylineOptions()
                                    .addAll(list)
                                    .color(Color.GREEN)
                                    .width(5.0f)
                            );
                        }
                    });
                } else {
                    if (polyline != null) {
                        aegisMap.removeAnnotation(polyline);
                    }
                }
            } else if (buttonView.getId() == R.id.rb_add_polygon) {
                if (isChecked) {
                    //添加
                    aegisMap.getStyle(new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            List<LatLng> list = new ArrayList<>();
                            list.add(new LatLng(24.4783232500, 118.1459569800));
                            list.add(new LatLng(24.4783232500, 118.1789569800));
                            list.add(new LatLng(24.4983232500, 118.1789569800));
                            list.add(new LatLng(24.4983232500, 118.1459569800));
                            List<LatLng> hole = new ArrayList<>();
                            hole.add(new LatLng(24.4833232500, 118.1563569800));
                            hole.add(new LatLng(24.4833232500, 118.1672569800));
                            hole.add(new LatLng(24.4883232500, 118.1672569800));
                            hole.add(new LatLng(24.4883232500, 118.1563569800));
                            polygon = aegisMap.addPolygon(new PolygonOptions()
                                    .addAll(list)
                                    .addHole(hole)
                                    .fillColor(Color.RED)
                                    .strokeColor(Color.BLUE)
                                    .alpha(0.5f)
                            );
                        }
                    });
                } else {
                    if (polygon != null) {
                        aegisMap.removeAnnotation(polygon);
                    }
                }
            }


        }
    }

    //    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        if(aegisMap != null) {
//            if (checkedId == R.id.rb_add_point) {
//                aegisMap.getStyle(new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//                        aegisMap.clear();
//                        aegisMap.addMarker(new MarkerOptions()
//                                .position(new LatLng(24.4783232500,118.1779569800, LatLng.SGTYPE))
//                                .title("你点到我了")
//                        );
//                    }
//                });
//            } else if (checkedId == R.id.rb_add_polyline) {
//                aegisMap.getStyle(new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//                        aegisMap.clear();
//                        List<LatLng> list = new ArrayList<>();
//                        list.add(new LatLng(24.4483232500,118.1479569800));
//                        list.add(new LatLng(24.4783232500,118.1779569800));
//                        list.add(new LatLng(24.4983232500,118.1779569800));
//                        list.add(new LatLng(24.5283232500,118.1579569800));
//                        aegisMap.addPolyline(new PolylineOptions()
//                                .addAll(list)
//                                .color(Color.GREEN)
//                                .width(5.0f)
//                        );
//                    }
//                });
//            } else if (checkedId == R.id.rb_add_polygon) {
//                aegisMap.getStyle(new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//                        aegisMap.clear();
//                        List<LatLng> list = new ArrayList<>();
//                        list.add(new LatLng(24.4783232500,118.1459569800));
//                        list.add(new LatLng(24.4783232500,118.1789569800));
//                        list.add(new LatLng(24.4983232500,118.1789569800));
//                        list.add(new LatLng(24.4983232500,118.1459569800));
//
//                        List<LatLng> hole = new ArrayList<>();
//                        hole.add(new LatLng(24.4833232500,118.1563569800));
//                        hole.add(new LatLng(24.4833232500,118.1672569800));
//                        hole.add(new LatLng(24.4883232500,118.1672569800));
//                        hole.add(new LatLng(24.4883232500,118.1563569800));
//
//                        aegisMap.addPolygon(new PolygonOptions()
//                                .addAll(list)
//                                .addHole(hole)
//                                .fillColor(Color.RED)
//                                .strokeColor(Color.BLUE)
//                                .alpha(0.5f)
//
//                        );
//
//                    }
//                });
//            }
//        }
//    }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


}
