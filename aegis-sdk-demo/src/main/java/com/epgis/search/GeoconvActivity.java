package com.epgis.search;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.epgis.commons.geojson.Point;
import com.epgis.epgisapp.R;
import com.epgis.service.api.geoconv.GeoconvUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangsimin on 2019/7/26.
 */

public class GeoconvActivity extends AppCompatActivity {
    private String str = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geoconv);
    }

    public void cgcs2000ToSg(View view) {
        List<Point> list = new ArrayList<>();
        Point point1 = Point.fromLngLat(118.24,24.45);
        Point point2 = Point.fromLngLat(118.34,24.75);
        list.add(point1);
        list.add(point2);
        GeoconvUtil.cgcs2000ToSg(getApplicationContext(), list, new GeoconvUtil.GeoconvCallback() {
            @Override
            public void onSuccess(List<Point> points) {
                str = "";
                if(points != null && points.size() > 0){
                    for(Point point:points){
                        Log.d("GeoconvActivity1", "point.longitude():" + point.longitude());
                        Log.d("GeoconvActivity1", "point.latitude():" + point.latitude());
                        str = str + point.longitude() + "," + point.latitude() + "\n";
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GeoconvActivity.this, str, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }

    public void gjc02ToSg(View view) {
        List<Point> list = new ArrayList<>();
        Point point1 = Point.fromLngLat(118.24,24.45);
        Point point2 = Point.fromLngLat(118.34,24.75);
        list.add(point1);
        list.add(point2);
        GeoconvUtil.gjc02ToSg(getApplicationContext(), list, new GeoconvUtil.GeoconvCallback() {
            @Override
            public void onSuccess(List<Point> points) {
                str = "";
                if(points != null && points.size() > 0){
                    for(Point point:points){
                        Log.d("GeoconvActivity1", "point.longitude():" + point.longitude());
                        Log.d("GeoconvActivity1", "point.latitude():" + point.latitude());
                        str = str + point.longitude() + "," + point.latitude() + "\n";
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GeoconvActivity.this, str, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFail(String msg) {

            }
        });
    }



}
