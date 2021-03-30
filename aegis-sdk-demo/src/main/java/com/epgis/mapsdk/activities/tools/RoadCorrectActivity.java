package com.epgis.mapsdk.activities.tools;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.epgis.commons.geojson.Feature;
import com.epgis.commons.geojson.LineString;
import com.epgis.commons.geojson.Point;
import com.epgis.coordinate.CoordinateConverter;
import com.epgis.coordinate.GeoPoint;
import com.epgis.epgisapp.R;
import com.epgis.mapsdk.camera.CameraPosition;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.maps.AegisMap;
import com.epgis.mapsdk.maps.MapView;
import com.epgis.mapsdk.maps.OnMapReadyCallback;
import com.epgis.mapsdk.maps.Style;
import com.epgis.mapsdk.style.layers.LineLayer;
import com.epgis.mapsdk.style.layers.Property;
import com.epgis.mapsdk.style.layers.PropertyFactory;
import com.epgis.mapsdk.style.sources.GeoJsonSource;
import com.epgis.mapsdk.style.sources.Source;
import com.epgis.service.api.roadcorrect.RoadCorrect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.epgis.mapsdk.style.expressions.Expression.floor;
import static com.epgis.mapsdk.style.expressions.Expression.rgb;

/**
 * 纠偏例子
 */
public class RoadCorrectActivity extends Activity  implements View.OnClickListener {

	private final String TAG  = RoadCorrectActivity.class.getSimpleName();

	private MapView mapView;    // 地图视图对象
	private AegisMap mAegisMap;
	private LatLng center = new LatLng(39.030631, 117.260576);
	private LatLng center2 = new LatLng(24.48229525909181, 118.1900113592731);

	//原始数据 wgs84
	ArrayList<Location> locations_1 = new ArrayList<>();
	//原始数据 sg
	ArrayList<Location> locations_2 = new ArrayList<>();

	private RoadCorrect roadCorrect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapsdk_activity_roadcorrect);
		initView(savedInstanceState);
	}

	private RadioGroup radioGroup_orientation;
	private RadioButton radioButton1,radioButton2;

	private void initView(Bundle savedInstanceState){

		radioGroup_orientation = (RadioGroup) findViewById(R.id.rg_Orientation);
		radioGroup_orientation.setOnCheckedChangeListener(radioGrouplisten);

		radioButton1 = findViewById(R.id.radio_data_1);
		radioButton2 = findViewById(R.id.radio_data_2);


		roadCorrect = new RoadCorrect(this);

		findViewById(R.id.bt_jiupian).setOnClickListener(this);


		// 地图视图对象获取
		mapView = findViewById(R.id.mapView);
		// 用于地图恢复状态使用
		mapView.onCreate(savedInstanceState);
		// 异步回调地图视图交互对象
		mapView.getMapAsync(new OnMapReadyCallback() {
			@Override
			public void onMapReady(@NonNull AegisMap aegisMap) {

				mAegisMap = aegisMap;
				// 移图到指定位置
				aegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());
				// 设置地图显示样式
				aegisMap.setStyle(Style.STREETS, new Style.OnStyleLoaded() {
					@Override
					public void onStyleLoaded(@NonNull Style style) {
						drawData1Layer();

//						drawData2Layer();
					}
				});
			}
		});
	}


	//RadioGroup控件的初始化
	private RadioGroup.OnCheckedChangeListener radioGrouplisten = new RadioGroup.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {

			switch (group.getCheckedRadioButtonId()) {
				case R.id.radio_data_1:
//					Toast.makeText(RoadCorrectActivity.this, "radio_data_1", Toast.LENGTH_SHORT).show();
					drawData1Layer();
					removeRoadCorrectLayer();
					break;
				case R.id.radio_data_2:
//					Toast.makeText(RoadCorrectActivity.this, "radio_data_2", Toast.LENGTH_SHORT).show();
					drawData2Layer();
					removeRoadCorrectLayer();
					break;
				default:
					break;
			}
		}
	};

	/**
	 * 至少要两个点以上
	 */
	public void onRequestRoadCorrect(ArrayList<Location> sgLocs){

//		ArrayList<Location> sgLocs = new ArrayList<>();
//		for(Location location : locations_1){
//
//			//将wgs84坐标转成sg坐标
//			Location sgloc = new Location("dummyprovider");
//			double lon = location.getLongitude();
//			double lat = location.getLatitude();
//			GeoPoint sgpoint = wgs84ToSG(lat,lon);
//			sgloc.setLatitude(sgpoint.getLatitude());
//			sgloc.setLongitude(sgpoint.getLongitude());
//			sgloc.setAccuracy(location.getAccuracy());
//			sgloc.setTime(location.getTime());
//			sgloc.setBearing(location.getBearing());
//			sgloc.setSpeed(location.getSpeed());
//
//			sgLocs.add(sgloc);
//		}

		roadCorrect.requestRoadCorrect(sgLocs,new RoadCorrect.OnRoadCorrectListener(){

			@Override
			public void onRoadCorrectSuccess(List<Location> locations) {
				//纠偏成功回调
				Log.d(TAG,"onRoadCorrectSuccess() ");
				Toast.makeText(RoadCorrectActivity.this,"纠偏成功",Toast.LENGTH_SHORT).show();

				if(locations!=null){
					Log.d(TAG,"onRoadCorrectSuccess() size="+locations.size());
					drawRoadCorrectSuccessLineLayer(locations);
				}
			}

			@Override
			public void onSmoothRoadCorrectSuccess(List<Location> smoothlocations) {
				//返回平滑的坐标点（只有经纬度信息，返回坐标点较多，可用于绘制线）

				Log.d(TAG,"onSmoothRoadCorrectSuccess() ");

				if(locations_1 !=null){
					Log.d(TAG,"onSmoothRoadCorrectSuccess() size="+smoothlocations.size());
					drawSmoothLineLayer(smoothlocations);
				}
			}

			@Override
			public void onFailure(String msg) {
				//纠偏失败回调

				Log.d(TAG,"onFailure() ");
				Toast.makeText(RoadCorrectActivity.this,"纠偏失败 "+msg,Toast.LENGTH_SHORT).show();
			}
		});
	}

//	readAssertData_1();
//	LineString lineString = getLineString(locations_1);

	private void drawData1Layer(){

		mAegisMap.setCameraPosition(new CameraPosition.Builder().target(center).zoom(12D).build());

		readAssertData_1();
		LineString lineString = getLineString(locations_1);
		drawLineLayer(lineString);
	}

	private void drawData2Layer(){

		mAegisMap.setCameraPosition(new CameraPosition.Builder().target(center2).zoom(12D).build());

		readAssertData_2();
		LineString lineString = getLineString(locations_2);
		drawLineLayer(lineString);
	}

	/**
	 * 增加原始路线
	 */
	private void drawLineLayer(LineString lineString){

		if(lineString!=null){
			Source source = mAegisMap.getStyle().getSource("line-source1");
			if(source == null){
				mAegisMap.getStyle().addSource(new GeoJsonSource("line-source1", Feature.fromGeometry(lineString)));
				mAegisMap.getStyle().addLayer(new LineLayer("linelayer1", "line-source1").withProperties(
						PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
						PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
						PropertyFactory.lineWidth(5f),
						PropertyFactory.lineColor(rgb(255, 0, 0)
						)
				));
			} else {
				((GeoJsonSource)source).setGeoJson(Feature.fromGeometry(lineString));
			}
		}
	}

	private void removeRoadCorrectLayer(){
		// 线
		if (mAegisMap != null && mAegisMap.getStyle() != null) {
			mAegisMap.getStyle().removeLayer("linelayer2");
			if (mAegisMap.getStyle().getSource("line-source2") != null) {
				mAegisMap.getStyle().removeSource("line-source2");
			}

			mAegisMap.getStyle().removeLayer("linelayer3");
			if (mAegisMap.getStyle().getSource("line-source3") != null) {
				mAegisMap.getStyle().removeSource("line-source3");
			}
		}
	}

	/**
	 * 绘制纠偏成功路线
	 */
	private void drawRoadCorrectSuccessLineLayer(List<Location> locations){
		LineString lineString = getLineString(locations);
		if(lineString!=null){
			Source source = mAegisMap.getStyle().getSource("line-source2");
			if(source == null){
				mAegisMap.getStyle().addSource(new GeoJsonSource("line-source2", Feature.fromGeometry(lineString)));
				mAegisMap.getStyle().addLayer(new LineLayer("linelayer2", "line-source2").withProperties(
						PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
						PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
						PropertyFactory.lineWidth(5f),
						PropertyFactory.lineColor(rgb(0, 255, 0)
						)
				));
			} else {
				((GeoJsonSource)source).setGeoJson(Feature.fromGeometry(lineString));
			}
		}
	}

	/**
	 * 绘制平滑纠偏成功路线
	 */
	private void drawSmoothLineLayer(List<Location> locations){
		LineString lineString = getLineString(locations);
		if(lineString!=null){
			Source source = mAegisMap.getStyle().getSource("line-source3");
			if(source == null){
				mAegisMap.getStyle().addSource(new GeoJsonSource("line-source3", Feature.fromGeometry(lineString)));
				mAegisMap.getStyle().addLayer(new LineLayer("linelayer3", "line-source3").withProperties(
						PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
						PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
						PropertyFactory.lineWidth(5f),
						PropertyFactory.lineColor(rgb(0, 0, 255)
						)
				));
			} else {
				((GeoJsonSource)source).setGeoJson(Feature.fromGeometry(lineString));
			}
		}
	}

	private LineString getLineString(List<Location> locations){
		List<Point> points = new ArrayList<>();
		for(Location location : locations){
			Point point = Point.fromLngLat(location.getLongitude(),location.getLatitude());
			points.add(point);
		}
		LineString lineString = LineString.fromLngLats(points);
		return lineString;
	}

	/**
	 * 读取asset里的wgs84坐标数据
	 */
	private void readAssertData_1() {

		String str_data = loadGeoJsonFromAsset(this, "jiupian.txt");
		JSONArray jsonArray;

		try {
			jsonArray = new JSONArray(str_data);
			locations_1.clear();
			Log.d(TAG,"size = "+jsonArray.length());
			for(int i=0;i<jsonArray.length();i++){

				JSONObject object = (JSONObject) jsonArray.get(i);
				String accuracy = (String) object.get("accuracy");
				String direction = (String) object.get("direction");
				String latitude = (String) object.get("latitude");
				String longitude = (String) object.get("longitude");
				long locatetime = object.getLong("locatetime");
				String speed = (String) object.get("speed");
				Location loc_item = new Location("dummyprovider");
				loc_item.setAccuracy(Float.valueOf(accuracy));

				loc_item.setLatitude(Double.valueOf(latitude));
				loc_item.setLongitude(Double.valueOf(longitude));
				loc_item.setTime(locatetime);
				loc_item.setBearing(Float.valueOf(direction));
				loc_item.setSpeed(Float.valueOf(speed));

				locations_1.add(loc_item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取asset里的sg坐标数据
	 */
	private void readAssertData_2() {

		String str_data = loadGeoJsonFromAsset(this, "jiupian2.txt");
		JSONArray jsonArray;

		try {
			jsonArray = new JSONArray(str_data);
			locations_2.clear();
			Log.d(TAG,"size = "+jsonArray.length());
			for(int i=0;i<jsonArray.length();i++){

				JSONObject object = (JSONObject) jsonArray.get(i);

				double accuracy = (double) object.get("accuracy");
				double direction = (double) object.get("direction");
				double latitude = (double) object.get("latitude");
				double longitude = (double) object.get("longitude");
				long locatetime = object.getLong("locatetime");
				double speed = (double) object.get("speed");

				Location loc_item = new Location("dummyprovider");
				loc_item.setAccuracy((float) accuracy);

				loc_item.setLatitude(latitude);
				loc_item.setLongitude(longitude);
				loc_item.setTime(locatetime);
				loc_item.setBearing((float) direction);
				loc_item.setSpeed((float) speed);

				locations_2.add(loc_item);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private GeoPoint wgs84ToSG(double latitude , double longitude){
		GeoPoint point = new GeoPoint(longitude, latitude);
		GeoPoint sgPoint = CoordinateConverter.getInstance().wgs84ToSG(point);

		return sgPoint;
	}

	private String loadGeoJsonFromAsset(Context context, String filename) {
		try {
			// Load GeoJSON file from local asset folder
			InputStream is = context.getAssets().open(filename);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			return new String(buffer, Charset.forName("UTF-8"));
		} catch (Exception exception) {
			throw new RuntimeException(exception);
		}
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

	private ArrayList<Location> getData1(){
		ArrayList<Location> sgLocs = new ArrayList<>();
		for(Location location : locations_1){

			//将wgs84坐标转成sg坐标
			Location sgloc = new Location("dummyprovider");
			double lon = location.getLongitude();
			double lat = location.getLatitude();
			GeoPoint sgpoint = wgs84ToSG(lat,lon);
			sgloc.setLatitude(sgpoint.getLatitude());
			sgloc.setLongitude(sgpoint.getLongitude());
			sgloc.setAccuracy(location.getAccuracy());
			sgloc.setTime(location.getTime());
			sgloc.setBearing(location.getBearing());
			sgloc.setSpeed(location.getSpeed());

			sgLocs.add(sgloc);
		}

		return sgLocs;
	}

	private ArrayList<Location> getData2(){
		ArrayList<Location> sgLocs = new ArrayList<>();
		for(Location location : locations_2){

			//将wgs84坐标转成sg坐标
			Location sgloc = new Location("dummyprovider");
			double lon = location.getLongitude();
			double lat = location.getLatitude();
			sgloc.setLatitude(lat);
			sgloc.setLongitude(lon);
			sgloc.setAccuracy(location.getAccuracy());
			sgloc.setTime(location.getTime());
			sgloc.setBearing(location.getBearing());
			sgloc.setSpeed(location.getSpeed());

			sgLocs.add(sgloc);
		}

		return sgLocs;
	}

	@Override
	public void onClick(View view) {

		if(view.getId() == R.id.bt_jiupian){
			if(radioButton1.isChecked()){
				onRequestRoadCorrect(getData1());
			} else{
				onRequestRoadCorrect(getData2());
			}
		}
	}
}
