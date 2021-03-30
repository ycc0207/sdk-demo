package com.epgis.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.annotations.IconFactory;
import com.epgis.mapsdk.annotations.MarkerOptions;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.service.api.core.PoiItem;


/**
 * @author fcy
 * 2020-10-29 11:35:33
 */

public class PoiDetailActivity extends AppCompatActivity implements View.OnClickListener{
	private MapView mapView;        // 地图视图对象
	private PoiItem poi;
	public final static String EXTRA_POI = "extra_poi";
	private AegisMap aegisMap;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poi_detail);
		ImageView goBack = findViewById(R.id.iv_goback);
		goBack.setOnClickListener(this);
		mapView = findViewById(R.id.mapView);
		mapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(@NonNull AegisMap aegisMap) {
				// 设置地图显示样式
				aegisMap.setStyle(Style.STREETS);
				if(poi != null) {
					LatLng latLng = new LatLng(poi.getPoint().getLatitude(), poi.getPoint().getLongitude());
					aegisMap.setCameraPosition(new CameraPosition.Builder().target(latLng).zoom(12D).build());
					addMarker(aegisMap, latLng);
				}
			}
		});
		mapView.onCreate(savedInstanceState);
		Intent intent = getIntent();
		if(intent != null){
			poi = (PoiItem) intent.getParcelableExtra(EXTRA_POI);
		}
		initView();
	}

	private void initView(){
		TextView name = findViewById(R.id.sg_location_name_txt);
		TextView address = findViewById(R.id.sg_location_address_txt);
		if(poi != null) {
			name.setText(poi.getName());
			address.setText(poi.getAddress() != null ? poi.getAddress().replace("\"", "") : "");
		}
	}

	public void addMarker(AegisMap aegisMap, LatLng target) {
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(IconFactory.getInstance(PoiDetailActivity.this).fromResource(R.drawable.blue_marker));
		// 默认传入坐标为思极坐标
		markerOptions.position(target);
		aegisMap.addMarker(markerOptions);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mapView.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mapView.onStop();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mapView.onLowMemory();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_goback:
				finish();
				break;
			default:
				break;
		}
	}
}
