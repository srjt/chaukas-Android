package com.example.surjit.mymapsapplication.helpers;

import android.util.JsonReader;
import android.util.Log;

import com.example.surjit.mymapsapplication.models.constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by surjit on 1/12/2016.
 */
public   class helperJsonReader {
    public static List parseDoublesArray(JsonReader reader) throws IOException {
        List doubles = new ArrayList();
        reader.beginArray();
        while (reader.hasNext()) {
            doubles.add(reader.nextDouble());
        }
        reader.endArray();
        return doubles;
    }
    public static void closeJsonReader(JsonReader reader){
        try {
            if (reader != null) {
                reader.close();
            }
        }
        catch (Exception e){
            Log.e(constants.TAG_ParseJson, "Failed to close JsonReader");
        }
    }
    public static String removeNewLine(String str){
        return str.replaceAll("\\n","");
    }

}
