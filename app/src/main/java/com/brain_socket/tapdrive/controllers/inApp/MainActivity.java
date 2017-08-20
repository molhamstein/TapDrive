package com.brain_socket.tapdrive.controllers.inApp;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.fragments.MapFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.PartnerForgetPassword;
import com.brain_socket.tapdrive.controllers.inApp.fragments.PartnerLoginFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.PaymentFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.ProfileFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.SettingsFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.TripHistoryFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.VehicleBookingInformation;
import com.brain_socket.tapdrive.controllers.onBoarding.SplashScreen;
import com.brain_socket.tapdrive.customViews.FilterTypeView;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.delegates.BookVehicleButtonClicked;
import com.brain_socket.tapdrive.delegates.CarBookedEvent;
import com.brain_socket.tapdrive.delegates.FilterSelectedEvent;
import com.brain_socket.tapdrive.delegates.PermissionGrantedEvent;
import com.brain_socket.tapdrive.model.filters.Category;
import com.brain_socket.tapdrive.model.filters.CategoryField;
import com.brain_socket.tapdrive.model.filters.MapFilters;
import com.brain_socket.tapdrive.model.partner.Car;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.brain_socket.tapdrive.utils.CustomTypefaceSpan;
import com.brain_socket.tapdrive.utils.LocalizationHelper;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DataStore.DataStoreUpdateListener {

    private static String TAG_MAIN_MAP_FRAG = "mainMapFrag";
    private static String TAG_BOOKING_INFORMATION_FRAG = "bookingInformationFrag";
    private static String TAG_TRIP_HISTORY_FRAG = "tripHistoryFrag";
    private static String TAG_SETTINGS_FRAG = "settingsFrag";
    private static String TAG_PROFILE_FRAG = "profileFrag";
    private static String TAG_PAYMENT_FRAG = "paymentFrag";
    private static String TAG_NOTIFICATIONS_FRAG = "notificationsFrag";
    private static String TAG_PARTNER_LOGIN_FRAG = "partnerLoginFrag";

    Fragment fragment;
    FragmentManager fragmentManager;
    DrawerLayout drawer;
    View rlMainContent;
    Toolbar toolbar;

    View filtersView;
    ImageView toggleFiltersButton;
    LinearLayout filterTypesHolder;
    boolean isFiltersOpen = false;

    HashMap<FilterTypeView, ArrayList<FilterTypeView>> filterTypeViews = new HashMap<>();
    private MapFilters mapFilters = null;

    TextViewCustomFont toolbarTitle;
    ImageView toolbarLogo;
    TextViewCustomFont logoutButton;
    TextViewCustomFont partnerQuestionButton;
    ImageView headerLogoImageView;

    UserModel me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataStore.getInstance().addUpdateBroadcastListener(this);
        init();
        showMap();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isFiltersOpen) {
            toggleFiltersView();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {
        toolbarTitle = (TextViewCustomFont) findViewById(R.id.toolbar_title);
        toolbarLogo = (ImageView) findViewById(R.id.toolbar_logo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rlMainContent = findViewById(R.id.rlMainContent);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setScrimColor(Color.TRANSPARENT);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                //slide main content with the drawer

                String locale = LocalizationHelper.getCurrentLocale();
                if (!locale.equalsIgnoreCase("")) {
                    if (locale.equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                        rlMainContent.setTranslationX(slideOffset * drawerView.getWidth());
                    } else {
                        rlMainContent.setTranslationX(-(slideOffset * drawerView.getWidth()));

                    }
                } else {
                    if (LocalizationHelper.getDeviceLocale().equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                        rlMainContent.setTranslationX(slideOffset * drawerView.getWidth());
                    } else {
                        rlMainContent.setTranslationX(-(slideOffset * drawerView.getWidth()));

                    }
                }

                //rlMainContent.setScaleX(1.0f - (slideOffset * 0.05f));
                rlMainContent.setScaleY(1.0f - (slideOffset * 0.05f));
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();

                if (isFiltersOpen) {
                    toggleFiltersView();
                }
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    toggleFiltersButton.setVisibility(View.GONE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);// show back button
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            onBackPressed();
                        }
                    });
                } else {
                    //show hamburger
                    toolbarTitle.setVisibility(View.GONE);
                    toolbarLogo.setVisibility(View.VISIBLE);
                    toggleFiltersButton.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.syncState();
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    });
                }
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu navigationViewMenu = navigationView.getMenu();
        for (int i = 0; i < navigationViewMenu.size(); i++) {
            MenuItem mi = navigationViewMenu.getItem(i);
            applyFontToMenuItem(mi);
        }

        // hide default app logo and name on the left of the toolbar
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        View headerLayout = navigationView.getHeaderView(0);
        CircleImageView profilePicture = (CircleImageView) headerLayout.findViewById(R.id.ivProfilePic);
        TextView userName = (TextView) headerLayout.findViewById(R.id.tvUserName);
        TextView userType = (TextView) headerLayout.findViewById(R.id.user_type_text_view);
        headerLogoImageView = (ImageView) headerLayout.findViewById(R.id.header_logo_image_view);
        logoutButton = (TextViewCustomFont) findViewById(R.id.btnLogout);
        partnerQuestionButton = (TextViewCustomFont) findViewById(R.id.partner_question_button);

        me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, UserModel.class);

        if (me != null) {
            userName.setVisibility(View.VISIBLE);
            profilePicture.setVisibility(View.VISIBLE);
            userType.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);

            headerLogoImageView.setVisibility(View.GONE);

            userName.setText(me.getUsername());
            Glide.with(getApplicationContext()).load(me.getPhoto()).into(profilePicture);
        } else {
            profilePicture.setVisibility(View.GONE);
            userName.setVisibility(View.GONE);
            userType.setVisibility(View.GONE);
            logoutButton.setVisibility(View.GONE);

            headerLogoImageView.setVisibility(View.VISIBLE);
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.logout_confirmation)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                DataStore.getInstance().logoutUser();
                                Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                                MainActivity.this.startActivity(intent);
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });

        partnerQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPartnerLoginFragment();
            }
        });

        if (me != null) {
            MenuItem profileMenuItem = navigationViewMenu.findItem(R.id.nav_profile);
            if (profileMenuItem.getTitle().toString().equalsIgnoreCase("LOGIN")) {
                profileMenuItem.setTitle("PROFILE");
            }
            applyFontToMenuItem(profileMenuItem);

            if (me.getType().equalsIgnoreCase("PARTNER")) {
                MenuItem menuItem = navigationViewMenu.findItem(R.id.nav_trip_history);
                menuItem.setTitle("ORDERS");
//                menuItem.setIcon(R.drawable.car)
                applyFontToMenuItem(menuItem);

                partnerQuestionButton.setVisibility(View.GONE);
            }
        } else {
            MenuItem menuItem = navigationViewMenu.findItem(R.id.nav_profile);
            menuItem.setTitle("LOGIN");
            applyFontToMenuItem(menuItem);

            MenuItem tripsMenuItem = navigationViewMenu.findItem(R.id.nav_trip_history);
            tripsMenuItem.setTitle("TRIPS");
            applyFontToMenuItem(tripsMenuItem);
        }

        initFiltersView();

    }

    private void initFiltersView() {

        toggleFiltersButton = (ImageView) findViewById(R.id.filters_icon);
        toggleFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFiltersView();
            }
        });

        filtersView = findViewById(R.id.filters_view);
//        FrameLayout.LayoutParams filtersViewParams = (FrameLayout.LayoutParams) filtersView.getLayoutParams();
////        filtersViewParams.topMargin = (int) (toolbar.getLayoutParams().height * 1.4);
//        filtersViewParams.topMargin = toolbar.getLayoutParams().height + Helpers.getStatusBarHeight(this);
//        filtersView.setLayoutParams(filtersViewParams);

        filterTypesHolder = (LinearLayout) findViewById(R.id.filter_types_layout);
        ArrayList<Category> categories = DataCacheProvider.getInstance().getStoredCategoriesArray();

        if (categories != null) {
            for (Category category : categories) {
                for (CategoryField categoryField : category.getFields()) {

                    if (categoryField.getParentFieldId().equalsIgnoreCase("0")) {

                        FilterTypeView filterTypeView = new FilterTypeView(this, categoryField);
                        filterTypesHolder.addView(filterTypeView);
                        filterTypesHolder.requestLayout();

                        filterTypeViews.put(filterTypeView, null);

                    } else {

                        FilterTypeView filterTypeView = new FilterTypeView(this, categoryField, true);
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


        final TextViewCustomFont minPriceValue = (TextViewCustomFont) findViewById(R.id.min_price_value);
        final TextViewCustomFont maxPriceValue = (TextViewCustomFont) findViewById(R.id.max_price_value);
        final RangeBar priceRangeBar = (RangeBar) findViewById(R.id.price_range_bar);

        priceRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                minPriceValue.setText(leftPinValue);
                maxPriceValue.setText(rightPinValue);
            }
        });

        TextViewCustomFont applyFiltersButton = (TextViewCustomFont) findViewById(R.id.apply_filters_button);
        TextViewCustomFont resetFiltersButton = (TextViewCustomFont) findViewById(R.id.reset_filters_button);

        resetFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                priceRangeBar.setRangePinsByValue(0, 1000);

                Iterator it = filterTypeViews.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();

                    FilterTypeView parentFilterTypeView = (FilterTypeView) pair.getKey();

                    if (pair.getValue() == null) {
                        parentFilterTypeView.clearSelected();
                    } else {
                        parentFilterTypeView.clearSelected();
                        ArrayList<FilterTypeView> values = filterTypeViews.get(parentFilterTypeView);
                        for (FilterTypeView filterTypeView : values) {
                            filterTypeView.clearSelected();
                            filterTypeView.setHidden(true);
                        }
                    }

                }

                if (mapFilters != null) {
                    mapFilters.clear();
                } else {
                    mapFilters = new MapFilters();
                }

                EventBus.getDefault().post(mapFilters);

                toggleFiltersView();

            }
        });

        applyFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mapFilters == null) {
                    mapFilters = new MapFilters();
                }

                mapFilters.setPriceFrom(Integer.parseInt(minPriceValue.getText().toString()));
                mapFilters.setPriceTo(Integer.parseInt(maxPriceValue.getText().toString()));

                StringBuilder optionsIds = new StringBuilder();
                Iterator it = filterTypeViews.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();

                    FilterTypeView parentFilterTypeView = (FilterTypeView) pair.getKey();

                    if (pair.getValue() == null) {
                        if (parentFilterTypeView.getSelectedItem() != null) {
                            optionsIds.append(parentFilterTypeView.getSelectedItem().second);
                            optionsIds.append(",");
                        }
                    } else {
                        if (parentFilterTypeView.getSelectedItem() != null) {
                            optionsIds.append(parentFilterTypeView.getSelectedItem().second);
                            optionsIds.append(",");
                        }

                        ArrayList<FilterTypeView> values = filterTypeViews.get(parentFilterTypeView);
                        for (FilterTypeView filterTypeView : values) {
                            if (filterTypeView.getSelectedItem() != null) {
                                optionsIds.append(filterTypeView.getSelectedItem().second);
                                optionsIds.append(",");
                            }
                        }
                    }

                }

                if (optionsIds.length() > 0) {
                    mapFilters.setCategoryOptionsIds(optionsIds.toString().substring(0, optionsIds.toString().length() - 1));
                }

                EventBus.getDefault().post(mapFilters);

                toggleFiltersView();

            }
        });

    }

    private void toggleFiltersView() {

        int cx;

        String locale = LocalizationHelper.getCurrentLocale();
        if (!locale.equalsIgnoreCase("")) {
            if (locale.equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                cx = filtersView.getRight();
            } else {
                cx = filtersView.getLeft();
            }
        } else {
            if (LocalizationHelper.getDeviceLocale().equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                cx = filtersView.getRight();
            } else {
                cx = filtersView.getLeft();
            }
        }

        int cy = filtersView.getTop();

        // get the final radius for the clipping circle
        int dx = Math.max(cx, filtersView.getWidth() - cx);
        int dy = Math.max(cy, filtersView.getHeight() - cy);
        float finalRadius = (float) Math.hypot(dx, dy);

        if (!isFiltersOpen) {
            Animator animator =
                    io.codetail.animation.ViewAnimationUtils.createCircularReveal(filtersView, cx, cy, 0, finalRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(300);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    filtersView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {

                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();

            toggleFiltersButton.setBackgroundColor(Color.parseColor("#ededed"));
            toggleFiltersButton.setColorFilter(Color.parseColor("#2b2b2b"));
            isFiltersOpen = true;
        } else {
            Animator animator =
                    io.codetail.animation.ViewAnimationUtils.createCircularReveal(filtersView, cx, cy, finalRadius, 0);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(300);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    filtersView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.start();

            toggleFiltersButton.setBackgroundColor(Color.parseColor("#2b2b2b"));
            toggleFiltersButton.setColorFilter(Color.parseColor("#ededed"));
            isFiltersOpen = false;
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            if (me != null) {
                openProfileFragment();
            } else {
                openPartnerLoginFragment();
            }
        } else if (id == R.id.nav_notifications) {
            openNotificationsScreen();
        } else if (id == R.id.nav_trip_history) {
            openTripHistoryScreen();
        } else {
            openSettingsScreen();
        }

//        else if (id == R.id.nav_manage) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBookVehicleButtonClicked(BookVehicleButtonClicked bookVehicleButtonClicked) {

        openBookingInformationScreen(bookVehicleButtonClicked.getCar());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCarBookedEvent(CarBookedEvent carBookedEvent) {

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

    private void openBookingInformationScreen(Car car) {

        fragmentManager = getSupportFragmentManager();
        VehicleBookingInformation vehicleBookingInformation = VehicleBookingInformation.newInstance(car);
        fragment = vehicleBookingInformation;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_BOOKING_INFORMATION_FRAG)
                .addToBackStack(TAG_BOOKING_INFORMATION_FRAG)
                .commit();

    }

    private void openPartnerLoginFragment() {

        drawer.closeDrawer(GravityCompat.START);

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText("PARTNER LOGIN");
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        PartnerLoginFragment partnerLoginFragment = new PartnerLoginFragment();
        fragment = partnerLoginFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_PARTNER_LOGIN_FRAG)
                .addToBackStack(TAG_PARTNER_LOGIN_FRAG)
                .commit();

    }

    public void openPartnerForgetPasswordScreen() {

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText("Forget Password");
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        PartnerForgetPassword partnerForgetPassword = PartnerForgetPassword.newInstance();
        fragment = partnerForgetPassword;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_PAYMENT_FRAG)
                .addToBackStack(TAG_PAYMENT_FRAG)
                .commit();

    }

    private void openNotificationsScreen() {

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText(R.string.notifications_screen_title);
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        TripHistoryFragment tripHistoryFragment = TripHistoryFragment.newInstance(TripHistoryFragment.NOTIFICATIONS_SCREEN_TYPE);
        fragment = tripHistoryFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_NOTIFICATIONS_FRAG)
                .addToBackStack(TAG_NOTIFICATIONS_FRAG)
                .commit();

    }

    public void openPaymentScreen(HashMap<String, Pair<Integer, Float>> bookingDetailsHashMap, Car item, String startDate, String endDate) {

        fragmentManager = getSupportFragmentManager();
        PaymentFragment paymentFragment = PaymentFragment.newInstance(bookingDetailsHashMap, item, startDate, endDate);
        fragment = paymentFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_PAYMENT_FRAG)
                .addToBackStack(TAG_PAYMENT_FRAG)
                .commit();

    }

    private void openTripHistoryScreen() {

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText(R.string.history_screen_title);
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        TripHistoryFragment tripHistoryFragment = TripHistoryFragment.newInstance(TripHistoryFragment.ORDERS_SCREEN_TYPE);
        fragment = tripHistoryFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_TRIP_HISTORY_FRAG)
                .addToBackStack(TAG_TRIP_HISTORY_FRAG)
                .commit();

    }

    private void openSettingsScreen() {

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText(R.string.settings_screen_title);
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        SettingsFragment settingsFragment = new SettingsFragment();
        fragment = settingsFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_SETTINGS_FRAG)
                .addToBackStack(TAG_SETTINGS_FRAG)
                .commit();

    }

    private void openProfileFragment() {

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText("Profile");
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        ProfileFragment profileFragment = new ProfileFragment();
        fragment = profileFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_PROFILE_FRAG)
                .addToBackStack(TAG_PROFILE_FRAG)
                .commit();

    }

    private void showMap() {

        fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = MapFragment.newInstance();
        fragment = mapFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_MAIN_MAP_FRAG)
                .commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font;

        String locale = LocalizationHelper.getCurrentLocale();

        if (!locale.equalsIgnoreCase("")) {
            if (locale.equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                font = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
            } else {
                font = Typeface.createFromAsset(getAssets(), "fonts/DroidKufi-Regular.ttf");
            }
        } else {
            if (LocalizationHelper.getDeviceLocale().equalsIgnoreCase(LocalizationHelper.ENGLISH_LOCALE)) {
                font = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
            } else {
                font = Typeface.createFromAsset(getAssets(), "fonts/DroidKufi-Regular.ttf");
            }
        }

        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public void closeFiltersView() {
        if (isFiltersOpen) {
            toggleFiltersView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //resume tasks needing this permission
            EventBus.getDefault().post(new PermissionGrantedEvent(true));
        } else {
            EventBus.getDefault().post(new PermissionGrantedEvent(false));
        }
    }

    public MapFilters getMapFilters() {
        if (mapFilters == null) {
            mapFilters = new MapFilters();
        }
        return mapFilters;
    }

    public void setMapFilters(MapFilters mapFilters) {
        this.mapFilters = mapFilters;
    }

    @Override
    public void onDataStoreUpdate() {

    }

    @Override
    public void onUserLocationUpdate() {

    }

    @Override
    public void onNewEventNotificationsAvailable() {

    }

    @Override
    public void onLoginStateChange() {

        Log.d("EYAD", "onLoginStateChange: ");
        me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, UserModel.class);
        init();

    }
}
