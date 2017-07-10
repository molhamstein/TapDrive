package com.brain_socket.tapdrive.model;

import com.brain_socket.tapdrive.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by Albert on 7/8/17.
 */
public class UserModel extends AppBaseModel {

    private String id;

    @SerializedName("full_name")
    private String username;
    private String email;
    private String token;
    private String photo;
    private String phone;
    private String gender;
    private String birthdate;
    private double longitude;
    private double latitude;
    private String socialId;
    private String socialToken;

    public static UserModel fromJson(JSONObject json) {
        try {
            return TapApp.getSharedGsonParser().fromJson(json.toString(), UserModel.class);
        }catch (Exception ignored){}
        return new UserModel();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getSocialId() {
        return socialId;
    }

    public void setSocialId(String socialId) {
        this.socialId = socialId;
    }

    public String getSocialToken() {
        return socialToken;
    }

    public void setSocialToken(String socialToken) {
        this.socialToken = socialToken;
    }
}
