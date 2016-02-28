package com.example.surjit.mymapsapplication.helpers;

import android.util.Log;

import com.example.surjit.mymapsapplication.models.constants;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by surjit on 1/14/2016.
 */
public class helperGeo {

    public static double toRadians(double n) {
        return n * Math.PI / 180;
    }
    public static double calculateDistance(LatLng point1, LatLng point2) {
        //calculate distance
        int R = 6371000; // metres
        double φ1 = toRadians(point1.latitude);
        double φ2 = toRadians(point2.latitude);
        double Δφ = toRadians(point2.latitude - point1.latitude);
        double Δλ = toRadians(point2.longitude - point1.longitude);

        double a = Math.sin(Δφ / 2) * Math.sin(Δφ / 2) +
                Math.cos(φ1) * Math.cos(φ2) *
                        Math.sin(Δλ / 2) * Math.sin(Δλ / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        Log.d(constants.TAG_MapsActivity, "Distance: " + (R * c) + " Point1.lat: " + point1.latitude + " Point1.lng" + point1.longitude + " Point2.lat" + point2.latitude + " Point2.lng" + point2.longitude);
        return R * c;
    }
}
