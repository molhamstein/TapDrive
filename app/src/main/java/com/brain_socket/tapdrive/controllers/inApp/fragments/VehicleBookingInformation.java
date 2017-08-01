package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.model.partner.Car;
import com.bumptech.glide.Glide;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class VehicleBookingInformation extends Fragment {

    @BindView(R.id.title)
    TextViewCustomFont title;
    @BindView(R.id.item_image)
    RoundedImageView itemImage;
    @BindView(R.id.item_name)
    TextViewCustomFont itemName;
    @BindView(R.id.item_daily_price)
    TextViewCustomFont itemDailyPrice;
    @BindView(R.id.item_hourly_price)
    TextViewCustomFont itemHourlyPrice;
    @BindView(R.id.calendar_view)
    MaterialCalendarView calendarView;
    @BindView(R.id.selected_from_date)
    TextViewCustomFont selectedFromDate;
    @BindView(R.id.selected_to_date)
    TextViewCustomFont selectedToDate;
    @BindView(R.id.book_price_value)
    TextViewCustomFont bookPriceValue;
    @BindView(R.id.item_drive_now_button)
    TextViewCustomFont itemDriveNowButton;
    @BindView(R.id.content_layout)
    LinearLayout contentLayout;
    Unbinder unbinder;

    private Car car;

    public VehicleBookingInformation() {
        // Required empty public constructor
    }

    public static VehicleBookingInformation newInstance(Car car) {
        VehicleBookingInformation fragment = new VehicleBookingInformation();
        fragment.setCar(car);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_vehicle_booking_information, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindData();

        initCalendar();

    }

    private void initCalendar() {

        Calendar calendar = Calendar.getInstance();

        calendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setMinimumDate(CalendarDay.from(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)))
                .setMaximumDate(CalendarDay.from(calendar.get(Calendar.YEAR) + 1, calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

    }

    private void bindData() {

        Glide.with(getActivity()).load(car.getPhoto()).into(itemImage);
        itemName.setText(car.getEnglishName());
        itemDailyPrice.setText("Daily Price: " + car.getDailyPrice() + " AED");
        itemHourlyPrice.setText("Hourly Price: " + car.getHourlyPrice() + " AED");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.item_drive_now_button)
    public void onViewClicked() {
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
