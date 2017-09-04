package com.lmachine.mlda.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by SailFlorve on 2017/9/4 0004.
 * SharedPrefs 封装
 */

public class SPUtil {

    /**
     * 使用默认SharedPreferences名字
     */
    public static SharedPrefsManager load(Context context) {
        return new SharedPrefsManager(context);
    }

    public static class SharedPrefsManager {
        private SharedPreferences prefs;

        public SharedPrefsManager(Context context) {
            prefs = PreferenceManager.getDefaultSharedPreferences(context);
        }

        public SharedPrefsManager put(String key, Object object) {
            SharedPreferences.Editor editor = prefs.edit();

            if (object instanceof Integer) {
                editor.putInt(key, (Integer) object);
            }

            if (object instanceof String || object == null) {
                editor.putString(key, (String) object);
            }

            if (object instanceof Boolean) {
                editor.putBoolean(key, (Boolean) object);
            }
            editor.apply();
            return this;
        }

        public int getInt(String key, int defaultValue) {
            return prefs.getInt(key, defaultValue);
        }

        public String getString(String key, String defaultValue) {
            return prefs.getString(key, defaultValue);
        }

        public boolean getBoolean(String key, boolean defaultValue) {
            return prefs.getBoolean(key, defaultValue);
        }

        public SharedPrefsManager remove(String key) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove(key);
            editor.apply();
            return this;
        }
    }
}
