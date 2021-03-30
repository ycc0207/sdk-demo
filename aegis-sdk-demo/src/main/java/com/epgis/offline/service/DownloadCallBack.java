package com.epgis.offline.service;

import com.epgis.offline.OfflineMapCity;

/**
 * Created by Lynn on 2018/04/28.
 * 下载进度回调
 */
public interface DownloadCallBack {
    /**
     * 初始化回调
     *
     * @param city
     */
    void undownloadCallBack(OfflineMapCity city);

    /**
     * 开始回调
     *
     * @param city
     */
    void startCallBack(OfflineMapCity city);

    /**
     * 等待回调
     *
     * @param city
     */
    void waitCallBack(OfflineMapCity city);

    /**
     * 暂停回调
     *
     * @param city
     */
    void pauseCallBack(OfflineMapCity city);

    /**
     * 异常回调
     *
     * @param city
     */
    void errorCallBack(OfflineMapCity city);

    /**
     * 下载进度
     *
     * @param city, length
     */
    void progressCallBack(OfflineMapCity city, long length);

    /**
     * 线程下载完毕
     *
     * @param city
     */
    void finishedCallBack(OfflineMapCity city);

    /***
     * 网络错误
     * @param city
     * @param code
     * @param message
     */
    void neterror(OfflineMapCity city, int code, String message);

    /**
     * 崩溃回调
     */
    void exceptionCallBack(OfflineMapCity city, String msg);
}
