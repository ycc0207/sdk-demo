package com.epgis.offline.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.epgis.epgisapp.R;
import com.epgis.offline.DownloadState;
import com.epgis.offline.OfflineMapCity;
import com.epgis.offline.OfflineMapManager;
import com.epgis.offline.OfflineMapProvince;
import com.epgis.offline.adapter.OfflineAllCityListAdapter;
import com.epgis.offline.adapter.OfflineDownloadListAdapter;
import com.epgis.offline.adapter.OfflinePagerAdapter;
import com.epgis.offline.adapter.SearchCityListAdapter;
import com.epgis.offline.helper.MyLinearLayoutManager;
import com.epgis.offline.helper.NoScrollRecycleview;
import com.epgis.offline.helper.RecyclerViewListDecoration;
import com.epgis.offline.init.OfflineDatabaseHelper;
import com.epgis.offline.service.DownloadService;
import com.epgis.offline.service.EventMessage;
import com.epgis.offline.util.NetworkUtil;
import com.epgis.offline.util.ToastUtil;
import com.epgis.offline.widget.CommonDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.epgis.offline.adapter.OfflineAllCityListAdapter.ITEM_CLICK_TYPE_DOWNLOAD_ALL;
import static com.epgis.offline.adapter.OfflineDownloadListAdapter.COLLET_ITEM_CLICK_TYPE_DEL;

/**
 * Created by Lynn on 2019/3/15.
 */
public class OfflineMapFragment extends NodeFragment implements OfflineMapManager.OnOfflineMapDownloadListener, View.OnClickListener {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_download_all_city_list)
    TextView tvDownloadAllCityList;
    @BindView(R.id.tv_downloaded_city_list)
    TextView tvDownloadedCityList;
    @BindView(R.id.vp_content_viewpage)
    ViewPager mContentViewPage;
    @BindView(R.id.del_all)
    ImageView delAll;
    @BindView(R.id.offline_downloadcity_tv)
    TextView offlineDownloadCity;

    Unbinder mUnbinder;
    private Handler mHandler;
    public final static int UPDATE_LIST = 0;//????????????

    private ProgressDialog initDialog;// ???????????????????????????????????????dialog
    private ExpandableListView mAllOfflineMapList;
    private RecyclerView mDownLoadedList;
    private RecyclerView mDownLoadedCompletedList;
    private TextView mDownloadToptip, updateall, downloadall, pauseall;
    public DownloadCityPageTip mDownloadCityPageTip;  //????????????????????????
    private NetworkReceiver mNetReceiver;      //  ??????????????????????????????????????????
    private RelativeLayout mOfflineAllCityContainer;
    private RelativeLayout mOfflineDownloadCityContainer;
    private PagerAdapter mPageAdapter;
    private OfflineAllCityListAdapter mOfflineListAdapter;
    private OfflineDownloadListAdapter mOfflineDownloadListAdapter, mOfflineDownCompleteListAdapter;
    private OfflineMapManager mOfflineMapManager;
    private TextView mDownLoadingTextTips, mDownCompleteTextTips;
    private ScrollView mDownManagerScrollView;//???????????????????????????
    private String TAG = "OfflineMapManager";
    //????????????????????????
    private List<OfflineMapProvince> provinceList = new ArrayList<OfflineMapProvince>();
    private SparseArray<ArrayList<OfflineMapCity>> cityInfoMap = null;
    // ?????????????????????????????????
    public ArrayList<OfflineMapCity> mDownloadCities = new ArrayList<OfflineMapCity>();

    private Context mContext;
    private boolean isDebug = true;

    /**
     * ???????????????????????????
     */
    private static int count = 1;
    private static long mLastClickTime;
    private boolean isDelMoreOpen = false;

    public OfflineAllCityListAdapter getOfflineListAdapter() {
        return mOfflineListAdapter;
    }

    public OfflineDownloadListAdapter getOfflineDownloadListAdapter() {
        return mOfflineDownloadListAdapter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_offline_map_download_fragment);
        mContext = getActivity().getApplicationContext();
        EventBus.getDefault().register(this);
        mHandler = new MyHandler(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initDialog();
        mOfflineMapManager = new OfflineMapManager(mContext, this);
    }

    @Override
    public void onDownload(int status, int completeCode, String name) {
    }

    @Override
    public void onCheckUpdate(boolean hasNew, String name) {
    }

    @Override
    public void onRemove(boolean success, String name, String describe) {
    }

    @Override
    public void onInited(boolean isInited) {
        new MyAsyncTask(isInited).execute();

    }

    @SuppressLint("StaticFieldLeak")
    public class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        private boolean isInited;

        private MyAsyncTask(boolean init) {
            isInited = init;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            initViewPage();
            dissmissDialog();
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (isInited) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mContentViewPage != null)
                                mContentViewPage.setVisibility(View.VISIBLE);
                            if (offlineDownloadCity != null)
                                offlineDownloadCity.setVisibility(View.GONE);
                        }
                    });
                }

                initAllCityList();
                initDownloadCityList();
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mContentViewPage != null) mContentViewPage.setVisibility(View.GONE);
                            if (offlineDownloadCity != null)
                                offlineDownloadCity.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
            return null;
        }
    }

    private void initViewPage() {
        if (mContentViewPage == null) {
            return;
        }
        mPageAdapter = new OfflinePagerAdapter(mContentViewPage, mOfflineAllCityContainer, mOfflineDownloadCityContainer);
        mContentViewPage.setAdapter(mPageAdapter);
        mContentViewPage.setCurrentItem(0);
        mContentViewPage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tvDownloadAllCityList.setBackground(getResources().getDrawable(R.drawable.offline_left_checked));
                        tvDownloadAllCityList.setTextColor(Color.WHITE);
                        tvDownloadedCityList.setBackground(getResources().getDrawable(R.drawable.offline_right_normal));
                        tvDownloadedCityList.setTextColor(Color.GRAY);
                        if (mOfflineListAdapter != null) mOfflineListAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        tvDownloadAllCityList.setBackground(getResources().getDrawable(R.drawable.offline_left_normal));
                        tvDownloadAllCityList.setTextColor(Color.GRAY);
                        tvDownloadedCityList.setBackground(getResources().getDrawable(R.drawable.offline_right_checked));
                        tvDownloadedCityList.setTextColor(Color.WHITE);
                        if (mOfflineDownloadListAdapter != null)
                            mOfflineDownloadListAdapter.notifyDataSetChanged();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initAllCityList() {
        View provinceContainer = LayoutInflater.from(mContext).inflate(R.layout.offline_allcity_container, null);
        mOfflineAllCityContainer = provinceContainer.findViewById(R.id.offline_allcity_container);
        mAllOfflineMapList = (ExpandableListView) provinceContainer.findViewById(R.id.province_download_list);
        provinceList = mOfflineMapManager.getAllOfflineMapProvinceList();
        cityInfoMap = mOfflineMapManager.getAllOffineCityInMap();
        mOfflineListAdapter = new OfflineAllCityListAdapter(this, mHandler, provinceList, cityInfoMap);
        // ??????????????????
        mOfflineListAdapter.setOnItemCliclkListener(new OfflineAllCityListAdapter.ItemClickListener() {
            @Override
            public void onItemCliclk(String type, int position) {
                if (type.equals(ITEM_CLICK_TYPE_DOWNLOAD_ALL)) {
                    downloadAll(position);
                }
            }
        });
        // ????????????????????????
        mAllOfflineMapList.setAdapter(mOfflineListAdapter);
        // adapter?????????????????????????????????????????????
        mAllOfflineMapList.setOnGroupCollapseListener(mOfflineListAdapter);
        mAllOfflineMapList.setOnGroupExpandListener(mOfflineListAdapter);
        mAllOfflineMapList.setGroupIndicator(null);
        initAllCitySearchList(provinceContainer);
    }

    private EditText searchContentEt;
    private ListView citySearchListLV;
    private TextView noResultCity;
    protected List<OfflineMapCity> searchCityList = new ArrayList<>();
    //????????????
    private SearchCityListAdapter searchCityListAdapter;

    private void initAllCitySearchList(View view) {
        searchContentEt = view.findViewById(R.id.search_edit);
        citySearchListLV = view.findViewById(R.id.search_city_lv);
        noResultCity = view.findViewById(R.id.no_search_result_tv);
        searchCityListAdapter = new SearchCityListAdapter(mContext);
        searchCityListAdapter.setDataList(searchCityList);
        citySearchListLV.setAdapter(searchCityListAdapter);
        //initTotalCityList();
        searchContentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = searchContentEt.getText().toString().trim().toLowerCase();
                setSearchCityList(content);
            }
        });

        searchContentEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftInput(searchContentEt.getWindowToken());
                    String content = searchContentEt.getText().toString().trim().toLowerCase();
                    setSearchCityList(content);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * ???????????????
     */
    public void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * ????????????????????????
     */
    private void setSearchCityList(String content) {
        searchCityList.clear();
        if (TextUtils.isEmpty(content)) {
            mAllOfflineMapList.setVisibility(View.VISIBLE);
            citySearchListLV.setVisibility(View.GONE);
            noResultCity.setVisibility(View.GONE);
        } else {
            mAllOfflineMapList.setVisibility(View.GONE);
            if (provinceList != null && provinceList.size() > 0 && cityInfoMap != null && cityInfoMap.size() > 0) {
                for (OfflineMapProvince offlineMapProvince : provinceList) {
                    for (OfflineMapCity data : cityInfoMap.get(offlineMapProvince.getAdCode(), null)) {
                        if (data.getCityName().contains(content) || data.getPinyin().contains(content) || data.getJianpin().contains(content)) {
                            data.setProCode(offlineMapProvince.getAdCode());
                            searchCityList.add(data);
                        }
                    }
                }
            }
            if (searchCityList.size() != 0) {
                noResultCity.setVisibility(View.GONE);
                citySearchListLV.setVisibility(View.VISIBLE);
            } else {
                noResultCity.setVisibility(View.VISIBLE);
                citySearchListLV.setVisibility(View.GONE);
            }
            searchCityListAdapter.notifyDataSetChanged();
        }
    }


    private void initDownloadCityList() {
        View downloadCityContainer = LayoutInflater.from(mContext).inflate(R.layout.offline_downloadcity_container, null);
        mDownManagerScrollView = downloadCityContainer.findViewById(R.id.scroll_view);
        mDownLoadingTextTips = downloadCityContainer.findViewById(R.id.tv_tip_downloading);
        mDownCompleteTextTips = downloadCityContainer.findViewById(R.id.tv_tip_downloaded);
        mDownloadToptip = downloadCityContainer.findViewById(R.id.tv_offline_downloadcity_top_tip);
        mOfflineDownloadCityContainer = downloadCityContainer.findViewById(R.id.offline_downloadcity_container);
        mDownLoadedList = downloadCityContainer.findViewById(R.id.offline_downloadcity_list);
        NoScrollRecycleview noScrollRecycleview = new NoScrollRecycleview(mContext);  //????????????????????????
        noScrollRecycleview.setOrientation(OrientationHelper.VERTICAL);  //??????
        noScrollRecycleview.setScrollEnabled(false);  //????????????
        mDownLoadedList.setLayoutManager(new MyLinearLayoutManager(mContext));
        mDownLoadedList.addItemDecoration(new RecyclerViewListDecoration(mContext, RecyclerViewListDecoration.VERTICAL_LIST));

        //mDownLoadedList.setHasFixedSize(true);
//        mDownLoadedList.addItemDecoration(new RecyclerViewListDecoration(this,
//                RecyclerViewListDecoration.VERTICAL_LIST));

        // ??????????????????\??????\?????????????????????
//        updateall = downloadCityContainer.findViewById(R.id.updateall);
//        downloadall = downloadCityContainer.findViewById(R.id.downloadall);
//        pauseall = downloadCityContainer.findViewById(R.id.pauseall);
//        updateall.setOnClickListener(this);
//        downloadall.setOnClickListener(this);
//        pauseall.setOnClickListener(this);
        mOfflineDownloadListAdapter = new OfflineDownloadListAdapter(this, mHandler);
        mOfflineDownloadListAdapter.setAdpaterType(OfflineDownloadListAdapter.TYPE_DOWNLOADING_LIST);
        mDownloadCities = mOfflineMapManager.getDownloadCityList(true);
        mOfflineDownloadListAdapter.setListData(mDownloadCities);
        mDownLoadingTextTips.setVisibility(mOfflineDownloadListAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        mOfflineDownloadListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mOfflineDownloadListAdapter != null) {
                    mDownLoadingTextTips.setVisibility(mOfflineDownloadListAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
                }
            }
        });
        // ??????????????????
        mOfflineDownloadListAdapter.setOnItemCliclkListener(new OfflineDownloadListAdapter.ItemClickListener() {
            @Override
            public void onItemCliclk(String type, OfflineMapCity offlineMapCity, int position) {
                if (type.equals(COLLET_ITEM_CLICK_TYPE_DEL)) {
                    deleteDownload(offlineMapCity, position, OfflineDownloadListAdapter.TYPE_DOWNLOADING_LIST);
                }
            }
        });
        mDownLoadedList.setAdapter(mOfflineDownloadListAdapter);
        mDownLoadedCompletedList = downloadCityContainer.findViewById(R.id.offline_download_complete_city_list);
        NoScrollRecycleview noScrollRecycleview2 = new NoScrollRecycleview(mContext);  //????????????????????????
        noScrollRecycleview2.setOrientation(OrientationHelper.VERTICAL);  //??????
        noScrollRecycleview2.setScrollEnabled(false);  //????????????
        mDownLoadedCompletedList.setLayoutManager(new MyLinearLayoutManager(mContext));
        mDownLoadedCompletedList.addItemDecoration(new RecyclerViewListDecoration(mContext, RecyclerViewListDecoration.VERTICAL_LIST));
        mOfflineDownCompleteListAdapter = new OfflineDownloadListAdapter(this, mHandler);
        mOfflineDownCompleteListAdapter.setAdpaterType(OfflineDownloadListAdapter.TYPE_DOWNLOAD_COMPLETE_LIST);
        mOfflineDownCompleteListAdapter.setListData(mDownloadCities);
        mDownCompleteTextTips.setVisibility(mOfflineDownCompleteListAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
        mOfflineDownCompleteListAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (mOfflineDownCompleteListAdapter != null) {
                    mDownCompleteTextTips.setVisibility(mOfflineDownCompleteListAdapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
                }
            }
        });
        mOfflineDownCompleteListAdapter.setOnItemCliclkListener(new OfflineDownloadListAdapter.ItemClickListener() {
            @Override
            public void onItemCliclk(String type, OfflineMapCity offlineMapCity, int position) {
                if (type.equals(COLLET_ITEM_CLICK_TYPE_DEL)) {
                    deleteDownload(offlineMapCity, position, OfflineDownloadListAdapter.TYPE_DOWNLOAD_COMPLETE_LIST);
                }
            }
        });
        mDownLoadedCompletedList.setAdapter(mOfflineDownCompleteListAdapter);
        mDownLoadedList.setOnTouchListener(recylviewtouchListener);
        mDownLoadedCompletedList.setOnTouchListener(recylviewtouchListener);
    }

    /**
     * ??????????????????touch??????
     */
    int mLastX = 0, mLastY = 0;
    View.OnTouchListener recylviewtouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            int x = (int) event.getX();
            int y = (int) event.getY();
            if (action == MotionEvent.ACTION_DOWN) {
                mDownManagerScrollView.requestDisallowInterceptTouchEvent(true);
            } else if (action == MotionEvent.ACTION_MOVE) {
                int xDiff = Math.abs(x - mLastX);
                int yDiff = Math.abs(y - mLastY);
                // ?????????????????????????????????????????????????????????????????????scrollView????????????
                if (xDiff < yDiff) {
                    mDownManagerScrollView.requestDisallowInterceptTouchEvent(false);
                } else {
                    mDownManagerScrollView.requestDisallowInterceptTouchEvent(true);
                }
            } else if (action == MotionEvent.ACTION_UP) {
                mDownManagerScrollView.requestDisallowInterceptTouchEvent(true);
            }
            mLastX = x;
            mLastY = y;
            return false;
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //????????????
//            case R.id.updateall:
//                boolean needUpdate = false;
//                ArrayList<OfflineMapCity> offlineMapCityArrayList = OfflineMapDownloadController.getInstance(mContext).getDownloadCityList(false);
//                for (OfflineMapCity offlineMapCity : offlineMapCityArrayList) {
//                    if (offlineMapCity.mStatus == DownloadState.state_upgrade) {
//                        needUpdate = true;
//                    }
//                }
//                if (needUpdate) {
//                    new CommonDialog(getContext(), R.style.dialog, "???????????????????????????", new CommonDialog.OnCloseListener() {
//                        @Override
//                        public void onClick(Dialog dialog, boolean confirm) {
//                            if (confirm) {
//                                if (mOfflineDownloadListAdapter != null) {
//                                    mOfflineDownloadListAdapter.updateAll();
//                                }
//                                dialog.dismiss();
//                            }
//                        }
//                    }).setTitle("").show();
//                } else {
//                    ToastHelper.showToast("????????????????????????????????????");
//                }
//                break;
//            //????????????
//            case R.id.downloadall:
//                boolean needStart = false;
//                ArrayList<OfflineMapCity> offlineMapCityArrayList1 = OfflineMapDownloadController.getInstance(mContext).getDownloadCityList(false);
//                for (OfflineMapCity offlineMapCity : offlineMapCityArrayList1) {
//                    if (offlineMapCity.mStatus != DownloadState.state_download_completed &&
//                            offlineMapCity.mStatus != DownloadState.state_download_doing) {
//                        needStart = true;
//                    }
//                }
//                if (needStart) {
//                    new CommonDialog(getContext(), R.style.dialog, "???????????????????????????", new CommonDialog.OnCloseListener() {
//                        @Override
//                        public void onClick(Dialog dialog, boolean confirm) {
//                            if (confirm) {
//                                if (mOfflineDownloadListAdapter != null) {
//                                    mOfflineDownloadListAdapter.downloadAll();
//                                }
//                                dialog.dismiss();
//                            }
//                        }
//                    }).setTitle("").show();
//                } else {
//                    ToastHelper.showToast("?????????????????????????????????");
//                }
//                break;
//            //????????????
//            case R.id.pauseall:
//                boolean needStop = false;
//                ArrayList<OfflineMapCity> offlineMapCityArrayList2 = OfflineMapDownloadController.getInstance(mContext).getDownloadCityList(false);
//                for (OfflineMapCity offlineMapCity : offlineMapCityArrayList2) {
//                    if (offlineMapCity.mStatus == DownloadState.state_download_doing) {
//                        needStop = true;
//                    }
//                }
//                if (needStop) {
//                    new CommonDialog(getContext(), R.style.dialog, "???????????????????????????", new CommonDialog.OnCloseListener() {
//                        @Override
//                        public void onClick(Dialog dialog, boolean confirm) {
//                            if (confirm) {
//                                if (mOfflineDownloadListAdapter != null) {
//                                    mOfflineDownloadListAdapter.pauseAll();
//                                }
//                                dialog.dismiss();
//                            }
//                        }
//                    }).setTitle("").show();
//                } else {
//                    ToastHelper.showToast("?????????????????????????????????");
//                }
//                break;
        }
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    private void initDialog() {
        if (initDialog == null)
            initDialog = new ProgressDialog(getContext());
        initDialog.setMessage("?????????????????????????????????");
        initDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        initDialog.setIndeterminate(false);
        initDialog.setCancelable(true);
        initDialog.show();
    }

    /**
     * ???????????????
     */
    private void dissmissDialog() {
        if (initDialog != null) {
            initDialog.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        if (mNetReceiver != null) {
            getActivity().getApplicationContext().unregisterReceiver(mNetReceiver);
        }
        mOfflineMapManager.backupToSdcard(mContext, false);
        mUnbinder.unbind();
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (searchContentEt != null) {
            hideSoftInput(searchContentEt.getWindowToken());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (initDialog != null) {
            initDialog.dismiss();
            initDialog.cancel();
        }
    }

    @Override
    public void onAttach(Context context) {
        mNetReceiver = new NetworkReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        context.getApplicationContext().registerReceiver(mNetReceiver, filter);
        mDownloadCityPageTip = new DownloadCityPageTip();
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }


    @OnClick({R.id.iv_back, R.id.del_all,
            R.id.tv_download_all_city_list, R.id.tv_downloaded_city_list})
    public void onViewPagedSwitch(View v) {
        switch (v.getId()) {
            case R.id.tv_download_all_city_list:
                hideSoftInput(searchContentEt.getWindowToken());
                if (mContentViewPage.getCurrentItem() != 0) {
                    mContentViewPage.setCurrentItem(0);
                    tvDownloadAllCityList.setBackground(getResources().getDrawable(R.drawable.offline_left_checked));
                    tvDownloadAllCityList.setTextColor(Color.WHITE);
                    tvDownloadedCityList.setBackground(getResources().getDrawable(R.drawable.offline_right_normal));
                    tvDownloadedCityList.setTextColor(Color.GRAY);
                }
                break;
            case R.id.tv_downloaded_city_list:
                hideSoftInput(searchContentEt.getWindowToken());
                if (mContentViewPage.getCurrentItem() != 1) {
                    mContentViewPage.setCurrentItem(1);
                    tvDownloadAllCityList.setBackground(getResources().getDrawable(R.drawable.offline_left_normal));
                    tvDownloadAllCityList.setTextColor(Color.GRAY);
                    tvDownloadedCityList.setBackground(getResources().getDrawable(R.drawable.offline_right_checked));
                    tvDownloadedCityList.setTextColor(Color.WHITE);
                } else {
                    if (!isDelMoreOpen) {
                        if (count < 6) {
                            long time = System.currentTimeMillis();
                            if (Math.abs(time - mLastClickTime) < 500) {
                                ++count; // ????????????
                            } else {
                                count = 1;
                            }
                            if (count >= 5) {
                                ToastUtil.show(mContext, "????????? " + count + "/ 7 ???");
                            }
                            mLastClickTime = time;
                        } else {
                            count = 1;
                            isDelMoreOpen = true;
                            delAll.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.del_all:
                if (mOfflineDownloadListAdapter != null) {
                    mOfflineDownloadListAdapter.setDelMore(true);
                    mOfflineDownloadListAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }

    }

    public void updateDownloadTopTipState(int e) {
        if (mDownloadCityPageTip != null) {
            mDownloadCityPageTip.moveState(e);
            updateDownloadTopTipContent(mDownloadCityPageTip.getCurContent());
        }
    }

    private void updateDownloadTopTipContent(String content) {
        if (mDownloadToptip != null) {
            final int state = mDownloadCityPageTip.getCurState();
            Spannable sp = new SpannableStringBuilder(content);
            if (null != sp) {
                if (state == DownloadCityPageTip.STATE_NETWORK_WIFI) {
                    sp.setSpan(new ForegroundColorSpan(Color.BLACK), 3, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 3, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else if (state == DownloadCityPageTip.STATE_NETWORK_NON_WIFI) {
                    sp.setSpan(new ForegroundColorSpan(Color.BLACK), 4, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    sp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 4, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            mDownloadToptip.setText(sp);
        }
    }

    /**
     * ????????????????????????????????????????????? ???????????????????????????????????????
     */
    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            //call float info board sm ,move state
            //update ui
            DownloadCityPageTip sm = mDownloadCityPageTip;
            if (sm == null) {
                return;
            }

            int net_stat = NetworkUtil.checkNetwork(mContext);
            switch (net_stat) {
                case 0://network connection error
                    sm.moveState(DownloadCityPageTip.EVENT_NETWORK_ERROR);
                    break;
                case 1://wifi network
                    sm.moveState(DownloadCityPageTip.EVENT_NETWORK_WIFI);
                    break;
                default://cell network
                    sm.moveState(DownloadCityPageTip.EVENT_NETWORK_NON_WIFI);
                    break;
            }
            updateDownloadWork(net_stat);// ??????????????????????????????????????????????????????
            updateDownloadTopTipContent(sm.getCurContent());
        }
    }

    // ??????????????????????????????????????????????????????
    private void updateDownloadWork(int net_state) {
        Intent startIntent = new Intent(mContext, DownloadService.class);
        startIntent.setAction(DownloadService.ACTION_NET_CHANGE);
        startIntent.putExtra("NetState", net_state);
        mContext.startService(startIntent);
    }

    /**
     * ??????Page????????????
     */
    public class DownloadCityPageTip {
        private int mCurState = -1;
        public static final int EVENT_NONE = 0;
        public static final int EVENT_NETWORK_ERROR = 1;
        public static final int EVENT_NETWORK_WIFI = 2;
        public static final int EVENT_NETWORK_NON_WIFI = 3;

        public static final int STATE_NETWORK_ERROR = 1;
        public static final int STATE_NETWORK_WIFI = 2;
        public static final int STATE_NETWORK_NON_WIFI = 3;

        public void moveState(int event) {
            int net_stat;
            switch (event) {
                case EVENT_NONE:
                    net_stat = NetworkUtil.checkNetwork(mContext);
                    if (net_stat == 0) {
                        mCurState = STATE_NETWORK_ERROR;
                    } else {
                        if (net_stat == 1) {
                            mCurState = STATE_NETWORK_WIFI;
                        } else {
                            mCurState = STATE_NETWORK_NON_WIFI;
                        }
                    }
                    break;
                case EVENT_NETWORK_ERROR:
                    mCurState = STATE_NETWORK_ERROR;
                    break;
                case EVENT_NETWORK_WIFI:
                    mCurState = STATE_NETWORK_WIFI;
                    break;
                case EVENT_NETWORK_NON_WIFI:
                    mCurState = STATE_NETWORK_NON_WIFI;
                    break;
                default:
                    break;
            }
        }

        public int getCurState() {
            return mCurState;
        }

        public String getCurContent() {
            String cur_content = "????????????";
            switch (mCurState) {
                case STATE_NETWORK_ERROR:
                    cur_content = "???????????????????????????????????????";
                    break;
                case STATE_NETWORK_WIFI:
                    cur_content = "?????????Wi-Fi??????????????????????????????????????????????????????";//??????????????????
                    break;
                case STATE_NETWORK_NON_WIFI:
                    cur_content = "?????????????????????????????????????????????Wi-Fi????????????";
                    break;
                default:
                    cur_content = "?????????????????????????????????????????????";
                    break;
            }
            return cur_content;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        OfflineMapCity city = (OfflineMapCity) eventMessage.getObject();
        if (city != null) {
            switch (eventMessage.getType()) {
                case DownloadState.state_download_completed://????????????
                    //mOfflineDownloadListAdapter.finished(city);
                    try {
                        mOfflineDownloadListAdapter.notifyDownCompleteListChanged(city);
                        mOfflineDownCompleteListAdapter.notifyDownCompleteListChanged(city);
                        mOfflineDownCompleteListAdapter.finished(city);
                        mOfflineListAdapter.finished(city);
                    } catch (Exception e) {
                        if(isDebug) Log.i(TAG,"getEventMessage exception:" + e.getMessage());
                        e.printStackTrace();
                    }
                    // ??????????????????????????????sd???
                    if (mOfflineMapManager != null) {
                        if (isDebug)
                            Log.i(TAG, "mOfflineMapFragment backupToSdcard");
                        mOfflineMapManager.backupToSdcard(mContext, false);
                    }
                    if (searchCityList.size() > 0) {
                        searchCityListAdapter.finished(city);
                    }
                    break;
                case DownloadState.state_download_pause://????????????
                    mOfflineDownloadListAdapter.pause(city);
                    mOfflineListAdapter.pause(city);
                    // ????????????????????????sd???
                    if (mOfflineMapManager != null) {
                        mOfflineMapManager.backupToSdcard(mContext, false);
                    }
                    if (searchCityList.size() > 0) {
                        searchCityListAdapter.pause(city);
                    }
                    break;
                case DownloadState.state_download_progress://??????????????????
                    mOfflineDownloadListAdapter.updateProgress(city);
                    //mOfflineListAdapter.updateProgress(city);
                    break;
                case DownloadState.state_data_error://????????????
                    mOfflineDownloadListAdapter.error(city);
                    mOfflineListAdapter.error(city);
                    // ??????????????????????????????sd???
                    if (mOfflineMapManager != null) {
                        mOfflineMapManager.backupToSdcard(mContext, false);
                    }
                    if (searchCityList.size() > 0) {
                        searchCityListAdapter.error(city);
                    }
                    break;
                case DownloadState.state_download_doing://????????????
                    mOfflineDownloadListAdapter.start(city);
                    mOfflineListAdapter.start(city);
                    // ??????????????????????????????sd???
                    if (mOfflineMapManager != null) {
                        mOfflineMapManager.backupToSdcard(mContext, false);
                    }
                    if (searchCityList.size() > 0) {
                        searchCityListAdapter.start(city);
                    }
                    break;

                case DownloadState.state_download_waiting://???????????? -- ?????????????????????????????????????????????????????????
                    mOfflineDownloadListAdapter.loadData(true, city);
                    mOfflineDownloadListAdapter.notifyDownloadListChanged(city);
                    mOfflineDownloadListAdapter.waiting(city);
                    mOfflineListAdapter.waiting(city);
//                    // ??????????????????????????????sd???
//                    if (mOfflineMapManager != null) {
//                        mOfflineMapManager.backupToSdcard(mContext, false);
//                    }
                    if (searchCityList.size() > 0) {
                        searchCityListAdapter.waiting(city);
                    }
                    break;
            }
        }
    }

    /**
     * ??????
     */
    private void deleteDownload(final OfflineMapCity offlineMapCity, final int position, final int type) {
        String name = offlineMapCity.getCityName();
        new CommonDialog(getContext(), R.style.dialog, "???????????????????????????" + name + "??????", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    // ??????????????????????????????
                    OfflineMapCity offlineMapCity1 = mOfflineDownloadListAdapter.deleDownload(offlineMapCity);
                    // ?????????????????????
                    if (position != -1) {
                        //mDownloadCities.remove(position);
                        if (type == OfflineDownloadListAdapter.TYPE_DOWNLOADING_LIST) {
                            if (mOfflineDownloadListAdapter != null) {
                                mOfflineDownloadListAdapter.notifyDeleteListChanged(offlineMapCity);
                            }
                        } else {
                            if (mOfflineDownCompleteListAdapter != null) {
                                mOfflineDownCompleteListAdapter.notifyDeleteListChanged(offlineMapCity);
                            }
                        }
                    } else {
                        if (type == OfflineDownloadListAdapter.TYPE_DOWNLOADING_LIST) {
                            if (mOfflineDownloadListAdapter != null) {
                                mOfflineDownloadListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (mOfflineDownCompleteListAdapter != null) {
                                mOfflineDownCompleteListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    // ?????????????????????????????????
                    if (mOfflineListAdapter != null) {
                        mOfflineListAdapter.unStart(offlineMapCity1);
                    }
                    // ????????????????????????sd???
                    if (mOfflineMapManager != null) {
                        mOfflineMapManager.backupToSdcard(mContext, false);
                    }
                    dialog.dismiss();
                }
            }
        }).setTitle("").show();
    }

    private void downloadAll(int position) {
        new CommonDialog(getContext(), R.style.dialog, "?????????????????????", new CommonDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    if (mContext != null && position != -1 && provinceList != null && provinceList.size() > 0 && provinceList.get(position) != null) {
                        OfflineMapProvince province = provinceList.get(position);
                        if (province != null && province.getAdCode() != -1) {
                            ArrayList<OfflineMapCity> cityList = cityInfoMap.get(province.getAdCode(), null);
                            mOfflineListAdapter.downloadBasePkg();
                            for (OfflineMapCity city : cityList) {
                                city.setProCode(provinceList.get(position).getAdCode());
                                Intent startIntent = new Intent(mContext, DownloadService.class);
                                startIntent.setAction(DownloadService.ACTION_START);
                                startIntent.putExtra("OfflineMapCity", city);
                                mContext.startService(startIntent);
                                Log.i(TAG,"downloadAll  startService DownloadService " + city.getAdCode());
                            }
                        }
                    }
                    dialog.dismiss();
                }
            }
        }).setTitle("").show();
    }

    private static class MyHandler extends Handler {
        WeakReference<OfflineMapFragment> mWeakReference = null;

        MyHandler(OfflineMapFragment fragment) {
            this.mWeakReference = new WeakReference<OfflineMapFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            OfflineMapFragment fragment = mWeakReference.get();
            if (fragment != null) {
                switch (msg.what) {
                    case UPDATE_LIST:
                        // ?????????????????????UI
                        OfflineMapCity city = (OfflineMapCity) msg.obj;
                        fragment.updateDownloadCityListView(city);
                        fragment.updateAllCityListView(city);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void updateDownloadCityListView(final OfflineMapCity city) {
        if (null != mOfflineDownloadListAdapter) {
            mOfflineDownloadListAdapter.clearListData();
            mOfflineDownloadListAdapter.setListData(mOfflineMapManager.getDownloadCityList(false));
            mOfflineDownloadListAdapter.notifyDataSetChanged();
        }
        mOfflineMapManager.downloadOfflineMapCity(city);
    }

    public void updateAllCityListView(final OfflineMapCity city) {
        if (null != mOfflineListAdapter) {
            mOfflineListAdapter.notifyDataSetChanged();
        }
        mOfflineMapManager.downloadOfflineMapCity(city);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        OfflineDatabaseHelper.createInstance(mContext).closeDownloadDatabase();
    }
}