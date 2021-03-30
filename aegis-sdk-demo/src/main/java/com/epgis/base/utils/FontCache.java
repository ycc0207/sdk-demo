package com.epgis.base.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * 缓存字体TTF文件
 */
public class FontCache {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);

        if (tf == null) {
            try {
                // 从Asset获取TTF资源
                tf = Typeface.createFromAsset(context.getAssets(), name);
                fontCache.put(name, tf);
            } catch (Exception exception) {
            }
        }
        return tf;
    }
}
