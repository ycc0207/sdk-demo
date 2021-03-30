package com.epgis;

import android.app.Application;
import android.os.Environment;

import com.epgis.auth.AuthenticationManager;
import com.epgis.mapsdk.Aegis;

import java.io.File;

/**
 * Created by huangsiwen on 2019/2/25.
 */

public class AegisApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 使用地图SDK必须优先调用该接口，建议放在Application里面
        //申请单的现有服务列表，选择公司外网，请用该域名：https://maps.epgis.com
        //Aegis.getInstance(this, "https://lbs.sgmap.cn");
        Aegis.getInstance(this, "https://map.sgcc.com.cn");
        //申请单的现有服务列表，选择阿里云，请用该域名：https://map.epgis.com.cn
//        Aegis.getInstance(this, "https://map.epgis.com.cn");
    }
}
