package com.epgis.offline.widget;

import android.content.Context;

import com.epgis.commons.utils.SharedPreferencesUtil;
import com.epgis.offline.DownloadState;

/**
 * SharedPreferences工具类
 * Created by Lynn on 2018/4/4.
 */
public class SpUtil extends SharedPreferencesUtil {
    private static final String FZ_STATUS = "fzStatus";
    private static final String XM_STATUS = "xmStatue";


    public static void init(Context context) {
        SharedPreferencesUtil.init(context);
    }

    /**
     * 设置福州市的状态
     *
     * @return
     */
    public static int getFzStatus() {
        return getInt(FZ_STATUS, DownloadState.state_undownload);
    }

    public static void setFzStatus(int fzStatus) {
        saveInt(FZ_STATUS, fzStatus);
    }

    /**
     * 设置厦门市的状态
     *
     * @return
     */
    public static int getXmStatus() {
        return getInt(XM_STATUS, DownloadState.state_undownload);
    }

    public static void setXmStatus(int xmStatue) {
        saveInt(XM_STATUS, xmStatue);
    }
}