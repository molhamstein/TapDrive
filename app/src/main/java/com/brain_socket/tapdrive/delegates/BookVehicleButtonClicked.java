package com.brain_socket.tapdrive.delegates;

import com.brain_socket.tapdrive.model.partner.Car;

/**
 * Created by EYADOOS-PC on 7/31/2017.
 */

public class BookVehicleButtonClicked {

    private Car car;

    public BookVehicleButtonClicked(Car car) {
        this.car = car;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
