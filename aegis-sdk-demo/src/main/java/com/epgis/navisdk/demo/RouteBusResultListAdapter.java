package com.epgis.navisdk.demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.epgis.base.utils.VerticalImageSpan;
import com.epgis.epgisapp.R;
import com.epgis.navisdk.ui.model.GuideBusRoute;
import com.epgis.navisdk.ui.utils.MapUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressLint("InflateParams")
public class RouteBusResultListAdapter extends BaseAdapter {

    private static final String POINT_ICON_REPLACE_TEXT = " "; // space
    private static final Pattern POINT_ICON_REPLACE_PATTERN = Pattern.compile(POINT_ICON_REPLACE_TEXT);

    private static final String NEXT_ICON_REPLACE_TEXT = ">";
    private static final Pattern NEXT_ICON_REPLACE_PATTERN = Pattern.compile(NEXT_ICON_REPLACE_TEXT);

    private LayoutInflater mLayoutInflater;
    private List<GuideBusRoute> guideBusRoutes;
    private Context mContext;

    public RouteBusResultListAdapter(Context context, List<GuideBusRoute> guideBusRoutes) {
        mContext = context;
        this.guideBusRoutes = guideBusRoutes;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (guideBusRoutes != null) {
            return guideBusRoutes.size();
        } else {
            return 0;
        }
    }

    public void setListData(List<GuideBusRoute> guideBusRoutes) {
        this.guideBusRoutes = guideBusRoutes;
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {

        return guideBusRoutes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        BusListViewHolder viewHolder = null;
        final GuideBusRoute data = guideBusRoutes.get(position);
        if (convertView == null) {
            viewHolder = new BusListViewHolder();
            convertView = mLayoutInflater.inflate(
                    R.layout.v4_fromto_bus_result_item, null);
            viewHolder.main_des_tv = (TextView) convertView
                    .findViewById(R.id.main_des);
            viewHolder.sub_des_tv = (TextView) convertView
                    .findViewById(R.id.sub_des);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (BusListViewHolder) convertView.getTag();
        }

        String str = data.transferBrief;

        SpannableString ss = new SpannableString(str);
        // set next icon
        Matcher matcher = NEXT_ICON_REPLACE_PATTERN.matcher(str);
        while (matcher.find()) {
            ss.setSpan(new VerticalImageSpan(mContext, R.drawable.bus_result_item_main_des_next),
                    matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        viewHolder.main_des_tv.setText(ss);

        String subDesText = MapUtils.getStringRestTime(guideBusRoutes.get(position).duration); //"33分钟"; //sb.toString();
        ss = new SpannableString(subDesText);
        // set point icon
        matcher = POINT_ICON_REPLACE_PATTERN.matcher(subDesText);
        while (matcher.find()) {
            ss.setSpan(new VerticalImageSpan(mContext, R.drawable.bus_result_item_sub_des_point),
                    matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        viewHolder.sub_des_tv.setTextColor(mContext.getResources().getColor(
                R.color.fromto_bus_result_item_sub_text));
        viewHolder.sub_des_tv.setText(ss);

        return convertView;
    }

    /**
     * 超过25个字，截断，用省略号替代
     */
    private String truncate(String str) {
        int LIMIT = 25;
        String text = "";

        if (TextUtils.isEmpty(str)) {
            return text;
        }

        text = str.trim();

        if (text.length() > LIMIT) {
            text = text.replace(text.substring(LIMIT, text.length()), "...");
        }

        return text;
    }


    private static class BusListViewHolder {
        private TextView main_des_tv;
        private TextView sub_des_tv;

    }
}
