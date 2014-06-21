package com.joshliberty.veganfriendly.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by caligula on 08/06/14.
 * This file is part of VeganFriendly.
 */
public class App {

    public static final String APPNAME = "VeganFriendly";
    public static final String PREFERENCES = "VeganFriendlyPrefs";

    public static SharedPreferences getPreferences(Context context){
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public static void saveSetting(Context context, String name, String value){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putString(name, value).apply();
    }

    public static void saveSetting(Context context, String name, Long value){
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        prefs.edit().putLong(name, value).apply();
    }

}
