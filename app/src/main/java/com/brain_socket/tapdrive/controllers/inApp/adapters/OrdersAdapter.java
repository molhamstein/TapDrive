package com.brain_socket.tapdrive.controllers.inApp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.viewHolders.OrderItemViewHolder;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.delegates.OrderUpdatedEvent;
import com.brain_socket.tapdrive.model.orders.Order;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by EYADOOS-PC on 8/5/2017.
 */

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Order> data;
    private Context context;
    private boolean isPartner;

    public OrdersAdapter(Context context, ArrayList<Order> data, boolean isPartner) {
        this.data = data;
        this.context = context;
        this.isPartner = isPartner;
        EventBus.getDefault().register(this);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (isPartner) {
            View view = inflater.inflate(R.layout.partner_order_item_layout, parent, false);
            viewHolder = new OrderItemViewHolder(context, view);
        } else {
            View view = inflater.inflate(R.layout.order_item_layout, parent, false);
            viewHolder = new OrderItemViewHolder(context, view);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((OrderItemViewHolder) holder).bind(getData().get(position), isPartner, position);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<Order> getData() {
        return data;
    }

    public void setData(ArrayList<Order> data) {
        this.data = data;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onItemStatusChanges(OrderUpdatedEvent orderUpdatedEvent) {
        getData().get(orderUpdatedEvent.getPosition()).setStatus(orderUpdatedEvent.getUpdatedOrder().getStatus());
        this.notifyItemChanged(orderUpdatedEvent.getPosition());
    }

}
