package com.brain_socket.tapdrive.model;

import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 7/17/2017.
 */

public class AppCategory extends AppBaseModel {

    private String id;
    @SerializedName("name_en")
    private String englishName;
    @SerializedName("name_ar")
    private String arabicName;
    private ArrayList<CategoryField> fields;

    public static AppCategory fromJson(JSONObject json) {
        try {
            AppCategory category = TapApp.getSharedGsonParser().fromJson(json.toString(), AppCategory.class);

            if (json.has("fields")) {
                ArrayList<CategoryField> fields = new ArrayList<>();
                JSONArray fieldsJSONArray = json.getJSONArray("fields");
                for (int i = 0; i < fieldsJSONArray.length(); i++) {
                    CategoryField categoryField = CategoryField.fromJson(fieldsJSONArray.getJSONObject(i));
                    fields.add(categoryField);
                }
                category.setFields(fields);
            }

            return category;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new AppCategory();
    }

    public JSONObject getJsonObject() {
        try {
            return new JSONObject(TapApp.getSharedGsonParser().toJson(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new JSONObject();
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

    public ArrayList<CategoryField> getFields() {
        return fields;
    }

    public void setFields(ArrayList<CategoryField> fields) {
        this.fields = fields;
    }
}
