package com.epgis.search;

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
import com.epgis.service.api.core.LonLatPoint;
import com.epgis.service.api.district.DistrictResult;
import com.epgis.service.api.district.DistrictSearch;
import com.epgis.service.api.district.DistrictLocationQuery;
import com.epgis.service.api.district.model.District;
import com.epgis.service.api.district.DistrictKeywordQuery;
import com.epgis.service.api.geocoder.GeocodeSearch;
import com.epgis.service.api.geocoder.geocode.GeocodeQuery;
import com.epgis.service.api.geocoder.geocode.GeocodeResult;
import com.epgis.service.api.geocoder.regeocode.RegeocodeQuery;
import com.epgis.service.api.geocoder.regeocode.RegeocodeResult;

import java.io.IOException;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mKeySearchBT;
    private Button mResGeoBT;
    private Button mGeoBT;
    private Button mSearchByLocation;
    private Button mSearchByKeyword;
    private Button mSearchByLocation4Town;
    private Button mGeoconvService;
    private final int HANDLER_RESGEO_SUCCESS = 1000;
    private final int HANDLER_ERROR = 1001;
    private final int HANDLER_GEO_SUCCESS = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        setTitle("地图SDK-搜索");
    }

    private void initView() {
        mKeySearchBT = findViewById(R.id.keySearchBT);
        mResGeoBT = findViewById(R.id.resgeoBT);
        mGeoBT = findViewById(R.id.geoBT);
        mSearchByLocation = findViewById(R.id.searchByLocation);
        mSearchByKeyword = findViewById(R.id.searchByKeyword);
        mSearchByLocation4Town = findViewById(R.id.searchByLocation4Town);
        mGeoconvService = findViewById(R.id.geoconvService);
        mKeySearchBT.setOnClickListener(this);
        mResGeoBT.setOnClickListener(this);
        mGeoBT.setOnClickListener(this);
        mSearchByLocation.setOnClickListener(this);
        mSearchByKeyword.setOnClickListener(this);
        mSearchByLocation4Town.setOnClickListener(this);
        mGeoconvService.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == mKeySearchBT) {
            Intent intent = new Intent(this, KeywordSearchActivity.class);
            startActivity(intent);
        } else if (view == mResGeoBT) {
            LonLatPoint lonLatPoint = new LonLatPoint(118.102555, 24.436341);
            resGeoLocation(lonLatPoint);
        } else if (view == mGeoBT) {
            geoLocation("厦门大学");
        } else if (view == mSearchByLocation) {
            Point point = Point.fromLngLat(118.1025, 24.4363);
            searchByLocation(point);
        } else if (view == mSearchByKeyword) {
            searchByKeyWord("厦门市");
        }else if(view == mSearchByLocation4Town){
            String coordSgPolylineCode = "a{veFw|mmT";
            List<Point> listPoints = PolylineUtils.decode(coordSgPolylineCode);

//            Point point = Point.fromLngLat(118.1025, 24.4363);
            searchByLocation(listPoints.get(0));
        }else if(view == mGeoconvService){
            startActivity(new Intent(SearchActivity.this, GeoconvActivity.class));
        }
    }

    Handler mGeoHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case HANDLER_RESGEO_SUCCESS:
                    if (msg.obj != null) {
                        RegeocodeResult result = (RegeocodeResult) msg.obj;
                        if (result.getAddressComponent() == null) {
                            Toast.makeText(SearchActivity.this, "逆地理编码失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String info = "逆地理编码:\n";
                        RegeocodeResult.AddressComponentBean aCBean = result.getAddressComponent();
                        if (aCBean != null) {
                            info += aCBean.getCountry() + aCBean.getProvince() + aCBean.getCity()
                                    + aCBean.getDistrict() + "\n";
                            info += aCBean.getAdcode() + "\n";
                        }
                        info += result.getFormattedAddress();
                        Toast.makeText(SearchActivity.this, info, Toast.LENGTH_LONG).show();
                    }
                    break;
                case HANDLER_GEO_SUCCESS:
                    if (msg.obj != null) {
                        GeocodeResult geoResult = (GeocodeResult) msg.obj;
                        if (geoResult.getAddressComponent() == null) {
                            Toast.makeText(SearchActivity.this, "地理编码失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String info = "地理编码:\n";
                        GeocodeResult.AddressComponentBean geoBean = geoResult.getAddressComponent();
                        if (geoBean != null) {
                            info += geoBean.getCountry() + geoBean.getProvince() + geoBean.getCity()
                                    + geoBean.getDistrict() + "\n";
                            info += geoBean.getAdcode() + "\n";
                        }
                        if (geoResult != null) {
                            List<Point> listPoints = PolylineUtils.decode(geoResult.getLocation());
                            if (listPoints != null && listPoints.size() > 0) {
                                info += listPoints.get(0).coordinates().toString() + "\n";
                            }
                        }
                        info += geoResult.getFormattedAddress();
                        Toast.makeText(SearchActivity.this, info, Toast.LENGTH_LONG).show();
                    }
                    break;
                case HANDLER_ERROR:
                    Toast.makeText(SearchActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    /**
     * 逆地理
     *
     * @param lonLatPoint
     */
    private void resGeoLocation(LonLatPoint lonLatPoint) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        RegeocodeQuery regeocodeQuery = new RegeocodeQuery();
        Point point = Point.fromLngLat(lonLatPoint.getLongitude(), lonLatPoint.getLatitude());
        regeocodeQuery.setLocation(point);
        geocodeSearch.setOnRegeocodeSearchedListener(new GeocodeSearch.OnRegeocodeSearchedListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult) {
                mGeoHandler.obtainMessage(HANDLER_RESGEO_SUCCESS, regeocodeResult).sendToTarget();
            }

            @Override
            public void onFailure(Throwable throwable) {
                mGeoHandler.obtainMessage(HANDLER_ERROR, throwable.toString()).sendToTarget();
            }
        });
        geocodeSearch.getFromLocationAsyn(regeocodeQuery);
    }

    /**
     * 地理编码
     *
     * @param address
     */
    private void geoLocation(String address) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        GeocodeQuery geocodeQuery = new GeocodeQuery();
        geocodeQuery.setAddress(address);
        geocodeSearch.setOnGeocodeSearchedListener(new GeocodeSearch.OnGeocodeSearchedListener() {
            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult) {
                mGeoHandler.obtainMessage(HANDLER_GEO_SUCCESS, geocodeResult).sendToTarget();
            }

            @Override
            public void onFailure(Throwable throwable) {
                mGeoHandler.obtainMessage(HANDLER_ERROR, throwable.toString()).sendToTarget();
            }
        });
        geocodeSearch.getFromLocationNameAsyn(geocodeQuery);
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
                Toast.makeText(SearchActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
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
                    }
                }
                Toast.makeText(SearchActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(SearchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
