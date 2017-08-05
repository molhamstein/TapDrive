package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.utils.TapApp;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by EYADOOS-PC on 7/20/2017.
 */

public class Reservation extends AppBaseModel {

    private String orderStartDate;
    private String orderEndDate;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

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

    public Date getOrderStartDate() {
        try {
            Date date = sdf.parse(this.orderStartDate);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public void setOrderStartDate(String orderStartDate) {
        this.orderStartDate = orderStartDate;
    }

    public Date getOrderEndDate() {
        try {
            Date date = sdf.parse(this.orderEndDate);
            return date;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Date();
    }

    public void setOrderEndDate(String orderEndDate) {
        this.orderEndDate = orderEndDate;
    }
}
