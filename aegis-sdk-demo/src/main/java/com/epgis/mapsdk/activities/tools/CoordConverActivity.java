package com.epgis.mapsdk.activities.tools;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.coordinate.CoordinateConverter;
import com.epgis.coordinate.GeoPoint;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.geometry.LatLng;

public class CoordConverActivity extends Activity  {

	private TextView resultview;
	private LatLng sourceLatLng = new LatLng(31.4666819259,118.1160736084);

	private EditText elat1 , elng1 ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapsdk_activity_convert);
		init();
	}

	private void init() {

		elat1 = findViewById(R.id.lat1);
		elng1 = findViewById(R.id.lng1);

		elat1.setText(sourceLatLng.getLatitude()+"");
		elng1.setText(sourceLatLng.getLongitude()+"");

		resultview = (TextView)findViewById(R.id.result);

		findViewById(R.id.bt_w84_sg).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				String textlat1 = elat1.getText().toString();
				String textlng1 = elng1.getText().toString();

				if(TextUtils.isEmpty(textlat1) || TextUtils.isEmpty(textlng1)){
					Toast.makeText(getApplicationContext(),"经纬度不能为空",Toast.LENGTH_SHORT).show();
					return;
				}
				wgs84ToSG(Double.parseDouble(textlat1),
						Double.parseDouble(textlng1));
			}
		});

		findViewById(R.id.bt_cgcs2000_sg).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String textlat1 = elat1.getText().toString();
				String textlng1 = elng1.getText().toString();

				if(TextUtils.isEmpty(textlat1) || TextUtils.isEmpty(textlng1)){
					Toast.makeText(getApplicationContext(),"经纬度不能为空",Toast.LENGTH_SHORT).show();
					return;
				}
				cgcs2000ToSG(Double.parseDouble(textlat1),
						Double.parseDouble(textlng1));
			}
		});
	}

	private void cgcs2000ToSG(double latitude , double longitude){
		GeoPoint point = new GeoPoint(longitude, latitude);
		GeoPoint sgPoint = CoordinateConverter.getInstance().cgcs2000ToSG(point);
		resultview.setText("结果2（lat/lng）： "+sgPoint.getLatitude()+" , "+sgPoint.getLongitude() );
	}

	private void wgs84ToSG(double latitude , double longitude){
		GeoPoint point = new GeoPoint(longitude, latitude);
		GeoPoint sgPoint = CoordinateConverter.getInstance().wgs84ToSG(point);
		resultview.setText("结果1（lat/lng）： "+sgPoint.getLatitude()+" , "+sgPoint.getLongitude() );
	}
}
