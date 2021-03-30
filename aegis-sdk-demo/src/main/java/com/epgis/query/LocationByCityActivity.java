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
import com.epgis.service.api.district.DistrictKeywordQuery;
import com.epgis.service.api.district.DistrictResult;
import com.epgis.service.api.district.DistrictSearch;
import com.epgis.service.api.district.model.District;
import com.epgis.service.api.geocoder.GeocodeSearch;
import com.epgis.service.api.geocoder.geocode.GeocodeQuery;
import com.epgis.service.api.geocoder.geocode.GeocodeResult;
import com.epgis.service.api.geocoder.regeocode.RegeocodeResult;

import java.io.IOException;
import java.util.List;

/**
 * @author fcy
 * 地理编解码
 * 2020-10-30 15:07:47
 */
public class LocationByCityActivity extends Activity {

	//地名
	private EditText text1;

	private TextView result1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_locationbycity);
		init();
	}
	private void init() {
		//初始化地名
		text1 = findViewById(R.id.text1);
		result1 = (TextView) findViewById(R.id.result);
		findViewById(R.id.bt_cal_dis).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String placeName = text1.getText().toString();
				if(TextUtils.isEmpty(placeName)){
					//Toast.makeText(getApplicationContext(),"地名不能为空",Toast.LENGTH_SHORT).show();
					result1.setText("地名不能为空");
					return;
				}
				searchByKeyWord(placeName);
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
		String info="行政区编码：\n";
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
						stringBuilder = new StringBuilder(stringBuilder.substring(0, stringBuilder.toString().lastIndexOf("\n")));
					}
				}
				//Toast.makeText(LocationByCityActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
				result1.setText(info+stringBuilder.toString());
			}

			@Override
			public void onFailure(Throwable t) {
				//Toast.makeText(LocationByCityActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
				result1.setText(info+t.getMessage());
			}
		});
	}
}
