package com.epgis.base.utils;

import com.epgis.mapsdk.Aegis;

import java.io.IOException;
import java.io.InputStream;

public class AssetsUtils {

    public static String getJsonFromAssets(String name) {
        try {
            InputStream is = Aegis.getApplicationContext().getResources().getAssets().open(name);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            is.close();
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
