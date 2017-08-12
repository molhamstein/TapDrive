package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.adapters.OrdersAdapter;
import com.brain_socket.tapdrive.controllers.inApp.adapters.ServerNotificationsAdapter;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.orders.Order;
import com.brain_socket.tapdrive.model.orders.ServerNotification;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class TripHistoryFragment extends Fragment {

    public static final int ORDERS_SCREEN_TYPE = 0;
    public static final int NOTIFICATIONS_SCREEN_TYPE = 1;

    @BindView(R.id.orders_recycler_view)
    RecyclerView dataRecyclerView;
    @BindView(R.id.loader_view)
    ProgressBar loaderView;
    @BindView(R.id.empty_data_text_view)
    TextViewCustomFont emptyTextView;
    Unbinder unbinder;

    OrdersAdapter ordersAdapter;
    ServerNotificationsAdapter serverNotificationsAdapter;
    private int screenType;

    public TripHistoryFragment() {
        // Required empty public constructor
    }

    public static TripHistoryFragment newInstance(int screenType) {
        Bundle args = new Bundle();
        TripHistoryFragment fragment = new TripHistoryFragment();
        fragment.setArguments(args);
        fragment.setScreenType(screenType);
        return fragment;
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

        if (screenType == ORDERS_SCREEN_TYPE) {

            DataStore.getInstance().getOrders(ordersDataRequestCallback);

            ordersAdapter = new OrdersAdapter(getActivity(), new ArrayList<Order>());
            ordersAdapter.notifyDataSetChanged();
            dataRecyclerView.setAdapter(ordersAdapter);

        } else {

            DataStore.getInstance().getServerNotifications(serverNotificationsDataRequestCallback);

            serverNotificationsAdapter = new ServerNotificationsAdapter(getActivity(), new ArrayList<ServerNotification>());
            serverNotificationsAdapter.notifyDataSetChanged();
            dataRecyclerView.setAdapter(serverNotificationsAdapter);

        }

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

    public DataStore.DataRequestCallback serverNotificationsDataRequestCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            if (success) {
                ArrayList<ServerNotification> serverNotifications;
                try {
                    if (result.getPairs().containsKey("server_notifications")) {
                        serverNotifications = new ArrayList<>();
                        ArrayList<ServerNotification> receivedServerNotifications = (ArrayList<ServerNotification>) result.get("server_notifications");
                        serverNotifications.addAll(receivedServerNotifications);

                        updateServerNotificationsDataAdapter(serverNotifications);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void updateServerNotificationsDataAdapter(ArrayList<ServerNotification> serverNotifications) {

        loaderView.setVisibility(View.GONE);

        if (serverNotifications.size() > 0) {
            serverNotificationsAdapter.setData(serverNotifications);
            serverNotificationsAdapter.notifyDataSetChanged();
        } else {
            dataRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }

        

    }

    private void updateDataAdapter(ArrayList<Order> orders) {

        loaderView.setVisibility(View.GONE);

        if (orders.size() > 0) {
            ordersAdapter.setData(orders);
            ordersAdapter.notifyDataSetChanged();
        } else {
            dataRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }

    }

    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
    }
}
