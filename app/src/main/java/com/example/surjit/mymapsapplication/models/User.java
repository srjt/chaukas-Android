package com.example.surjit.mymapsapplication.models;

import android.util.JsonReader;
import android.util.JsonToken;

/**
 * Created by surjit on 1/20/2016.
 */
public class User {

    private String id;
    private String displayName;
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static  User parseUser(JsonReader reader){
        User user=new User();

        try{
            reader.beginObject();
            while(reader.hasNext()) {
                String name= reader.nextName();
                if(name.equals("_id") &&  reader.peek() != JsonToken.NULL){
                    user.setId(reader.nextString());
                } else if (name.equals("displayName") &&  reader.peek() != JsonToken.NULL) {
                    user.setDisplayName(reader.nextString());
                } else if(name.equals("email") &&  reader.peek() != JsonToken.NULL){
                    user.setEmail(reader.nextString());
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
        return user;

    }
}
