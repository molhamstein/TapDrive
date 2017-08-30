package com.brain_socket.tapdrive.controllers.inApp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.viewHolders.OrderItemViewHolder;
import com.brain_socket.tapdrive.controllers.inApp.viewHolders.ServerNotificationsItemViewHolder;
import com.brain_socket.tapdrive.model.orders.Order;
import com.brain_socket.tapdrive.model.orders.ServerNotification;

import java.util.ArrayList;

/**
 * Created by EYADOOS-PC on 8/8/2017.
 */

public class ServerNotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ServerNotification> data;
    private Context context;

    public ServerNotificationsAdapter(Context context, ArrayList<ServerNotification> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.notifications_item_layout, parent, false);
        viewHolder = new ServerNotificationsItemViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((ServerNotificationsItemViewHolder) holder).bind(getData().get(position));

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public ArrayList<ServerNotification> getData() {
        return data;
    }

    public void setData(ArrayList<ServerNotification> data) {
        this.data = data;
    }
}
