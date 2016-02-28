package com.example.surjit.mymapsapplication.helpers;

import android.net.ParseException;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by surjit on 1/13/2016.
 */
public class helperDate {
    //static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    static final String DATE_FORMAT = "yyyy-MM-dd";
    public static Date GetCurrentUTCDateTimeAsDate()
    {
        return ConvertStringDateToDate(GetCurrentUTCDateTimeAsString());
    }

    public static String GetCurrentUTCDateTimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }
    public static  String GetPastUTCDateTimeAsString(long days){
        long mSeconds=days * 24 * 60 *60 *1000;
        Log.d("Chaukas_Date",Long.toString( mSeconds ));
        Date pastDate = new Date(GetCurrentUTCDateTimeAsDate().getTime() - mSeconds);

        final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(pastDate).toString();
    }
    public static Date ConvertStringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        try {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return dateToReturn;
    }
}
