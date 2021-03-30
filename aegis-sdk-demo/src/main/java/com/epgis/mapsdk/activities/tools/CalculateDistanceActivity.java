package com.epgis.mapsdk.activities.tools;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.epgisapp.R;
import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.utils.AegisUtils;

public class CalculateDistanceActivity extends Activity {

//	24.4666819259,118.1160736084
//	24.5030821130,118.1780433655

	private EditText elat1 , elng1 , elat2 , elng2;

	private LatLng latlngA = new LatLng(31.4666819259,118.1160736084);
	private LatLng latlngB = new LatLng(31.5030821130,118.1780433655);

	private TextView result;
	private float distance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapsdk_activity_cal_dis);
		init();
//		distance = AMapUtils.calculateLineDistance(makerA.getPosition(), makerB.getPosition());
	}
	private void init() {

		elat1 = findViewById(R.id.lat1);
		elng1 = findViewById(R.id.lng1);
		elat2 = findViewById(R.id.lat2);
		elng2 = findViewById(R.id.lng2);

		elat1.setText(latlngA.getLatitude()+"");
		elng1.setText(latlngA.getLongitude()+"");
		elat2.setText(latlngB.getLatitude()+"");
		elng2.setText(latlngB.getLongitude()+"");

		result = (TextView) findViewById(R.id.result);
		findViewById(R.id.bt_cal_dis).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {


				String textlat1 = elat1.getText().toString();
				String textlng1 = elng1.getText().toString();
				String textlat2 = elat2.getText().toString();
				String textlng2 = elng2.getText().toString();


				if(TextUtils.isEmpty(textlat1)
						|| TextUtils.isEmpty(textlng1)
						|| TextUtils.isEmpty(textlat2)
						|| TextUtils.isEmpty(textlng2)){

					Toast.makeText(getApplicationContext(),"经纬度不能为空",Toast.LENGTH_SHORT).show();
					return;
				}

				latlngA.setLatitude(Double.parseDouble(textlat1));
				latlngA.setLongitude(Double.parseDouble(textlng1));
				latlngB.setLatitude(Double.parseDouble(textlat2));
				latlngB.setLongitude(Double.parseDouble(textlng2));

				double dis = AegisUtils.calculateLineDistance(latlngA,latlngB);

				result.setText("距离："+dis+" 米");
			}
		});
	}

	private  final double EARTH_RADIUS = 6371393;

	/**
	 * 通过AB点经纬度获取距离
	 * @return 距离(单位：米)
	 */
	public double getDistance(double lng1,double lat1,double lng2,double lat2) {


		// 经纬度（角度）转弧度。弧度用作参数，以调用Math.cos和Math.sin
		double radiansAX = Math.toRadians(lng1); // A经弧度
		double radiansAY = Math.toRadians(lat1); // A纬弧度
		double radiansBX = Math.toRadians(lng2); // B经弧度
		double radiansBY = Math.toRadians(lat2); // B纬弧度

		// 公式中“cosβ1cosβ2cos（α1-α2）+sinβ1sinβ2”的部分，得到∠AOB的cos值
		double cos = Math.cos(radiansAY) * Math.cos(radiansBY) * Math.cos(radiansAX - radiansBX)
				+ Math.sin(radiansAY) * Math.sin(radiansBY);
//        System.out.println("cos = " + cos); // 值域[-1,1]
		double acos = Math.acos(cos); // 反余弦值
//        System.out.println("acos = " + acos); // 值域[0,π]
//        System.out.println("∠AOB = " + Math.toDegrees(acos)); // 球心角 值域[0,180]
		return EARTH_RADIUS * acos; // 最终结果

	}

}
