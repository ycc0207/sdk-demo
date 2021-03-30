package com.epgis.navisdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.epgis.epgisapp.R;
import com.epgis.navisdk.ui.AegisNavi;
import com.epgis.navisdk.ui.model.GuideBusRoute;

import java.util.List;

/**
 * 公交路线规划界面
 */
public class BusResultActivity extends Activity implements View.OnClickListener {

    private static final String TAG = BusResultActivity.class.getSimpleName();

    private List<GuideBusRoute> guideBusRoutes;

    private ListView mResultListView;//路线结果View
    private RouteBusResultListAdapter mRouteBusResultListAdapter;//公交换乘默认条件设置

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.activity_bus_result);
        initView();
        initData();
    }

    private void initView() {

        findViewById(R.id.title_btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView titleView = (TextView) findViewById(R.id.title_text_name);

        titleView.setText("公交详情");

        mResultListView = (ListView) findViewById(R.id.result_listview);

        mRouteBusResultListAdapter = new RouteBusResultListAdapter(
                this, guideBusRoutes);
//        mRouteBusResultListAdapter.setShowBusTag(mCurMethodIndex == 0); // 只有推荐线路时显示标签
        mResultListView.setAdapter(mRouteBusResultListAdapter);
        mResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(BusResultActivity.this, BusMapActivity.class);

                intent.putExtra("postion", i);

                startActivity(intent);
            }
        });
    }

    private void initData() {
        guideBusRoutes = AegisNavi.getInstance().getGuideBusRoutes();
        if (mRouteBusResultListAdapter != null) {
            mRouteBusResultListAdapter.setListData(guideBusRoutes);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
