package com.epgis.mapsdk.activities.tools;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.commons.utils.TextUtils;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.geometry.VisibleRegion;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;

public class ScreenToGeoActivity extends Activity  implements OnClickListener,OnMapReadyCallback{

	private Button mBt;
	private PointF mPoint;
	private LatLng mLatlng;

	private TextView textView ;
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
		setContentView(R.layout.mapsdk_activity_screen2loc);
		mMapView = (MapView) findViewById(R.id.mymapView);
		mMapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
		
	}

	private void init() {
		mBt = (Button)findViewById(R.id.bt_sceent_2_geo);
		mBt.setOnClickListener(this);

		textView = findViewById(R.id.result);

		//3、地图创建与设置
		mMapView.getMapAsync(this);

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
		case R.id.bt_sceent_2_geo:
			if(mAegisMap!=null){
				VisibleRegion visibleRegion = mAegisMap.getProjection().getVisibleRegion();
//				visibleRegion.latLngBounds;

				String str1 = visibleRegion.farLeft.getLatitude()+","+visibleRegion.farLeft.getLongitude()+"\n";
				String str2 = visibleRegion.farRight.getLatitude()+","+visibleRegion.farRight.getLongitude()+"\n";
				String str3 = visibleRegion.nearLeft.getLatitude()+","+visibleRegion.nearLeft.getLongitude()+"\n";
				String str4 = visibleRegion.nearRight.getLatitude()+","+visibleRegion.nearRight.getLongitude()+"\n";

				textView.setText(str1+str2+str3+str4);


			}

			break;

		default:
			break;
		}
		
	}

	@Override
	public void onMapReady(@NonNull AegisMap aegisMap) {

		mAegisMap = aegisMap;
		mAegisMap.setStyle(Style.STREETS);
		mAegisMap.setCameraPosition(new CameraPosition.Builder().target(currentLocation).zoom(12.0f).build());

	}



}
