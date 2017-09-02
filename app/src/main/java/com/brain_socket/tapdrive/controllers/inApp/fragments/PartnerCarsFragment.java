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
import com.brain_socket.tapdrive.controllers.inApp.adapters.VehiclesAdapter;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.partner.Car;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PartnerCarsFragment extends Fragment {

    @BindView(R.id.cars_recycler_view)
    RecyclerView dataRecyclerView;
    @BindView(R.id.loader_view)
    ProgressBar loaderView;
    @BindView(R.id.empty_data_text_view)
    TextViewCustomFont emptyTextView;
    Unbinder unbinder;

    VehiclesAdapter carsAdapter;

    public PartnerCarsFragment() {
        // Required empty public constructor
    }

    public static PartnerCarsFragment newInstance() {
        Bundle args = new Bundle();
        PartnerCarsFragment fragment = new PartnerCarsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_partner_cars, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DataStore.getInstance().getPartnerCars(carssDataRequestCallback);

        carsAdapter = new VehiclesAdapter(getActivity(), new ArrayList<Car>(), false);
        carsAdapter.notifyDataSetChanged();
        dataRecyclerView.setAdapter(carsAdapter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public DataStore.DataRequestCallback carssDataRequestCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            if (success) {
                ArrayList<Car> cars;
                try {

                    if (result.getPairs().containsKey("cars")) {
                        cars = new ArrayList<>();
                        @SuppressWarnings("unchecked")
                        ArrayList<Car> receivedCars = (ArrayList<Car>) result.get("cars");
                        cars.addAll(receivedCars);

                        updateDataAdapter(cars);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void updateDataAdapter(ArrayList<Car> cars) {

        loaderView.setVisibility(View.GONE);

        if (cars.size() > 0) {
            carsAdapter.setData(cars);
            carsAdapter.notifyDataSetChanged();
        } else {
            dataRecyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        }

    }

}
