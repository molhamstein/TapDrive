package com.brain_socket.tapdrive.controllers.inApp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.viewHolders.VehicleItemViewHolder;
import com.brain_socket.tapdrive.model.partner.Car;

import java.util.ArrayList;

/**
 * Created by MhannaCloudAppers on 7/20/2017.
 */

public class VehiclesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Car> data = new ArrayList<>();
    private Context context;

    public VehiclesAdapter(Context context, ArrayList<Car> data) {
        this.context = context;
        this.setData(data);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.vehicle_item_layout, parent, false);
        viewHolder = new VehicleItemViewHolder(context, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ((VehicleItemViewHolder) holder).bindData(getData().get(position));

    }

    @Override
    public int getItemCount() {
        return getData().size();
    }

    public ArrayList<Car> getData() {
        return data;
    }

    public void setData(ArrayList<Car> data) {
        this.data = data;
    }
}
