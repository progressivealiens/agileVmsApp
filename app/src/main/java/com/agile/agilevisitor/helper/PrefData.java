package com.agile.agilevisitor.helper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;



/**
 * Created by Hp on 4/5/2018.
 */

public class PrefData extends Application {

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;

    private static PrefData mInstance;
    private static String sharedPrefName = "Agile_Visitor";


    public static String PREF_LOGINSTATUS = "pref_loginstatus";

    public static String pref_wtm_Called = "pref_wtm_Called";
    public static String pref_fcm_token = "pref_fcm_token";
    public static String pref_user_mobile = "pref_user_mobile";
    public static String user_type = "pref_user_type";
    public static String isFirstTimeRun = "pref_first_run";
    public static String pref_visiting_mobile = "pref_visiting_mobile";
    public static String pref_visiter_id = "pref_visiter_id";
    public static String pref_invite_id = "pref_invite_id";
    public static String pref_called_from_confirm_fragment = "pref_called_from_confirm_fragment";
    public static String pref_visitor_mobile_details = "pref_visitor_mobile_details";
    public static String pref_unit_name = "pref_unit_name";
    public static String pref_sub_unit_name = "pref_sub_unit_name";
    public static String pref_employee_name = "pref_employee_name";

    public static String pref_user_name = "pref_user_name";
    public static String pref_user_logo = "pref_user_logo";
    public static String pref_user_invites = "pref_user_invites";
    public static String pref_user_visitors = "pref_user_visitors";





    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        mInstance = this;

    }

    public PrefData() {
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized PrefData getInstance() {
        return mInstance;
    }

    public PrefData(Context con) {
        mSharedPreferences = con.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
    }

    public void clear() {
        editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearPref() {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static void clearKeyPref(String key) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);
        editor = mSharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static String readStringPref(String key) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        return mSharedPreferences.getString(key, "");
    }

    public static void writeStringPref(String key, String data) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        editor = mSharedPreferences.edit();
        editor.putString(key, data);
        editor.apply();

    }

    public static boolean readBooleanPref(String key) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        return mSharedPreferences.getBoolean(key, false);


    }

    public static void writeBooleanPref(String key, boolean data) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        editor = mSharedPreferences.edit();
        editor.putBoolean(key, data);
        editor.apply();

    }

    public static long readLongPref(String key) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        return mSharedPreferences.getLong(key, 0);
    }

    public static void writeLongPref(String key, long data) {
        mSharedPreferences = mInstance.getSharedPreferences(sharedPrefName, Context.MODE_PRIVATE);

        editor = mSharedPreferences.edit();
        editor.putLong(key, data);
        editor.apply();

    }

    public SharedPreferences getmSharedPreferences() {
        return mSharedPreferences;
    }

    public void setmSharedPreferences(SharedPreferences mSharedPreferences) {
        PrefData.mSharedPreferences = mSharedPreferences;
    }

    public String getSharedPrefName() {
        return sharedPrefName;
    }

    public void setSharedPrefName(String sharedPrefName) {
        PrefData.sharedPrefName = sharedPrefName;
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public void setEditor(SharedPreferences.Editor editor) {
        PrefData.editor = editor;
    }


}
