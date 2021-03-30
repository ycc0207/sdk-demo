package com.epgis.search.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.epgis.epgisapp.R;
import com.epgis.service.api.core.PoiItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhiwei on 2018/8/13.
 */

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.SuggestItemViewHolder> {

    private List<PoiItem> itemList = new ArrayList<PoiItem>();

    public SearchSuggestionAdapter() {

    }

    @Override
    public SuggestItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggest_search, parent, false);
        return new SuggestItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SuggestItemViewHolder holder, int position) {
        holder.Name.setText(itemList.get(position).getName());
        holder.Address.setText(itemList.get(position).getAddress());
    }

    public void addAll(List<PoiItem> lists) {
        addAll(lists, true);
    }

    private void addAll(List<PoiItem> t, boolean notifyDataChange) {
        if (t == null) {
            return;
        }
        itemList.addAll(t);
        if (notifyDataChange) {
            notifyDataSetChanged();
        }
    }

    public void clear() {
        clear(true);
    }

    private void clear(boolean notifyDataChange) {
        itemList.clear();
        if (notifyDataChange) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class SuggestItemViewHolder extends RecyclerView.ViewHolder {

        private TextView Name;
        private TextView Address;

        public SuggestItemViewHolder(View view) {
            super(view);
            Name = (TextView) view.findViewById(R.id.poinameTV);
            Address = (TextView) view.findViewById(R.id.poiadrressTV);
        }
    }
}
