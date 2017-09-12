package com.brain_socket.tapdrive.model.orders;

import com.brain_socket.tapdrive.data.DataStore;

import java.util.LinkedHashMap;

/**
 * Created by eyadmhanna on 8/28/17.
 */

public class OrderStatus {

    private static OrderStatus instance = null;

    private LinkedHashMap<String, String> statuses;
    private LinkedHashMap<String, String> newStatuses;

    public static OrderStatus getInstance() {
        if (instance == null) {
            return new OrderStatus();
        }
        return instance;
    }

    public OrderStatus() {

        statuses = new LinkedHashMap<>();

        statuses.put("PENDING", "Approve");
        statuses.put("Approved", "Mark as on the way");
        statuses.put("Delivery", "Mark as delivered");
        statuses.put("Delivered", "Mark as done");
        statuses.put("Done", "Done");

        newStatuses = new LinkedHashMap<>();
        statuses.put("PENDING", "APPROVED");
        statuses.put("Approved", "ON_THE_WAY");
        statuses.put("Delivery", "DELIVERED");
        statuses.put("Delivered", "COMPLETED");

    }

    public LinkedHashMap<String, String> getStatuses() {
        return statuses;
    }

    public void setStatuses(LinkedHashMap<String, String> statuses) {
        this.statuses = statuses;
    }

    public LinkedHashMap<String, String> getNewStatuses() {
        return newStatuses;
    }

    public void setNewStatuses(LinkedHashMap<String, String> newStatuses) {
        this.newStatuses = newStatuses;
    }
}
