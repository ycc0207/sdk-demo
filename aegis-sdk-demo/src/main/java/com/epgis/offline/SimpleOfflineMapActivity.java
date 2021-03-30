package com.epgis.offline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.epgis.commons.utils.EncryptUtil;
import com.epgis.epgisapp.R;
import com.epgis.offline.adapter.SimpleOfflineListAdapter;
import com.epgis.offline.controller.OfflineMapDownloadController;
import com.epgis.offline.util.FileUtil;
import com.epgis.offline.util.ToastUtil;
import com.epgis.offline.widget.SpUtil;
import com.epgis.service.api.offlinemap.MetaSearch;
import com.epgis.service.api.offlinemap.model.MetaCity;
import com.epgis.service.api.offlinemap.model.MetaDb;
import com.epgis.service.api.offlinemap.model.MetaResult;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.epgis.offline.service.EventMessage.HANDLER_GET_FILE_REAL_LENGTH;
import static com.epgis.offline.service.EventMessage.HANDLER_GET_FILE_REAL_LENGTH_ERROR;

/**
 * 离线下载demo
 * Created by Lynn on 2018/8/6.
 */
public class SimpleOfflineMapActivity extends Activity {
    private static final String TAG = SimpleOfflineMapActivity.class.getSimpleName();
    private SimpleOfflineListAdapter mSimpleOfflineListAdapter;
    private OfflineMapManager mOfflineMapManager;
    private ListView mListView;
    private List<OfflineMapCity> allCities;
    private Handler mHandler;
    private final static int INIT_LIST = 0;//初始化列表
    public final static int UPDATE_LIST = 1;//更新列表
    public final static int PROGRESS_LIST = 2;//列表进度条
    public final static int DEL_CITY = 3;//删除城市
    public final static int BACK_SD = 4;//备份到SD卡
    private Context mContext;
    public final static int FZ_CODE = 350100;
    public final static int XM_CODE = 350200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpUtil.init(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_simple_offline);
        //初始化控件
        initView();
        initData();
    }

    /**
     * 初始化View
     */
    private void initView() {
        mListView = (ListView) findViewById(R.id.offline_list);
        findViewById(R.id.iv_goback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 初始化数据
     */
    private void initData() {
        mContext = this;
        mHandler = new MyHandler(this);
        allCities = new ArrayList<>();
        //初始化管理器
        mOfflineMapManager = new OfflineMapManager(mContext, null, true);

        mSimpleOfflineListAdapter = new SimpleOfflineListAdapter(this, mHandler, allCities);
        mListView.setAdapter(mSimpleOfflineListAdapter);
        // 初始化下载列表
        MetaSearch client = new MetaSearch(mContext);
        client.setOnMetaSearchListener(new MetaSearch.OnMetaSearchListener() {
            @Override
            public void onMetaSearch(MetaResult metaResult) {
                if (metaResult != null) {
                    analysisDbJson(metaResult);
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.i(TAG, "");
            }
        });
        client.searchMetaAsync();
    }

    /**
     * 获取并解析响应数据
     *
     * @param metaResult
     */
    private void analysisDbJson(final MetaResult metaResult) {
        if (metaResult.getDb() != null && metaResult.getDb().size() > 0) {
            allCities.clear();
            for (int i = 0; i < metaResult.getDb().size(); i++) {
                MetaDb metaDb = metaResult.getDb().get(i);
                if (metaDb != null && metaDb.getAdcode().equals("350000")) {// 筛选福建下的福州和厦门
                    List<MetaCity> metaCities = metaDb.getCitys();
                    if (metaCities != null && metaCities.size() > 0) {
                        for (int j = 0; j < metaCities.size(); j++) {
                            MetaCity metaCity = metaCities.get(j);
                            if (metaCity != null) {
                                int adCode = Integer.valueOf(metaCity.getAdcode());
                                if (adCode == FZ_CODE || adCode == XM_CODE) {
                                    long size = Long.valueOf(metaCity.getLength());
                                    int version = Integer.valueOf(metaCity.getVersion());
                                    String name = "";
                                    int status = 0;
                                    switch (adCode) {
                                        case FZ_CODE:
                                            name = "福州市";
                                            status = SpUtil.getFzStatus();
                                            break;
                                        case XM_CODE:
                                            name = "厦门市";
                                            status = SpUtil.getFzStatus();
                                            break;
                                    }
                                    OfflineMapCity allCity = new OfflineMapCity(adCode, name);
                                    allCity.setMapSize(size);
                                    allCity.mStatus = status;
                                    allCity.setAdCode(adCode);
                                    allCity.setMapVersion(version);
                                    File file = new File(Environment.getExternalStorageDirectory(), EncryptUtil.getEncryptName(mContext, String.valueOf(adCode)));
                                    if (file.exists()) {
                                        if (file.length() == allCity.getMapSize()) { // 文件已下载完成的，变更下载状态和进度条
                                            allCity.mStatus = DownloadState.state_download_completed;
                                            allCity.setDownloadedSize(allCity.getMapSize());
                                        } else { //文件还未下载完成
                                            allCity.mStatus = DownloadState.state_download_pause;
                                            allCity.setDownloadedSize(file.length());
                                        }
                                    }
                                    try {
                                        OfflineMapDownloadController.getInstance(SimpleOfflineMapActivity.this).downloadOfflineMapCity(allCity);
                                    } catch (DBException e) {
                                        e.printStackTrace();
                                    }
                                    allCities.add(allCity);
                                }
                            }
                        }
                    }
                }
            }
            if (allCities.size() > 0 && mHandler != null) {
                mHandler.obtainMessage(INIT_LIST, "初始化完成").sendToTarget();
            }
        }
    }


    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        WeakReference<SimpleOfflineMapActivity> mWeakReference = null;

        MyHandler(SimpleOfflineMapActivity activity) {
            this.mWeakReference = new WeakReference<SimpleOfflineMapActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SimpleOfflineMapActivity activity = mWeakReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case INIT_LIST:
                        // 初始化UI
                        if (mSimpleOfflineListAdapter != null) {
                            mSimpleOfflineListAdapter.notifyDataSetChanged();
                        }
                        ToastUtil.show(mContext, msg.obj.toString());
                        backupToSdcard();
                        break;
                    case UPDATE_LIST:
                        // 刷新界面状态
                        OfflineMapCity city = (OfflineMapCity) msg.obj;
                        if (mSimpleOfflineListAdapter != null) {
                            mSimpleOfflineListAdapter.updateStatus(city);
                        }
                        if (city.mStatus == DownloadState.state_download_completed) {
                            OfflineMapDownloadController mOfflineMapDownloadController = OfflineMapDownloadController.getInstance(mContext);
                            try {
                                mOfflineMapDownloadController.updateOfflineMapCity(city);
                            } catch (DBException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case PROGRESS_LIST:
                        // 刷新界面进度条
                        OfflineMapCity offlineMapCity = (OfflineMapCity) msg.obj;
                        if (mSimpleOfflineListAdapter != null && offlineMapCity != null) {
                            Log.i(TAG, "offlineActivity progress is " + offlineMapCity.getDownloadedSize());
                            mSimpleOfflineListAdapter.updateProgress(offlineMapCity);
                        }
                        break;
                    case DEL_CITY:
                        // 删除城市
                        OfflineMapCity delCity = (OfflineMapCity) msg.obj;
                        if (mSimpleOfflineListAdapter != null) {
                            mSimpleOfflineListAdapter.updateDelCity(delCity);
                        }
                        backupToSdcard();
                        break;
                    case BACK_SD: // 暂停和完成走adapter的回调
                        backupToSdcard();
                        break;
                    case HANDLER_GET_FILE_REAL_LENGTH:
                        // 发起下载
                        OfflineMapCity offlineMapCity1 = (OfflineMapCity) msg.obj;
                        if (mSimpleOfflineListAdapter != null && offlineMapCity1 != null) {
                            mSimpleOfflineListAdapter.startOrContinueWork(offlineMapCity1);
                        }
                        break;
                    case HANDLER_GET_FILE_REAL_LENGTH_ERROR: // 重试
                        OfflineMapCity offlineMapCity2 = (OfflineMapCity) msg.obj;
                        if (mSimpleOfflineListAdapter != null && offlineMapCity2 != null) {
                            File file = new File(FileUtil.getAppSDCardMapPath(), EncryptUtil.getEncryptName(getApplicationContext(), String.valueOf(offlineMapCity2.getAdCode())));
                            if (file.exists()) {
                                file.delete();
                            }
                            offlineMapCity2.setDownloadedSize(0l);
                            mSimpleOfflineListAdapter.startOrContinueWork(offlineMapCity2);
                        }
                    default:
                        break;
                }
            }
        }
    }

    private void backupToSdcard() {
        // 每次暂停都备份到sd卡
        if (mOfflineMapManager != null) {
            mOfflineMapManager.backupToSdcard(mContext, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
