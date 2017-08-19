package com.brain_socket.tapdrive.model.orders;

import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

/**
 * Created by EYADOOS-PC on 8/8/2017.
 */

public class ServerNotification extends AppBaseModel {

    private String id;
    private String type;
    @SerializedName("new")
    private boolean isNewNotification;
    private UserModel actor;
    private Order object;

    public static ServerNotification fromJson(JSONObject json) {
        try {
            ServerNotification serverNotification = TapApp.getSharedGsonParser().fromJson(json.toString(), ServerNotification.class);
            return serverNotification;
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return new ServerNotification();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isNewNotification() {
        return isNewNotification;
    }

    public void setNewNotification(boolean newNotification) {
        isNewNotification = newNotification;
    }

    public UserModel getActor() {
        return actor;
    }

    public void setActor(UserModel actor) {
        this.actor = actor;
    }

    public Order getObject() {
        return object;
    }

    public void setObject(Order object) {
        this.object = object;
    }
}
