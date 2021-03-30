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
import com.epgis.service.api.geocoder.GeocodeSearch;
import com.epgis.service.api.geocoder.geocode.GeocodeQuery;
import com.epgis.service.api.geocoder.geocode.GeocodeResult;
import com.epgis.service.api.geocoder.regeocode.RegeocodeQuery;
import com.epgis.service.api.geocoder.regeocode.RegeocodeResult;

import java.util.List;

/**
 * @author fcy
 * 逆地理编解码
 * 2020-10-30 15:07:47
 */
public class resGeoLocationActivity extends Activity {

	private final int HANDLER_RESGEO_SUCCESS = 1000;
	private final int HANDLER_ERROR = 1001;
	private final int HANDLER_GEO_SUCCESS = 1002;

	//获取页面输入坐标
	private EditText lat;
	private EditText lng;

	private TextView result1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_resgeolocation);
		init();
	}
	private void init() {
		//初始化地名
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
				LonLatPoint lonLatPoint=new LonLatPoint(Double.parseDouble(lng.getText().toString()),Double.parseDouble(lat.getText().toString()));
				resGeoLocation(lonLatPoint);
			}
		});
	}

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


	Handler mGeoHandler = new Handler() {
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
				case HANDLER_RESGEO_SUCCESS:
					if (msg.obj != null) {
						RegeocodeResult result = (RegeocodeResult) msg.obj;
						if (result.getAddressComponent() == null) {
							//Toast.makeText(GeographicActivity.this, "逆地理编码失败", Toast.LENGTH_SHORT).show();
							result1.setText("逆地理编码失败");
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
						//Toast.makeText(GeographicActivity.this, info, Toast.LENGTH_LONG).show();
						result1.setText(info);
					}
					break;
				case HANDLER_GEO_SUCCESS:
					if (msg.obj != null) {
						GeocodeResult geoResult = (GeocodeResult) msg.obj;
						if (geoResult.getAddressComponent() == null) {
							//Toast.makeText(GeographicActivity.this, "地理编码失败", Toast.LENGTH_SHORT).show();
							result1.setText("地理编码失败");
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
						//Toast.makeText(GeographicActivity.this, info, Toast.LENGTH_LONG).show();
						result1.setText(info);
					}
					break;
				case HANDLER_ERROR:
					//Toast.makeText(GeographicActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
					result1.setText(msg.obj.toString());
					break;
			}
		}
	};
}
