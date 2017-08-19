package com.brain_socket.tapdrive.model;

import com.brain_socket.tapdrive.utils.TapApp;

import org.json.JSONObject;

/**
 * Created by Molham on 12/02/16.
 */
public class AppUser extends AppBaseModel{

    String id ;
    String token;
    String name;
    String email;

    // to be cleared
    String profileImage;
    String coverImage;

    public static AppUser fromJson(JSONObject json) {
        try {
            return TapApp.getSharedGsonParser().fromJson(json.toString(), AppUser.class);
        }catch (Exception ignored){}
        return new AppUser();
    }

    public AppUser() {}

    public AppUser(JSONObject json) {
        if (json == null)
            return;
        try {
            id = json.getString("id");
        }catch (Exception ignored){}
        try {
            email = json.getString("email");
        }catch (Exception ignored){}
        try {
            profileImage = json.getString("profileImage");
        }catch (Exception ignored){}
        try {
            coverImage = json.getString("coverImage");
        }catch (Exception ignored){}
    }

    public String getId() { return id; }

    public String getAccessToken() {
        return token;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}