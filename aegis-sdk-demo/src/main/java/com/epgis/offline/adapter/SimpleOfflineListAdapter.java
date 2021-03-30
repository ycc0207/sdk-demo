package com.epgis.offline.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.epgis.commons.utils.EncryptUtil;
import com.epgis.epgisapp.R;
import com.epgis.offline.DBException;
import com.epgis.offline.DownloadState;
import com.epgis.offline.OfflineMapCity;
import com.epgis.offline.controller.OfflineMapDownloadController;
import com.epgis.offline.SimpleOfflineMapActivity;
import com.epgis.offline.widget.CommonDialog;
import com.epgis.offline.service.SimpleInitWorkThread;
import com.epgis.offline.widget.SpUtil;
import com.epgis.service.api.offlinemap.DownloadQuery;
import com.epgis.service.api.offlinemap.DownloadSearch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.List;

import okhttp3.Response;

import static com.epgis.offline.service.DownloadService.ACTION_START;

/**
 * 离线下载的Adapter
 * <p>
 * Created by Lynn on 2018/8/6.
 */
public class SimpleOfflineListAdapter extends BaseAdapter {
    private static final String TAG = SimpleOfflineListAdapter.class.getSimpleName();
    private Context mAppContext;
    private Activity mActivity;
    private Handler mHandler;
    private List<OfflineMapCity> allCities;

    public SimpleOfflineListAdapter(SimpleOfflineMapActivity activity, Handler handler, List<OfflineMapCity> allCities) {
        this.mActivity = activity;
        this.mAppContext = activity.getApplicationContext();
        this.allCities = allCities;
        mHandler = handler;
    }

    @Override
    public int getCount() {
        return allCities.size();
    }

    @Override
    public Object getItem(int position) {
        return allCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final OfflineMapCity cityItem = allCities.get(position);

        OfflineListHolder holder;
        if (convertView == null) {
            convertView = (RelativeLayout) RelativeLayout.inflate(mAppContext, R.layout.item_offline_list, null);
            holder = new OfflineListHolder();

            holder.tvMapName = (TextView) convertView.findViewById(R.id.tv_map_name);
            holder.tvMapSize = (TextView) convertView.findViewById(R.id.tv_map_size);
            holder.tvProgress = (TextView) convertView.findViewById(R.id.tv_map_progress);
            holder.tvOperator = (TextView) convertView.findViewById(R.id.tv_map_operate);
            holder.tvDel = (TextView) convertView.findViewById(R.id.tv_map_del);
            convertView.setTag(holder);
        } else {
            holder = (OfflineListHolder) convertView.getTag();
        }

        bindChildView(cityItem, holder, position);
        return convertView;
    }

    /**
     * 绑定item进行数据操作
     *
     * @param cityItem      item
     * @param holder        复用holder
     * @param groupPosition 所处位置
     */
    private void bindChildView(final OfflineMapCity cityItem, final OfflineListHolder holder, final int groupPosition) {
        holder.tvOperator.setTag(groupPosition);
        holder.tvOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityItem.getAdCode() == SimpleOfflineMapActivity.FZ_CODE) {
                    cityItem.mStatus = SpUtil.getFzStatus();
                } else {
                    cityItem.mStatus = SpUtil.getXmStatus();
                }
                downloadCity(cityItem);
            }
        });
        holder.tvDel.setTag(groupPosition);
        holder.tvDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityItem.mStatus = DownloadState.state_download_pause;
                cityItem.setPause(true);//任务停止
                setStatus(cityItem);
                //再执行删除
                new CommonDialog(mActivity, R.style.dialog, "确定删除？", new CommonDialog.OnCloseListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            delCity(cityItem);
                            dialog.dismiss();
                        }
                    }
                }).setTitle("").show();
            }
        });
        // 1、刷新地图大小
        holder.tvMapName.setText(cityItem.getCityName());
        double size = ((int) (cityItem.getMapSize() / 1024.0 / 1024.0 * 100)) / 100.0;
        holder.tvMapSize.setText("地图 " + String.valueOf(size) + " M");
        if (cityItem.getMapSize() != 0) {
            // 2、 刷新下载按钮状态
            switch (cityItem.mStatus) {
                case DownloadState.state_undownload:
                    holder.tvOperator.setText("未下载");
                    holder.tvOperator.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_download_doing:
                    holder.tvOperator.setText("下载中");
                    holder.tvOperator.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_download_waiting:
                    holder.tvOperator.setText("等待中");
                    holder.tvOperator.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_download_pause:
                    holder.tvOperator.setText("已暂停");
                    holder.tvOperator.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_download_completed:
                    holder.tvOperator.setText("已下载");
                    holder.tvOperator.setVisibility(View.INVISIBLE);
                    break;
                case DownloadState.state_data_error:
                    holder.tvOperator.setText("重试");
                    holder.tvOperator.setVisibility(View.VISIBLE);
                    break;
                case DownloadState.state_upgrade:
                    holder.tvOperator.setText("有更新");
                    holder.tvOperator.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
            // 3、刷新status 和 进度条
            if (holder.tvMapSize == null || holder.tvProgress == null) {
                return;
            }
            double downloadedProgress = cityItem.getCompletedPercent();
            if (downloadedProgress >= 100) {
                downloadedProgress = 100;
            }
            holder.tvProgress.setText(formatStr(downloadedProgress) + "%");
        }
    }

    /**
     * 格式化进度条显示文本
     *
     * @param progress 进度条
     * @return 文本
     */
    private static String formatStr(double progress) {
        DecimalFormat df = new DecimalFormat("0.0");
        String format = df.format(progress);
        return format;
    }

    private String getFileName(String adCode) {
//        return String.valueOf(adCode);
        //如若需要在地图上识别出离线包，需要调用getEncyptName 方法生成指定加密文件名的数据文件
        return EncryptUtil.getEncryptName(mAppContext, String.valueOf(adCode));
    }

    /**
     * 下载城市
     *
     * @param offlineMapCity 城市
     */
    private void downloadCity(OfflineMapCity offlineMapCity) {
        if (offlineMapCity.mStatus == DownloadState.state_download_completed) {
            offlineMapCity.setPause(true);//任务停止
            setStatus(offlineMapCity);
            // 文件存在，但是未下载完
            try {
                //添加到DB文件
                OfflineMapDownloadController.getInstance(mAppContext).update(offlineMapCity);
            } catch (DBException e) {
                e.printStackTrace();
            }
        } else if (offlineMapCity.mStatus == DownloadState.state_download_pause ||
                offlineMapCity.mStatus == DownloadState.state_undownload) { // 下载、暂停 --> 开始
            offlineMapCity.mStatus = DownloadState.state_download_doing;
            offlineMapCity.setPause(false);//任务开始
            setStatus(offlineMapCity);
            initWork(offlineMapCity);
            try {
                //添加到DB文件
                OfflineMapDownloadController.getInstance(mAppContext).downloadOfflineMapCity(offlineMapCity);
            } catch (DBException e) {
                e.printStackTrace();
            }
        } else if (offlineMapCity.mStatus == DownloadState.state_data_error) { // 重试 -->（删除文件） 开始
            delCity(offlineMapCity);
            offlineMapCity.mStatus = DownloadState.state_download_doing;
            offlineMapCity.setPause(false);//任务开始
            setStatus(offlineMapCity);
            initWork(offlineMapCity);
            try {
                //添加到DB文件
                OfflineMapDownloadController.getInstance(mAppContext).downloadOfflineMapCity(offlineMapCity);
            } catch (DBException e) {
                e.printStackTrace();
            }
        } else { // 开始 -->  暂停
            offlineMapCity.mStatus = DownloadState.state_download_pause;
            offlineMapCity.setPause(true);//任务停止
            setStatus(offlineMapCity);
            if (mHandler != null) {
                mHandler.obtainMessage(SimpleOfflineMapActivity.BACK_SD, offlineMapCity).sendToTarget();
            }
        }
    }

    private void initWork(OfflineMapCity city) {
        SimpleInitWorkThread simpleInitWorkThread = new SimpleInitWorkThread(city, ACTION_START, mAppContext, mHandler);
        simpleInitWorkThread.start();
    }

    /**
     * 开始或继续下载
     *
     * @param city 城市
     */
    public void startOrContinueWork(final OfflineMapCity city) {
        if (city.isPause()) {
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), getFileName(String.valueOf(city.getAdCode())));
        // 错误的任务，需要删除历史文件
        if (city.mStatus == DownloadState.state_data_error && file.exists()) {
            city.mStatus = DownloadState.state_undownload;
            setStatus(city);
            file.delete();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadSearch client = new DownloadSearch(mAppContext);
                DownloadQuery downloadQuery = new DownloadQuery();
                downloadQuery.setAdCode(city.getAdCode().toString());
                downloadQuery.setRange("bytes=" + file.length() + "-" + city.getMapSize());
                try {
                    Response response = client.download(downloadQuery);
                    if (response != null && response.body() != null) {
                        // code 非206 不支持断点续传
                        if (response != null && response.body() != null && (response.code() == 200 || response.code() == 206)) {
                            if ((file.length() + response.body().contentLength()) != city.getMapSize()) {
                                // 文件大小不一致
//                                city.setMapSize(response.body().contentLength());
                                city.mStatus = DownloadState.state_data_error;
                                setStatus(city);
                                city.setPause(true);
                                Log.i(TAG, "error： 文件大小不一致，请重新请求下载");
                            } else {
                                InputStream is = response.body().byteStream();
                                BufferedInputStream bis = new BufferedInputStream(is);
                                byte[] buffer = new byte[1024];
                                int len;

                                RandomAccessFile randomFile = new RandomAccessFile(file, "rwd");
                                if (file.exists()) {
                                    randomFile.seek(file.length());//找到了文件,代表已经下载过,则获取其长度
                                }
                                city.mStatus = DownloadState.state_download_progress;
                                setStatus(city);
                                while ((len = bis.read(buffer)) != -1) {
                                    randomFile.write(buffer, 0, len);
                                    //将加载的进度回调出去
                                    Log.i(TAG, "progress is " + (file.length() + len));
                                    //保存进度
                                    city.setDownloadedSize(file.length() + len);
                                    if (mHandler != null) {
                                        mHandler.obtainMessage(SimpleOfflineMapActivity.PROGRESS_LIST, city).sendToTarget();
                                    }
                                    //判断是否是暂停状态，在下载暂停的时候将下载进度保存到数据库
                                    if (city.isPause() || city.mStatus == DownloadState.state_download_pause) {
                                        Log.i(TAG, "pause");
                                        city.mStatus = DownloadState.state_download_pause;
                                        setStatus(city);
                                        break; //结束循环
                                    }
                                }
                                bis.close();
                                is.close();
                                if (file.length() != city.getMapSize()) {
                                    city.setMapSize(response.body().contentLength());
                                    Log.i(TAG, "bis.close,but length not equal");
                                    city.mStatus = DownloadState.state_download_pause;
                                    setStatus(city);
                                }
                                if (file.length() == city.getMapSize()) {
                                    Log.i(TAG, "finish");
                                    if (city != null) {
                                        city.mStatus = DownloadState.state_download_completed;
                                        setStatus(city);
                                        city.setPause(true);
                                    }
                                }
                            }
                        } else if (response != null && response.body() != null) {
                            // 没有此文件
                            Log.i(TAG, "net error, code is " + response.code() + ", message is " + response.message());
                            if (city != null) {
                                city.setPause(true);
                                city.mStatus = DownloadState.state_data_error;
                                setStatus(city);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i(TAG, "exception error, message is " + e.getMessage());
                    if (city != null) {
                        city.setPause(true);
                        city.mStatus = DownloadState.state_data_error;
                        setStatus(city);
                    }
                }
            }
        }).start();
    }

    /**
     * 设置城市的下载状态
     *
     * @param city 城市
     */
    private void setStatus(OfflineMapCity city) {
        File file = new File(Environment.getExternalStorageDirectory(), getFileName(String.valueOf(city.getAdCode())));
        // 对于文件已存在，且下载完整的，不进行状态变更，除非是标记为数据错误
        if (file.exists() && city.getMapSize() == file.length() && city.mStatus != DownloadState.state_data_error) {
            city.mStatus = DownloadState.state_download_completed;
            if (mHandler != null) {
                mHandler.obtainMessage(SimpleOfflineMapActivity.BACK_SD, city).sendToTarget();
            }
        } else {
            if (city.getAdCode() == SimpleOfflineMapActivity.FZ_CODE) {
                SpUtil.setFzStatus(city.mStatus);
            } else {
                SpUtil.setXmStatus(city.mStatus);
            }
        }
        if (mHandler != null) {
            mHandler.obtainMessage(SimpleOfflineMapActivity.UPDATE_LIST, city).sendToTarget();
        }
    }


    /**
     * 删除城市
     *
     * @param offlineMapCity 城市
     */
    private void delCity(OfflineMapCity offlineMapCity) {
        File file = new File(Environment.getExternalStorageDirectory(), getFileName(String.valueOf(offlineMapCity.getAdCode())));
        if (file.exists()) { // 存在 --> 删除
            // 先清空状态
            if (offlineMapCity.getAdCode() == SimpleOfflineMapActivity.FZ_CODE) {
                SpUtil.setFzStatus(DownloadState.state_undownload);
            } else {
                SpUtil.setXmStatus(DownloadState.state_undownload);
            }
            // 再删除文件
            file.delete();
        }
        if (mHandler != null) {
            mHandler.obtainMessage(SimpleOfflineMapActivity.DEL_CITY, offlineMapCity).sendToTarget();
        }
    }

    private long curTime = 0;

    /**
     * 更新下载城市的进度条信息
     *
     * @param fileBean 下载城市
     */
    public void updateProgress(OfflineMapCity fileBean) {
        for (int i = 0; i < allCities.size(); i++) {
            OfflineMapCity data = allCities.get(i);
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.setMapSize(fileBean.getMapSize());
                data.setDownloadedSize(fileBean.getDownloadedSize());
                data.mStatus = fileBean.mStatus;
                if (fileBean.mStatus != DownloadState.state_download_completed && System.currentTimeMillis() - curTime > 1000) {
                    curTime = System.currentTimeMillis();
                    notifyDataSetChanged();
                }
                return;
            }
        }
    }

    /**
     * 刷新下载状态 UI
     *
     * @param fileBean 城市
     */
    public void updateStatus(OfflineMapCity fileBean) {
        for (int i = 0; i < allCities.size(); i++) {
            OfflineMapCity data = allCities.get(i);
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.mStatus = fileBean.mStatus;
                notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 删除下载城市
     *
     * @param fileBean 城市
     */
    public void updateDelCity(OfflineMapCity fileBean) {
        for (int i = 0; i < allCities.size(); i++) {
            OfflineMapCity data = allCities.get(i);
            if (data.getAdCode().equals(fileBean.getAdCode())) {
                data.setMapSize(fileBean.getMapSize());
                data.setDownloadedSize(0L);
                data.mStatus = DownloadState.state_undownload;
                notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * ListView 复用holder
     */
    private class OfflineListHolder {
        private TextView tvMapName; // 城市名字
        private TextView tvMapSize; // 地图大小
        private TextView tvProgress;// 进度条
        private TextView tvOperator; // 操作按钮：下载、更新、暂停、重试、继续、 已下载
        private TextView tvDel; // 删除
    }
}