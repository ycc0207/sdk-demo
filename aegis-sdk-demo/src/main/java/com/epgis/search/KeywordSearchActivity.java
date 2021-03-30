package com.epgis.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.epgis.epgisapp.R;
import com.epgis.search.adapter.SearchSuggestionAdapter;
import com.epgis.search.widget.SearchEditText;
import com.epgis.service.api.core.PoiItem;
import com.epgis.service.api.inputtips.InputtipsQuery;
import com.epgis.service.api.inputtips.InputtipsResult;
import com.epgis.service.api.inputtips.InputtipsSearch;
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

/**
 * @author fcy
 * poi/关键字查询
 * 2020-10-29 10:59:28
 */
public class KeywordSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private static boolean mIsInlectricMode = false;
    private SearchEditText mSearchText;
    private SearchEditText mCityText;
    private Button mSearchBtn;
    private String mKeyword;
    private RecyclerView mSuggetstionRcview;
    private SearchSuggestionAdapter mSearchSuggestionAdapter;
    private String mCity;
    private SearchManager mSearchManager;
    private InputtipsSearch inputtipsSearch;
    private PlaceSearch mPlaceSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyword_search);
        mSearchManager = new SearchManager();
        inputtipsSearch = new InputtipsSearch(this);
        mPlaceSearch = new PlaceSearch(this);
        initView();
        setTitle("地图SDK-关键字搜索");
    }

    private void initView() {
        mSuggetstionRcview = (RecyclerView) findViewById(R.id.suggestionRcyView);
        mSearchSuggestionAdapter = new SearchSuggestionAdapter();
        mSuggetstionRcview.setLayoutManager(new LinearLayoutManager(this));
        mSuggetstionRcview.setAdapter(mSearchSuggestionAdapter);
        mSearchText = (SearchEditText) findViewById(R.id.searchET);
        mCityText = (SearchEditText) findViewById(R.id.cityET);
        mSearchBtn = (Button) findViewById(R.id.searchBT);
        mSearchBtn.setOnClickListener(this);
        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mKeyword = s.toString().trim();
                if (mKeyword.length() == 0) {
                } else {
                    suggestAsyncSearch();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == mSearchBtn) {
            startSearch();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case SearchManager.HANDLER_AUTO_SEARCH_SUCCESS:
                    if (msg.obj != null) {
                        PoiResult results = (PoiResult) msg.obj;
                        List<PoiItem> carmenFeatureList = results.getPois();
                        if (carmenFeatureList != null && carmenFeatureList.size() > 0) {
                            mSearchSuggestionAdapter.clear();
                            mSearchSuggestionAdapter.addAll(carmenFeatureList);
                        } else {
                            mSearchSuggestionAdapter.clear();
                        }
                    }
                    break;
                case SearchManager.HANDLER_SEARCH_ERROR:
                    break;
                case SearchManager.HANDLER_NO_DATA:
                    Toast.makeText(KeywordSearchActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case SearchManager.HANDLER_SEARCH_SUCCESS:
                    PoiResult results = (PoiResult) msg.obj;
                    Intent intent = new Intent(KeywordSearchActivity.this, SearchResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("city", mCity);
                    bundle.putString("key", mKeyword);
                    bundle.putInt("pageNum", results.getPageCount());
                    intent.putExtras(bundle);
                    SearchManager.response = results;
                    startActivity(intent);
                    break;
            }
        }
    };

    private boolean isCanSearch() {
        mCity = mCityText.getText().toString().trim();
        if (TextUtils.isEmpty(mCity)) {
            Toast.makeText(KeywordSearchActivity.this, "城市必填", Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(mKeyword)) {
            Toast.makeText(KeywordSearchActivity.this, "关键字必填", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void startSearch() {
        if (!isCanSearch()) {
            return;
        }
        asyncSearch();
    }

    /**
     * 关键字搜索
     */
    private void asyncSearch() {
        PlaceQuery query = new PlaceQuery();
        query.setQuery(mKeyword);
        query.setRegion(mCity);
        query.setStart(0);
        if (mPlaceSearch != null) {
            mPlaceSearch.setOnPlaceSearchListener(new PlaceSearch.OnPlaceSearchListener(){
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

    /**
     * 联想搜索
     */
    private void suggestAsyncSearch() {
        if (!isCanSearch()) {
            return;
        }
        asyncAutoSearch();
    }

    private void asyncAutoSearch() {
        InputtipsQuery query = new InputtipsQuery();
        query.setQuery(mKeyword);
        query.setRegion(mCity);
        if (inputtipsSearch != null) {
            inputtipsSearch.setOnInputtipsSearchListener(new InputtipsSearch.OnInputtipsSearchListener() {
                @Override
                public void onInputtipsSearch(InputtipsResult result) {
                    PoiResult poiResult = getPoiResult(result);
                    mHandler.obtainMessage(SearchManager.HANDLER_AUTO_SEARCH_SUCCESS, poiResult).sendToTarget();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    mHandler.obtainMessage(SearchManager.HANDLER_NO_DATA, throwable.toString()).sendToTarget();
                }
            });
            inputtipsSearch.searchTipsAsync(query);
        }
    }

    private PoiResult getPoiResult(InputtipsResult result) {
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
}
