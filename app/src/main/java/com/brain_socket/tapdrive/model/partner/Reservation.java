package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.utils.TapApp;

import org.json.JSONObject;

/**
 * Created by EYADOOS-PC on 7/20/2017.
 */

public class Reservation extends AppBaseModel {

    private String orderStartDate;
    private String orderEndDate;

    public static Reservation fromJson(JSONObject json) {
        try {
            Reservation reservation = TapApp.getSharedGsonParser().fromJson(json.toString(), Reservation.class);
            return reservation;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new Reservation();
    }

    @Override
    public String getId() {
        return null;
    }

    public String getOrderStartDate() {
        return orderStartDate;
    }

    public void setOrderStartDate(String orderStartDate) {
        this.orderStartDate = orderStartDate;
    }

    public String getOrderEndDate() {
        return orderEndDate;
    }

    public void setOrderEndDate(String orderEndDate) {
        this.orderEndDate = orderEndDate;
    }
}
