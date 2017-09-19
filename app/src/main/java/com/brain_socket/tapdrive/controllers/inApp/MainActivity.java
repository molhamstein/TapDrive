package com.brain_socket.tapdrive.controllers.inApp;

import android.animation.Animator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appyvet.rangebar.RangeBar;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.fragments.InnerForgetPasswordFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.InnerLoginFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.MapFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.PartnerCarsFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.PartnerInvoicesFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.PartnerِAddCarFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.PaymentFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.ProfileFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.SettingsFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.TripHistoryFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.VehicleBookingInformation;
import com.brain_socket.tapdrive.controllers.onBoarding.LoginActivity;
import com.brain_socket.tapdrive.customViews.FilterTypeView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.delegates.BookVehicleButtonClicked;
import com.brain_socket.tapdrive.delegates.CarBookedEvent;
import com.brain_socket.tapdrive.delegates.FilterSelectedEvent;
import com.brain_socket.tapdrive.delegates.ForegroundNotificationRecievedEvent;
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
    private static String TAG_CARS_FRAG = "carsFrag";
    private static String TAG_ADD_CAR_FRAG = "addCarFrag";
    private static String TAG_INVOICES_FRAG = "invoicesFrag";
    private static String TAG_NOTIFICATIONS_FRAG = "notificationsFrag";
    private static String TAG_PARTNER_LOGIN_FRAG = "partnerLoginFrag";

    Fragment fragment;
    FragmentManager fragmentManager;
    DrawerLayout drawer;
    View rlMainContent;
    Toolbar toolbar;
    FrameLayout mainFragmentContainer;
    RelativeLayout.LayoutParams fragmentMainContainerLayoutParams;

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
    View dimmingView;
    ImageView addCarImageView;
    ImageView notificationsImageView;

    UserModel me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataStore.getInstance().addUpdateBroadcastListener(this);
        init();
        showMap();

        // show login screen on first run if user is not logged in
        boolean isLoggedInUser = !DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN).equalsIgnoreCase("");
        if (!isLoggedInUser && DataStore.getInstance().isFirstRun()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                }
            }, 1500);
        }
        DataStore.getInstance().setFirstRun(false);
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
        dimmingView = (View) findViewById(R.id.dimming_view);
        dimmingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFiltersView();
            }
        });

        toolbarTitle = (TextViewCustomFont) findViewById(R.id.toolbar_title);
        toolbarLogo = (ImageView) findViewById(R.id.toolbar_logo);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rlMainContent = findViewById(R.id.rlMainContent);
        mainFragmentContainer = (FrameLayout) findViewById(R.id.flMainFragmentContainer);

        fragmentMainContainerLayoutParams = (RelativeLayout.LayoutParams) mainFragmentContainer.getLayoutParams();
        removeMainFragmentContainerMargins();

        setSupportActionBar(toolbar);

        notificationsImageView = (ImageView) findViewById(R.id.notification_icon);
        addCarImageView = (ImageView) findViewById(R.id.add_car_icon);
        addCarImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPartnerAddCarScreen();
            }
        });

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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

        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.ic_ham_menu);

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

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

                    if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                        if (getSupportFragmentManager().getBackStackEntryAt(0).getName().equalsIgnoreCase(TAG_CARS_FRAG)) {
                            addCarImageView.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    //show hamburger
                    removeMainFragmentContainerMargins();
                    toolbarTitle.setVisibility(View.GONE);
                    toolbarLogo.setVisibility(View.GONE);
                    toggleFiltersButton.setVisibility(View.VISIBLE);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    toggle.setDrawerIndicatorEnabled(false);
                    toggle.setHomeAsUpIndicator(R.drawable.ic_ham_menu);
                    toggle.syncState();
                    addCarImageView.setVisibility(View.GONE);
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
        headerLogoImageView = (ImageView) headerLayout.findViewById(R.id.header_logo_image_view);
        logoutButton = (TextViewCustomFont) findViewById(R.id.btnLogout);
        partnerQuestionButton = (TextViewCustomFont) findViewById(R.id.partner_question_button);

        me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, UserModel.class);

        if (me != null) {
            userName.setVisibility(View.VISIBLE);
            profilePicture.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.VISIBLE);

            headerLogoImageView.setVisibility(View.GONE);

            userName.setText(me.getUsername());
            Glide.with(getApplicationContext()).load(me.getPhoto()).placeholder(R.drawable.nobody).dontAnimate().into(profilePicture);

            profilePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!me.getType().equalsIgnoreCase("PARTNER")) {
                        drawer.closeDrawer(Gravity.START);
                        openProfileFragment();
                    }
                }
            });

        } else {
            profilePicture.setVisibility(View.GONE);
            userName.setVisibility(View.GONE);
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
//                                Intent intent = new Intent(MainActivity.this, SplashScreen.class);
//                                MainActivity.this.startActivity(intent);
//                                MainActivity.this.finish();
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
                openInnerLoginFragment(InnerLoginFragment.PARTNER_LOGIN);
            }
        });

        if (me != null) {

            if (me.getType().equalsIgnoreCase("PARTNER")) {
                MenuItem menuItem = navigationViewMenu.findItem(R.id.nav_trip_history);
                menuItem.setTitle(getString(R.string.drawer_orders));
                applyFontToMenuItem(menuItem);

                MenuItem carsMenuItem = navigationViewMenu.findItem(R.id.nav_partner_cars);
                carsMenuItem.setVisible(true);
                applyFontToMenuItem(carsMenuItem);

                MenuItem invoicesMenuItem = navigationViewMenu.findItem(R.id.nav_partner_invoices);
                invoicesMenuItem.setVisible(true);
                applyFontToMenuItem(invoicesMenuItem);

                MenuItem profileMenuItem = navigationViewMenu.findItem(R.id.nav_profile);
                profileMenuItem.setVisible(false);

                partnerQuestionButton.setVisibility(View.GONE);
            } else {
                MenuItem carsMenuItem = navigationViewMenu.findItem(R.id.nav_partner_cars);
                carsMenuItem.setVisible(false);

                MenuItem invoicesMenuItem = navigationViewMenu.findItem(R.id.nav_partner_invoices);
                invoicesMenuItem.setVisible(false);

                MenuItem menuItem = navigationViewMenu.findItem(R.id.nav_profile);
                menuItem.setTitle(getString(R.string.drawer_profile));
                applyFontToMenuItem(menuItem);
            }

        } else {
            MenuItem menuItem = navigationViewMenu.findItem(R.id.nav_profile);
            menuItem.setTitle(getString(R.string.drawer_login));
            applyFontToMenuItem(menuItem);

            MenuItem tripsMenuItem = navigationViewMenu.findItem(R.id.nav_trip_history);
            tripsMenuItem.setTitle(getString(R.string.drawer_trips));
            applyFontToMenuItem(tripsMenuItem);

            MenuItem carsMenuItem = navigationViewMenu.findItem(R.id.nav_partner_cars);
            carsMenuItem.setVisible(false);

            MenuItem invoicesMenuItem = navigationViewMenu.findItem(R.id.nav_partner_invoices);
            invoicesMenuItem.setVisible(false);
        }

        initFiltersView();


        String fcmToken = DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_FCM_TOKEN);

        if (me != null) {

            if (fcmToken != null) {

                if (!fcmToken.equalsIgnoreCase("")) {

                    DataStore.getInstance().setFCMId(fcmToken, new DataStore.DataRequestCallback() {
                        @Override
                        public void onDataReady(ServerResult result, boolean success) {

                        }
                    });

                }

            }


        }

    }

    private void removeMainFragmentContainerMargins() {

        toolbar.setBackgroundResource(R.drawable.translucent_toolbar_background);
        fragmentMainContainerLayoutParams.setMargins(0, 0, 0, 0);

    }

    private void applyMainFragmentContainerMargins() {

        toolbar.setBackgroundResource(R.drawable.black_toolbar_background);

        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        fragmentMainContainerLayoutParams.setMargins(0, actionBarHeight, 0, 0);

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

            dimmingView.setVisibility(View.VISIBLE);
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

            toggleFiltersButton.setBackgroundColor(Color.TRANSPARENT);
            toggleFiltersButton.setColorFilter(null);
            isFiltersOpen = false;

            dimmingView.setVisibility(View.GONE);
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
                openInnerLoginFragment(InnerLoginFragment.USER_LOGIN);
            }
        } else if (id == R.id.nav_notifications) {
            if (me != null) {
                openNotificationsScreen();
            } else {
                Toast.makeText(this, R.string.please_login_hint, Toast.LENGTH_SHORT).show();
                openInnerLoginFragment(InnerLoginFragment.USER_LOGIN);
            }
        } else if (id == R.id.nav_trip_history) {
            if (me != null) {
                openTripHistoryScreen();
            } else {
                Toast.makeText(this, R.string.please_login_hint, Toast.LENGTH_SHORT).show();
                openInnerLoginFragment(InnerLoginFragment.USER_LOGIN);
            }
        } else if (id == R.id.nav_partner_cars) {
            openPartnerCarsScreen();
        } else if (id == R.id.nav_partner_invoices) {
            openPartnerInvoicesScreen();
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

        applyMainFragmentContainerMargins();

        toolbarLogo.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        VehicleBookingInformation vehicleBookingInformation = VehicleBookingInformation.newInstance(car);
        fragment = vehicleBookingInformation;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_BOOKING_INFORMATION_FRAG)
                .addToBackStack(TAG_BOOKING_INFORMATION_FRAG)
                .commit();

    }

    private void openInnerLoginFragment(int screenType) {

        if (screenType == InnerLoginFragment.PARTNER_LOGIN) {

            applyMainFragmentContainerMargins();

            drawer.closeDrawer(GravityCompat.START);

            toolbarTitle.setText("PARTNER LOGIN");
            toolbarTitle.setVisibility(View.VISIBLE);

            fragmentManager = getSupportFragmentManager();
            InnerLoginFragment innerLoginFragment = InnerLoginFragment.newInstance(screenType);
            fragment = innerLoginFragment;
            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.pull_in_right,
                            R.anim.push_out_left,
                            R.anim.pull_in_left,
                            R.anim.push_out_right)
                    .add(R.id.flMainFragmentContainer, fragment, TAG_PARTNER_LOGIN_FRAG)
                    .addToBackStack(TAG_PARTNER_LOGIN_FRAG)
                    .commit();

        } else {

            Intent loginIntent = new Intent(this, LoginActivity.class);
            this.startActivity(loginIntent);

        }

    }

    public void openInnerForgetPasswordScreen(int screenType) {

        applyMainFragmentContainerMargins();

        toolbarTitle.setText("Forget Password");
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        InnerForgetPasswordFragment innerForgetPasswordFragment = InnerForgetPasswordFragment.newInstance(screenType);
        fragment = innerForgetPasswordFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_PAYMENT_FRAG)
                .addToBackStack(TAG_PAYMENT_FRAG)
                .commit();

    }

    private void openNotificationsScreen() {

        applyMainFragmentContainerMargins();

        toolbarTitle.setText(R.string.notifications_screen_title);
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        TripHistoryFragment tripHistoryFragment = TripHistoryFragment.newInstance(TripHistoryFragment.NOTIFICATIONS_SCREEN_TYPE);
        fragment = tripHistoryFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_NOTIFICATIONS_FRAG)
                .addToBackStack(TAG_NOTIFICATIONS_FRAG)
                .commit();

    }

    public void openPaymentScreen(HashMap<String, Pair<Integer, Float>> bookingDetailsHashMap, Car item, String startDate, String endDate) {

        applyMainFragmentContainerMargins();

        fragmentManager = getSupportFragmentManager();
        PaymentFragment paymentFragment = PaymentFragment.newInstance(bookingDetailsHashMap, item, startDate, endDate);
        fragment = paymentFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_PAYMENT_FRAG)
                .addToBackStack(TAG_PAYMENT_FRAG)
                .commit();

    }

    private void openTripHistoryScreen() {

        applyMainFragmentContainerMargins();

        if (!me.getType().equalsIgnoreCase("PARTNER")) {
            toolbarTitle.setText(R.string.history_screen_title);
        } else {
            toolbarTitle.setText(R.string.drawer_orders);
        }
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        TripHistoryFragment tripHistoryFragment = TripHistoryFragment.newInstance(TripHistoryFragment.ORDERS_SCREEN_TYPE);
        fragment = tripHistoryFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_TRIP_HISTORY_FRAG)
                .addToBackStack(TAG_TRIP_HISTORY_FRAG)
                .commit();

    }

    private void openPartnerCarsScreen() {

        applyMainFragmentContainerMargins();

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText(R.string.cars_screen_title);
        toolbarTitle.setVisibility(View.VISIBLE);

        addCarImageView.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        PartnerCarsFragment carsFragment = PartnerCarsFragment.newInstance();
        fragment = carsFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_CARS_FRAG)
                .addToBackStack(TAG_CARS_FRAG)
                .commit();
    }

    private void openPartnerAddCarScreen() {

        applyMainFragmentContainerMargins();

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText(R.string.cars_screen_title);
        toolbarTitle.setVisibility(View.VISIBLE);

        addCarImageView.setVisibility(View.GONE);

        fragmentManager = getSupportFragmentManager();
        PartnerِAddCarFragment carsFragment = PartnerِAddCarFragment.newInstance();
        fragment = carsFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_ADD_CAR_FRAG)
                .addToBackStack(TAG_ADD_CAR_FRAG)
                .commit();
    }

    private void openPartnerInvoicesScreen() {

        toolbarLogo.setVisibility(View.GONE);
        toolbarTitle.setText(R.string.invoices_screen_title);
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        PartnerInvoicesFragment invoicesFragment = PartnerInvoicesFragment.newInstance();
        fragment = invoicesFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_INVOICES_FRAG)
                .addToBackStack(TAG_INVOICES_FRAG)
                .commit();
    }

    private void openSettingsScreen() {

        applyMainFragmentContainerMargins();

        toolbarTitle.setText(R.string.settings_screen_title);
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        SettingsFragment settingsFragment = new SettingsFragment();
        fragment = settingsFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_SETTINGS_FRAG)
                .addToBackStack(TAG_SETTINGS_FRAG)
                .commit();

    }

    private void openProfileFragment() {

        applyMainFragmentContainerMargins();

        toolbarTitle.setText("Profile");
        toolbarTitle.setVisibility(View.VISIBLE);

        fragmentManager = getSupportFragmentManager();
        ProfileFragment profileFragment = new ProfileFragment();
        fragment = profileFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
                .add(R.id.flMainFragmentContainer, fragment, TAG_PROFILE_FRAG)
                .addToBackStack(TAG_PROFILE_FRAG)
                .commit();

    }

    private void showMap() {

        fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = MapFragment.newInstance();
        fragment = mapFragment;
        fragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.pull_in_right,
                        R.anim.push_out_left,
                        R.anim.pull_in_left,
                        R.anim.push_out_right)
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

        if (me != null) {

            me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, UserModel.class);
            init();

        } else {

            init();

        }

    }

    @Override
    public void onUserInfoUpdated() {

        if (me != null) {

            me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, UserModel.class);
            init();

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onForegroundNotificationRecieved(ForegroundNotificationRecievedEvent foregroundNotificationRecievedEvent) {

        notificationsImageView.setVisibility(View.VISIBLE);

        notificationsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationsImageView.setVisibility(View.GONE);
                openNotificationsScreen();
            }
        });

    }

}
