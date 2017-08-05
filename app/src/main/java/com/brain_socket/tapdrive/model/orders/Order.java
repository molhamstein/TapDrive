package com.brain_socket.tapdrive.model.orders;

import android.util.Log;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.model.partner.Car;
import com.brain_socket.tapdrive.model.partner.CarValue;
import com.brain_socket.tapdrive.model.partner.Partner;
import com.brain_socket.tapdrive.model.partner.Reservation;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 8/5/2017.
 */

public class Order extends AppBaseModel {

    private String id;
    @SerializedName("start_date")
    private String startDate;
    @SerializedName("end_date")
    private String endDate;
    private String status;
    private String total;
    private Car item;
    private Partner partner;
    private UserModel user;

    public static Order fromJson(JSONObject json) {
        try {
            Order order = TapApp.getSharedGsonParser().fromJson(json.toString(), Order.class);
            return order;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new Order();
    }

    @Override
    public String getId() {
        return null;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Car getItem() {
        return item;
    }

    public void setItem(Car item) {
        this.item = item;
    }

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
}
