package com.epgis.offline.service;

import android.content.Context;
import android.util.Log;

import com.epgis.commons.utils.EncryptUtil;
import com.epgis.offline.DownloadState;
import com.epgis.offline.OfflineMapCity;
import com.epgis.offline.util.FileUtil;
import com.epgis.service.api.offlinemap.DownloadQuery;
import com.epgis.service.api.offlinemap.DownloadSearch;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * Created by Lynn on 2018/4/28.
 * 下载线程
 */
public class DownloadThread extends Thread {
    private String TAG = "OfflineMapManager";
    private OfflineMapCity city;
    private DownloadCallBack callback;
    private Boolean isPause = false;
    private boolean isDebug = true;
    private Context context;

    public DownloadThread(Context context, OfflineMapCity bean, DownloadCallBack callback) {
        this.city = bean;
        this.callback = callback;
        this.context = context;
    }

    public OfflineMapCity getCity() {
        return city;
    }

    public void setPause(Boolean pause) {
        isPause = pause;
        city.setPause(true);
        city.mStatus = DownloadState.state_download_pause;
    }

    @Override
    public void run() {
        if (isPause || city.isPause() || city.mStatus == DownloadState.state_download_pause) {
            return;
        }
        callback.startCallBack(city);
        final File file = new File(FileUtil.getAppSDCardMapPath(), EncryptUtil.getEncryptName(context, String.valueOf(city.getAdCode())));
        long fileLength = file.length();
        DownloadSearch client = new DownloadSearch(context);
        DownloadQuery downloadQuery = new DownloadQuery();
        downloadQuery.setAdCode(city.getAdCode().toString());
        downloadQuery.setRange("bytes=" + fileLength + "-" + city.getMapSize());

        if(isDebug) Log.i(TAG, "DownloadThread - DownloadSearch: bytes= fileLength:" + fileLength + ",mapSize:" + city.getMapSize());
        try {
            Response response = client.download(downloadQuery);
            try {
                if (response != null && response.body() != null) {
                    // code 非206 不支持断点续传
                    if (response != null && response.body() != null && (response.code() == 200 || response.code() == 206)) {
                        if ((fileLength + response.body().contentLength()) != city.getMapSize()) {
                            city.setMapSize(response.body().contentLength());
                            if(isDebug) Log.i(TAG, "errorCallBack DownloadThread:fileLength:" + fileLength+",responseLength:"+ response.body().contentLength() + ",mapSize:" + city.getMapSize());
                            callback.errorCallBack(city);
                        } else {
                            InputStream is = response.body().byteStream();
                            BufferedInputStream bis = new BufferedInputStream(is);
                            byte[] buffer = new byte[1024];
                            int len;
                            boolean isFinish = true;

                            RandomAccessFile randomFile = new RandomAccessFile(file, "rwd");
                            if (file.exists()) {
                                randomFile.seek(fileLength);//找到了文件,代表已经下载过,则获取其长度
                            }
                            while ((len = bis.read(buffer)) != -1) {
                                randomFile.write(buffer, 0, len);
                                //将加载的进度回调出去
                                callback.progressCallBack(city, file.length() + len);
                                //保存进度
                                city.setDownloadedSize(file.length() + len);
                                //判断是否是暂停状态，在下载暂停的时候将下载进度保存到数据库
                                if (isPause || city.isPause() || city.mStatus == DownloadState.state_download_pause) {
                                    isFinish = false;
                                    callback.pauseCallBack(city);
                                    break; //结束循环
                                }
                            }
                            bis.close();
                            is.close();
                            if (isFinish && file.length() != city.getMapSize()) {
                                city.setMapSize(response.body().contentLength());
                                if(isDebug) Log.i(TAG, "errorCallBack DownloadThread finish bug:fileLength:" + file.length() + ",mapSize:" + city.getMapSize());
                                callback.errorCallBack(city);
                            }
                            if (file.length() == city.getMapSize()) {
                                callback.finishedCallBack(city);
                            }
                        }
                    } else if (response != null && response.body() != null) {
                        // 没有此文件
                        callback.neterror(city, response.code(), response.message());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                callback.exceptionCallBack(city, e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}