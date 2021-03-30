package com.epgis;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.Toast;

import com.epgis.base.adapters.AegisAdapter;
import com.epgis.epgisapp.BuildConfig;
import com.epgis.epgisapp.R;
import com.epgis.base.model.MapItem;
import com.epgis.base.utils.ItemClickSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by yangsimin on 2019/3/12.
 */

public class AegisActivity extends CheckPermissionsActivity {

    private static final String TAG = AegisActivity.class.getSimpleName();
    private TextView tvTimer;
    private RecyclerView recyclerView;
    private List<MapItem> mapItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aegis);

        // 改变5.0以上版本状态栏颜色,设为黑色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack));
        }
        initViews();
        initData();
    }


    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        tvTimer = findViewById(R.id.tv_timer);
    }

    private void initData() {

        PackageInfo app = null;
        try {
            app = getPackageManager().getPackageInfo(getPackageName(),
                    PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        for (ActivityInfo info : app.activities) {
            String kind = resolveMetaData(info.metaData, "icon");
            if (kind != null) {
                String label = getString(info.labelRes);
                String description = resolveString(info.descriptionRes);
                mapItems.add(new MapItem(info.name, label, description, kind));
            }
        }
        AegisAdapter aegisAdapter = new AegisAdapter(this, mapItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(aegisAdapter);

        //设置列表点击监听，调整到各个功能activity
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView, position, view) -> {
            startFeature(mapItems.get(position));
        });

        // 添加提交记录和安装时间
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);
            long installTime = pi.firstInstallTime;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA);
            String info = getResources().getString(R.string.app_info) + sdf.format(installTime);
            String output = "提交SHA1 = " + BuildConfig.GIT_SHA1 + "\r\n" + info;
            tvTimer.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/huawenxinsong.ttf"));
            tvTimer.setText(output);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void startFeature(MapItem feature) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getPackageName(), feature.getName()));
        startActivity(intent);
    }

    private String resolveMetaData(Bundle bundle, String key) {
        String category = null;
        if (bundle != null) {
            category = bundle.getString(key);
        }
        return category;
    }

    private String resolveString(@StringRes int stringRes) {
        try {
            return getString(stringRes);
        } catch (Resources.NotFoundException exception) {
            return "-";
        }
    }

    private long curTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - curTime >= 1500) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                curTime = System.currentTimeMillis();
            } else {
                this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
