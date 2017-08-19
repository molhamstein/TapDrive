package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.model.filters.CategoryField;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 7/20/2017.
 */

public class Country extends AppBaseModel {

    private String id;
    @SerializedName("name_en")
    private String englishName;
    @SerializedName("name_ar")
    private String arabicName;
    private ArrayList<City> cities;

    public static Country fromJson(JSONObject json) {
        try {
            Country country = TapApp.getSharedGsonParser().fromJson(json.toString(), Country.class);

            if (json.has("cities")) {
                ArrayList<City> cities = new ArrayList<>();
                JSONArray citiesJSONArray = json.getJSONArray("cities");
                if (citiesJSONArray != null) {
                    for (int i = 0; i < citiesJSONArray.length(); i++) {
                        City city = City.fromJson(citiesJSONArray.getJSONObject(i));
                        cities.add(city);
                    }
                }
                country.setCities(cities);
            } else {
                country.setCities(new ArrayList<City>());
            }

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

    public ArrayList<City> getCities() {
        return cities;
    }

    public void setCities(ArrayList<City> cities) {
        this.cities = cities;
    }
}
