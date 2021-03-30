package com.epgis.offline.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.epgis.commons.utils.EncryptUtil;
import com.epgis.offline.DBException;
import com.epgis.offline.DownloadState;
import com.epgis.offline.OfflineMapCity;
import com.epgis.offline.controller.AllCityController;
import com.epgis.offline.controller.DownloadCityController;
import com.epgis.offline.controller.OfflineMapDownloadController;
import com.epgis.offline.db.AllCity;
import com.epgis.offline.db.DownloadCity;
import com.epgis.offline.init.OfflineDatabaseHelper;
import com.epgis.offline.util.FileUtil;
import com.epgis.offline.util.SharePreferenceUtil;
import com.epgis.offline.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Lynn on 2018/4/28.
 * 下载服务
 */
public class DownloadService extends Service implements DownloadCallBack {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_UPGRADE = "ACTION_UPGRADE";
    public static final String ACTION_ERROR = "ACTION_ERROR";
    public static final String ACTION_UPDATE_ALL = "ACTION_UPDATE_ALL";
    public static final String ACTION_PAUSE_ALL = "ACTION_PAUSE_ALL";
    public static final String ACTION_DOWNLOAD_ALL = "ACTION_DOWNLOAD_ALL";
    public static final String ACTION_NET_CHANGE = "ACTION_NET_CHANGE";
    private String TAG = "OfflineMapManager";
    private OfflineMapDownloadController mOfflineMapDownloadController;
    private AllCityController allCityController;

    // 任务列表--被动行为:多次重新进入下载界面后的在下载中的任务获取
    private volatile CopyOnWriteArraySet<String> PASSIVE_LISTS = new CopyOnWriteArraySet<>();
    // 任务列表--用户主动行为:记录下载列表中网络断后用户主动点击暂停的城市，这样当网络恢复的时候，就不再自动下载。
    private volatile CopyOnWriteArraySet<String> PASSIVE_LISTS_MOBILE_NETWORK = new CopyOnWriteArraySet<>();
    // 需要更新的任务
    private volatile CopyOnWriteArraySet<String> UPGRADE_CITY_LISTS = new CopyOnWriteArraySet<>();
    // 正常任务
    private static ConcurrentHashMap<Integer, DownloadThread> mThreadMap = new ConcurrentHashMap<>();
    // 网络恢复的任务
    private static ConcurrentHashMap<Integer, DownloadThread> mThreadMapForNet = new ConcurrentHashMap<>();
    // 需要更新的任务
    private static ConcurrentHashMap<Integer, DownloadThread> mThreadMapForUpgrade = new ConcurrentHashMap<>();
    private SharePreferenceUtil sharePreferenceUtil;

    private boolean isUpdate = false;// 针对断网会调用两次net_receive
    private boolean isDebug = true;
    public static ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        sharePreferenceUtil = SharePreferenceUtil.getInstance(this.getApplicationContext());
        this.mOfflineMapDownloadController = OfflineMapDownloadController.getInstance(this.getApplicationContext());
        this.allCityController = AllCityController.getInstance(this.getApplicationContext());

        //用于处理用户主动和被动行为的处理
        final Set<String> passiveSet = sharePreferenceUtil.get(SharePreferenceUtil.ACTIVE_POSSIVE);
        if (passiveSet != null && passiveSet.size() > 0) {
            Iterator iterator = passiveSet.iterator();
            while (iterator.hasNext()) {
                String tmp = (String) iterator.next();
                if (!PASSIVE_LISTS.contains(tmp)) PASSIVE_LISTS.add(tmp);
                if (mThreadMap != null && !mThreadMap.containsKey(Integer.valueOf(tmp))) {
                    DownloadCity city;
                    try {
                        city = DownloadCityController.getInstance(this.getApplicationContext()).getDownloadCityByCityId(Integer.valueOf(tmp));
                        OfflineMapCity offlineMapCity = new OfflineMapCity(city.getId().intValue(), "");
                        offlineMapCity.mStatus = city.getMapDownloadStatus();
                        offlineMapCity.setDownloadedSize(city.getMapDownloadedSize());
                        offlineMapCity.setMapSize(city.getMapTotalSize());
                        offlineMapCity.setMapVersion(city.getMapVersionNum());
                        DownloadThread downloadThread = new DownloadThread(this.getApplicationContext(), offlineMapCity, this);
                        mThreadMap.putIfAbsent(Integer.valueOf(tmp), downloadThread);
                    } catch (Exception e) {
                    }

                }
            }
        }
        // 处理需要更新的任务
        final Set<String> upgradeSet = sharePreferenceUtil.get(SharePreferenceUtil.UPGRADE_LISTS);
        if (upgradeSet != null && upgradeSet.size() > 0) {
            Iterator iterator = upgradeSet.iterator();
            while (iterator.hasNext()) {
                String tmp = (String) iterator.next();
                if (!UPGRADE_CITY_LISTS.contains(tmp)) UPGRADE_CITY_LISTS.add(tmp);
                if (mThreadMapForUpgrade != null && !mThreadMapForUpgrade.containsKey(Integer.valueOf(tmp))) {
                    AllCity city;
                    try {
                        city = AllCityController.getInstance(this.getApplicationContext()).getCityByAdCode(Integer.valueOf(tmp));
                        OfflineMapCity offlineMapCity = new OfflineMapCity(city.getAdCode(), city.getCityName());
                        offlineMapCity.mStatus = DownloadState.state_upgrade;
                        offlineMapCity.setDownloadedSize(0l);
                        offlineMapCity.setMapSize(city.getMapPkgSize());
                        offlineMapCity.setMapVersion(city.getMapVersion());
                        DownloadThread downloadThread = new DownloadThread(this.getApplicationContext(), offlineMapCity, this);
                        mThreadMapForUpgrade.putIfAbsent(Integer.valueOf(tmp), downloadThread);
                    } catch (Exception e) {
                    }

                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if(isDebug) Log.i(TAG,"onStartCommand");
            // 三大按钮响应事件和网络变化
            if (intent.getAction() != null && intent.getAction().equals(ACTION_UPDATE_ALL)) {
                updateAllWork();
            } else if (intent.getAction() != null && intent.getAction().equals(ACTION_PAUSE_ALL)) {
                pauseAllWork();
            } else if (intent.getAction() != null && intent.getAction().equals(ACTION_DOWNLOAD_ALL)) {
                startAllWork();
            } else if (intent.getAction() != null && intent.getAction().equals(ACTION_NET_CHANGE)) {
                // 0:无连接,1:wifi,2:2G,3:3G,4:其他
                // 默认wifi下自动继续已下载任务
                int net_state = intent.getIntExtra("NetState", 1);
                switch (net_state) {
                    case 0://network connection error
                        setUnfinishWorkToSp();
                        pauseAllWork();
                        break;
                    case 1://wifi network
                        setSpToContinueWork();
                        break;
                    case 2://cell network
                    case 3://cell network
                        setUnfinishWorkToSp();
                        pauseAllWork();
                        break;
                    default:
                        break;
                }
            } else {
                //其他为正常服务事件
                OfflineMapCity city = (OfflineMapCity) intent.getParcelableExtra("OfflineMapCity");
                if (city == null) {
                    throw new IllegalArgumentException("city must be not null !!!");
                }
                if (intent.getAction() != null && intent.getAction().equals(ACTION_START)) {
                    // 通知主页刷新下载记录
                    if(isDebug) Log.i(TAG,"通知主页刷新下载记录");
                    waitCallBack(city);
                    executorService.execute(new InitThread(city, ACTION_START, getApplicationContext()));//Log.i("offline----","DownloadService ACTION_START");
                } else if (intent.getAction() != null && intent.getAction().equals(ACTION_ERROR)) { // 失败需要清除已下载文件
                    executorService.execute(new InitThread(city, ACTION_ERROR, getApplicationContext()));
                } else if (intent.getAction() != null && intent.getAction().equals(ACTION_PAUSE)) {
                    if (mThreadMap != null && mThreadMap.size() > 0 && mThreadMap.containsKey(city.getAdCode())) {
                        DownloadThread downloadThread = mThreadMap.get(city.getAdCode());
                        pauseWork(downloadThread);
                    }else if (mThreadMapForNet != null && mThreadMapForNet.size() > 0 && mThreadMapForNet.containsKey(city.getAdCode())) {
                        mThreadMapForNet.remove(city.getAdCode());
                    }else {
                        pauseCallBack(city);
                    }

                } else if (intent.getAction() != null && intent.getAction().equals(ACTION_UPGRADE)) {
                    if (mThreadMapForUpgrade != null && mThreadMapForUpgrade.size() > 0 && mThreadMapForUpgrade.containsKey(city.getAdCode())) {
                        DownloadThread downloadThread = mThreadMapForUpgrade.get(city.getAdCode());
                        upgradeWork(downloadThread);
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage eventMessage) {
        if (eventMessage.getObject() instanceof OfflineMapCity) {
            OfflineMapCity city = (OfflineMapCity) eventMessage.getObject();
            if (city != null) {
                switch (eventMessage.getType()) {
                    case EventMessage.HANDLER_GET_FILE_REAL_LENGTH://下载线程初始化完毕
                        if (mThreadMapForNet != null && mThreadMapForNet.size() > 0 && mThreadMapForNet.containsKey(city.getAdCode())) {
                            mThreadMapForNet.put(city.getAdCode(), new DownloadThread(this.getApplicationContext(), city, this));
                        }
                        if (mThreadMap != null && mThreadMap.size() > 0 && mThreadMap.containsKey(city.getAdCode())) {
                            DownloadThread downloadThread = mThreadMap.get(city.getAdCode());
                            if (downloadThread.getCity() != null)
                                downloadThread.getCity().setMapSize(city.getMapSize());
                            startWork(downloadThread);
                        } else {
                            DownloadThread downloadThread = new DownloadThread(this.getApplicationContext(), city, this);
                            mThreadMap.put(city.getAdCode(), downloadThread);
                            startWork(downloadThread);
                        }
                        break;
                    case EventMessage.HANDLER_GET_FILE_REAL_LENGTH_ERROR:
                        File file = new File(FileUtil.getAppSDCardMapPath(),
                                EncryptUtil.getEncryptName(getApplicationContext(), String.valueOf(city.getAdCode())));
                        if (file.exists()) {
                            file.delete();
                        }
                        city.setDownloadedSize(0l);
                        DownloadThread downloadThread;
                        // 需要更新最新的地图包大小值
                        if (mThreadMap != null && mThreadMap.size() > 0 && mThreadMap.containsKey(city.getAdCode())) {
                            downloadThread = mThreadMap.get(city.getAdCode());
                            if (downloadThread.getCity() != null)
                                downloadThread.getCity().setMapSize(city.getMapSize());
                        } else {
                            downloadThread = new DownloadThread(this.getApplicationContext(), city, this);
                            mThreadMap.put(city.getAdCode(), downloadThread);
                        }
                        DownLoadExecutor.execute(downloadThread);
                        break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void undownloadCallBack(OfflineMapCity city) {
    }

    @Override
    public void startCallBack(OfflineMapCity city) {
        if (isDebug) Log.i(TAG, city.getCityName() + "startCallBack");
        isUpdate = false;
        city.mStatus = DownloadState.state_download_doing;
        city.setPause(false);
        if (!checkRealLength(city)) { //db内长度与实际下载的地图包长度不一致，刷新allcity内的数据。
            AllCity allCity = allCityController.getCityByAdCode(city.getAdCode());
            if (allCity != null) {
                allCity.setMapPkgSize(city.getMapSize());
                try {
                    allCityController.update(allCity);
                } catch (DBException e) {
                    e.printStackTrace();
                }
            }
        }
        updateCityInDb(city);
        EventMessage message = new EventMessage(DownloadState.state_download_doing, city);
        EventBus.getDefault().post(message);
    }

    private boolean checkRealLength(OfflineMapCity city) {
        if (city != null) {
            AllCity bean = allCityController.getCityByAdCode(city.getAdCode());
            if (bean != null) {
                long length = bean.getMapPkgSize();
                if (city.getMapSize() != null && city.getMapSize() != length) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void waitCallBack(OfflineMapCity city) {
        if (isDebug) Log.i(TAG, city.getCityName() + "waitCallBack");
        isUpdate = false;
        DownloadCity downloadCity = null;
        File file = new File(FileUtil.getAppSDCardMapPath(),
                EncryptUtil.getEncryptName(getApplicationContext(), String.valueOf(city.getAdCode())));
        try {
            if (city != null && city.getAdCode() != null)
                downloadCity = DownloadCityController.getInstance(getApplicationContext()).getDownloadCityByCityId(city.getAdCode());
        } catch (DBException e) {
            e.printStackTrace();
        }
        if (file.exists() && file.length() >= city.getDownloadedSize() && downloadCity == null && file.length() > 100) { // 本地有离线地图包的缓存文件
            city.mStatus = DownloadState.state_download_completed;
            city.setPause(true);
            updateCityInDb(city);
            EventMessage message = new EventMessage(DownloadState.state_download_completed, city);
            EventBus.getDefault().post(message);
        } else {
            // 从未下载 或者 不在下载列表中
            city.mStatus = DownloadState.state_download_waiting;
            city.setPause(false);
            updateCityInDb(city);
            EventMessage message = new EventMessage(DownloadState.state_download_waiting, city);//通知列表更新
            EventBus.getDefault().post(message);
        }
    }

    @Override
    public void pauseCallBack(OfflineMapCity city) {
        if (isDebug) Log.i(TAG, city.getCityName() + "pauseCallBack");
        isUpdate = false;
        city.mStatus = DownloadState.state_download_pause;
        city.setPause(true);
        //保存下载进度到数据库
        updateCityInDb(city);
        EventMessage message = new EventMessage(DownloadState.state_download_pause, city);
        EventBus.getDefault().post(message);
    }

    @Override
    public void errorCallBack(OfflineMapCity city) {
        if (isDebug) Log.i(TAG, city.getCityName() + "errorCallBack");
        isUpdate = false;
        city.mStatus = DownloadState.state_data_error;
        city.setPause(false);
        city.setDownloadedSize(0l);
        updateCityInDb(city);
        // 重试的时候 请求最新数据库版本信息
        OfflineDatabaseHelper.createInstance(getApplicationContext()).initOrUpgradeDatabase(null);
        EventMessage message = new EventMessage(DownloadState.state_data_error, city);
        EventBus.getDefault().post(message);
    }

    private long curTime = 0;

    @Override
    public void progressCallBack(OfflineMapCity city, long length) {
//        if (isDebug) Log.i(TAG, city.getCityName() + "progressCallBack");
        isUpdate = false;
        //每500毫秒发送刷新进度事件
        if (System.currentTimeMillis() - curTime > 1000 || length == city.getMapSize() || city.mStatus == DownloadState.state_download_completed) {
            city.setDownloadedSize(length);
            city.mStatus = DownloadState.state_download_doing;
//            updateCityInDb(city); // 避免频繁表操作
            EventMessage message = new EventMessage(DownloadState.state_download_progress, city);
            EventBus.getDefault().post(message);
            curTime = System.currentTimeMillis();
        }
    }

    @Override
    public void finishedCallBack(OfflineMapCity city) {
        if (isDebug) Log.i(TAG, city.getCityName() + "finishedCallBack");
        isUpdate = false;
        if (city != null) {
            city.mStatus = DownloadState.state_download_completed;
            city.setPause(false);
            // 本地加密
            city.setBaseUrl(EncryptUtil.getFileMd5Name(getApplicationContext(), String.valueOf(city.getAdCode())));
            if (city.getDownloadedSize() > city.getMapSize()) {
                city.setDownloadedSize(city.getMapSize()); //重置溢出大小，不然总有DownloadSize > TotalSize 的情况出现
            }
            updateCityInDb(city);

            if (mThreadMap.containsKey(city.getAdCode())) {
                mThreadMap.remove(city.getAdCode());
            }
            if (mThreadMapForNet.containsKey(city.getAdCode())) {
                mThreadMapForNet.remove(city.getAdCode());
            }
            if (mThreadMapForUpgrade.containsKey(city.getAdCode())) {
                mThreadMapForUpgrade.remove(city.getAdCode());
            }
            if (PASSIVE_LISTS.contains(String.valueOf(city.getAdCode()))) {
                PASSIVE_LISTS.remove(String.valueOf(city.getAdCode()));
                sharePreferenceUtil.update(SharePreferenceUtil.ACTIVE_POSSIVE, PASSIVE_LISTS);
            }
            if (UPGRADE_CITY_LISTS.contains(String.valueOf(city.getAdCode()))) {
                UPGRADE_CITY_LISTS.remove(String.valueOf(city.getAdCode()));
                sharePreferenceUtil.update(SharePreferenceUtil.UPGRADE_LISTS, UPGRADE_CITY_LISTS);
            }
            if (PASSIVE_LISTS_MOBILE_NETWORK.contains(String.valueOf(city.getAdCode()))) {
                PASSIVE_LISTS_MOBILE_NETWORK.remove(String.valueOf(city.getAdCode()));
                sharePreferenceUtil.update(SharePreferenceUtil.PASSIVE_LISTS_MOBILE_NETWORK, PASSIVE_LISTS_MOBILE_NETWORK);
            }
            EventMessage message = new EventMessage(DownloadState.state_download_completed, city);
            EventBus.getDefault().post(message);
        }
    }

    @Override
    public void neterror(OfflineMapCity city, int code, String message) {
        if (isDebug) Log.i(TAG, city.getCityName() + "neterror");
        if (getApplicationContext() != null) {
            ToastUtil.show(getApplicationContext(), "网络错误，请稍候重试。 错误代码：" + code + "，" + message);
            pauseCallBack(city);
        }
    }

    @Override
    public void exceptionCallBack(OfflineMapCity city, String msg) {
        if (isDebug) Log.i(TAG, city.getCityName() + "exceptionCallBack");
        if (getApplicationContext() != null) {
//            ToastUtil.show(getApplicationContext(), "网络错误，请稍候重试。 错误代码：" + msg);
            pauseCallBack(city);
        }
    }

    private void updateCityInDb(final OfflineMapCity city) {
        try {
            if (isDebug)
                Log.i(TAG, "DownloadService updateCityInDb,name:" + city.getCityName() + ",adCode:" + city.getAdCode() +
                        ",downloadSize:" + city.getDownloadedSize() + ",status:" + city.mStatus + ",completedPercent:" + city.getCompletedPercent() +
                        ",mapVersion:" + city.getMapVersion() + ",mapSize:" + city.getMapSize());
            mOfflineMapDownloadController.downloadOfflineMapCity(city);
        } catch (DBException e) {
//            performDBException(e, loader);
        }
    }

    // 开始所有下载
    private void startAllWork() {
        ArrayList<OfflineMapCity> offlineMapCityArrayList = OfflineMapDownloadController.getInstance(this.getApplicationContext()).getDownloadCityList(false);
        for (OfflineMapCity offlineMapCity : offlineMapCityArrayList) {
            if (mThreadMap != null && !mThreadMap.containsKey(offlineMapCity.getAdCode())) {
                mThreadMap.put(offlineMapCity.getAdCode(), new DownloadThread(this.getApplicationContext(), offlineMapCity, this));
            }
        }
        if (mThreadMap != null && mThreadMap.size() > 0) {
            Iterator<Map.Entry<Integer, DownloadThread>> iterator = mThreadMap.entrySet().iterator();
            while (iterator.hasNext()) {
                startWork(iterator.next().getValue());
            }

        }
    }

    // 开始任务下载
    public void startWork(DownloadThread downloadThread) {
        isUpdate = false;
        if (downloadThread != null && downloadThread.getCity() != null && downloadThread.getCity().getAdCode() != null) {
            // 防止文件已下载但是并没有设置为完结状态
            File file = new File(FileUtil.getAppSDCardMapPath(),
                    EncryptUtil.getEncryptName(getApplicationContext(), String.valueOf(downloadThread.getCity().getAdCode())));
            if (file.exists() && file.length() >= downloadThread.getCity().getMapSize()) {
                finishedCallBack(downloadThread.getCity());
            } else {
                downloadThread.setPause(false);
                if (downloadThread.getCity() != null) {
                    // 更新downloadThread状态,put会覆盖最新
                    downloadThread.getCity().setPause(false);
                    downloadThread.getCity().mStatus = DownloadState.state_download_waiting;
                    mThreadMap.put(downloadThread.getCity().getAdCode(), downloadThread);
                    if(isDebug) Log.i(TAG,"开始任务下载");
                    waitCallBack(downloadThread.getCity());
                }
                DownLoadExecutor.execute(downloadThread);
            }
        }
    }

    // 暂停所有下载
    private void pauseAllWork() {
        if (mThreadMap != null && mThreadMap.size() > 0) {
            Iterator<Map.Entry<Integer, DownloadThread>> iterator = mThreadMap.entrySet().iterator();
            while (iterator.hasNext()) {
                pauseWork(iterator.next().getValue());
            }
        }
    }

    // 暂停任务下载
    public void pauseWork(DownloadThread downloadThread) {
        isUpdate = false;
        if (downloadThread != null) {
            downloadThread.setPause(true);
            if (downloadThread.getCity() != null) {
                // 更新downloadThread状态
                downloadThread.getCity().setPause(true);
                downloadThread.getCity().mStatus = DownloadState.state_download_pause;
                mThreadMap.put(downloadThread.getCity().getAdCode(), downloadThread);
                pauseCallBack(downloadThread.getCity());
            }
        }
    }

    // 更新任务下载
    private void updateAllWork() {
        isUpdate = false;
        final Set<String> passiveSet = sharePreferenceUtil.get(SharePreferenceUtil.UPGRADE_LISTS);
        if (passiveSet != null && passiveSet.size() > 0) {
            Iterator iterator = passiveSet.iterator();
            while (iterator.hasNext()) {
                String tmp = (String) iterator.next();
                if (!UPGRADE_CITY_LISTS.contains(tmp)) UPGRADE_CITY_LISTS.add(tmp);
                if (mThreadMapForUpgrade != null && !mThreadMapForUpgrade.containsKey(Integer.valueOf(tmp))) {
                    AllCity city;
                    try {
                        city = AllCityController.getInstance(this.getApplicationContext()).getCityByAdCode(Integer.valueOf(tmp));
                        OfflineMapCity offlineMapCity = new OfflineMapCity(city.getAdCode(), city.getCityName());
                        offlineMapCity.mStatus = DownloadState.state_download_waiting;
                        offlineMapCity.setDownloadedSize(0l);
                        offlineMapCity.setMapSize(city.getMapPkgSize());
                        offlineMapCity.setMapVersion(city.getMapVersion());
                        DownloadThread downloadThread = new DownloadThread(this.getApplicationContext(), offlineMapCity, this);
                        mThreadMapForUpgrade.put(Integer.valueOf(tmp), downloadThread);
                    } catch (Exception e) {
                    }
                }
            }
            if (mThreadMapForUpgrade != null && mThreadMapForUpgrade.size() > 0) {
                Iterator<Map.Entry<Integer, DownloadThread>> iteratorForNet = mThreadMapForUpgrade.entrySet().iterator();
                while (iteratorForNet.hasNext()) {
                    upgradeWork(iteratorForNet.next().getValue());
                }
            }
        }
    }

    private void upgradeWork(DownloadThread value) {
        if (value != null) {
            OfflineMapCity city = value.getCity();
            File file = new File(FileUtil.getAppSDCardMapPath(),
                    EncryptUtil.getEncryptName(getApplicationContext(), String.valueOf(city.getAdCode())));
            if (file.exists()) {
                file.delete();
            }
            DownLoadExecutor.execute(value);
        }
    }

    // 网络中断或者切换手机网络的时候，暂存下载任务
    private void setUnfinishWorkToSp() {
        if (!isUpdate && mThreadMap != null && mThreadMap.size() > 0) {
            isUpdate = true;
            Iterator<Map.Entry<Integer, DownloadThread>> iterator = mThreadMap.entrySet().iterator();
            while (iterator.hasNext()) {
                DownloadThread item = iterator.next().getValue();
                if (item != null && item.getCity() != null
                        && item.getCity().mStatus != DownloadState.state_download_pause
                        && item.getCity().mStatus != DownloadState.state_download_completed
                        && !PASSIVE_LISTS_MOBILE_NETWORK.contains(item.getCity().getAdCode().toString())) {
                    PASSIVE_LISTS_MOBILE_NETWORK.add(item.getCity().getAdCode().toString());
                }
            }
            sharePreferenceUtil.update(SharePreferenceUtil.PASSIVE_LISTS_MOBILE_NETWORK, PASSIVE_LISTS_MOBILE_NETWORK);
        }
    }

    // 网络恢复的时候，恢复下载任务
    private void setSpToContinueWork() {
        isUpdate = false;
        final Set<String> passiveSet = sharePreferenceUtil.get(SharePreferenceUtil.PASSIVE_LISTS_MOBILE_NETWORK);
        if (passiveSet != null && passiveSet.size() > 0) {
            Iterator iterator = passiveSet.iterator();
            while (iterator.hasNext()) {
                String tmp = (String) iterator.next();
                if (mThreadMapForNet != null && !mThreadMapForNet.containsKey(Integer.valueOf(tmp))) {
                    DownloadCity city;
                    try {
                        city = DownloadCityController.getInstance(this.getApplicationContext()).getDownloadCityByCityId(Integer.valueOf(tmp));
                        OfflineMapCity offlineMapCity = new OfflineMapCity(city.getId().intValue(), "");
                        offlineMapCity.mStatus = city.getMapDownloadStatus();
                        offlineMapCity.setDownloadedSize(city.getMapDownloadedSize());
                        offlineMapCity.setMapSize(city.getMapTotalSize());
                        offlineMapCity.setMapVersion(city.getMapVersionNum());
                        DownloadThread downloadThread = new DownloadThread(this.getApplicationContext(), offlineMapCity, this);
                        mThreadMapForNet.put(Integer.valueOf(tmp), downloadThread);
                    } catch (Exception e) {
                    }
                }
            }
            if (mThreadMapForNet != null && mThreadMapForNet.size() > 0) {
                Iterator<Map.Entry<Integer, DownloadThread>> iteratorForNet = mThreadMapForNet.entrySet().iterator();
                while (iteratorForNet.hasNext()) {
                    startWork(iteratorForNet.next().getValue());
                }
            }
        }
    }
}
