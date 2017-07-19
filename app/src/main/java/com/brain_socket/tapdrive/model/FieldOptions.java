package com.brain_socket.tapdrive.model;

import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by EYADOOS-PC on 7/17/2017.
 */

public class FieldOptions extends AppBaseModel {

    private String id;
    @SerializedName("category_id")
    private String categoryId;
    @SerializedName("field_id")
    private String fieldId;
    @SerializedName("parent_option_id")
    private String parentOptionId;
    @SerializedName("name_en")
    private String englishName;
    @SerializedName("name_ar")
    private String arabicName;

    public static FieldOptions fromJson(JSONObject json) {
        try {
            FieldOptions option = TapApp.getSharedGsonParser().fromJson(json.toString(), FieldOptions.class);
            return option;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new FieldOptions();
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getFieldId() {
        return fieldId;
    }

    public void setFieldId(String fieldId) {
        this.fieldId = fieldId;
    }

    public String getParentOptionId() {
        return parentOptionId;
    }

    public void setParentOptionId(String parentOptionId) {
        this.parentOptionId = parentOptionId;
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
