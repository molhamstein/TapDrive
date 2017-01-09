package com.brain_socket.tapdrive.model;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.TapApp;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

/**
 * @author Molham
 */
public class AppCar extends AppBaseModel{
    public static enum CAR_TYPE {NORMAL, SUV, SPORT}

    private String id;
    private String name;
    private String logo;
    private float lat;
    private int type;
    private float lng;
    private String status;


    public static AppCar fromJson(JSONObject json) {
        try {
            AppCar brand = TapApp.getSharedGsonParser().fromJson(json.toString(), AppCar.class);
            return brand;
        }catch (Exception ignored){}
        return new AppCar();
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

    public float getLat() {
        return lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public CAR_TYPE getType() {
        switch (type){
            case 0:
                return CAR_TYPE.NORMAL;
            case 1:
                return CAR_TYPE.SUV;
            case 2:
                return CAR_TYPE.SPORT;
        }
        return CAR_TYPE.NORMAL;
    }

    public int getMarkerResource() {
        switch (type){
            case 0:
                return R.drawable.ic_marker_sport;
            case 1:
                return R.drawable.ic_marker_suv;
            case 2:
                return R.drawable.ic_marker_conv;
        }
        return R.drawable.ic_marker_sport;
    }

    public void setType(int type) {
        this.type = type;
    }

    public JSONObject getJsonObject(){
        try {
            return new JSONObject(TapApp.getSharedGsonParser().toJson(this));
        }catch (Exception e){}
        return  new JSONObject();
    }

    public LatLng getCoords(){
        try {
            LatLng coords = new LatLng(lat, lng);
            return coords;
        }catch (Exception e){
            return new LatLng(0, 0);
        }
    }
}