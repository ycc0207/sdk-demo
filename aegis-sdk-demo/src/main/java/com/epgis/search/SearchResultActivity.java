package com.epgis.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.epgis.epgisapp.R;
import com.epgis.search.adapter.LoadMoreWrapper;
import com.epgis.search.adapter.RecyclerOnScrollListener;
import com.epgis.search.adapter.SearchResultAdapter;
import com.epgis.service.api.core.PoiItem;
import com.epgis.service.api.placesearch.PlaceQuery;
import com.epgis.service.api.placesearch.PlaceResult;
import com.epgis.service.api.placesearch.PlaceSearch;
import com.epgis.service.api.search.PoiQuery;
import com.epgis.service.api.search.PoiResult;
import com.epgis.service.api.search.PoiSearch;
import com.epgis.service.api.search.models.CarmenFeature;
import com.epgis.service.api.search.util.SearchUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView mSearchResultRecyView;
    private SearchResultAdapter mSearchResultAdapter;
    private List<PoiItem> dataList = new ArrayList<PoiItem>();
    private PoiResult results;
    private SearchManager mSearchManager;
    private String mKeyword;
    private String mCity;
    private int mcurrentPageNum = 1;
    private LoadMoreWrapper loadMoreWrapper;
    private PlaceSearch mPlaceSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
		mPlaceSearch = new PlaceSearch(this);
        mcurrentPageNum = 1;
        mSearchManager = new SearchManager();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            mKeyword = bundle.getString("key");
            mCity = bundle.getString("city");
        }
        if (savedInstanceState != null) {
            mKeyword = savedInstanceState.getString("key");
            mCity = savedInstanceState.getString("city");
            mcurrentPageNum = savedInstanceState.getInt("pagenum");
        }
        initData();
        initView();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString("key", mKeyword);
        outState.putString("city", mCity);
        outState.putInt("pagenum", mcurrentPageNum);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initData() {
        results = SearchManager.response;
        if (results == null) {
            return;
        }
        dataList.clear();
        for (PoiItem item : results.getPois()) {
            dataList.add(item);
        }
    }

    private void initView() {
        mSearchResultRecyView = (RecyclerView) findViewById(R.id.searchResultRcyView);
        mSearchResultAdapter = new SearchResultAdapter(dataList);
        loadMoreWrapper = new LoadMoreWrapper(mSearchResultAdapter);
        mSearchResultRecyView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultRecyView.setAdapter(loadMoreWrapper);
        mSearchResultAdapter.setOnItemClick(new SearchResultAdapter.OnItemClick() {
            @Override
            public void Onclik(PoiItem item) {
                Intent intent = new Intent(SearchResultActivity.this, PoiDetailActivity.class);
                intent.putExtra(PoiDetailActivity.EXTRA_POI, item);
                startActivity(intent);
            }
        });
        mSearchResultRecyView.addOnScrollListener(new RecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (dataList.size() < results.getTotal()) {
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING);
                    asyncSearch();
                } else {
                    loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_END);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //SearchManager.response = null;
    }

    private void asyncSearch() {
		PlaceQuery query = new PlaceQuery();
		query.setQuery(mKeyword);
		query.setRegion(mCity);
		query.setStart(mcurrentPageNum * 10);
        if (mPlaceSearch != null) {
            mPlaceSearch.setOnPlaceSearchListener(new PlaceSearch.OnPlaceSearchListener() {
				@Override
				public void onPlaceSearch(PlaceResult result) {
					PoiResult poiResult = getPoiResult(result);
					mHandler.obtainMessage(SearchManager.HANDLER_SEARCH_SUCCESS, poiResult).sendToTarget();
				}

				@Override
				public void onFailure(Throwable throwable) {
					mHandler.obtainMessage(SearchManager.HANDLER_NO_DATA, throwable.toString()).sendToTarget();
				}
            });
            mPlaceSearch.searchPlaceAsyn(query);
        }
    }

	private PoiResult getPoiResult(PlaceResult result) {
		PoiResult poiResult;
		if (result != null) {
			ArrayList<PoiItem> pois = new ArrayList<PoiItem>();
			for (CarmenFeature feature : result.getFeatures()) {
				pois.add(SearchUtils.featureToPoiItem(feature));
			}
			poiResult = new PoiResult(pois.size(), pois);
			poiResult.setTotal(result.getTotal());
		} else {
			poiResult = new PoiResult(0, null);
		}
		return poiResult;
	}

	private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case SearchManager.HANDLER_SEARCH_ERROR:
                    //Toast.makeText(SearchResultActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    break;
                case SearchManager.HANDLER_NO_DATA:
                    break;
                case SearchManager.HANDLER_SEARCH_SUCCESS:
                    mcurrentPageNum++;
                    if (msg.obj != null) {
                        PoiResult newresults = (PoiResult) msg.obj;
                        if (newresults.getPois() != null) {
                            //List<PoiItem> pois = new ArrayList<PoiItem>();
                            for (PoiItem item : newresults.getPois()) {
                                dataList.add(item);
                            }
                        }
                    }
                    //Log.d("tt", "" + dataList);
                    //mSearchResultAdapter.addAll(pois);
                    //mSearchResultAdapter.appendList(pois);
                    break;
            }

            loadMoreWrapper.setLoadState(loadMoreWrapper.LOADING_COMPLETE);
        }
    };
}
