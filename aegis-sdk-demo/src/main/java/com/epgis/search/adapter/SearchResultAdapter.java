package com.epgis.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.epgis.epgisapp.R;
import com.epgis.service.api.core.PoiItem;

import java.util.List;

/**
 * Created by zhiwei on 2018/8/13.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<PoiItem> itemList;

    public SearchResultAdapter(List<PoiItem> list) {
        itemList = list;
    }

    public OnItemClick onItemClick;
    public void setOnItemClick(OnItemClick click){
        onItemClick = click;
    }

    @Override
    public ResultItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search, parent, false);
        return new SearchResultAdapter.ResultItemViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //CarmenFeature carmenFeature = itemList.get(position);
        ResultItemViewHolder mHolder = (ResultItemViewHolder) holder;
        final PoiItem item = itemList.get(position);
        mHolder.Name.setText(item.getName());
        mHolder.Address.setText(item.getAddress().replace("\"", "").replace("\"", ""));
        labelDecomposeAndAdd(mHolder.LableLayout, item.getCategory());
        mHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClick != null){
                    onItemClick.Onclik(itemList.get(position));
                }
            }
        });
    }

    private void labelDecomposeAndAdd(ViewGroup viewGroup, String source) {
        if (source != null) {
            source = source.replace("\"", "").replace("\"", "")
                    .replace("\\\\", "\\");
            String[] labels = source.split(",");
            viewGroup.removeAllViews();
            for (int i = 0; i < labels.length; i++) {
                addLabelText(viewGroup, labels[i]);
            }
        }
    }

    private void addLabelText(ViewGroup viewGroup, String input) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.label_text_layout, null, false);
        TextView textView = view.findViewById(R.id.tv_poi_item_category);
        textView.setText(input);
        viewGroup.addView(view);
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    public class ResultItemViewHolder extends RecyclerView.ViewHolder {
        private TextView Name;
        private TextView Address;
        //private TextView Distance;
        private LinearLayout LableLayout;

        public ResultItemViewHolder(View view) {
            super(view);
            Name = (TextView) view.findViewById(R.id.poinameTV);
            Address = (TextView) view.findViewById(R.id.poiaddressTV);
            //Distance = (TextView)view.findViewById(R.id.poidistanceTV);
            LableLayout = (LinearLayout) view.findViewById(R.id.label_layout);
        }
    }

    public interface OnItemClick{
        public void Onclik(PoiItem item);
    }
}
