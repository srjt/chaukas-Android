package com.example.surjit.mymapsapplication.models;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.example.surjit.mymapsapplication.helpers.helperJsonReader;

import java.util.ArrayList;

/**
 * Created by surjit on 1/12/2016.
 */
public class Location {
    private double longitude;
    private double latitude;
    private String type;

    public Location(){}
    public Location(double lng,double lat){
        longitude=lng;
        latitude=lat;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public static Location parseLocation(JsonReader reader){
        Location location=new Location();
        ArrayList coordinates = new ArrayList();
        try{
            reader.beginObject();
            while(reader.hasNext()) {
                String name= reader.nextName();
                boolean hasCoordinates = reader.peek() != JsonToken.NULL;
                if (name.equals("coordinates") && hasCoordinates) {
                    coordinates = (ArrayList) helperJsonReader.parseDoublesArray(reader);
                    location.setLongitude((double)coordinates.get(0));
                    location.setLatitude((double)coordinates.get(1));
                }
                else if(name.equals("type") &&  reader.peek() != JsonToken.NULL){
                    location.setType(reader.nextString());
                }
                else {
                    reader.skipValue();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                reader.endObject();
            }catch (Exception eInner){eInner.printStackTrace();}
        }
        return location;
    }
}
