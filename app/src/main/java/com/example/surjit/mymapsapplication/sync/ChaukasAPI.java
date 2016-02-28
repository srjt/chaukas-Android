package com.example.surjit.mymapsapplication.sync;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.example.surjit.mymapsapplication.activities.MapsActivity;
import com.example.surjit.mymapsapplication.helpers.helperHttp;
import com.example.surjit.mymapsapplication.helpers.helperJsonReader;
import com.example.surjit.mymapsapplication.models.Incident;
import com.example.surjit.mymapsapplication.models.Location;
import com.example.surjit.mymapsapplication.models.constants;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by surjit on 1/9/2016.
 */
public class ChaukasAPI extends AsyncTask<String,Integer,ArrayList<Incident>> {

    public static  boolean loading=false;

    ConnectivityManager mConnectivityMgr;
    DataDownloadListener dataDownloadListener;
    public void setDataDownloadListener(DataDownloadListener dataDownloadListener) {
        this.dataDownloadListener = dataDownloadListener;
    }
    public ChaukasAPI(ConnectivityManager connMgr){
        mConnectivityMgr=connMgr;
    }
    public boolean isConnected(){
        NetworkInfo networkInfo = mConnectivityMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    protected ArrayList<Incident> doInBackground(String... params) {
        String urlString=params[0];
        InputStream in = null;
        JsonReader reader=null;
        ArrayList<Incident> incidents=null;
        try {

            URL url = new URL(urlString);
            Log.d(constants.TAG_SyncHttp, "Executing apiURL " + urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            helperHttp.setHTTPGetRequestMethod(urlConnection);

            loading=true;
            urlConnection.connect();
            Log.d(constants.TAG_SyncHttp, "Connected to " + urlString);

            InputStream inputStream=urlConnection.getInputStream();
            BufferedInputStream bufferedStream = new BufferedInputStream(
                    inputStream);
            InputStreamReader streamReader = new InputStreamReader(
                    bufferedStream);


            incidents=   Incident.parseIncidents(streamReader);
            Log.d(constants.TAG_MapsActivity,incidents.size() +  " incidents found.");

        } catch (Exception e ) {
            dataDownloadListener.dataDownloadFailed(e.getMessage());
            e.printStackTrace();
            cancel(true);
        }
        finally {
            helperJsonReader.closeJsonReader(reader);
        }
        return incidents;
    }

    @Override
    protected void onPostExecute(ArrayList<Incident> result) {
        super.onPostExecute(result);
        loading=false;
        dataDownloadListener.dataDownloadedSuccessfully(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        loading=false;
    }
}

