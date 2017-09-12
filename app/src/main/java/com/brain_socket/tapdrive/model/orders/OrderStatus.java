package com.brain_socket.tapdrive.model.orders;

import android.content.Context;

import com.brain_socket.tapdrive.R;

import java.util.LinkedHashMap;

/**
 * Created by eyadmhanna on 8/28/17.
 */

public class OrderStatus {

    private static OrderStatus instance = null;

    private LinkedHashMap<String, String> partnerStatuses;
    private LinkedHashMap<String, String> newStatuses;
    private LinkedHashMap<String, String> statuses;

    public static OrderStatus getInstance(Context context) {
        if (instance == null) {
            return new OrderStatus(context);
        }
        return instance;
    }

    public OrderStatus(Context context) {

        statuses = new LinkedHashMap<>();

        statuses.put("PENDING", context.getString(R.string.order_state_pending));
        statuses.put("APPROVED", context.getString(R.string.order_state_approved));
        statuses.put("ON_THE_WAY", context.getString(R.string.order_state_on_the_way));
        statuses.put("DELIVERED", context.getString(R.string.order_state_delivered));
        statuses.put("COMPLETED", context.getString(R.string.order_state_completed));


        partnerStatuses = new LinkedHashMap<>();

        partnerStatuses.put("PENDING", context.getString(R.string.order_state_approve));
        partnerStatuses.put("APPROVED", context.getString(R.string.order_state_on_the_way));
        partnerStatuses.put("ON_THE_WAY", context.getString(R.string.order_state_delivered));
        partnerStatuses.put("DELIVERED", context.getString(R.string.order_state_completed));
        partnerStatuses.put("COMPLETED", context.getString(R.string.order_state_completed));

        newStatuses = new LinkedHashMap<>();

        newStatuses.put("PENDING", "APPROVED");
        newStatuses.put("APPROVED", "ON_THE_WAY");
        newStatuses.put("ON_THE_WAY", "DELIVERED");
        newStatuses.put("DELIVERED", "COMPLETED");

    }

    public LinkedHashMap<String, String> getPartnerStatuses() {
        return partnerStatuses;
    }

    public void setPartnerStatuses(LinkedHashMap<String, String> partnerStatuses) {
        this.partnerStatuses = partnerStatuses;
    }

    public LinkedHashMap<String, String> getNewStatuses() {
        return newStatuses;
    }

    public void setNewStatuses(LinkedHashMap<String, String> newStatuses) {
        this.newStatuses = newStatuses;
    }

    public LinkedHashMap<String, String> getStatuses() {
        return statuses;
    }

    public void setStatuses(LinkedHashMap<String, String> statuses) {
        this.statuses = statuses;
    }
}
