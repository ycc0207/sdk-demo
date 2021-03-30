package com.epgis.query;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.commons.geojson.Point;
import com.epgis.commons.geojson.utils.PolylineUtils;
import com.epgis.epgisapp.R;
import com.epgis.service.api.core.LonLatPoint;
import com.epgis.service.api.district.DistrictLocationQuery;
import com.epgis.service.api.district.DistrictResult;
import com.epgis.service.api.district.DistrictSearch;
import com.epgis.service.api.district.model.District;
import com.epgis.service.api.geocoder.GeocodeSearch;
import com.epgis.service.api.geocoder.geocode.GeocodeResult;
import com.epgis.service.api.geocoder.regeocode.RegeocodeQuery;
import com.epgis.service.api.geocoder.regeocode.RegeocodeResult;

import java.io.IOException;
import java.util.List;

/**
 * @author fcy
 * 逆地理编解码
 * 2020-10-30 15:07:47
 */
public class LocationActivity extends Activity {

	//获取页面输入坐标
	private EditText lat;
	private EditText lng;

	private TextView result1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_location);
		init();
	}
	private void init() {
		//初始化获取坐标
		lat = findViewById(R.id.lat);
		lng = findViewById(R.id.lng);
		result1 = (TextView) findViewById(R.id.result);
		findViewById(R.id.bt_cal_dis).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if(TextUtils.isEmpty(lat.getText().toString()) || TextUtils.isEmpty(lng.getText().toString())){
					//Toast.makeText(getApplicationContext(),"地名不能为空",Toast.LENGTH_SHORT).show();
					result1.setText("坐标不能为空");
					return;
				}
				//LonLatPoint lonLatPoint=new LonLatPoint(Double.parseDouble(lng.getText().toString()),Double.parseDouble(lat.getText().toString()));
				Point point=Point.fromLatLng(Double.parseDouble(lat.getText().toString()),Double.parseDouble(lng.getText().toString()));
				searchByLocation(point);
			}
		});
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
		String info = "行政区信息：\n";
		districtSearch.setOnDistrictSearchListener(new DistrictSearch.OnDistrictSearchListener() {
			@Override
			public void OnDistrictSearch(final DistrictResult result) {
				List<District> list = result.getDistricts();
				StringBuilder stringBuilder = new StringBuilder();
				for (District district : list) {
					stringBuilder.append(district.getName());
				}
				//Toast.makeText(LocationActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
				result1.setText(info+stringBuilder.toString());
			}

			@Override
			public void onFailure(Throwable t) {
				//Toast.makeText(LocationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
				result1.setText(info+t.getMessage());
			}
		});
	}
}
