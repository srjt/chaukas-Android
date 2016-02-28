package com.example.surjit.mymapsapplication.helpers;

import android.util.Log;

import com.example.surjit.mymapsapplication.models.constants;

import java.net.HttpURLConnection;

/**
 * Created by surjit on 1/12/2016.
 */
public class helperHttp {
    public static void setHTTPJsonHeaders(HttpURLConnection conn){
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");

    }
    public static void setHTTPGetRequestMethod(HttpURLConnection conn){
        try {
            setHTTPJsonHeaders(conn);
            conn.setRequestMethod("GET");
        }catch(Exception e){
            Log.e(constants.TAG_HTTP,e.getMessage());
        }
    }
}
