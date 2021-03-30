package com.epgis.base.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.epgis.epgisapp.R;
import com.epgis.base.model.MapItem;

import java.util.List;

/**
 * Created by yangsimin on 2019/6/12.
 */

public class AegisAdapter extends RecyclerView.Adapter<AegisAdapter.AegisViewHolder> {

    private List<MapItem> mapItemList; // 条目列表

    private Context context;

    public AegisAdapter(Context context, List<MapItem> mapItems) {
        mapItemList = mapItems;
        this.context = context;
    }

    @Override
    public AegisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false);
        AegisViewHolder holder = new AegisViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AegisViewHolder holder, int position) {

        String iconName = mapItemList.get(position).getCategory();
        int icon = context.getResources().getIdentifier(iconName, "drawable", context.getPackageName());

        holder.itemName.setText(mapItemList.get(position).getLabel());
        holder.tvItemDescription.setText(mapItemList.get(position).getDescription());
        holder.ivItem.setImageResource(icon);
    }

    @Override
    public int getItemCount() {
        if (mapItemList != null) {
            return mapItemList.size();
        }
        return 0;
    }

    class AegisViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        TextView tvItemDescription;
        ImageView ivItem;

        public AegisViewHolder(View view) {
            super(view);
            itemName = view.findViewById(R.id.tv_item_name);
            tvItemDescription = view.findViewById(R.id.tv_item_description);
            ivItem = view.findViewById(R.id.iv_item);
        }
    }
}
