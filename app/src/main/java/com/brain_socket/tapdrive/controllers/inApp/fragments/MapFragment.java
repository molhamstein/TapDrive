package com.brain_socket.tapdrive.controllers.inApp.fragments;

import android.Manifest.permission;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.brain_socket.tapdrive.R;
import com.brain_socket.tapdrive.controllers.inApp.MainActivity;
import com.brain_socket.tapdrive.controllers.inApp.adapters.VehiclesAdapter;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.DataStore.DataRequestCallback;
import com.brain_socket.tapdrive.data.DataStore.DataStoreUpdateListener;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.AppBaseModel;
import com.brain_socket.tapdrive.model.AppCarBrand;
import com.brain_socket.tapdrive.model.filters.MapFilters;
import com.brain_socket.tapdrive.model.partner.Car;
import com.brain_socket.tapdrive.model.partner.Partner;
import com.brain_socket.tapdrive.popups.DiagPickFilter.FiltersPickerCallback;
import com.brain_socket.tapdrive.utils.Helpers;
import com.brain_socket.tapdrive.utils.TapApp;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.gson.reflect.TypeToken;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MapFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, OnMarkerClickListener,
        GoogleMap.InfoWindowAdapter, FiltersPickerCallback, DataStoreUpdateListener {

    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingUpPanelLayout;
    @BindView(R.id.vehicles_recycler_view)
    RecyclerView vehiclesRecyclerView;
    @BindView(R.id.my_location_button)
    ImageView myLocationButton;

    SupportMapFragment fragment;
    GoogleMap googleMap;

    ArrayList<LocatableWorkshop> providers = null;
    ArrayList<Partner> partners = null;
    HashMap<String, LocatableWorkshop> mapMarkerIdToLocatableProvider;
    LocatableWorkshop selectedPartner;

    VehiclesAdapter vehiclesAdapter;

    // user location pulse animation
    private GroundOverlay lastUserCircle;
    private long pulseDuration = 2500;
    private ValueAnimator lastPulseAnimator;

    // marker location pulse animation
    private GroundOverlay lastMarkerCircle;
    private ValueAnimator lastMarkerPulseAnimator;

    //    AppWorkshopCard vItemDetailsPreview;
    boolean focusMap;
    public DataRequestCallback searchResultCallback = new DataRequestCallback() {
        @Override
        public void onDataReady(ServerResult result, boolean success) {
            if (success) {
                ArrayList<Partner> brands;
                try {
                    if (result.getPairs().containsKey("partners")) {
                        brands = new ArrayList<>();
                        ArrayList<Partner> recievedPartners = (ArrayList<Partner>) result.get("partners");
                        brands.addAll(recievedPartners);
                        updateView(brands, focusMap);
                        focusMap = false;
                    }
                } catch (Exception ignored) {
                }
            }
        }
    };
    float radius;
    double centerLat;
    double centerLon;
    Handler handler;
    ArrayList<AppCarBrand> filterBrands;
    GoogleMap.OnCameraChangeListener onCameraChangeListener = new GoogleMap.OnCameraChangeListener() {
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            getNearbyPartners();

            ((MainActivity) getActivity()).closeFiltersView();
        }
    };

    public static MapFragment newInstance() {
        MapFragment frag = new MapFragment();
        Bundle extras = new Bundle();
        frag.setArguments(extras);
        return frag;
    }

    public void resolveExtra(Bundle extra) {
        try {
            if (extra.containsKey("brand")) {
                String jsonStr = extra.getString("brand");
                filterBrands = AppBaseModel.getArrayFromJsonSting(jsonStr, new TypeToken<ArrayList<AppCarBrand>>() {
                }.getType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_main_map, container, false);
        ButterKnife.bind(this, view);
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
//        View btnFilter = getView().findViewById(R.id.btnFilter);

        focusMap = false;
        mapMarkerIdToLocatableProvider = new HashMap<>();
        handler = new Handler();

//        btnFilter.setOnClickListener(this);

        // load Map Frag
        FragmentManager fm = getChildFragmentManager();
        fragment = (SupportMapFragment) fm.findFragmentById(R.id.flContentFrame);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.flContentFrame, fragment).commit();
            fragment.getMapAsync(this);
        }

        myLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusOnUserLocation();
            }
        });

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                this.googleMap.setMyLocationEnabled(true);
                this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                myLocationButton.setVisibility(View.VISIBLE);
            }
        } else {
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            myLocationButton.setVisibility(View.GONE);
        }
        // used to force Google maps bring
        // the marker to top onClick by showing an empty info window
        this.googleMap.setInfoWindowAdapter(this);
        this.googleMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                selectedPartner = null;
                hidePreview();

                slidingUpPanelLayout.setPanelHeight(0);
                ((MainActivity) getActivity()).closeFiltersView();
            }
        });

        MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.map_style);
        this.googleMap.setMapStyle(style);

        // initial data load
        selectedPartner = null;
        focusMap = true;
        getNearbyPartners();
    }

    @SuppressLint("StringFormatMatches")
    private void updateView(ArrayList<Partner> partners, boolean reFocusMap) {
        this.partners = partners;
        try {
            if (partners != null && googleMap != null) {

                this.providers = new ArrayList<>();

                for (Partner partner : partners) {
                    LocatableWorkshop locatableWorkshop = new LocatableWorkshop();
                    locatableWorkshop.partner = partner;
                    locatableWorkshop.type = LocatableWorkshop.MarkType.BRAND;
                    boolean isSelected = selectedPartner != null
                            && locatableWorkshop.partner != null
                            && locatableWorkshop.partner.getId().equals(selectedPartner.partner.getId());

                    locatableWorkshop.markerOptions = new MarkerOptions()
                            .position(partner.getCoords())
                            .icon(BitmapDescriptorFactory.fromResource(locatableWorkshop.partner.getMarkerResource()));
                    this.providers.add(locatableWorkshop);
                }
                // Map
                drawProvidersOnMap(this.providers, reFocusMap);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * used to re draw all providers and update Camera position if required
     *
     * @param providers array of providers that wil be represented on map with markers
     * @param focusMap: if true, we animated the map camera to the current user location
     */
    private void drawProvidersOnMap(ArrayList<LocatableWorkshop> providers, boolean focusMap) {
        try {
            if (providers != null && googleMap != null) {
                googleMap.clear();
                lastUserCircle = null;

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (LocatableWorkshop provider : providers) {
                    try {

                        Marker marker = googleMap.addMarker(provider.markerOptions);
                        mapMarkerIdToLocatableProvider.put(marker.getId(), provider);
                        LatLng position = provider.markerOptions.getPosition();
                        builder.include(position);
                        if (selectedPartner != null && provider.partner.getId().equals(selectedPartner.partner.getId()))
                            marker.showInfoWindow();
                    } catch (Exception ignored) {
                    }
                }

                LatLng userLocation = new LatLng(DataStore.getInstance().getMyLocationLatitude(), DataStore.getInstance().getMyLocationLongitude());
                if (userLocation.latitude != 0 && userLocation.longitude != 0) {
                    // add a pulsing circle arround user location
                    addPulsatingEffect(userLocation);

                    if (focusMap) {
                        focusMapToCoords(userLocation);
                    }
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
     * centers the map to the given coords with animation
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
//        if (locatableWorkshop != null && locatableWorkshop.partner != null) {
//            vItemDetailsPreview.updateUI(locatableWorkshop.partner);
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
            ((MainActivity) getActivity()).closeFiltersView();

            LocatableWorkshop locatableWorkshop = mapMarkerIdToLocatableProvider.get(marker.getId());
            if (locatableWorkshop != null) {
                displayProviderDetailsPreview(locatableWorkshop);
                selectedPartner = locatableWorkshop;
                //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_active));
                ///TODO trigger marker animation
                focusMapOnMarker(marker.getPosition());
                populateVehiclesData(selectedPartner);

                addMarkerPulsatingEffect(marker.getPosition());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    private void populateVehiclesData(LocatableWorkshop selectedPartner) {

        if (selectedPartner.partner.getCars().size() > 0) {
            slidingUpPanelLayout.setPanelHeight(Helpers.dpToPx(getActivity(), 200));

            if (vehiclesAdapter == null) {
                vehiclesAdapter = new VehiclesAdapter(getActivity(), selectedPartner.partner.getCars(), true);
                vehiclesRecyclerView.setAdapter(vehiclesAdapter);
                vehiclesAdapter.notifyDataSetChanged();
            } else {
                vehiclesAdapter.setData(selectedPartner.partner.getCars());
                vehiclesAdapter.notifyDataSetChanged();
            }
        } else {
            slidingUpPanelLayout.setPanelHeight(0);
            slidingUpPanelLayout.requestLayout();

            if (vehiclesAdapter == null) {
                vehiclesAdapter = new VehiclesAdapter(getActivity(), new ArrayList<Car>(), true);
                vehiclesRecyclerView.setAdapter(vehiclesAdapter);
                vehiclesAdapter.notifyDataSetChanged();
            } else {
                vehiclesAdapter.setData(new ArrayList<Car>());
                vehiclesAdapter.notifyDataSetChanged();
            }
        }

    }

    // used to force Google maps bring
    // the marker to top onClick by showing an empty info window
    @Override
    public View getInfoWindow(Marker marker) {
        return getActivity().getLayoutInflater().inflate(R.layout.layout_marker_info_window, null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    private void getNearbyPartners() {
        Log.i("MAP", "getNearbyPartners");
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
        DataStore.getInstance().getNearbyPartners((float) centerLat, (float) centerLon, (int) radius, ((MainActivity) getActivity()).getMapFilters(), searchResultCallback);
//        DataStore.getInstance().getNearbyPartners((float) centerLat, (float) centerLon, radius, 0, activity.getKeyWord(), activity.getCategoriesFilter(), null, searchResultCallback);
    }

    @Override
    public void onFiltersSelected(ArrayList<AppCarBrand> categories) {
        hidePreview();
        focusMap = true;
        this.filterBrands = categories;
        getNearbyPartners();
    }


    /**
     * adds a pulse animation arround the given coords
     */
    private void addPulsatingEffect(final LatLng userLatlng) {
        if (lastPulseAnimator != null) {
            lastPulseAnimator.cancel();
        }
        if (lastUserCircle != null)
            lastUserCircle.setPosition(userLatlng);
        lastPulseAnimator = valueAnimate(3000, pulseDuration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (lastUserCircle != null) {
                    float zoomPrecent = googleMap.getCameraPosition().zoom / googleMap.getMaxZoomLevel();
                    // TODO do some caculations based on th zoom level to make sure the size of the pulse circle remains fixed regardless of zoom level
                    lastUserCircle.setDimensions((Float) animation.getAnimatedValue());
                    lastUserCircle.setTransparency(animation.getAnimatedFraction());
                } else {
                    BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.pulse);
                    lastUserCircle = googleMap.addGroundOverlay(new GroundOverlayOptions()
                            .position(userLatlng, (Float) animation.getAnimatedValue())
                            .anchor(0.5f, 0.5f)
                            .transparency(0)
                            .image(image));
                }
            }
        });
    }

    /**
     * adds a pulse animation arround the given coords
     */
    private void addMarkerPulsatingEffect(final LatLng markerLatLng) {
        if (lastMarkerPulseAnimator != null) {
            lastMarkerPulseAnimator.cancel();
        }
        if (lastMarkerCircle != null)
            lastMarkerCircle.setPosition(markerLatLng);
        lastMarkerPulseAnimator = valueAnimate(3000, pulseDuration, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (lastMarkerCircle != null) {
                    float zoomPrecent = googleMap.getCameraPosition().zoom / googleMap.getMaxZoomLevel();
                    // TODO do some caculations based on th zoom level to make sure the size of the pulse circle remains fixed regardless of zoom level
                    lastMarkerCircle.setDimensions((Float) animation.getAnimatedValue());
                    lastMarkerCircle.setTransparency(animation.getAnimatedFraction());
                } else {
                    BitmapDescriptor image = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_glow);
                    lastMarkerCircle = googleMap.addGroundOverlay(new GroundOverlayOptions()
                            .position(markerLatLng, (Float) animation.getAnimatedValue())
                            .anchor(0.5f, 0.5f)
                            .transparency(0)
                            .image(image));
                }
            }
        });
    }

    protected ValueAnimator valueAnimate(float accuracy, long duration, ValueAnimator.AnimatorUpdateListener updateListener) {
        Log.d("valueAnimate: ", "called");
        ValueAnimator va = ValueAnimator.ofFloat(0, accuracy);
        va.setDuration(duration);
        va.addUpdateListener(updateListener);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);
        va.setInterpolator(new DecelerateInterpolator());

        va.start();
        return va;
    }

    ///
    /// ------- DataStore Broadcasts -----
    //
    @Override
    public void onDataStoreUpdate() {
    }

    @Override
    public void onUserLocationUpdate() {
        if (focusMap) {
            LatLng userLocation = new LatLng(DataStore.getInstance().getMyLocationLatitude(), DataStore.getInstance().getMyLocationLongitude());
            focusMapToCoords(userLocation);
        }
    }

    @Override
    public void onLoginStateChange() {
    }

    @Override
    public void onUserInfoUpdated() {

    }

    @Override
    public void onNewEventNotificationsAvailable() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.btnFilter:
//                DiagPickFilter diag = new DiagPickFilter(getContext(), filterBrands, this);
//                diag.show();
//                break;
        }
    }

    ///
    /// ------ permissions -----
    ///
    public void requestLocationPermissionIfRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{permission.ACCESS_FINE_LOCATION}, TapApp.PERMISSIONS_REQUEST_LOCATION);
            } else {
                TapApp.checkAndPromptForLocationServices(getActivity());
            }
        } else {
            TapApp.checkAndPromptForLocationServices(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case TapApp.PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TapApp.checkAndPromptForLocationServices(getActivity());
                    try {
                        if (this.googleMap != null) {
                            this.googleMap.setMyLocationEnabled(true);
                            this.googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                            myLocationButton.setVisibility(View.VISIBLE);
                            myLocationButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    focusOnUserLocation();
                                }
                            });
                        }
                    } catch (SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static class LocatableWorkshop {
        Partner partner;
        MarkerOptions markerOptions;
        MarkType type;

        enum MarkType {BRAND, BRANCH}
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFiltersUpdated(MapFilters mapFilters) {

        getNearbyPartners();
        slidingUpPanelLayout.setPanelHeight(0);

    }

    private void focusOnUserLocation() {
        LatLng userLocation = new LatLng(DataStore.getInstance().getMyLocationLatitude(), DataStore.getInstance().getMyLocationLongitude());
        focusMapToCoords(userLocation);
    }

}
