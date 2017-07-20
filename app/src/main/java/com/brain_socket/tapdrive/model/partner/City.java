package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by EYADOOS-PC on 7/20/2017.
 */

public class City extends AppBaseModel {


    private String id;
    @SerializedName("country_id")
    private String countryId;
    @SerializedName("name_en")
    private String englishName;
    @SerializedName("name_ar")
    private String arabicName;

    public static City fromJson(JSONObject json) {
        try {
            City city = TapApp.getSharedGsonParser().fromJson(json.toString(), City.class);
            return city;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new City();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getArabicName() {
        return arabicName;
    }

    public void setArabicName(String arabicName) {
        this.arabicName = arabicName;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }
}