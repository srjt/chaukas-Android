package com.example.surjit.mymapsapplication.models;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import com.example.surjit.mymapsapplication.helpers.helperJsonReader;

import java.util.ArrayList;

/**
 * Created by surjit on 1/20/2016.
 */
public class Comment {

    private String id;
    private User user;
    private String comment;
    private String date;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User u) {
        this.user= u;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public static  Comment parseComment(JsonReader reader){
        Comment comment=new Comment();

        try{
            reader.beginObject();
            while(reader.hasNext()) {
                String name= reader.nextName();
                if(name.equals("_id") &&  reader.peek() != JsonToken.NULL){
                  comment.setId(reader.nextString());
                } else if (name.equals("user") &&  reader.peek() != JsonToken.NULL) {
                   comment.setUser(User.parseUser(reader));
                } else if(name.equals("comment") &&  reader.peek() != JsonToken.NULL){
                    comment.setComment(reader.nextString());
                } else if(name.equals("date") &&  reader.peek() != JsonToken.NULL){
                    comment.setDate(reader.nextString());
                } else {
                    reader.skipValue();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                reader.endObject();
            }catch (Exception eInner){eInner.printStackTrace();}
        }
        return comment;
    }
    public  static ArrayList<Comment> parseCommentList(JsonReader reader){
        ArrayList<Comment> commentArrayList=new ArrayList<Comment>();
        try{
            reader.beginArray();
            while (reader.hasNext()) {
                if(reader.peek() != JsonToken.NULL) {
                    Comment comment = Comment.parseComment(reader);
                    if (comment != null && comment.getId().length()>0) {
                        commentArrayList.add(comment);
                    }
                }
            }
            reader.endArray();;
        }
        catch (Exception e){
            e.printStackTrace();
        }
       return   commentArrayList;
    }
}
