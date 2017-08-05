package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.model.filters.CategoryField;
import com.brain_socket.tapdrive.model.filters.FieldOption;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 7/20/2017.
 */

public class CarValue extends AppBaseModel {

    private String id;
    private String value;
    private CategoryField categoryField;
    private ArrayList<FieldOption> fieldOptions;

    public static CarValue fromJson(JSONObject json) {
        try {
            CarValue carValue = new CarValue();

            carValue.id = json.optString("id");
            carValue.value = json.optString("value");
            carValue.categoryField = CategoryField.fromJson(json.optJSONObject("categoryField"));

            if (json.has("categoryFieldOption")) {
                ArrayList<FieldOption> fieldOptions = new ArrayList<>();
                JSONArray fieldOptionsJSONArray = json.optJSONArray("categoryFieldOption");
                if (fieldOptionsJSONArray != null) {
                    if (fieldOptionsJSONArray.length() > 0) {
                        for (int i = 0; i < fieldOptionsJSONArray.length(); i++) {
                            fieldOptions.add(FieldOption.fromJson(fieldOptionsJSONArray.optJSONObject(i)));
                        }
                    }
                } else {
                    carValue.setFieldOptions(new ArrayList<FieldOption>());
                }
                carValue.setFieldOptions(fieldOptions);
            } else {
                carValue.setFieldOptions(new ArrayList<FieldOption>());
            }

            return carValue;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new CarValue();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CategoryField getCategoryField() {
        return categoryField;
    }

    public void setCategoryField(CategoryField categoryField) {
        this.categoryField = categoryField;
    }

    public ArrayList<FieldOption> getFieldOptions() {
        return fieldOptions;
    }

    public void setFieldOptions(ArrayList<FieldOption> fieldOptions) {
        this.fieldOptions = fieldOptions;
    }
}
