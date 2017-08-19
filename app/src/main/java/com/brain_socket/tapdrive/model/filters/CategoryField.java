package com.brain_socket.tapdrive.model.filters;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 7/17/2017.
 */

public class CategoryField extends AppBaseModel {

    private String id;
    @SerializedName("parent_field_id")
    private String parentFieldId;
    @SerializedName("category_id")
    private String categoryId;
    @SerializedName("name_en")
    private String englishName;
    @SerializedName("name_ar")
    private String arabicName;
    private String type;
    private ArrayList<FieldOption> options;

    public static CategoryField fromJson(JSONObject json) {
        try {
            CategoryField field = TapApp.getSharedGsonParser().fromJson(json.toString(), CategoryField.class);

            if (json.has("options")) {
                ArrayList<FieldOption> options = new ArrayList<>();
                JSONArray optionsJSONArray = json.getJSONArray("options");
                for (int i = 0; i < optionsJSONArray.length(); i++) {
                    FieldOption fieldOption = FieldOption.fromJson(optionsJSONArray.getJSONObject(i));
                    options.add(fieldOption);
                }
                field.setOptions(options);
            }

            return field;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new CategoryField();
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

    public String getParentFieldId() {
        return parentFieldId;
    }

    public void setParentFieldId(String parentFieldId) {
        this.parentFieldId = parentFieldId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<FieldOption> getOptions() {
        return options;
    }

    public void setOptions(ArrayList<FieldOption> options) {
        this.options = options;
    }
}
