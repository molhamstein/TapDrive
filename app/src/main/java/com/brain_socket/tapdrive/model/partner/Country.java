package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by EYADOOS-PC on 7/20/2017.
 */

public class Country extends AppBaseModel {

    private String id;
    @SerializedName("name_en")
    private String englishName;
    @SerializedName("name_ar")
    private String arabicName;

    public static Country fromJson(JSONObject json) {
        try {
            Country country = TapApp.getSharedGsonParser().fromJson(json.toString(), Country.class);
            return country;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new Country();
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
}
