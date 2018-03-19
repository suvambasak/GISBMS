package com.example.codebox.gisbms;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by codebox on 3/8/17.
 */

public class UserInfo {
    private static UserInfo mInstance;
    private static Context mCtx;


    private static final String SHARED_PREF_NAME = "gisbmsData";
    private static final String TOKEN_SHARED_PREF = "gisbmsToken";
    private static final String EMERGENCY_PREF = "emgLocation";

    private static final String KEY_ID = "account_id";
    private static final String KEY_EMAIL = "email_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_EMG_LAT = "emgLat";
    private static final String KEY_EMG_LNG = "emgLng";
    private static final String KEY_EMG_TITLE = "emgTitle";



    private UserInfo(Context context) {
        mCtx = context;
    }

    public static synchronized UserInfo getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new UserInfo(context);
        }
        return mInstance;
    }

    public boolean saveEmergencyLocation(String title, String lat,String lng){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(EMERGENCY_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_EMG_TITLE, title);
        editor.putString(KEY_EMG_LAT, lat);
        editor.putString(KEY_EMG_LNG, lng);
        editor.apply();
        return true;
    }

    public String getEmergencyTitle(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(EMERGENCY_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMG_TITLE,null);
    }

    public String getEmergencyLat(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(EMERGENCY_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMG_LAT,null);
    }

    public String getEmergencyLng(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(EMERGENCY_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMG_LNG,null);
    }

    public boolean completeEmergencyService(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(EMERGENCY_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
        return true;
    }


    public boolean saveToken(String token){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(TOKEN_SHARED_PREF,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_TOKEN, token);
        editor.apply();
        return true;
    }

    public String getToken(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(TOKEN_SHARED_PREF,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN,null);
    }






    public boolean userLogin(String id, String email){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
        return true;
    }


    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);

        if (sharedPreferences.getString(KEY_ID,null) != null){
            return true;
        }
        return false;
    }

    public boolean logOut(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
        return true;
    }

    public String getAccoutnId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ID,null);
    }

    public String getEmailId(){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL,null);
    }


}
