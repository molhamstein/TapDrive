package com.brain_socket.tapdrive.model.partner;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.model.orders.Order;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by Molhamstein on 8/20/2017.
 */

public class Invoice extends AppBaseModel {

    private String id;
    private String date;
    private String ref;
    private float  total;
    private String status;
    private Order order;

    public static Invoice fromJson(JSONObject json) {
        try {
            Invoice invoice = TapApp.getSharedGsonParser().fromJson(json.toString(), Invoice.class);
            return invoice;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new Invoice();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Order getItem() {
        return order;
    }

    public float getTotal() {
        return total;
    }

}
