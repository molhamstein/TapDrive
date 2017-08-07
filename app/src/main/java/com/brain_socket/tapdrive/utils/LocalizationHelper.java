package com.brain_socket.tapdrive.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.brain_socket.tapdrive.data.DataCacheProvider;

import java.util.Locale;

/**
 * Created by EYADOOS-PC on 8/6/2017.
 */

public class LocalizationHelper {

    public static final String ENGLISH_LOCALE = "en";
    public static final String ARABIC_LOCALE = "ar";

    public static void setLocale(Context context, String localeCode) {

        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Resources res = context.getResources();
        Configuration config = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, null);

    }

    public static String getCurrentLocale() {
        return DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_APP_LOCALE);
    }

    public static String getDeviceLocale() {
        return TapApp.sDefSystemLanguage;
    }

}
