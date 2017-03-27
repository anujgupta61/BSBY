package com.what_the_hack.bsby;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

class SaveSharedPreference {
    private static final String PREF_LOG = "isLoggedin";
    private static final String PREF_NAME = "name";
    private static final String PREF_BMSH_ID = "bhamashah_id";
    private static final String PREF_AADHAR_NO = "aadhar_no";
    private static final String PREF_DOB = "dob";
    private static final String PREF_ECO_GROUP = "eco_group";

    private static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    static void setLog(Context ctx, String isLoggedIn) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOG , isLoggedIn);
        editor.apply();
    }

    static String getLog(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_LOG , "");
    }

    static void setName(Context ctx, String name) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_NAME , name);
        editor.apply();
    }

    static String getName(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_NAME , "");
    }

    static void setBmshId(Context ctx, String bmshId) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_LOG , bmshId);
        editor.apply();
    }

    static String getBmshId(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_BMSH_ID , "");
    }

    static void setAadharNo(Context ctx, String aadhar_no) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_AADHAR_NO , aadhar_no);
        editor.apply();
    }

    static String getAadharNo(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_AADHAR_NO , "");
    }

    static void setDOB(Context ctx, String dob) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_DOB , dob);
        editor.apply();
    }

    static String getDOB(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_DOB , "");
    }

    static void setEcoGrp(Context ctx, String eco_gp) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_ECO_GROUP , eco_gp);
        editor.apply();
    }

    static String getEcoGrp(Context ctx) {
        return getSharedPreferences(ctx).getString(PREF_ECO_GROUP , "");
    }
}