package com.brain_socket.tapdrive.model.filters;

/**
 * Created by EYADOOS-PC on 8/1/2017.
 */

public class MapFilters {

    private int priceFrom;
    private int priceTo;
    private String categoryOptionsIds;

    public MapFilters() {
        this.clear();
    }

    public MapFilters(int priceFrom, int priceTo, String categoryOptionsIds) {
        this.priceFrom = priceFrom;
        this.priceTo = priceTo;
        this.categoryOptionsIds = categoryOptionsIds;
    }

    public void clear() {
        priceFrom = 0;
        priceTo = 1000;
        categoryOptionsIds = "";
    }

    public boolean isEmptyOptions() {
        if (getCategoryOptionsIds() == null) {
            return false;
        }

        return getCategoryOptionsIds().equalsIgnoreCase("");
    }

    public int getPriceFrom() {
        return priceFrom;
    }

    public void setPriceFrom(int priceFrom) {
        this.priceFrom = priceFrom;
    }

    public int getPriceTo() {
        return priceTo;
    }

    public void setPriceTo(int priceTo) {
        this.priceTo = priceTo;
    }

    public String getCategoryOptionsIds() {
        return categoryOptionsIds;
    }

    public void setCategoryOptionsIds(String categoryOptionsIds) {
        this.categoryOptionsIds = categoryOptionsIds;
    }
}
