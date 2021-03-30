package com.epgis.offline.service;

import android.content.Context;

import com.epgis.offline.OfflineMapCity;
import com.epgis.service.api.offlinemap.DownloadQuery;
import com.epgis.service.api.offlinemap.DownloadSearch;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Response;

import static com.epgis.offline.service.DownloadService.ACTION_ERROR;
import static com.epgis.offline.service.DownloadService.ACTION_START;
import static com.epgis.offline.service.EventMessage.HANDLER_GET_FILE_REAL_LENGTH;
import static com.epgis.offline.service.EventMessage.HANDLER_GET_FILE_REAL_LENGTH_ERROR;

/**
 * Created by Lynn on 2018/5/30.
 * 初始化线程
 */
public class InitThread extends Thread {

    private OfflineMapCity fileBean;
    private String type;
    private Context mContext;

    public InitThread(OfflineMapCity fileBean, String type, Context mContext) {
        this.fileBean = fileBean;
        this.type = type;
        this.mContext = mContext;
    }

    @Override
    public void run() {
        try {
            DownloadSearch client = new DownloadSearch(mContext);
            DownloadQuery downloadQuery = new DownloadQuery();

            downloadQuery.setAdCode(fileBean.getAdCode().toString());
            downloadQuery.setRange("bytes=0-");
            try {
                Response response = client.download(downloadQuery);
                if (response != null && response.body() != null) {
                    long fileLength = -1;
                    if (response.code() == HttpURLConnection.HTTP_PARTIAL && response.body() != null) {
                        fileLength = response.body().contentLength();
                    }
                    if (fileLength > 0) {
                        fileBean.setMapSize((long) fileLength);
                    }
                    if (type.equals(ACTION_START)) {
                        EventMessage eventMessage = new EventMessage(HANDLER_GET_FILE_REAL_LENGTH, fileBean);
                        EventBus.getDefault().post(eventMessage);
                    } else if (type.equals(ACTION_ERROR)) {
                        EventMessage eventMessage = new EventMessage(HANDLER_GET_FILE_REAL_LENGTH_ERROR, fileBean);
                        EventBus.getDefault().post(eventMessage);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
