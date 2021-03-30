package com.epgis.mapsdk.android;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.epgis.auth.AuthenticationException;
import com.epgis.auth.AuthenticationListener;
import com.epgis.auth.AuthenticationManager;
import com.epgis.auth.Credentials;
import com.epgis.commons.geojson.Point;
import com.epgis.service.api.route.RouteQuery;
import com.epgis.service.api.route.RouteResult;
import com.epgis.service.api.route.RouteSearch;
import com.epgis.service.api.route.models.DirectionsCriteria;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static junit.framework.Assert.assertTrue;

/**
 * Created by yangsimin on 2019/4/2.
 */
@RunWith(AndroidJUnit4.class)
public class RouteSearchTest {

    private Context appContext;


    private RouteQuery routeQuery;
    private RouteSearch routeSearch;
    private String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcHBJZCI6IjhkYzU0OThiZDcxMDMzYTA5OTJlNGQ3NzhhMDZlN2UzIiwiY2xpZW50SXAiOiIxOTIuMTY4LjI4LjI1NCIsImV4cCI6MTU1NDI5OTE3OCwiaWF0IjoxNTU0MDgzMTc4LCJpc3MiOiJ3d3cuYWVnaXMuY29tIiwianRpIjoiWFNQTkpLS1BZTyIsInN1YiI6ImJmMmMwYzBiYjFmYzMyNWJhNGJiZDdkYjIzN2RlMmUyIiwic3ViVHlwZSI6ImFwcGtleSIsInRva2VuVFRMIjoyMTYwMDAwMDAsInVzZXJOYW1lIjoiIn0.E6wtKqG1cIyoXHTEnVQYewau37GaTCjXJ8QgxBJK80g";

    @Test
    public void testDirectionsSearch() {
        appContext = InstrumentationRegistry.getTargetContext();
        initAuth();
        delay();
        init();
        routeSearch.calculateRoute(routeQuery, new RouteSearch.DirectionsResponseCallback() {
            @Override
            public void onFailure(IOException e) {
                System.out.println(e.getMessage());
                System.out.print("onFailure");
                Log.d("RouteSearchTest", "onFailure");
            }

            @Override
            public void onResponse(RouteResult routeResult) {
                System.out.print("onBusLineSearch");
                Log.d("RouteSearchTest", "onBusLineSearch");
                if (routeResult != null) {
                    System.out.println(routeResult.toString());
                }
            }
        });
    }

    private void init() {

        routeQuery = new RouteQuery();
        routeSearch = new RouteSearch(appContext);
        Point origin = Point.fromLngLat(118.102555, 24.436341);
        Point destination = Point.fromLngLat(118.182171, 24.483892);
        routeQuery.setOrigin(origin);
        routeQuery.setDestination(destination);
        routeQuery.setVoiceInstructions(true);
        routeQuery.setBannerInstructions(true);
        routeQuery.setSteps(true);
        routeQuery.setExclude("");
        routeQuery.setProfile(DirectionsCriteria.PROFILE_DRIVING);
    }

    private void initAuth(){
        /**
         * 启动鉴权
         */
        AuthenticationManager.getInstance(appContext).initialize(new AuthenticationListener() {
            @Override
            public void onSuccess(Credentials credentials) {
                Log.d("PoiAddClient","OnDistrictSearch");
                Log.d("PoiAddClient",credentials.toJson());
                assertTrue(true);
            }

            @Override
            public void onFailure(AuthenticationException ex) {
                Log.d("PoiAddClient","onFailure");
                Log.d("PoiAddClient",ex.getErrorMesage());
                assertTrue(false);
            }
        },"https://lbs.sgmap.cn");
    }

    private void delay(){
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
