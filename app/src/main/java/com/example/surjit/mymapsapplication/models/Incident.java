package com.example.surjit.mymapsapplication.models;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.example.surjit.mymapsapplication.helpers.helperJsonReader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by surjit on 1/12/2016.
 */
public class Incident {
    private String id;
    private String title;
    private String desc;
    private Location location;
    private String address;
    private String date;
    private String source;
    private  User user;
    private ArrayList<Comment> comments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = helperJsonReader.removeNewLine(title);
    }

    public String getDesc(){return desc;}

    public void setDesc(String desc){this.desc=desc;}

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate(){return this.date;}

    public void setDate(String date) {
        this.date = date;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }
    public String getReportedBy(){
        if(this.source!=null){
            return "by " + this.source;
        }else if(this.user!=null){
            return "by " + this.user.getDisplayName();
        }
        return "";
    }

    public String getCommentsLabel(){
        if(this.comments!=null ){
            int size=this.comments.size();
            if(size==1){
                return "1 COMMENT";
            }
            else{
                return size + " COMMENTS";
            }
        }
        return "";
    }
    public static ArrayList<Incident> parseIncidents(InputStreamReader streamReader){
        ArrayList<Incident> incidents=new ArrayList<Incident>();
        JsonReader reader=null;
        try {
            reader = new JsonReader(streamReader);
            reader.beginArray();
            while (reader.hasNext()) {
                try {
                    Incident incident = Incident.parseIncident(reader);
                    if (incident != null) {
                        incidents.add(incident);
                    }
                }catch (Exception e){

                }
            }
            reader.endArray();
        }
        catch (Exception e){
           e.printStackTrace();
        }
        finally {
            helperJsonReader.closeJsonReader(reader);
        }

        return incidents;
    }
    public static Incident parseIncident(JsonReader reader){
        Incident incident=null;
        try{
            reader.beginObject();
            while(reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("_id")) {
                    incident = new Incident();
                    incident.setId(reader.nextString());
                } else if (name.equals("title") &&  reader.peek() != JsonToken.NULL) {
                    incident.setTitle(reader.nextString());
                } else if (name.equals("desc") &&  reader.peek() != JsonToken.NULL) {
                    incident.setDesc(reader.nextString());
                } else if (name.equals("address") &&  reader.peek() != JsonToken.NULL) {
                    incident.setAddress(reader.nextString());
                } else if (name.equals("source") &&  reader.peek() != JsonToken.NULL) {
                    incident.setSource(reader.nextString());
                } else if (name.equals("user") &&  reader.peek() != JsonToken.NULL) {
                    incident.setUser(User.parseUser(reader));
                } else if (name.equals("comments") &&  reader.peek() != JsonToken.NULL) {
                    incident.setComments(Comment.parseCommentList(reader));
                } else if (name.equals("loc") &&  reader.peek() != JsonToken.NULL) {
                    incident.setLocation(Location.parseLocation(reader));
                } else if (name.equals("date") &&  reader.peek() != JsonToken.NULL) {
                    incident.setDate(reader.nextString());
                } else {
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
        return incident;
    }
}
