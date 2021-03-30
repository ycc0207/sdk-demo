package com.epgis.base.utils;

import com.epgis.mapsdk.geometry.LatLng;
import com.epgis.mapsdk.utils.AegisUtils;

import java.util.ArrayList;
import java.util.List;

public class RotationAngleUtils {

    /**
     * 根据start和end两个坐标的连线，获取连线相对于Y轴正轴的角度
     *
     * @param start
     * @param end
     * @return
     */
    public static float getRotationAngle(LatLng start, LatLng end){

        int type = getLatLngType(start, end);
        List<Integer> list = getPerimeter(start, end, type);
        float angle = getAngle(list, type);
        return angle;
    }

    private static List<Integer> getPerimeter(LatLng start, LatLng end, int type){
        List<Integer> perimeter = new ArrayList<>();
        LatLng creatLatLng = getThirdLatLng(start, end, type);
        int a = (int) AegisUtils.calculateLineDistance(start,creatLatLng);
        int b = (int)AegisUtils.calculateLineDistance(creatLatLng,end);
        int c = (int)AegisUtils.calculateLineDistance(start,end);
        perimeter.add(a);
        perimeter.add(b);
        perimeter.add(c);
        return perimeter;
    }

    private static float getAngle(List<Integer> list, int type){
        java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");

        // 初始化数据
        int a = list.get(0);
        int b = list.get(1);
        int c = list.get(2);

//        int a = 3;
//        int b = 4;
//        int c = 5;

        // 计算弧度表示的角
        double B = Math.acos((a*a + c*c - b*b)/(2.0*a*c));
        // 用角度表示的角
        B = Math.toDegrees(B);
        switch (type){
            case 1:
                break;
            case 2:
                B = B + 90;
                break;
            case 3:
                B = B + 180;
                break;
            case 4:
                B = B + 270;
                break;
        }
        if(B >= 360){
            B = B - 360;
        }
        // 格式化数据，保留两位小数
        String temp = df.format(B);
        System.out.println(temp);
        return Float.parseFloat(temp);
    }

    /**
     * 根据end坐标相对start坐标的位置，得到的数值，end坐标在start的右上为1。
     * 4 | 1
     * ----
     * 3 | 2
     * start在中心，end在1的位置则返回1， 在2的位置则返回2
     *
     * @return
     */
    private static int getLatLngType(LatLng start, LatLng end){
        double startLat = start.getLatitude();
        double startLng = start.getLongitude();
        double endLat = end.getLatitude();
        double endLng = end.getLongitude();
        if(endLng >= startLng){
            if(endLat >= startLat){
                return 1;
            }else {
                return 2;
            }
        }else {
            if(endLat >= startLat){
                return 4;
            }else {
                return 3;
            }
        }
    }

    private static LatLng getThirdLatLng(LatLng start, LatLng end, int type){
        LatLng thirdLatLng = new LatLng();

        switch (type){
            case 1:
            case 3:
                thirdLatLng.setLongitude(start.getLongitude());
                thirdLatLng.setLatitude(end.getLatitude());
                break;
            case 2:
            case 4:
                thirdLatLng.setLongitude(end.getLongitude());
                thirdLatLng.setLatitude(start.getLatitude());
                break;
        }
        return thirdLatLng;
    }
}
