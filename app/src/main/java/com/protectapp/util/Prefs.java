package com.protectapp.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

public class Prefs {

    private static final Prefs instance = new Prefs();
    public static final String PREF_NAME = "PROTECT_PREF";

    public static Prefs getInstance() {
        return instance;
    }


    public static void resetPreference(Context context) {
        SharedPreferences info = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.clear();
        editor.commit();
    }

    public static void saveString(Context context, String key, String value) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences info = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return info.getString(key, defValue);
    }

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }


    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return info.getBoolean(key, defValue);
    }

    public static void saveInt(Context context, String key, int value) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return info.getInt(key, defValue);
    }


    public static void saveLong(Context context, String key, long value) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return info.getLong(key, defValue);
    }

    public static void saveStringArray(Context context, String key, String[] values) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2;
        try {
            jsonArray2 = new JSONArray(info.getString(key, "[]"));


            for (int i = 0; i < jsonArray2.length(); i++) {
                jsonArray.put(jsonArray2.getString(i));
            }
            for (String value : values)
                jsonArray.put(value);

            editor.putString(key, jsonArray.toString());
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveStringArray(Context context, String key, String value) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2;
        try {
            jsonArray2 = new JSONArray(info.getString(key, "[]"));


            for (int i = 0; i < jsonArray2.length(); i++) {
                jsonArray.put(jsonArray2.getString(i));
            }
            jsonArray.put(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.putString(key, jsonArray.toString());
        editor.commit();
    }

    public static String[] getStringArray(Context context, String key, String defvalue) {
        String[] result = null;
        SharedPreferences info = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        JSONArray jsonArray2;
        try {
            jsonArray2 = new JSONArray(info.getString(key, defvalue));

            result = new String[jsonArray2.length()];
            for (int i = 0; i < jsonArray2.length(); i++) {
                result[i] = jsonArray2.getString(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static void savePreference(SharedPreferences prefs, String key,
                                      boolean value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static void clearAll(Context context) {
        SharedPreferences info = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = info.edit();
        editor.clear();
        editor.commit();
    }

}