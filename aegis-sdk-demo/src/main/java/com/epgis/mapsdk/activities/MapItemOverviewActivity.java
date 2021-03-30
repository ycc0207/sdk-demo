package com.epgis.mapsdk.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.epgis.epgisapp.R;
import com.epgis.base.adapters.MapItemAdapter;
import com.epgis.base.adapters.MapItemSectionAdapter;
import com.epgis.base.model.MapItem;
import com.epgis.base.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

public class MapItemOverviewActivity extends AppCompatActivity {

    private static final String KEY_STATE_FEATURES = "featureList";
    private RecyclerView recyclerView;
    private MapItemSectionAdapter sectionAdapter;
    private List<MapItem> features;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapsdk_activity_feature_overview);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener());
        recyclerView.setHasFixedSize(true);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener((recyclerView, position, view) -> {
            if (!sectionAdapter.isSectionHeaderPosition(position)) {
                int itemPosition = sectionAdapter.getConvertedPosition(position);
                MapItem feature = features.get(itemPosition);
                startFeature(feature);
            }
        });

        if (savedInstanceState == null) {
            loadFeatures();
        } else {
            features = savedInstanceState.getParcelableArrayList(KEY_STATE_FEATURES);
            onFeaturesLoaded(features);
        }
    }

    private void loadFeatures() {
        try {
            new LoadFeatureTask().execute(
                    getPackageManager().getPackageInfo(getPackageName(),
                            PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException exception) {
        }
    }

    private void onFeaturesLoaded(List<MapItem> featuresList) {
        features = featuresList;
        if (featuresList == null || featuresList.isEmpty()) {
            return;
        }

        List<MapItemSectionAdapter.Section> sections = new ArrayList<>();
        String currentCat = "";
        for (int i = 0; i < features.size(); i++) {
            String category = features.get(i).getCategory();
            if (!currentCat.equals(category)) {
                sections.add(new MapItemSectionAdapter.Section(i, category));
                currentCat = category;
            }
        }

        MapItemSectionAdapter.Section[] dummy = new MapItemSectionAdapter.Section[sections.size()];
        sectionAdapter = new MapItemSectionAdapter(
                this, R.layout.mapsdk_recycle_section_item, R.id.section_text, new MapItemAdapter(features));
        sectionAdapter.setSections(sections.toArray(dummy));
        recyclerView.setAdapter(sectionAdapter);
    }

    private void startFeature(MapItem feature) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(getPackageName(), feature.getName()));
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(KEY_STATE_FEATURES, (ArrayList<MapItem>) features);
    }

    private class LoadFeatureTask extends AsyncTask<PackageInfo, Void, List<MapItem>> {

        @Override
        protected List<MapItem> doInBackground(PackageInfo... params) {
            List<MapItem> features = new ArrayList<>();
            PackageInfo app = params[0];

            String packageName = "com.epgis.mapsdk";
            String metaDataKey = getString(R.string.category);
            for (ActivityInfo info : app.activities) {
                if (info.labelRes != 0 && info.name.startsWith(packageName)
                        && !info.name.equals(MapItemOverviewActivity.class.getName())) {
                    String label = getString(info.labelRes);
                    String description = resolveString(info.descriptionRes);
                    String category = resolveMetaData(info.metaData, metaDataKey);
                    features.add(new MapItem(info.name, label, description, category));
                }
            }

            return features;
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

        @Override
        protected void onPostExecute(List<MapItem> features) {
            super.onPostExecute(features);
            onFeaturesLoaded(features);
        }
    }
}
