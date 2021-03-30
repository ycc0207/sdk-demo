package com.epgis.mapsdk.activities.debug;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.epgis.epgisapp.R;

/**
 * Created by Lynn on 2019/6/14 0014.
 */

public class CrashActivity extends AppCompatActivity {
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_crash);
        mContext = this;

        findViewById(R.id.btn_crash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext = null;
                mContext.getApplicationContext();// 手动触发crash
            }
        });
    }
}
