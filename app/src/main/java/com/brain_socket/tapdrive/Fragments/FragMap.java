package com.brain_socket.tapdrive.Fragments;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brain_socket.tapdrive.Fragments.DiagPickFilter.FiltersPickerCallback;
import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.TapApp;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.DataStore.DataRequestCallback;
import com.brain_socket.tapdrive.data.DataStore.DataStoreUpdateListener;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.model.AppCar;
import com.brain_socket.tapdrive.model.AppCarBrand;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class FragMap extends Fragment implements View.OnClickListener, OnMapReadyCallback, OnMarkerClickListener, GoogleMap.InfoWindowAdapter, FiltersPickerCallback, DataStoreUpdateListener{


    SupportMapFragment fragment;
    GoogleMap googleMap;

    ArrayList<LocatableWorkshop> providers = null;
    ArrayList<AppCar> workshops = null;
    HashMap<String, LocatableWorkshop> mapMarkerIdToLocatableProvider;
    LocatableWorkshop selectedLocatableWorkshop;

//    AppWorkshopCard vItemDetailsPreview;
    boolean focusMap;


    float radius;
    double centerLat;
    double centerLon;

    Handler handler;

    ArrayList<AppCarBrand> filterBrands;

    public DataRequestCallback searchResultCallback = new DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            if (success) {
                ArrayList<AppCar> brands;
                try {
                    if (result.getPairs().containsKey("workshops")) {
                        brands = new ArrayList<>();
                        ArrayList<AppCar> recievedShops = (ArrayList<AppCar>) result.get("workshops");
                        brands.addAll(recievedShops);
                        updateView(brands, focusMap);
                        focusMap = false;
                    }
                } catch (Exception e) {
                }
            }
        }
    };

    public static FragMap newInstance(ArrayList<AppCarBrand> brandsFilter) {
        FragMap frag = new FragMap();
        Bundle extras = new Bundle();

        if(brandsFilter != null)
            extras.putString("brand", AppBaseModel.getJSONArray(brandsFilter).toString());
        frag.setArguments(extras);
        return frag;
    }

    public void resolveExtra(Bundle extra) {
        try {
            if(extra.containsKey("brand")) {
                String jsonStr = extra.getString("brand");
                filterBrands = AppBaseModel.getArrayFromJsonSting(jsonStr, new TypeToken<ArrayList<AppCarBrand>>(){}.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_main_map, container, false);
        resolveExtra(getArguments());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TapApp.checkPlayServices(getActivity());
        requestLocationPermissionIfRequired();
        DataStore.getInstance().addUpdateBroadcastListener(this);
        init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DataStore.getInstance().removeUpdateBroadcastListener(this);
    }

    private void init() {

//        vItemDetailsPreview = (AppWorkshopCard) getView().findViewById(R.id.vProviderPreview);
        View btnFilter = getView().findViewById(R.id.btnFilter);

        focusMap = false;
        mapMarkerIdToLocatableProvider = new HashMap<>();
        handler = new Handler();

        btnFilter.setOnClickListener(this);

        // load Map Frag
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.flContentFrame);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.flContentFrame, fragment).commit();
            fragment.getMapAsync(this);
        }

        //inital state
        hidePreview();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // this block is needed on some devices that throw a
        // "java.lang.NullPointerException: IBitmapDescriptorFactory is not initialized" exception
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.googleMap = googleMap;
        this.googleMap.setOnCameraChangeListener(onCameraChangeListener);
        this.googleMap.setOnMarkerClickListener(this);
        // used to force Google maps bring
        // the marker to top onClick by showing an empty info window
        this.googleMap.setInfoWindowAdapter(this);
        this.googleMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedLocatableWorkshop = null;
                hidePreview();
            }
        });

        // initial data load
        selectedLocatableWorkshop = null;
        focusMap = true;
        getNearByBrands();
    }

    @SuppressLint("StringFormatMatches")
    private void updateView(ArrayList<AppCar> brands, boolean reFocusMap) {
        this.workshops = brands;
        try {
            if (brands != null && googleMap != null) {

                this.providers = new ArrayList<>();

                for (AppCar brand : brands) {
                    LocatableWorkshop locatableWorkshop = new LocatableWorkshop();
                    locatableWorkshop.workshop = brand;
                    locatableWorkshop.type = LocatableWorkshop.MarkType.BRAND;
                    boolean isSelected = selectedLocatableWorkshop != null
                            && locatableWorkshop.workshop != null
                            && locatableWorkshop.workshop.getId().equals(selectedLocatableWorkshop.workshop.getId());

                    locatableWorkshop.markerOptions = new MarkerOptions()
                                    .position(brand.getCoords())
                            .icon(BitmapDescriptorFactory.fromResource(locatableWorkshop.workshop.getMarkerResource()));
                    this.providers.add(locatableWorkshop);
                }
                // Map
                drawProvidersOnMap(this.providers, reFocusMap);
            }
        } catch (Exception e) {
        }
    }

    /**
     * used to re draw all providers and update Camera position if required
     * @param providers array of providers that wil be represented on map with markers
     * @param focusMap: if true, we animated the map camera to the current user location
     */
    private void drawProvidersOnMap(ArrayList<LocatableWorkshop> providers, boolean focusMap) {
        try {
            if (providers != null && googleMap != null) {
                googleMap.clear();

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LocatableWorkshop provider : providers) {
                    try {

                        Marker marker = googleMap.addMarker(provider.markerOptions);
                        mapMarkerIdToLocatableProvider.put(marker.getId(), provider);
                        LatLng position = provider.markerOptions.getPosition();
                        builder.include(position);
                        if (selectedLocatableWorkshop != null && provider.workshop.getId().equals(selectedLocatableWorkshop.workshop.getId()))
                            marker.showInfoWindow();
                    } catch (Exception e) {
                    }
                }

                if (focusMap) {
                    LatLngBounds bounds = builder.build();
                    LatLng userLocation = new LatLng(DataStore.getInstance().getMyLocationLatitude(), DataStore.getInstance().getMyLocationLongitude());
                    if(userLocation.latitude != 0 && userLocation.longitude != 0)
                        focusMapToCoords(userLocation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void focusMap(LatLngBounds bounds) {
        int padding = 50;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.animateCamera(cu);
    }

    /**
     * centers the ma pto the given coords with animation
     */
    private void focusMapToCoords(LatLng latLng) {
        try {
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, 12);
            googleMap.animateCamera(center);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update the camera to make the marker at the bottom half of the screen and zoom in to focus on marker
     * by first zooming in
     */
    private void focusMapOnMarker(LatLng latLng) {
        try {
            // zooming above the marker a little to make sure the marker stays at
            // the lower half of screen so it wont be covered by details card
            float paddingAboveMarker = 0.006f;
            final LatLng markerLatLng = new LatLng(latLng.latitude + paddingAboveMarker, latLng.longitude);
            CameraUpdate center = CameraUpdateFactory.newLatLng(markerLatLng);
            googleMap.animateCamera(center);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayProviderDetailsPreview(LocatableWorkshop locatableWorkshop) {
//        if (locatableWorkshop != null && locatableWorkshop.workshop != null) {
//            vItemDetailsPreview.updateUI(locatableWorkshop.workshop);
//            vItemDetailsPreview.setVisibility(View.VISIBLE);
//        }
    }

    public void displayBrandPreview(AppCar workshop) {
//        if (workshop != null) {
//            vItemDetailsPreview.updateUI(workshop);
//            vItemDetailsPreview.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * hide details Preview
     */
    public void hidePreview() {
//        vItemDetailsPreview.setVisibility(View.GONE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        try {
            LocatableWorkshop locatableWorkshop = mapMarkerIdToLocatableProvider.get(marker.getId());
            if (locatableWorkshop != null) {
                displayProviderDetailsPreview(locatableWorkshop);
                selectedLocatableWorkshop = locatableWorkshop;
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_active));
                ///TODO trigger marker animation
                focusMapOnMarker(marker.getPosition());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    // used to force Google maps bring
    // the marker to top onClick by showing an empty info window
    @Override
    public View getInfoWindow(Marker marker) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.layout_marker_info_window, null);
        return v;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void getNearByBrands() {
        Log.i("MAP", "getNearByBrands");
        //get coordinates of center screen point
        VisibleRegion vr = googleMap.getProjection().getVisibleRegion();
        centerLat = vr.latLngBounds.getCenter().latitude;
        centerLon = vr.latLngBounds.getCenter().longitude;

        //calculate distance between middleSouthEdge and screen center map location

        //center location
        Location centerLoc = new Location("center");
        centerLoc.setLatitude(centerLat);
        centerLoc.setLongitude(centerLon);
        //middle South location
        Location middlleSouthLocation = new Location("left");
        middlleSouthLocation.setLongitude(centerLoc.getLongitude());
        middlleSouthLocation.setLatitude(vr.latLngBounds.southwest.latitude);

        radius = centerLoc.distanceTo(middlleSouthLocation);
        Log.i("Params", centerLat + "," + centerLon + "," + radius);
        DataStore.getInstance().requestNearbyWorkshops((float) centerLat, (float) centerLon, radius, filterBrands, searchResultCallback);
    }

    GoogleMap.OnCameraChangeListener onCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            getNearByBrands();
        }
    };


    @Override
    public void onFiltersSelected(ArrayList<AppCarBrand> categories) {
        hidePreview();
        focusMap = true;
        this.filterBrands = categories;
        getNearByBrands();
    }

    ///
    /// ------- DataStore Broadcasts -----
    //
    @Override
    public void onDataStoreUpdate() {}

    @Override
    public void onUserLocationUpdate() {
        if(focusMap) {
            LatLng userLocation = new LatLng(DataStore.getInstance().getMyLocationLatitude(), DataStore.getInstance().getMyLocationLongitude());
            focusMapToCoords(userLocation);
        }
    }

    @Override
    public void onLoginStateChange() {}
    @Override
    public void onNewEventNotificationsAvailable() {}


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnFilter:
                DiagPickFilter diag = new DiagPickFilter(getContext(), filterBrands, this);
                diag.show();
                break;
        }
    }

    public static class LocatableWorkshop {
        AppCar workshop;
        MarkerOptions markerOptions;

        enum MarkType {BRAND, BRANCH}
        MarkType type;
    }

    ///
    /// ------ permissions -----
    ///
    public void requestLocationPermissionIfRequired(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION},TapApp.PERMISSIONS_REQUEST_LOCATION);
            }else{
                TapApp.checkAndPromptForLocationServices(getActivity());
            }
        }else{
            TapApp.checkAndPromptForLocationServices(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TapApp.PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TapApp.checkAndPromptForLocationServices(getActivity());
                }
            }
        }
    }
}
