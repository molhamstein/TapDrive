package com.brain_socket.tapdrive.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.brain_socket.tapdrive.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by EYADOOS-PC on 8/2/2017.
 */

public class DayDisableDecorator implements DayViewDecorator {

    private HashSet<CalendarDay> dates;
    private Context context;

    public DayDisableDecorator(Collection<CalendarDay> dates, Context context) {
        this.dates = new HashSet<>(dates);
        this.context = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        Drawable d = context.getResources().getDrawable(R.drawable.disabled_date_background);
        view.setDaysDisabled(true);
        view.setBackgroundDrawable(d);
    }

}