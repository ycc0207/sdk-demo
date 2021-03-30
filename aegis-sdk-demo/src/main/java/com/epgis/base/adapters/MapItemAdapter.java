package com.epgis.base.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epgis.base.model.MapItem;
import com.epgis.base.utils.FontCache;
import com.epgis.epgisapp.R;

import java.util.List;

public class MapItemAdapter extends RecyclerView.Adapter<MapItemAdapter.ViewHolder> {

    private List<MapItem> mapItemList; // 条目列表

    public MapItemAdapter(List<MapItem> mapItemList) {
        this.mapItemList = mapItemList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mapsdk_recycle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.labelView.setText(mapItemList.get(position).getLabel());
        holder.descriptionView.setText(mapItemList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return mapItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView labelView;
        public TextView descriptionView;

        public ViewHolder(View view) {
            super(view);
            Typeface typeface = FontCache.get("fonts/huawenxinsong.ttf", view.getContext());
            labelView = view.findViewById(R.id.nameView);
            labelView.setTypeface(typeface);
            descriptionView = view.findViewById(R.id.descriptionView);
            descriptionView.setTypeface(typeface);
        }
    }
}
