package com.brain_socket.tapdrive.delegates;

/**
 * Created by eyadmhanna on 8/14/17.
 */

public class PermissionGrantedEvent {

    private boolean isPermissionGranted;

    public PermissionGrantedEvent(boolean isPermissionGranted) {
        this.isPermissionGranted = isPermissionGranted;
    }

    public boolean isPermissionGranted() {
        return isPermissionGranted;
    }

    public void setPermissionGranted(boolean permissionGranted) {
        isPermissionGranted = permissionGranted;
    }
}
