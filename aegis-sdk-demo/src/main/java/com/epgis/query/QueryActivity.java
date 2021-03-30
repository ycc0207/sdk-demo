package com.epgis.query;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.epgis.commons.geojson.Point;
import com.epgis.commons.geojson.utils.PolylineUtils;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.activities.tools.CalculateDistanceActivity;
import com.epgis.mapsdk.activities.tools.CoordConverActivity;
import com.epgis.service.api.core.LonLatPoint;
import com.epgis.service.api.district.DistrictKeywordQuery;
import com.epgis.service.api.district.DistrictLocationQuery;
import com.epgis.service.api.district.DistrictResult;
import com.epgis.service.api.district.DistrictSearch;
import com.epgis.service.api.district.model.District;
import com.epgis.service.api.geocoder.GeocodeSearch;
import com.epgis.service.api.geocoder.geocode.GeocodeQuery;
import com.epgis.service.api.geocoder.geocode.GeocodeResult;
import com.epgis.service.api.geocoder.regeocode.RegeocodeQuery;
import com.epgis.service.api.geocoder.regeocode.RegeocodeResult;

import java.io.IOException;
import java.util.List;

/**
 * @author fcy
 * poi、地理编解码、行政区化查询
 * 2020-10-29 10:58:53
 */
public class QueryActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mKeySearchBT;
    private Button mResGeoBT;
    private Button mGeoBT;
    private Button mSearchByLocation;
    private Button mSearchByKeyword;
    private Button mSearchByLocation4Town;
    private Button mGeoconvService;
    private Button mZbxzhService;
    private final int HANDLER_RESGEO_SUCCESS = 1000;
    private final int HANDLER_ERROR = 1001;
    private final int HANDLER_GEO_SUCCESS = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query);
        initView();
        setTitle("地图SDK-搜索");
    }

    private void initView() {
        mKeySearchBT = findViewById(R.id.keySearchBT);
        mResGeoBT = findViewById(R.id.resgeoBT);
        mGeoBT = findViewById(R.id.geoBT);
        mSearchByLocation = findViewById(R.id.searchByLocation);
        mSearchByKeyword = findViewById(R.id.searchByKeyword);
        //mSearchByLocation4Town = findViewById(R.id.searchByLocation4Town);
        mGeoconvService = findViewById(R.id.geoconvService);
        mZbxzhService=findViewById(R.id.zbxzhService);
        mKeySearchBT.setOnClickListener(this);
        mResGeoBT.setOnClickListener(this);
        mGeoBT.setOnClickListener(this);
        mSearchByLocation.setOnClickListener(this);
        mSearchByKeyword.setOnClickListener(this);
        //mSearchByLocation4Town.setOnClickListener(this);
        mGeoconvService.setOnClickListener(this);
        mZbxzhService.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mKeySearchBT) {
            Intent intent = new Intent(this, KeywordQueryActivity.class);
            startActivity(intent);
        } else if (view == mResGeoBT) {
            startActivity(new Intent(QueryActivity.this, resGeoLocationActivity.class));
        } else if (view == mGeoBT) {
            startActivity(new Intent(QueryActivity.this, GeographicActivity.class));
            //geoLocation("国家电网江苏省电力公司电力科学研究院");
        } else if (view == mSearchByLocation) {
            startActivity(new Intent(QueryActivity.this,LocationActivity.class));
        } else if (view == mSearchByKeyword) {
            startActivity(new Intent(QueryActivity.this,LocationByCityActivity.class));
        }else if(view == mSearchByLocation4Town){
            String coordSgPolylineCode = "a{veFw|mmT";
            List<Point> listPoints = PolylineUtils.decode(coordSgPolylineCode);

//            Point point = Point.fromLngLat(118.1025, 24.4363);
            searchByLocation(listPoints.get(0));
        }else if(view == mGeoconvService){
            startActivity(new Intent(QueryActivity.this, CoordConverActivity.class));
        }else if (view == mZbxzhService){
            startActivity(new Intent(QueryActivity.this, CalculateDistanceActivity.class));
        }
    }

    /**
     * 根据经纬度获取行政区信息
     *
     * @param point
     */
    private void searchByLocation(Point point) {
        DistrictSearch districtSearch = new DistrictSearch(this);
        DistrictLocationQuery districtLocationQuery = new DistrictLocationQuery();
        districtLocationQuery.setLocation(point);
        try {
            districtSearch.searchByLocationAsync(districtLocationQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
        districtSearch.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
            @Override
            public void OnDistrictSearch(final DistrictResult result) {
                List<District> list = result.getDistricts();
                StringBuilder stringBuilder = new StringBuilder();
                for (District district : list) {
                    stringBuilder.append(district.getName());
                }
                Toast.makeText(QueryActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(QueryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 获取行政区的信息
     *
     * @param keyword
     */
    private void searchByKeyWord(String keyword) {
        DistrictSearch districtSearch = new DistrictSearch(this);
        DistrictKeywordQuery districtKeywordQuery = new DistrictKeywordQuery();
        districtKeywordQuery.setKeyword(keyword);
        //获取行政区边界坐标，如果不需要可以不设置
        districtKeywordQuery.setExtension(true);
        try {
            districtSearch.searchByKeywordAsync(districtKeywordQuery);
        } catch (IOException e) {
            e.printStackTrace();
        }
        districtSearch.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
            @Override
            public void OnDistrictSearch(DistrictResult result) {
                StringBuilder stringBuilder = new StringBuilder();
                for (District district : result.getDistricts()) {
                    stringBuilder.append(district.getName() + district.getAdcode() + "\n");
                    if (district != null && district.getSubDistricts() != null) {
                        for (District subDistrict : district.getSubDistricts()) {
                            stringBuilder.append(subDistrict.getName() + subDistrict.getAdcode() + "\n");
                        }
                        stringBuilder=new StringBuilder(stringBuilder.substring(0,stringBuilder.toString().lastIndexOf("\n")));
                    }
                }
                Toast.makeText(QueryActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(QueryActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
