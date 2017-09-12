package com.brain_socket.tapdrive.controllers.inApp.fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.partner.Car;
import com.mvc.imagepicker.ImagePicker;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.adapters.VehiclesAdapter;
import com.brain_socket.tapdrive.customViews.FilterTypeView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.delegates.FilterSelectedEvent;
import com.brain_socket.tapdrive.model.filters.Category;
import com.brain_socket.tapdrive.model.filters.CategoryField;
import com.brain_socket.tapdrive.model.partner.City;
import com.brain_socket.tapdrive.model.partner.Country;
import com.brain_socket.tapdrive.utils.TapApp;
import com.bumptech.glide.Glide;
import com.github.florent37.viewanimator.ViewAnimator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class PartnerِAddCarFragment extends Fragment {

    private enum PRICING_TYPE{MONTHLY, WEEKLY, DAILY, HOURLY}

    private static final int MAX_PRICE = 1000;

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
    TextView tvPriceMonthly;
    @BindView(R.id.tvPriceWeekly)
    TextView tvPriceWeekly;
    @BindView(R.id.tvPriceDaily)
    TextView tvPriceDaily;
    @BindView(R.id.tvPriceHourly)
    TextView tvPriceHourly;
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

    //private ImagePicker imagePicker;
    //private CameraImagePicker cameraImagePicker;

    private String selectedProfileImagePath;
    private String categoryOptionsIds;
    private ArrayList<String> arrayOptionsIds;
    private float monthlyPrice;
    private float weeklyPrice;
    private float dailyPrice;
    private float hourlyPrice;
    private Country selectedCountry;
    private City selectedCity;
    private Bitmap selectedImage;
    private String selectedImagePath;

    private ArrayList<Country> arrayCountries;

    private boolean requiredPermissionGranted = false;

    private ArrayList<View> uiElementsToAnimateInS1;
    private ArrayList<View> uiElementsToAnimateInS2;

    private Dialog loadingDialog;

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
        updateViews();
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

        loadingDialog = TapApp.getNewLoadingDilaog(getActivity());
        checkForPermission();

        arrayCountries = DataStore.getInstance().getCountries();
    }

    private void updateViews() {
        if(monthlyPrice > 0)
            tvPriceMonthly.setText(String.valueOf(monthlyPrice));
        else
            tvPriceMonthly.setText("-");

        if(weeklyPrice > 0)
            tvPriceWeekly.setText(String.valueOf(weeklyPrice));
        else
            tvPriceWeekly.setText("-");

        if(dailyPrice > 0)
            tvPriceDaily.setText(String.valueOf(dailyPrice));
        else
            tvPriceDaily.setText("-");

        if(hourlyPrice > 0)
            tvPriceHourly.setText(String.valueOf(hourlyPrice));
        else
            tvPriceHourly.setText("-");

        if(selectedCountry != null)
            tvSelectedCountry.setText(selectedCountry.getName());

        if(selectedCity != null)
            tvSelectedCity.setText(selectedCity.getName());
    }

    private void attempMoveToStage2(){
        //validate data has been collected
        String arName = etArabicName.getText().toString();
        String enName = etEnglishName.getText().toString();
        collectCarOptions();

        if (monthlyPrice <= 0 || weeklyPrice <= 0 || dailyPrice <= 0 || hourlyPrice<= 0){
            TapApp.toast(getString(R.string.add_car_price_not_set));
            return;
        }

        if (arName == null || arName.isEmpty() || enName == null || enName.isEmpty()){
            TapApp.toast(getString(R.string.add_car_name_not_set));
            return;
        }

        if (selectedCountry == null) {
            TapApp.toast(getString(R.string.add_car_info_not_set));
            return;
        }

        if (selectedCity == null) {
            TapApp.toast(getString(R.string.add_car_info_not_set));
            return;
        }

        if (selectedImagePath == null) {
            TapApp.toast(getString(R.string.add_car_image_not_set));
            return;
        }

        moveToStage(1);
    }

    private void attempCreateCar(){
        ArrayList<Category> categories = DataCacheProvider.getInstance().getStoredCategoriesArray();
        if(categories != null && categories.size() > 0){
            Category carsCategory = categories.get(0);
            if(carsCategory.getFields().size() != arrayOptionsIds.size()) {
                TapApp.toast(getString(R.string.add_car_info_not_set));
                return;
            }
        }

        ArrayList<CategoryField> fields = new ArrayList<>();
        ArrayList<String> optionsIds = new ArrayList<>();
        Iterator it = filterTypeViews.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            FilterTypeView parentFilterTypeView = (FilterTypeView) pair.getKey();
            fields.add(parentFilterTypeView.getCategoryField());
            optionsIds.add(parentFilterTypeView.getSelectedItem().second);

        }

        Car car = new Car();
        car.setArabicName(etArabicName.getText().toString());
        car.setEnglishName(etEnglishName.getText().toString());
        car.setHourlyPrice(String.valueOf(hourlyPrice));
        car.setDailyPrice(String.valueOf(dailyPrice));
        car.setWeeklyPrice(String.valueOf(weeklyPrice));
        car.setMonthlyPrice(String.valueOf(monthlyPrice));

        loadingDialog.show();
        DataStore.getInstance().createCar(car, fields, optionsIds, selectedImagePath, new DataStore.DataRequestCallback() {
            @Override
            public void onDataReady(ServerResult result, boolean success) {
                loadingDialog.dismiss();
                if(success) {
                    // TODO close fragment
                } else {
                    TapApp.toast(getString(R.string.err_connection));
                }
            }
        });
    }

    private void moveToStage(int index){
        if(index == 0){
            stage1Contentlayout.setVisibility(View.VISIBLE);
            stage2Contentlayout.setVisibility(View.GONE);
            if (uiElementsToAnimateInS1 != null) {
                for (int i = 0; i < uiElementsToAnimateInS1.size(); i++) {
                    animateViewIn(uiElementsToAnimateInS1.get(i), ((i + 1) * 60));
                }
            }
        } else {
            // hide views of stage 1
            if (uiElementsToAnimateInS1 != null) {
                for (int i = uiElementsToAnimateInS1.size()-1; i >= 0; i--) {
                    animateViewOut(uiElementsToAnimateInS1.get(i), ((i + 1) * 60));
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

    private void openImagePicker() {

        com.mvc.imagepicker.ImagePicker.pickImage(this, getString(R.string.select_image_text));

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

    private void collectCarOptions() {
        categoryOptionsIds = "";
        arrayOptionsIds = new ArrayList<>();

        StringBuilder optionsIds = new StringBuilder();
        Iterator it = filterTypeViews.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            FilterTypeView parentFilterTypeView = (FilterTypeView) pair.getKey();

            if (pair.getValue() == null) {
                if (parentFilterTypeView.getSelectedItem() != null) {
                    optionsIds.append(parentFilterTypeView.getSelectedItem().second);
                    optionsIds.append(",");
                    arrayOptionsIds.add(parentFilterTypeView.getSelectedItem().second);
                }
            } else {
                if (parentFilterTypeView.getSelectedItem() != null) {
                    optionsIds.append(parentFilterTypeView.getSelectedItem().second);
                    optionsIds.append(",");
                    arrayOptionsIds.add(parentFilterTypeView.getSelectedItem().second);
                }

                ArrayList<FilterTypeView> values = filterTypeViews.get(parentFilterTypeView);
                for (FilterTypeView filterTypeView : values) {
                    if (filterTypeView.getSelectedItem() != null) {
                        optionsIds.append(filterTypeView.getSelectedItem().second);
                        optionsIds.append(",");
                        arrayOptionsIds.add(parentFilterTypeView.getSelectedItem().second);
                    }
                }
            }
        }

        if (optionsIds.length() > 0) {
            categoryOptionsIds = optionsIds.toString().substring(0, optionsIds.toString().length() - 1);
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
        ViewAnimator.animate(v).startDelay(delay).dp().translationY(50, 0).alpha(0, 1).duration(350)
                .interpolator(new OvershootInterpolator(4f))
                .start();
    }

    private void animateViewOut(View v, int delay) {
        ViewAnimator.animate(v).startDelay(delay).dp().translationY(0, 50).alpha(1, 0).duration(300)
                .interpolator(new OvershootInterpolator(4f))
                .start();
    }

    private void pickCity() {
        if (selectedCountry == null) {
            pickCountry();
            return;
        }

        String diagTitle = getString(R.string.add_car_city);

        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < selectedCountry.getCities().size(); i++) {
            data.add(selectedCountry.getCities().get(i).getName());
        }

        new MaterialDialog.Builder(getContext())
                .title(diagTitle)
                .items(data)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        selectedCity = selectedCountry.getCities().get(which);
                        updateViews();
                        return true;
                    }
                })
                .positiveText(getString(R.string.choose_button_text))
                .show();
    }

    private void pickCountry() {
        String diagTitle = getString(R.string.add_car_country);

        ArrayList<String> data = new ArrayList<>();
        for (int i = 0; i < arrayCountries.size(); i++) {
            data.add(arrayCountries.get(i).getName());
        }

        new MaterialDialog.Builder(getContext())
                .title(diagTitle)
                .items(data)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        selectedCountry = arrayCountries.get(which);
                        updateViews();
                        return true;
                    }
                })
                .positiveText(getString(R.string.choose_button_text))
                .show();
    }

    private void pickPricing(final PRICING_TYPE pricingType) {

        String diagTitle = "";

        ArrayList<String> data = new ArrayList<String>();
        int maxPrice = MAX_PRICE;
        for (int i = 1; i < maxPrice; i++) {
            data.add(Integer.toString(i));
        }

        new MaterialDialog.Builder(getContext())
                .title(diagTitle)
                .items(data)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (text != null) {
                            float value = Float.valueOf(String.valueOf(text));
                            switch (pricingType){
                                case MONTHLY:
                                    monthlyPrice = value;
                                    break;
                                case WEEKLY:
                                    weeklyPrice = value;
                                    break;
                                case DAILY:
                                    dailyPrice = value;
                                    break;
                                case HOURLY:
                                    hourlyPrice = value;
                                    break;
                            }
                        }
                        updateViews();
                        return true;
                    }
                })
                .positiveText(getString(R.string.choose_button_text))
                .show();
    }

    private void checkForPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //File write logic here
                requiredPermissionGranted = true;
                return;
            } else {
                getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        } else {
            requiredPermissionGranted = true;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == Picker.PICK_IMAGE_DEVICE) {
//            imagePicker.submit(data);
//        } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
//            cameraImagePicker.submit(data);
//        }
        Bitmap bitmap = com.mvc.imagepicker.ImagePicker.getImageFromResult(getActivity(), requestCode, resultCode, data);
        if(bitmap != null ) {
            selectedImage = bitmap;
            itemImage.setImageBitmap(bitmap);
            selectedImagePath = ImagePicker.getImagePathFromResult(getActivity(), requestCode, resultCode, data);
        }
    }

    @OnClick({R.id.btnHourly,R.id.btnDaily,R.id.btnWeekly,R.id.btnMonthly,R.id.btnNext,R.id.btnFinish,R.id.vCityInfo,R.id.vCountryInfo, R.id.item_image})
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
                attempCreateCar();
                break;
            case R.id.item_image:
                openImagePicker();
                break;
        }
    }
}
