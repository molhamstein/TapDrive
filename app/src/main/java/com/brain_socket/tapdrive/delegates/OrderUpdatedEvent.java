package com.brain_socket.tapdrive.delegates;

import com.brain_socket.tapdrive.model.orders.Order;

/**
 * Created by eyadmhanna on 8/20/17.
 */

public class OrderUpdatedEvent {

    private Order updatedOrder;
    private int position;

    public OrderUpdatedEvent(Order updatedOrder, int position) {
        this.updatedOrder = updatedOrder;
        this.position = position;
    }

    public Order getUpdatedOrder() {
        return updatedOrder;
    }

    public void setUpdatedOrder(Order updatedOrder) {
        this.updatedOrder = updatedOrder;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
