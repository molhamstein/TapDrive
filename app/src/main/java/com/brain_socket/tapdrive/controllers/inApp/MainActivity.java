package com.brain_socket.tapdrive.controllers.inApp;

import android.animation.Animator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.fragments.MapFragment;
import com.brain_socket.tapdrive.controllers.inApp.fragments.VehicleBookingInformation;
import com.brain_socket.tapdrive.customViews.FilterTypeView;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.customViews.TextViewCustomFont;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.delegates.BookVehicleButtonClicked;
import com.brain_socket.tapdrive.delegates.FilterSelectedEvent;
import com.brain_socket.tapdrive.model.filters.Category;
import com.brain_socket.tapdrive.model.filters.CategoryField;
import com.brain_socket.tapdrive.model.partner.Car;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static String TAG_MAIN_MAP_FRAG = "mainMapFrag";
    private static String TAG_BOOKING_INFORMATION_FRAG = "bookingInformationFrag";

    Fragment fragment;
    FragmentManager fragmentManager;
    DrawerLayout drawer;
    View rlMainContent;
    Toolbar toolbar;

    View filtersView;
    ImageView toggleFiltersButton;
    LinearLayout filterTypesHolder;
    boolean isFiltersOpen = false;

    HashMap<FilterTypeView, ArrayList<FilterTypeView>> filters = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        showMap();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        rlMainContent = findViewById(R.id.rlMainContent);
        setSupportActionBar(toolbar);

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
                rlMainContent.setTranslationX(slideOffset * drawerView.getWidth());
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

        // hide default app logo and name on the left of the toolbar
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true); // enable overriding the default toolbar layout
        ab.setDisplayShowTitleEnabled(false); // disable the default title element here (for centered title)

        View headerLayout = navigationView.getHeaderView(0);
        RoundedImageView profilePicture = (RoundedImageView) headerLayout.findViewById(R.id.ivProfilePic);
        TextView userName = (TextView) headerLayout.findViewById(R.id.tvUserName);

        UserModel me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, UserModel.class);
        userName.setText(me.getUsername());
        Glide.with(this).load(me.getPhoto()).into(profilePicture);

        initFiltersView();

    }

    private void initFiltersView() {

        filtersView = findViewById(R.id.filters_view);
        FrameLayout.LayoutParams filtersViewParams = (FrameLayout.LayoutParams) filtersView.getLayoutParams();
        filtersViewParams.topMargin = (int) (toolbar.getLayoutParams().height * 1.4);
        filtersView.setLayoutParams(filtersViewParams);

        toggleFiltersButton = (ImageView) findViewById(R.id.filters_icon);
        toggleFiltersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFiltersView();
            }
        });

        filterTypesHolder = (LinearLayout) findViewById(R.id.filter_types_layout);
        ArrayList<Category> categories = DataCacheProvider.getInstance().getStoredCategoriesArray();

        for (Category category : categories) {
            for (CategoryField categoryField : category.getFields()) {

                if (categoryField.getParentFieldId().equalsIgnoreCase("0")) {

                    FilterTypeView filterTypeView = new FilterTypeView(this, categoryField);
                    filterTypesHolder.addView(filterTypeView);
                    filterTypesHolder.requestLayout();

                    filters.put(filterTypeView, null);

                } else {

                    FilterTypeView filterTypeView = new FilterTypeView(this, categoryField, true);
                    filterTypesHolder.addView(filterTypeView);
                    filterTypesHolder.requestLayout();

                    Iterator it = filters.entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();

                        FilterTypeView parentFilterTypeView = (FilterTypeView) pair.getKey();
                        if (parentFilterTypeView.getCategoryField().getId().equalsIgnoreCase(filterTypeView.getCategoryField().getParentFieldId())) {
                            if (pair.getValue() == null) {
                                ArrayList<FilterTypeView> values = new ArrayList<>();
                                values.add(filterTypeView);
                                filters.put(parentFilterTypeView, values);
                            } else {
                                ArrayList<FilterTypeView> values = filters.get(parentFilterTypeView);
                                values.add(filterTypeView);
                                filters.put(parentFilterTypeView, values);
                            }
                        }
                    }

                }

            }
        }


        final TextViewCustomFont minPriceValue = (TextViewCustomFont) findViewById(R.id.min_price_value);
        final TextViewCustomFont maxPriceValue = (TextViewCustomFont) findViewById(R.id.max_price_value);
        RangeBar priceRangeBar = (RangeBar) findViewById(R.id.price_range_bar);

        priceRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex,
                                              String leftPinValue, String rightPinValue) {
                minPriceValue.setText(leftPinValue);
                maxPriceValue.setText(rightPinValue);
            }
        });

    }

    private void toggleFiltersView() {

        int cx = filtersView.getRight();
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

    private void showMap() {
        fragmentManager = getSupportFragmentManager();
        MapFragment mapFragment = MapFragment.newInstance();
        fragment = mapFragment;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_MAIN_MAP_FRAG)
                .commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        }

//        else if (id == R.id.nav_manage) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectedFilterValueEvent(FilterSelectedEvent filterSelectedEvent) {

        if (filters.get(filterSelectedEvent.getFilterTypeView()) == null) {
            return;
        } else {
            for (FilterTypeView filterTypeView : filters.get(filterSelectedEvent.getFilterTypeView())) {
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

    private void openBookingInformationScreen(Car car) {

        fragmentManager = getSupportFragmentManager();
        VehicleBookingInformation vehicleBookingInformation = VehicleBookingInformation.newInstance(car);
        fragment = vehicleBookingInformation;
        fragmentManager.beginTransaction()
                .add(R.id.flMainFragmentContainer, fragment, TAG_MAIN_MAP_FRAG)
                .addToBackStack(TAG_MAIN_MAP_FRAG)
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



}
