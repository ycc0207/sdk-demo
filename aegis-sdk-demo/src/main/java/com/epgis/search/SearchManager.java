package com.epgis.search;

import com.epgis.service.api.search.PoiResult;

/**
 * Created by zhiwei on 2018/8/15.
 */

public class SearchManager {
    public static final int HANDLER_AUTO_SEARCH_SUCCESS = 1000;
    public static final int HANDLER_SEARCH_SUCCESS = 1001;
    public static final int HANDLER_SEARCH_ERROR = 1002;
    public static final int HANDLER_NO_DATA = 1003;
    public static PoiResult response = null;

//    public void asyncSearch(Context context, SearchPoiBean bean, Handler handler){
////        SearchQuery query = new SearchQuery(keyword);
////        SearchPoiBean bean = getSearchPoiBean(context, query, city);
//
//        PoiSearchApi poiSearchApi = new PoiSearchApi();
//        poiSearchApi.setPoiSearchCallback(new PoiSearchApi.PoiSearchCallback() {
//            @Override
//            public void OnBusLineSearch(GeocodingResponse response) {
//                //onSearchListener.OnDistrictSearch(new SearchResult(response.features()));
//                if(response != null) {
//                    if(response.features() != null && response.features().size() > 0) {
//                        handler.obtainMessage(HANDLER_SEARCH_SUCCESS, response).sendToTarget();
//                    }else{
//                        handler.obtainMessage(HANDLER_NO_DATA, "没有查询结果").sendToTarget();
//                    }
//                }else{
//                    handler.obtainMessage(HANDLER_NO_DATA, "没有查询结果").sendToTarget();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                //onSearchListener.onFailure(t)
//                handler.obtainMessage(HANDLER_SEARCH_ERROR, t.toString()).sendToTarget();
//            }
//        });
//        poiSearchApi.asynSearch(bean);
//    }
//
//    public SearchPoiBean getSearchPoiBean(Context context, SearchQuery query, String city) {
//        SearchPoiBean searchPoiBean = new SearchPoiBean();
//        searchPoiBean.accessToken = AuthenticationManager.getInstance(context).getAccessToken();
//        searchPoiBean.query = query.getQuery();
//        searchPoiBean.region = city;
//        searchPoiBean.start = String.valueOf(query.getStart());
//        searchPoiBean.limit = String.valueOf(query.getLimit());
//        return searchPoiBean;
//    }
//
//    public void asyncAutoSearch(Context context, String keyword, String city, Handler handler){
//        SearchQuery query = new SearchQuery(keyword);
//        AutoCompleteBean bean = getAutoCompleteBean(context, query, city);
//
//        PoiSearchApi poiSearchApi = new PoiSearchApi();
//        poiSearchApi.setPoiSearchCallback(new PoiSearchApi.PoiSearchCallback() {
//            @Override
//            public void OnBusLineSearch(GeocodingResponse response) {
//                //onSearchListener.OnDistrictSearch(new SearchResult(response.features()));
//                //Log.d("AutoComplete", "" + response.features());
////                if(response != null) {
////                    handler.obtainMessage(HANDLER_AUTO_SEARCH_SUCCESS, response).sendToTarget();
////                }else{
////                    handler.obtainMessage(HANDLER_SEARCH_ERROR, "没有查询结果").sendToTarget();
////                }
//                if(response != null) {
//                    if(response.features() != null && response.features().size() > 0) {
//                        handler.obtainMessage(HANDLER_AUTO_SEARCH_SUCCESS, response).sendToTarget();
//                    }else{
//                        handler.obtainMessage(HANDLER_NO_DATA, "没有查询结果").sendToTarget();
//                    }
//                }else{
//                    handler.obtainMessage(HANDLER_NO_DATA, "没有查询结果").sendToTarget();
//                }
//            }
//
//            @Override
//            public void onFailure(Throwable t) {
//                //onSearchListener.onFailure(t);
//                Log.d("AutoComplete", "" + t.toString());
//                handler.obtainMessage(HANDLER_SEARCH_ERROR, t.toString()).sendToTarget();
//            }
//        });
//
//        poiSearchApi.autoComplete(bean);
//    }
//
//    private AutoCompleteBean getAutoCompleteBean(Context context, SearchQuery query,  String city) {
//        AutoCompleteBean autoCompleteBean = new AutoCompleteBean();
//        autoCompleteBean.accessToken = AuthenticationManager.getInstance(context).getAccessToken();
//        autoCompleteBean.query = query.getQuery();
//        autoCompleteBean.region = city;
//        return autoCompleteBean;
//    }
}
