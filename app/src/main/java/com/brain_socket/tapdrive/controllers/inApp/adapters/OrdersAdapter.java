package com.brain_socket.tapdrive.controllers.inApp.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.viewHolders.OrderItemViewHolder;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.model.orders.Order;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by EYADOOS-PC on 8/5/2017.
 */

public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Order> data;
    private Context context;

    public OrdersAdapter(Context context, ArrayList<Order> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.order_item_layout, parent, false);
        viewHolder = new OrderItemViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((OrderItemViewHolder) holder).bind(getData().get(position));

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
}
