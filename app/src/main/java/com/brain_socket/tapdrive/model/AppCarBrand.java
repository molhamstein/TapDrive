package com.brain_socket.tapdrive.model;

import com.brain_socket.tapdrive.TapApp;

import org.json.JSONObject;

/**
 * @author Molham
 */
public class AppCarBrand extends AppBaseModel{
    private String id;
    private String name;
    private String logo;
    private String status;


    public static AppCarBrand fromJson(JSONObject json) {
        try {
            AppCarBrand brand = TapApp.getSharedGsonParser().fromJson(json.toString(), AppCarBrand.class);
            return brand;
        }catch (Exception ignored){}
        return new AppCarBrand();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public JSONObject getJsonObject(){
        try {
            return new JSONObject(TapApp.getSharedGsonParser().toJson(this));
        }catch (Exception e){}
        return  new JSONObject();
    }


}