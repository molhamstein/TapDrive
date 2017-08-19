package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.delegates.CarBookedEvent;
import com.brain_socket.tapdrive.model.partner.Car;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PaymentFragment extends Fragment {

    @BindView(R.id.visa_image_view)
    ImageView visaImageView;
    @BindView(R.id.visa_button)
    CardView visaButton;
    @BindView(R.id.paypal_image_view)
    ImageView paypalImageView;
    @BindView(R.id.paypal_button)
    CardView paypalButton;
    @BindView(R.id.cash_on_delivery_check_box)
    CheckBox cashOnDeliveryCheckBox;
    @BindView(R.id.months_text_view)
    TextViewCustomFont monthsTextView;
    @BindView(R.id.months_value_text_view)
    TextViewCustomFont monthsValueTextView;
    @BindView(R.id.months_cost_value_text_view)
    TextViewCustomFont monthsCostValueTextView;
    @BindView(R.id.weeks_text_view)
    TextViewCustomFont weeksTextView;
    @BindView(R.id.weeks_value_text_view)
    TextViewCustomFont weeksValueTextView;
    @BindView(R.id.weeks_cost_value_text_view)
    TextViewCustomFont weeksCostValueTextView;
    @BindView(R.id.days_text_view)
    TextViewCustomFont daysTextView;
    @BindView(R.id.days_value_text_view)
    TextViewCustomFont daysValueTextView;
    @BindView(R.id.days_cost_value_text_view)
    TextViewCustomFont daysCostValueTextView;
    @BindView(R.id.hours_text_view)
    TextViewCustomFont hoursTextView;
    @BindView(R.id.hours_value_text_view)
    TextViewCustomFont hoursValueTextView;
    @BindView(R.id.hours_cost_value_text_view)
    TextViewCustomFont hoursCostValueTextView;
    @BindView(R.id.total_cost_value_text_view)
    TextViewCustomFont totalCostValueTextView;
    @BindView(R.id.book_button)
    TextViewCustomFont bookButton;
    @BindView(R.id.loader_view)
    RelativeLayout loaderView;
    Unbinder unbinder;

    private HashMap<String, Pair<Integer, Float>> bookingDetailsHashMap;
    private Car selectedCar;
    private String bookingStartDate;
    private String bookingEndDate;

    public PaymentFragment() {
        // Required empty public constructor
    }

    public static PaymentFragment newInstance(HashMap<String, Pair<Integer, Float>> bookingDetailsHashMap, Car car, String startDate, String endDate) {
        PaymentFragment fragment = new PaymentFragment();
        fragment.setBookingDetailsHashMap(bookingDetailsHashMap);
        fragment.setSelectedCar(car);
        fragment.setBookingStartDate(startDate);
        fragment.setBookingEndDate(endDate);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_payment, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);
        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindData();

    }

    private void bindData() {

        monthsValueTextView.setText(bookingDetailsHashMap.get("Months").first + "");
        monthsCostValueTextView.setText(bookingDetailsHashMap.get("Months").second + " AED");

        weeksValueTextView.setText(bookingDetailsHashMap.get("Weeks").first + "");
        weeksCostValueTextView.setText(bookingDetailsHashMap.get("Weeks").second + " AED");

        daysValueTextView.setText(bookingDetailsHashMap.get("Days").first + "");
        daysCostValueTextView.setText(bookingDetailsHashMap.get("Days").second + " AED");

        hoursValueTextView.setText(bookingDetailsHashMap.get("Hours").first + "");
        hoursCostValueTextView.setText(bookingDetailsHashMap.get("Hours").second + " AED");

        totalCostValueTextView.setText(bookingDetailsHashMap.get("total").second + " AED");

    }

    @OnClick({R.id.book_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.book_button:
                bookItem();
                break;
        }
    }

    private void bookItem() {

        loaderView.setVisibility(View.VISIBLE);
        DataStore.getInstance().bookItem(bookingStartDate, bookingEndDate,
                selectedCar.getId(), selectedCar.getPartnerId(), new DataStore.DataRequestCallback() {
                    @Override
                    public void onDataReady(ServerResult result, boolean success) {

                        loaderView.setVisibility(View.GONE);

                        if (success) {
                            Toast.makeText(getActivity(), "Your item has been booked successfully", Toast.LENGTH_LONG).show();
                            EventBus.getDefault().post(new CarBookedEvent());
                        }

                    }
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public HashMap<String, Pair<Integer, Float>> getBookingDetailsHashMap() {
        return bookingDetailsHashMap;
    }

    public void setBookingDetailsHashMap(HashMap<String, Pair<Integer, Float>> bookingDetailsHashMap) {
        this.bookingDetailsHashMap = bookingDetailsHashMap;
    }

    public Car getSelectedCar() {
        return selectedCar;
    }

    public void setSelectedCar(Car selectedCar) {
        this.selectedCar = selectedCar;
    }

    public String getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(String bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public String getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(String bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }
}
