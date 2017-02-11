package com.allattentionhere.show.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by krupenghetiya on 10/02/17.
 */

public class SharedPrefsUtil {

    private static SharedPreferences sharedPrefs;

    public static void initialize(Context context) {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean contains(Context context, String key) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        return sharedPrefs.contains(key);
    }

    public static String getString(Context context, String key) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        return sharedPrefs.getString(key, "");
    }

    public static void saveString(Context context, String key, String value) {
        saveString(context, key, value, false);
    }

    public static void saveString(Context context, String key, String value, boolean commit) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        if (commit) {
            sharedPrefs.edit().putString(key, value).commit();
        } else {
            sharedPrefs.edit().putString(key, value).apply();
        }
    }



    public static int getInt(Context context, String key) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        return sharedPrefs.getInt(key, Integer.MIN_VALUE);
    }

    public static void saveInt(Context context, String key, int value) {
        saveInt(context, key, value, false);
    }

    public static void saveInt(Context context, String key, int value, boolean commit) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        if (commit) {
            sharedPrefs.edit().putInt(key, value).commit();
        } else {
            sharedPrefs.edit().putInt(key, value).apply();
        }
    }

    public static void remove(Context context, String key) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        sharedPrefs.edit().remove(key).apply();
    }

    public static void removeAll(Context context) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        sharedPrefs.edit().clear().apply();
    }

    public static void removeAllForSignOut(Context context) {
        if (sharedPrefs == null) {
            initialize(context);
        }
        SharedPrefsUtil.remove(context, Extras.PREFS_FIREBASE_USERID);
        SharedPrefsUtil.remove(context, Extras.PREFS_EMAIL);
        SharedPrefsUtil.remove(context, Extras.PREFS_FIRST_NAME);
        SharedPrefsUtil.remove(context, Extras.PREFS_LAST_NAME);
        SharedPrefsUtil.remove(context, Extras.PREFS_LOGIN_PROVIDER);
        SharedPrefsUtil.remove(context, Extras.PREFS_PROFILE_IMAGE);
        SharedPrefsUtil.remove(context, Extras.PREFS_PUSH_TOKEN);
        SharedPrefsUtil.remove(context, Extras.PREFS_LOGINPROVIDER_ID);
    }
}
