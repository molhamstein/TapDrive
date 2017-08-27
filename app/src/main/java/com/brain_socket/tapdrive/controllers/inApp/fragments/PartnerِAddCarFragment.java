package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.adapters.VehiclesAdapter;
import com.brain_socket.tapdrive.customViews.FilterTypeView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.delegates.FilterSelectedEvent;
import com.brain_socket.tapdrive.model.filters.Category;
import com.brain_socket.tapdrive.model.filters.CategoryField;
import com.brain_socket.tapdrive.model.partner.Car;
import com.github.florent37.viewanimator.ViewAnimator;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Handler;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PartnerِAddCarFragment extends Fragment {

    private enum PRICING_TYPE{MONTHLY, WEEKLY, DAILY, HOURLY}

    @BindView(R.id.stage1_layout)
    LinearLayout stage1Contentlayout;
    @BindView(R.id.title1)
    TextViewCustomFont title1;
    @BindView(R.id.vCarBasicInfo)
    View vCarBasicInfo;
    @BindView(R.id.item_image)
    ImageView itemImage;
    @BindView(R.id.etEnglishName)
    EditText etEnglishName;
    @BindView(R.id.etArabicName)
    EditText etArabicName;
    @BindView(R.id.vSep1)
    View sep1;
    @BindView(R.id.title2)
    TextViewCustomFont title2;
    @BindView(R.id.vPricing)
    View vPricing;
    @BindView(R.id.tvPriceMonthly)
    View tvPriceMonthly;
    @BindView(R.id.tvPriceWeekly)
    View tvPriceWeekly;
    @BindView(R.id.tvPriceDaily)
    View tvPriceDaily;
    @BindView(R.id.tvPriceHourly)
    View tvPriceHourly;
    @BindView(R.id.vSep2)
    View sep2;
    @BindView(R.id.title3)
    TextViewCustomFont title3;
    @BindView(R.id.vCountryInfo)
    View vCountryInfo;
    @BindView(R.id.tvSelectedCountry)
    TextView tvSelectedCountry;
    @BindView(R.id.vCityInfo)
    View vCityInfo;
    @BindView(R.id.tvSelectedCity)
    TextView tvSelectedCity;
    @BindView(R.id.vSep3)
    View sep3;
    @BindView(R.id.btnNext)
    View btnNext;
    // stage 2
    @BindView(R.id.stage2_layout)
    LinearLayout stage2Contentlayout;
    @BindView(R.id.title4)
    View title4;
    @BindView(R.id.filter_types_layout)
    LinearLayout filterTypesHolder;
    @BindView(R.id.btnFinish)
    View btnFinish;

    Unbinder unbinder;

    // filters
    HashMap<FilterTypeView, ArrayList<FilterTypeView>> filterTypeViews = new HashMap<>();


    private ArrayList<View> uiElementsToAnimateInS1;
    private ArrayList<View> uiElementsToAnimateInS2;

    public DataStore.DataRequestCallback carssDataRequestCallback = new DataStore.DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            if (success) {
                ArrayList<Car> orders;
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public PartnerِAddCarFragment() {
        // Required empty public constructor
    }

    public static PartnerِAddCarFragment newInstance() {
        Bundle args = new Bundle();
        PartnerِAddCarFragment fragment = new PartnerِAddCarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_add_car, container, false);
        unbinder = ButterKnife.bind(this, inflatedView);

        return inflatedView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
        DataStore.getInstance().getPartnerCars(carssDataRequestCallback);
        moveToStage(0);
    }

    private void init () {

        if (uiElementsToAnimateInS1 == null) {
            uiElementsToAnimateInS1 = new ArrayList<>();
            uiElementsToAnimateInS2 = new ArrayList<>();
        }

        // views of the first stage
        // add to an array to be animated later
        uiElementsToAnimateInS1.add(title1);
        uiElementsToAnimateInS1.add(vCarBasicInfo);
        uiElementsToAnimateInS1.add(sep1);
        uiElementsToAnimateInS1.add(title2);
        uiElementsToAnimateInS1.add(vPricing);
        uiElementsToAnimateInS1.add(sep2);
        uiElementsToAnimateInS1.add(title3);
        uiElementsToAnimateInS1.add(vCountryInfo);
        uiElementsToAnimateInS1.add(vCityInfo);
        uiElementsToAnimateInS1.add(sep3);
        uiElementsToAnimateInS1.add(btnNext);
        // stage 2
        uiElementsToAnimateInS2.add(title4);
        uiElementsToAnimateInS2.add(filterTypesHolder);
        uiElementsToAnimateInS2.add(btnFinish);

        // init filters list
        filterTypesHolder = (LinearLayout) getView().findViewById(R.id.filter_types_layout);
        ArrayList<Category> categories = DataCacheProvider.getInstance().getStoredCategoriesArray();

        for (Category category : categories) {
            for (CategoryField categoryField : category.getFields()) {

                if (categoryField.getParentFieldId().equalsIgnoreCase("0")) {

                    FilterTypeView filterTypeView = new FilterTypeView(getActivity(), categoryField);
                    filterTypesHolder.addView(filterTypeView);
                    filterTypesHolder.requestLayout();

                    filterTypeViews.put(filterTypeView, null);

                } else {

                    FilterTypeView filterTypeView = new FilterTypeView(this.getActivity(), categoryField, true);
                    filterTypesHolder.addView(filterTypeView);
                    filterTypesHolder.requestLayout();

                    Iterator it = filterTypeViews.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();

                        FilterTypeView parentFilterTypeView = (FilterTypeView) pair.getKey();
                        if (parentFilterTypeView.getCategoryField().getId().equalsIgnoreCase(filterTypeView.getCategoryField().getParentFieldId())) {
                            if (pair.getValue() == null) {
                                ArrayList<FilterTypeView> values = new ArrayList<>();
                                values.add(filterTypeView);
                                filterTypeViews.put(parentFilterTypeView, values);
                            } else {
                                ArrayList<FilterTypeView> values = filterTypeViews.get(parentFilterTypeView);
                                values.add(filterTypeView);
                                filterTypeViews.put(parentFilterTypeView, values);
                            }
                        }
                    }

                }

            }
        }
    }
    private void attempMoveToStage2(){
        //validate data has been collected
        moveToStage(1);
    }

    private void moveToStage(int index){
        if(index == 0){
            stage1Contentlayout.setVisibility(View.VISIBLE);
            stage2Contentlayout.setVisibility(View.GONE);
            if (uiElementsToAnimateInS1 != null) {
                for (int i = 0; i < uiElementsToAnimateInS1.size(); i++) {
                    animateViewIn(uiElementsToAnimateInS1.get(i), ((i + 1) * 90));
                }
            }
        } else {
            // hide views of stage 1
            if (uiElementsToAnimateInS1 != null) {
                for (int i = uiElementsToAnimateInS1.size()-1; i >= 0; i--) {
                    animateViewOut(uiElementsToAnimateInS1.get(i), ((i + 1) * 90));
                }
            }
            // show views of stage 2 after a delay
            android.os.Handler handler = new android.os.Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stage1Contentlayout.setVisibility(View.GONE);
                    stage2Contentlayout.setVisibility(View.VISIBLE);
                    if (uiElementsToAnimateInS2 != null) {
                        for (int i = 0; i < uiElementsToAnimateInS2.size(); i++) {
                            animateViewIn(uiElementsToAnimateInS2.get(i), ((i + 1) * 130));
                        }
                    }
                }
            },1500);

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectedFilterValueEvent(FilterSelectedEvent filterSelectedEvent) {

        if (filterTypeViews.get(filterSelectedEvent.getFilterTypeView()) == null) {
            return;
        } else {
            for (FilterTypeView filterTypeView : filterTypeViews.get(filterSelectedEvent.getFilterTypeView())) {
                filterTypeView.clearSelected();
                filterTypeView.setHidden(false);
                filterTypeView.setParentOptionId(filterSelectedEvent.getParentOptionId());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    // animate views
    private void animateViewIn(View v, int delay) {
        v.setAlpha(0);
        ViewAnimator.animate(v).startDelay(delay).dp().translationY(60, 0).alpha(0, 1).duration(400)
                .interpolator(new OvershootInterpolator(0.9f))
                .start();
    }

    private void animateViewOut(View v, int delay) {
        ViewAnimator.animate(v).startDelay(delay).dp().translationY(0, 60).alpha(1, 0).duration(400)
                .interpolator(new OvershootInterpolator(0.9f))
                .start();
    }

    private void pickCity() {

    }

    private void pickCountry() {

    }

    private void pickPricing(PRICING_TYPE pricingType) {

    }

    @OnClick({R.id.btnHourly,R.id.btnDaily,R.id.btnWeekly,R.id.btnMonthly,R.id.btnNext,R.id.btnFinish,R.id.vCityInfo,R.id.vCountryInfo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.vCityInfo:
                pickCity();
                break;
            case R.id.vCountryInfo:
                pickCountry();
                break;
            case R.id.btnMonthly:
                pickPricing(PRICING_TYPE.MONTHLY);
                break;
            case R.id.btnWeekly:
                pickPricing(PRICING_TYPE.WEEKLY);
                break;
            case R.id.btnDaily:
                pickPricing(PRICING_TYPE.DAILY);
                break;
            case R.id.btnHourly:
                pickPricing(PRICING_TYPE.HOURLY);
                break;
            case R.id.btnNext:
                attempMoveToStage2();
                break;
            case R.id.btnFinish:
                break;
        }
    }
}
