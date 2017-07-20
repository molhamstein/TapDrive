package com.brain_socket.tapdrive.controllers.inApp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.brain_socket.tapdrive.controllers.inApp.fragments.MapFragment;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.customViews.RoundedImageView;
import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.model.AppCarBrand;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragment;
    FragmentManager fragmentManager;
    private static String TAG_MAIN_MAP_FRAG = "mainMapFrag";
    DrawerLayout drawer;
    View rlMainContent;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        rlMainContent = findViewById(R.id.rlMainContent);
        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
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
            }
        };

        drawer.addDrawerListener(toggle);
        toggle.syncState();

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
}
