package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 7/20/2017.
 */

public class Partner extends AppBaseModel {

    private String id;
    private String baseUserId;
    @SerializedName("full_name")
    private String name;
    private String email;
    private String address;
    private String phone;
    private String logo;
    private float latitude;
    private float longitude;
    private String token;
    private ArrayList<Car> cars;
    private Country country;
    private City city;

    public static Partner fromJson(JSONObject json) {
        try {
            Partner partner = TapApp.getSharedGsonParser().fromJson(json.toString(), Partner.class);

            if (json.has("items")) {
                ArrayList<Car> cars = new ArrayList<>();
                JSONArray carsJSONArray = json.getJSONArray("items");
                if (carsJSONArray.length() > 0) {
                    for (int i = 0; i < carsJSONArray.length(); i++) {
                        Car car = Car.fromJson(carsJSONArray.getJSONObject(i));
                        cars.add(car);
                    }
                }
                partner.setCars(cars);
            } else {
                partner.setCars(new ArrayList<Car>());
            }

            partner.country = Country.fromJson(json.optJSONObject("country"));
            partner.city = City.fromJson(json.optJSONObject("city"));

            return partner;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new Partner();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }

    public int getMarkerResource() {
        return R.drawable.ic_marker_sport;
    }

    public LatLng getCoords(){
        try {
            LatLng coords = new LatLng(latitude, longitude);
            return coords;
        }catch (Exception e){
            return new LatLng(0, 0);
        }
    }
}
