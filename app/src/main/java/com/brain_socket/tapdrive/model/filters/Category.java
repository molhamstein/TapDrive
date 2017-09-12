package com.brain_socket.tapdrive.model.filters;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.utils.LocalizationHelper;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 7/17/2017.
 */

public class Category extends AppBaseModel {

    private String id;
    @SerializedName("name_en")
    private String englishName;
    @SerializedName("name_ar")
    private String arabicName;
    private ArrayList<CategoryField> fields;

    public static Category fromJson(JSONObject json) {
        try {
            Category category = TapApp.getSharedGsonParser().fromJson(json.toString(), Category.class);

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
        return new Category();
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

    public String getName() {

        String locale = LocalizationHelper.getCurrentLocale();
        if (!locale.equalsIgnoreCase("")) {
            if (locale.equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                return getEnglishName();
            } else {
                return getArabicName();
            }
        } else {
            if (LocalizationHelper.getDeviceLocale().equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                return getEnglishName();
            } else {
                return getArabicName();
            }
        }

    }

}
