package com.epgis.mapsdk.activities.tools;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.epgis.commons.utils.TextUtils;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;

public class GeoToScreenActivity extends Activity  implements OnClickListener,MapView.OnDidFinishLoadingStyleListener,OnMapReadyCallback,AegisMap.OnMapClickListener{
//	private AegisMap aMap;
//	private MapView mapView;
	private EditText latView, lngView, xView, yView;
	private Button lnglat2pointBtn, point2LatlngBtn;
	private PointF mPoint;
	private LatLng mLatlng;
	private int x, y;
	private float lat, lng;

	// 定义地图视图对象
	private MapView mMapView;
	// 定义地图管理对象
	private AegisMap mAegisMap;

	private LatLng currentLocation = new LatLng(24.5346308296, 118.1352996826);
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapsdk_activity_location2screen);
		mMapView = (MapView) findViewById(R.id.mymapView);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
	}

	private void init() {
//		if (aMap == null) {
//			aMap = mapView.getMap();
//			setUpMap();
//		}
		latView = (EditText)findViewById(R.id.pointLat);
		lngView = (EditText)findViewById(R.id.pointLng);
		xView = (EditText)findViewById(R.id.pointX);
		yView = (EditText)findViewById(R.id.pointY);
		lnglat2pointBtn = (Button)findViewById(R.id.lnglat2pointbtn);
		point2LatlngBtn = (Button)findViewById(R.id.point2Latlngbtn);
		lnglat2pointBtn.setOnClickListener(this);
		point2LatlngBtn.setOnClickListener(this);



		//3、地图创建与设置

		mMapView.addOnDidFinishLoadingStyleListener(this);
		mMapView.getMapAsync(this);

	}

	private void setUpMap() {
//		aMap.setOnMapClickListener(this);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lnglat2pointbtn:
			toScreenLocation();
			break;
		case R.id.point2Latlngbtn:
//			toGeoLocation();
			break;
		default:
			break;
		}
		
	}

	@Override
	public void onDidFinishLoadingStyle() {

	}

	@Override
	public void onMapReady(@NonNull AegisMap aegisMap) {

		mAegisMap = aegisMap;
		mAegisMap.setStyle(Style.STREETS);
		mAegisMap.setCameraPosition(new CameraPosition.Builder().target(currentLocation).zoom(12.0f).build());

		mAegisMap.addOnMapClickListener(this);

	}

	@Override
	public boolean onMapClick(@NonNull LatLng point) {


		latView.setText(point.getLatitude()+"");
		lngView.setText(point.getLongitude()+"");

		return false;
	}

//	private void toGeoLocation() {
//		if (AMapUtil.IsEmptyOrNullString(xView.getText().toString()) ||
//				AMapUtil.IsEmptyOrNullString(yView.getText().toString())) {
//			Toast.makeText(GeoToScreenActivity.this, "x和y为空", Toast.LENGTH_SHORT).show();
//		} else {
//			x = Integer.parseInt(xView.getText().toString().trim());
//			y = Integer.parseInt(yView.getText().toString().trim());
//			mPoint = new Point(x, y);
//			mLatlng = aMap.getProjection().fromScreenLocation(mPoint);
//			if (mLatlng != null) {
//				latView.setText(String.valueOf(mLatlng.latitude));
//				lngView.setText(String.valueOf(mLatlng.longitude));
//			}
//		}
//
//	}
	private void toScreenLocation() {
		if (TextUtils.isEmpty(latView.getText().toString()) || TextUtils.isEmpty(lngView.getText().toString())) {
			Toast.makeText(GeoToScreenActivity.this, "经纬度为空", Toast.LENGTH_SHORT).show();
		} else {
			lat = Float.parseFloat(latView.getText().toString().trim());
			lng = Float.parseFloat(lngView.getText().toString().trim());
			mLatlng = new LatLng(lat, lng);
			mPoint = mAegisMap.getProjection().toScreenLocation(mLatlng);
			if (mPoint != null) {
				xView.setText(String.valueOf(mPoint.x));
				yView.setText(String.valueOf(mPoint.y));
			}
		}
	}

}
