package com.epgis.navisdk.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

//import com.aegis.aegismapdemo.R;
import com.epgis.epgisapp.R;
import com.epgis.navisdk.core.utils.DistanceUtils;
import com.epgis.navisdk.core.utils.IconUtil;
import com.epgis.navisdk.core.utils.LocaleUtils;
import com.epgis.navisdk.ui.AegisNavi;
import com.epgis.navisdk.ui.model.GuideRoute;
import com.epgis.navisdk.ui.model.GuideRouteDetailItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import static com.epgis.navisdk.core.navigation.NavigationUnitType.NONE_SPECIFIED;


/**
 * 路线详情
 */
public class RouteDetailActivity extends Activity {

    private static final String TAG = RouteDetailActivity.class.getSimpleName();
    public static final String BUNDLE_KEY_RESULT = "route_result";

    private MyAdapter mAdapter;

    private ListView rvInstructions;

    private GuideRoute route;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.route_detail_activity);

        initView();
        initData();
    }


    protected void initView() {

        findViewById(R.id.title_btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView titleView = (TextView) findViewById(R.id.title_text_name);

        titleView.setText("路线详情");
        rvInstructions = findViewById(R.id.rvInstructions);
        mAdapter = new MyAdapter(this);
        rvInstructions.setAdapter(mAdapter);
    }

    protected void initData() {
        route = AegisNavi.getInstance().getCurRoute();
        if (route != null) {
            List<GuideRouteDetailItem> steps = new ArrayList<>();//= route.legs().get(0).steps();

            if (route != null) {
                for (int i = 0; i < route.steps.size(); i++) {
                    GuideRouteDetailItem leg = route.steps.get(i);
                    steps.add(leg);
                }
            }

            mAdapter.updateSteps(steps);
        }
    }

    class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        private List<GuideRouteDetailItem> stepList;
        private DistanceUtils distanceUtils;

        public MyAdapter(Context context) {
//            super(context, textViewResourceId,obj);
            // TODO Auto-generated constructor stub


            this.mInflater = LayoutInflater.from(context);

            stepList = new ArrayList<>();
            Locale locale = LocaleUtils.getDeviceLocale(getApplicationContext());
            distanceUtils = new DistanceUtils(context, locale, NONE_SPECIFIED);
        }


        public void updateSteps(List<GuideRouteDetailItem> steps) {
            stepList.clear();
            stepList.addAll(steps);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
//            return 0;
            return stepList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
//            return null;

            if (convertView == null) {
                //创建新的view视图.
                convertView = mInflater.inflate(R.layout.route_detail_listitem, null); //see above, you can use the passed resource id.
            }
            ViewHolder holder = (ViewHolder) convertView.getTag();

            if (holder == null) {
                holder = new ViewHolder();

                holder.maneuverView = convertView.findViewById(R.id.maneuverView);
                holder.stepDistanceText = convertView.findViewById(R.id.stepDistanceText);
                holder.stepPrimaryText = convertView.findViewById(R.id.stepPrimaryText);
//                holder.stepSecondaryText = convertView.findViewById(com.epgis.services.android.navigation.R.id.stepSecondaryText);
                holder.instructionLayoutText = convertView.findViewById(R.id.instructionLayoutText);

                //保存对每个显示的ViewItem中, 各个子View的引用对象
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            GuideRouteDetailItem step = stepList.get(position);

            holder.stepPrimaryText.setText(step.name);

            Log.d(TAG, "position=" + position + ", name=" + step.name + " , iconid=" + step.iconId);

            holder.maneuverView.setImageResource(IconUtil.getDrawableIcon(step.iconId));

            SpannableString distanceText = distanceUtils.formatDistance(step.distance);
            holder.stepDistanceText.setText(distanceText);

            return convertView;
        }

        private class ViewHolder {
            ImageView maneuverView;
            TextView stepDistanceText;
            TextView stepPrimaryText;
            //            TextView stepSecondaryText;
            View instructionLayoutText;
        }
    }
}
