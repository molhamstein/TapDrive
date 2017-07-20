package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 7/20/2017.
 */

public class Car extends AppBaseModel {

    private String id;
    @SerializedName("partner_id")
    private String partnerId;
    private String name;
    @SerializedName("hourly_price")
    private String hourlyPrice;
    @SerializedName("daily_price")
    private String dailyPrice;
    @SerializedName("weekly_price")
    private String weeklyPrice;
    @SerializedName("monthly_price")
    private String monthlyPrice;
    private String photo;
    private ArrayList<Reservation> reservations;
    private ArrayList<CarValue> carValues;

    public static Car fromJson(JSONObject json) {
        try {
            Car car = TapApp.getSharedGsonParser().fromJson(json.toString(), Car.class);

            if (json.has("reserved_dates")) {
                ArrayList<Reservation> reservations = new ArrayList<>();
                JSONArray reservationsJSONArray = json.getJSONArray("reserved_dates");
                if (reservationsJSONArray.length() > 0) {
                    for (int i = 0; i < reservationsJSONArray.length(); i++) {
                        Reservation reservation = Reservation.fromJson(reservationsJSONArray.getJSONObject(i));
                        reservations.add(reservation);
                    }
                }
                car.setReservations(reservations);
            }

            if (json.has("values")) {
                ArrayList<CarValue> carValues = new ArrayList<>();
                JSONArray carValuesJSONArray = json.getJSONArray("values");
                if (carValuesJSONArray.length() > 0) {
                    for (int i = 0; i < carValuesJSONArray.length(); i++) {
                        CarValue reservation = CarValue.fromJson(carValuesJSONArray.getJSONObject(i));
                        carValues.add(reservation);
                    }
                }
                car.setCarValues(carValues);
            }

            return car;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new Car();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHourlyPrice() {
        return hourlyPrice;
    }

    public void setHourlyPrice(String hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }

    public String getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(String dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public String getWeeklyPrice() {
        return weeklyPrice;
    }

    public void setWeeklyPrice(String weeklyPrice) {
        this.weeklyPrice = weeklyPrice;
    }

    public String getMonthlyPrice() {
        return monthlyPrice;
    }

    public void setMonthlyPrice(String monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<CarValue> getCarValues() {
        return carValues;
    }

    public void setCarValues(ArrayList<CarValue> carValues) {
        this.carValues = carValues;
    }

    public ArrayList<Reservation> getReservations() {
        return reservations;
    }

    public void setReservations(ArrayList<Reservation> reservations) {
        this.reservations = reservations;
    }
}
