package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.MainActivity;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.model.partner.Car;
import com.brain_socket.tapdrive.model.partner.Reservation;
import com.brain_socket.tapdrive.utils.DayDisableDecorator;
import com.brain_socket.tapdrive.utils.Helpers;
import com.bumptech.glide.Glide;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Weeks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    boolean selectedRange = false;

    CalendarDay startDayDate, endDayDate;
    Calendar lastReservationDate;
    int fromHourOfDay = 0, fromMinute = 0, toHourOfDay = 0, toMinute = 0;

    HashMap<String, Pair<Integer, Float>> bookingCostDetailsMap = null;

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

        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_RANGE);

        final ArrayList<CalendarDay> disabledDates = new ArrayList<>();

        for (Reservation reservation : car.getReservations()) {

            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(reservation.getOrderStartDate());
            cal2.setTime(reservation.getOrderEndDate());

            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

            if (sameDay) {
                disabledDates.add(CalendarDay.from(cal1));
            } else {
                while (!cal1.after(cal2)) {
                    disabledDates.add(CalendarDay.from(cal1));
                    cal1.add(Calendar.DATE, 1);
                }
            }

        }

        calendarView.addDecorator(new DayDisableDecorator(disabledDates, getActivity()));

        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {

                disabledDates.clear();

                for (Reservation reservation : car.getReservations()) {

                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(reservation.getOrderStartDate());
                    cal2.setTime(reservation.getOrderEndDate());

                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

                    if (sameDay) {
                        disabledDates.add(CalendarDay.from(cal1));
                    } else {
                        while (!cal1.after(cal2)) {
                            disabledDates.add(CalendarDay.from(cal1));
                            cal1.add(Calendar.DATE, 1);
                        }
                    }

                }

                calendarView.addDecorator(new DayDisableDecorator(disabledDates, getActivity()));

                ArrayList<CalendarDay> selectedDays = new ArrayList<>();
                int index = 0;

                Reservation lastDayReservation = null;

                for (CalendarDay selectedDay : dates) {
                    Iterator<CalendarDay> disabledDaysIterator = disabledDates.iterator();
                    while (disabledDaysIterator.hasNext()) {
                        CalendarDay disabledDay = disabledDaysIterator.next();

                        boolean sameDay = selectedDay.getYear() == disabledDay.getYear() &&
                                selectedDay.getMonth() == disabledDay.getMonth() &&
                                selectedDay.getDay() == disabledDay.getDay();

                        if (!sameDay) {
                            if (index < 1) {
                                selectedDays.add(selectedDay);
                            }
                        } else {
                            if (index < 1) {
                                index++;
                                selectedDays.add(selectedDay);
                                disabledDaysIterator.remove();
                                calendarView.removeDecorators();
                                calendarView.addDecorator(new DayDisableDecorator(disabledDates, getActivity()));
                            } else {
                                break;
                            }
                        }
                    }
                }

                if (!selectedRange) {
                    selectedRange = true;
                    calendarView.selectRange(selectedDays.get(0), selectedDays.get(selectedDays.size() - 1));
                }


                startDayDate = selectedDays.get(0);
                selectedFromDate.setText(selectedDays.get(0).getYear() + "/" + (selectedDays.get(0).getMonth() + 1) + "/" + selectedDays.get(0).getDay() + " " + "00:00");

                for (Reservation reservation : car.getReservations()) {

                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(selectedDays.get(selectedDays.size() - 1).getCalendar().getTime());
                    cal2.setTime(reservation.getOrderStartDate());

                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

                    if (sameDay) {
                        lastDayReservation = reservation;
                        break;
                    }

                }

                if (selectedRange) {
                    if (lastDayReservation != null) {

                        Calendar cal = Calendar.getInstance();
                        cal.setTime(lastDayReservation.getOrderStartDate());
                        cal.add(Calendar.MINUTE, -30);

                        lastReservationDate = cal;
                        toHourOfDay = lastReservationDate.get(Calendar.HOUR_OF_DAY);
                        toMinute = lastReservationDate.get(Calendar.MINUTE);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                        String lastDayReservationOrderStartTime = simpleDateFormat.format(cal.getTime());

                        selectedToDate.setText(cal.get(Calendar.YEAR) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.DAY_OF_MONTH) + " " + lastDayReservationOrderStartTime);

                    } else {

                        lastReservationDate = null;
                        selectedToDate.setText(selectedDays.get(selectedDays.size() - 1).getYear() + "/" + (selectedDays.get(selectedDays.size() - 1).getMonth() + 1) + "/" + selectedDays.get(selectedDays.size() - 1).getDay() + " " + "00:00");

                    }

                    endDayDate = selectedDays.get(selectedDays.size() - 1);
                    calculateCost();
                }

            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                selectedRange = false;

                calendarView.removeDecorators();
                disabledDates.clear();

                for (Reservation reservation : car.getReservations()) {

                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(reservation.getOrderStartDate());
                    cal2.setTime(reservation.getOrderEndDate());

                    boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);

                    if (sameDay) {
                        disabledDates.add(CalendarDay.from(cal1));
                    } else {
                        while (!cal1.after(cal2)) {
                            disabledDates.add(CalendarDay.from(cal1));
                            cal1.add(Calendar.DATE, 1);
                        }
                    }

                }

                calendarView.addDecorator(new DayDisableDecorator(disabledDates, getActivity()));

                startDayDate = date;
                endDayDate = date;
                lastReservationDate = null;

                fromHourOfDay = 0;
                fromMinute = 0;

                toHourOfDay = 1;
                toMinute = 0;

                selectedFromDate.setText(date.getYear() + "/" + (date.getMonth() + 1) + "/" + date.getDay() + " " + "00:00");
                selectedToDate.setText(date.getYear() + "/" + (date.getMonth() + 1) + "/" + date.getDay() + " " + "01:00");

                calculateCost();
            }
        });

    }

    private void calculateCost() {

        bookingCostDetailsMap = new HashMap<>();

        float totalCost = 0.0f;

        //If the booking in the same day, calculate hours cost only
        if (startDayDate.getDay() == endDayDate.getDay()
                && startDayDate.getMonth() == endDayDate.getMonth()
                && startDayDate.getYear() == endDayDate.getYear()) {

            int hoursDifference = toHourOfDay - fromHourOfDay;
            totalCost = hoursDifference * Float.parseFloat(car.getHourlyPrice());

            bookPriceValue.setText(totalCost + getString(R.string.currency));
            bookingCostDetailsMap.put("Months", new Pair<Integer, Float>(0, 0.0f));
            bookingCostDetailsMap.put("Weeks", new Pair<Integer, Float>(0, 0.0f));
            if (hoursDifference == 24) {
                bookingCostDetailsMap.put("Days", new Pair<Integer, Float>(1, Float.parseFloat(car.getDailyPrice())));
            } else {
                bookingCostDetailsMap.put("Days", new Pair<Integer, Float>(0, 0.0f));
            }
            bookingCostDetailsMap.put("Hours", new Pair<Integer, Float>(hoursDifference, totalCost));
            bookingCostDetailsMap.put("total", new Pair<Integer, Float>(0, totalCost));

            return;
        }

        //If the booking is in multiple days, calculate days and hours
        int daysDifference = Days.daysBetween(new DateTime(startDayDate.getCalendar()), new DateTime(endDayDate.getCalendar())).getDays();
        int weeksDifference = Weeks.weeksBetween(new DateTime(startDayDate.getCalendar()), new DateTime(endDayDate.getCalendar())).getWeeks();
        int monthsDifference = Months.monthsBetween(new DateTime(startDayDate.getCalendar()), new DateTime(endDayDate.getCalendar())).getMonths();

        bookingCostDetailsMap.put("Months", new Pair<Integer, Float>(monthsDifference, (monthsDifference * 30) * Float.parseFloat(car.getDailyPrice())));
        while (monthsDifference != 0) {

            monthsDifference -= 1;
            daysDifference -= 30;

        }

        bookingCostDetailsMap.put("Weeks", new Pair<Integer, Float>(weeksDifference, (weeksDifference * 7) * Float.parseFloat(car.getDailyPrice())));
        while (weeksDifference != 0) {

            weeksDifference -= 1;
            daysDifference -= 7;

        }

        bookingCostDetailsMap.put("Days", new Pair<Integer, Float>(daysDifference, daysDifference * Float.parseFloat(car.getDailyPrice())));

        int hoursDifference = toHourOfDay - fromHourOfDay;
        if (hoursDifference == 0) hoursDifference = 1;
        bookingCostDetailsMap.put("Hours", new Pair<Integer, Float>(hoursDifference, hoursDifference * Float.parseFloat(car.getHourlyPrice())));

        Iterator iterator = bookingCostDetailsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry) iterator.next();
            totalCost += ((Pair<Integer, Float>) pair.getValue()).second;
        }
        bookPriceValue.setText(totalCost + getString(R.string.currency));
        bookingCostDetailsMap.put("total", new Pair<Integer, Float>(0, totalCost));

    }

    private void bindData() {

        Glide.with(getActivity()).load(car.getPhoto()).into(itemImage);
        itemName.setText(car.getEnglishName());
        itemDailyPrice.setText(car.getDailyPrice() + getString(R.string.currency));
        itemHourlyPrice.setText(car.getHourlyPrice() + getString(R.string.currency));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.item_drive_now_button, R.id.selected_from_date, R.id.selected_to_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.selected_from_date:
                openTimePicker("from");
                break;

            case R.id.selected_to_date:
                openTimePicker("to");
                break;

            case R.id.item_drive_now_button:
                bookItem();
                break;
        }
    }

    private void bookItem() {

        if (bookingCostDetailsMap != null) {

            Calendar bookingStartDate = startDayDate.getCalendar();
            Calendar bookingEndDate = endDayDate.getCalendar();

            bookingStartDate.set(Calendar.HOUR_OF_DAY, fromHourOfDay);
            bookingStartDate.set(Calendar.MINUTE, fromMinute);

            bookingEndDate.set(Calendar.HOUR_OF_DAY, toHourOfDay);
            bookingEndDate.set(Calendar.MINUTE, toMinute);

            ((MainActivity) getActivity()).openPaymentScreen(bookingCostDetailsMap, car, Helpers.getFormattedDateString(bookingStartDate), Helpers.getFormattedDateString(bookingEndDate));

        } else {

            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.select_booking_period)
                    .setPositiveButton(android.R.string.yes, null)
                    .show();

        }

    }

    private void openTimePicker(final String type) {

        TimePickerDialog dpd = TimePickerDialog.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {

                if (type.equalsIgnoreCase("to")) {
                    if (!checkIfAfterStart(hourOfDay, minute)) {
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(lastReservationDate == null ? getString(R.string.return_after_start) : getString(R.string.return_after_start_and_before_reservation))
                                .setPositiveButton(android.R.string.yes, null)
                                .show();
                        if (hourOfDay != 23) {
                            selectedToDate.setText(endDayDate.getYear() + "/" + (endDayDate.getMonth() + 1) + "/" + endDayDate.getDay() + " " + (fromHourOfDay + 1) + ":" + minute);
                        }
                        return;
                    }
                }

                if (type.equalsIgnoreCase("from")) {
                    fromHourOfDay = hourOfDay;
                    fromMinute = minute;
                    selectedFromDate.setText(startDayDate.getYear() + "/" + (startDayDate.getMonth() + 1) + "/" + startDayDate.getDay() + " " + hourOfDay + ":" + minute);

                    if (hourOfDay != 23) {
                        if (!checkIfAfterStart(toHourOfDay, toMinute)) {
                            toHourOfDay = hourOfDay + 1;
                            toMinute = minute;
                            if (lastReservationDate == null) {
                                selectedToDate.setText(endDayDate.getYear() + "/" + (endDayDate.getMonth() + 1) + "/" + endDayDate.getDay() + " " + toHourOfDay + ":" + minute);
                            } else {
                                selectedToDate.setText(lastReservationDate.get(Calendar.YEAR) + "/" + (lastReservationDate.get(Calendar.MONTH) + 1) + "/" + lastReservationDate.get(Calendar.DAY_OF_MONTH) + " " + toHourOfDay + ":" + minute);
                            }
                        }
                    }

                } else {
                    toHourOfDay = hourOfDay;
                    toMinute = minute;
                    if (lastReservationDate == null) {
                        selectedToDate.setText(endDayDate.getYear() + "/" + (endDayDate.getMonth() + 1) + "/" + endDayDate.getDay() + " " + hourOfDay + ":" + minute);
                    } else {
                        selectedToDate.setText(lastReservationDate.get(Calendar.YEAR) + "/" + (lastReservationDate.get(Calendar.MONTH) + 1) + "/" + lastReservationDate.get(Calendar.DAY_OF_MONTH) + " " + hourOfDay + ":" + minute);
                    }
                }

                calculateCost();

            }
        }, 0, 0, 0, true);
        dpd.show(getActivity().getFragmentManager(), "Timepickerdialog");


    }

    private boolean checkIfAfterStart(int hourOfDay, int minute) {

        if (lastReservationDate != null) {

            if (hourOfDay <= lastReservationDate.get(Calendar.HOUR_OF_DAY)
                    && minute < lastReservationDate.get(Calendar.MINUTE)
                    && hourOfDay >= fromHourOfDay
                    && minute > fromMinute) {
                return true;
            } else {
                return false;
            }

        } else {

//            if (startDayDate.getDay() == endDayDate.getDay()
//                    && startDayDate.getMonth() == endDayDate.getMonth()
//                    && startDayDate.getYear() == endDayDate.getYear()) {

            if (hourOfDay > fromHourOfDay) {

                return true;

            } else {

                if (hourOfDay == fromHourOfDay) {
                    if (minute > fromMinute) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }

            }

//            } else if () else {
//
//                return true;
//
//            }

        }

    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
