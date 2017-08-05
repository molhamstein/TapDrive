package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.adapters.OrdersAdapter;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.orders.Order;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TripHistoryFragment extends Fragment {


    @BindView(R.id.orders_recycler_view)
    RecyclerView ordersRecyclerView;
    @BindView(R.id.loader_view)
    ProgressBar loaderView;
    @BindView(R.id.empty_data_text_view)
    TextViewCustomFont emptyTextView;
    Unbinder unbinder;

    OrdersAdapter ordersAdapter;

    public TripHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_trip_history, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataStore.getInstance().getOrders(ordersDataRequestCallback);

        ordersAdapter = new OrdersAdapter(getActivity(), new ArrayList<Order>());
        ordersAdapter.notifyDataSetChanged();
        ordersRecyclerView.setAdapter(ordersAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public DataStore.DataRequestCallback ordersDataRequestCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            if (success) {
                ArrayList<Order> orders;
                try {
                    if (result.getPairs().containsKey("orders")) {
                        orders = new ArrayList<>();
                        ArrayList<Order> receivedOrders = (ArrayList<Order>) result.get("orders");
                        orders.addAll(receivedOrders);

                        updateDataAdapter(orders);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void updateDataAdapter(ArrayList<Order> orders) {

        loaderView.setVisibility(View.GONE);

        if (orders.size() > 0) {
            ordersAdapter.setData(orders);
            ordersAdapter.notifyDataSetChanged();
        } else {
            ordersRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }

    }

}
