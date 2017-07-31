package com.brain_socket.tapdrive.delegates;

import com.brain_socket.tapdrive.customViews.FilterTypeView;
import com.brain_socket.tapdrive.model.filters.CategoryField;

/**
 * Created by EYADOOS-PC on 7/31/2017.
 */

public class FilterSelectedEvent {

    private FilterTypeView filterTypeView;
    private String parentOptionId;

    public FilterSelectedEvent(FilterTypeView filterTypeView, String parentOptionId) {
        this.filterTypeView = filterTypeView;
        this.parentOptionId = parentOptionId;
    }

    public FilterTypeView getFilterTypeView() {
        return filterTypeView;
    }

    public void setFilterTypeView(FilterTypeView filterTypeView) {
        this.filterTypeView = filterTypeView;
    }

    public String getParentOptionId() {
        return parentOptionId;
    }

    public void setParentOptionId(String parentOptionId) {
        this.parentOptionId = parentOptionId;
    }
}
