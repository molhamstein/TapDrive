package com.brain_socket.tapdrive.data;

import android.location.Location;
import android.os.Handler;
import android.util.Log;

import com.brain_socket.tapdrive.model.AppCarBrand;
import com.brain_socket.tapdrive.model.filters.Category;
import com.brain_socket.tapdrive.model.AppUser;
import com.brain_socket.tapdrive.model.filters.MapFilters;
import com.brain_socket.tapdrive.model.partner.Country;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * This class will be responsible for requesting new data from the data providers
 * like  and invoking the callback when ready plus handling multithreading when required
 *
 * @author MolhamStein
 */
@SuppressWarnings({"unchecked", "UnusedAssignment"})
public class DataStore {

    public static String VERSIOIN_ID = "0.1";

    public enum GENERIC_ERROR_TYPE {NO_ERROR, UNDEFINED_ERROR, NO_CONNECTION, NOT_LOGGED_IN, NO_MORE_PAGES}

    public enum App_ACCESS_MODE {NOT_LOGGED_IN, NOT_VERIFIED, VERIFIED}

    private static DataStore instance = null;

    private Handler handler;
    private ArrayList<DataStoreUpdateListener> updateListeners;
    private ServerAccess serverHandler;

    private UserModel me;
    private String apiAccessToken;
    private App_ACCESS_MODE accessMode;

    // user location
    private float myLocationLatitude;
    private float myLocationLongitude;
    protected Location meLastLocation;

    // Home screen data
    private ArrayList<AppCarBrand> brands;

    // internal data
    private final int UPDATE_INTERVAL = 60000; // update Data each 60 sec
    private static boolean isUpdatingDataStore = false;


    private DataStore() {
        try {
            handler = new Handler();
            updateListeners = new ArrayList<DataStoreUpdateListener>();
            serverHandler = ServerAccess.getInstance();
            getLocalData();
        } catch (Exception ignored) {
        }
    }

    public static DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }


    /**
     * used to invoke the DataRequestCallback on the main thread
     */
    private void invokeCallback(final DataRequestCallback callback, final boolean success, final ServerResult result) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callback == null)
                    return;
                callback.onDataReady(result, success);
            }
        });
    }

    public void clearLocalData() {
        try {
            DataCacheProvider.getInstance().clearCache();
            me = null;
            brands = null;
        } catch (Exception ignored) {
        }
    }

    public void logout() {
        clearLocalData();
        broadcastloginStateChange();
    }

    public void getLocalData() {
        DataCacheProvider cache = DataCacheProvider.getInstance();

        //brands = cache.getStoredArrayWithKey(DataCacheProvider.KEY_APP_ARRAY_BRANDS, new TypeToken<ArrayList<BrandModel>>() {}.getType());
        me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, new TypeToken<AppUser>() {
        }.getType());
    }

    //--------------------
    // DataStore Update
    //-------------------------------------------

    public void startScheduledUpdates() {
        try {
            // start schedule timer
            handler.post(runnableUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopScheduledUpdates() {
        try {
            handler.removeCallbacks(runnableUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Runnable runnableUpdate = new Runnable() {
        @Override
        public void run() {
            //requestBrandsWithProducts(null);

            if (isUserLoggedIn()) {

            }
            handler.postDelayed(runnableUpdate, UPDATE_INTERVAL);
        }
    };

    public void triggerDataUpdate() {
        // get Following list and cache it for later use
        if (isUserLoggedIn()) {

        }
    }

    private void broadcastDataStoreUpdate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (DataStoreUpdateListener listener : updateListeners) {
                    listener.onDataStoreUpdate();
                }
            }
        });
    }

    private void broadcastUserLocationUpdate() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (DataStoreUpdateListener listener : updateListeners) {
                    listener.onUserLocationUpdate();
                }
            }
        });
    }

    public void removeUpdateBroadcastListener(DataStoreUpdateListener listener) {
        if (updateListeners != null && updateListeners.contains(listener))
            updateListeners.remove(listener);
    }

    public void addUpdateBroadcastListener(DataStoreUpdateListener listener) {
        if (updateListeners == null)
            updateListeners = new ArrayList();
        if (!updateListeners.contains(listener))
            updateListeners.add(listener);
    }

    private void broadcastloginStateChange() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                for (DataStoreUpdateListener listener : updateListeners) {
                    listener.onLoginStateChange();
                }
            }
        });
    }

    //--------------------
    // Login
    //-------------------------------------------

    /**
     * @param email
     * @param callback
     */
    public void attemptSignUp(final String email,final String password,final String fullName,
                              final String phone,final String gender,final String birthday,
                              final String countryId,final String socialId,final String socialPlatform,
                              final DataRequestCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.registerUser(email,password,fullName,phone,gender,birthday,countryId,socialId,socialPlatform);
                if (result.connectionFailed()) {
                    success = false;
                } else {
                    try {
                        if(result.isValid()){
                            UserModel me = (UserModel) result.getPairs().get("appUser");
                            apiAccessToken = me.getToken();
                            setApiAccessToken(apiAccessToken);
                            setMe(me);
                            broadcastloginStateChange();
                        }
                    } catch (Exception e) {
                        success = false;
                    }
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void updateUserInfo(final String email, final String fullName,
                              final String phone,final String gender,final String birthday,
                              final String countryId,final String filePath,
                              final DataRequestCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.updateUser(email,fullName,phone,gender,birthday,countryId,filePath);
                if (result.connectionFailed()) {
                    success = false;
                } else {
                    try {
                        if(result.isValid()){
                            UserModel me = (UserModel) result.getPairs().get("appUser");
                            apiAccessToken = me.getToken();
                            setApiAccessToken(apiAccessToken);
                            setMe(me);
                            broadcastloginStateChange();
                        }
                    } catch (Exception e) {
                        success = false;
                    }
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void bookItem(final String startDate, final String endDate,
                              final String itemId, final String partnerId,
                              final DataRequestCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.bookItem(startDate,endDate,itemId,partnerId);
                if (result.connectionFailed()) {
                    success = false;
                } else {
                    try {
                        if(result.isValid()){

                        }
                    } catch (Exception e) {
                        success = false;
                    }
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void attemptForgetUserPassword(final String email, final DataRequestCallback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.forgetUserPassword(email);
                if (result.connectionFailed()) {
                    success = false;
                } else {
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    /**
     * attempting login using phone number
     *
     * @param email
     */
    public void attemptLogin(final String email,final String password,final String socialId,final String socialPlatform, final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.login(email,password,socialId,socialPlatform);
                if (result.getRequestStatusCode() >= 400) {
                    success = false;
                } else {
                    if (result.isValid()) {
                        me = (UserModel) result.getPairs().get("appUser");
                        apiAccessToken = me.getToken();
                        setApiAccessToken(apiAccessToken);
                        setMe(me);
                        broadcastloginStateChange();
                    }
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void attemptPartnerLogin(final String email,final String password,final String socialId,final String socialPlatform, final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.partnerLogin(email,password,socialId,socialPlatform);
                if (result.getRequestStatusCode() >= 400) {
                    success = false;
                } else {
                    if (result.isValid()) {
                        me = (UserModel) result.getPairs().get("appUser");
                        apiAccessToken = me.getToken();
                        setApiAccessToken(apiAccessToken);
                        setMe(me);
                        broadcastloginStateChange();
                    }
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void logoutUser() {
        try {
            stopScheduledUpdates();
            clearLocalData();
            broadcastloginStateChange();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestVerificationMsg(final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.requestVerificationMsg(apiAccessToken);
                if (result.connectionFailed()) {
                    success = false;
                }
                if (callback != null)
                    invokeCallback(callback, success, result);
            }
        }).start();
    }

    public void verifyAccount(final String verifMsg, final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.verifyAccount(apiAccessToken, verifMsg);
                if (result.connectionFailed()) {
                    success = false;
                } else {
                    //boolean loginSuccess = (Boolean) data.get("verified");
                    if (!result.isValid()) {
                        setAccessMode(App_ACCESS_MODE.NOT_VERIFIED);
                    } else {
                        setAccessMode(App_ACCESS_MODE.VERIFIED);
                    }
                }
                if (callback != null)
                    invokeCallback(callback, success, result);
            }
        }).start();
    }



    //--------------------
    // Getters
    //----------------------------------------------

    public void getCountries(final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.getCountries();
                if (result.getRequestStatusCode() >= 400) {
                    success = false;
                } else {
                    ArrayList<Country> countries = (ArrayList<Country>) result.getPairs().get("countries");
                    setCountries(countries);
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void getCategories(final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.getCategories();
                if (result.getRequestStatusCode() >= 400) {
                    success = false;
                } else {
                    ArrayList<Category> categories = (ArrayList<Category>) result.getPairs().get("categories");
                    setCategories(categories);
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void getNearbyPartners(final float latitude, final float longitude, final int radius, final MapFilters mapFilters, final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.getNearbyPartners(latitude, longitude, radius, mapFilters);
                if (result.getRequestStatusCode() >= 400) {
                    success = false;
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void getOrders(final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.getOrders();
                if (result.getRequestStatusCode() >= 400) {
                    success = false;
                } else {
                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public void getServerNotifications(final DataRequestCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = true;
                ServerResult result = serverHandler.getServerNotifications();
                if (result.getRequestStatusCode() >= 400) {
                    success = false;
                } else {

                }
                invokeCallback(callback, success, result); // invoking the callback
            }
        }).start();
    }

    public ArrayList<AppCarBrand> getBrands() {
        return brands;
    }

    public float getMyLocationLatitude() {
        return myLocationLatitude;
    }

    public float getMyLocationLongitude() {
        return myLocationLongitude;
    }

    public Location getMeLastLocation() {
        return meLastLocation;
    }

    public void setMeLastLocation(Location meLastLocation) {
        this.meLastLocation = meLastLocation;
        this.myLocationLatitude = (float) meLastLocation.getLatitude();
        this.myLocationLongitude = (float) meLastLocation.getLongitude();
        broadcastUserLocationUpdate();
    }

    public boolean isUserLoggedIn() {
        return me != null;
    }

    public UserModel getMe() {
        if (me == null)
            me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, new TypeToken<AppUser>() {
            }.getType());
        return me;
    }

    public void setMe(UserModel newUser) {
        if (isUserLoggedIn())
            this.me = newUser;
        DataCacheProvider.getInstance().storeObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, newUser);
    }

    public void setCategories(ArrayList<Category> categories) {
        DataCacheProvider.getInstance().storeArrayWithKey(DataCacheProvider.KEY_APP_CATEGORIES, categories);
    }

    public void setCountries(ArrayList<Country> countries) {
        DataCacheProvider.getInstance().storeArrayWithKey(DataCacheProvider.KEY_APP_COUNTRIES, countries);
    }

    public void setApiAccessToken(String apiAccessToken) {
        this.apiAccessToken = apiAccessToken;
        DataCacheProvider.getInstance().storeStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN, apiAccessToken);

    }

    public void setAccessMode(App_ACCESS_MODE accessMode) {
        this.accessMode = accessMode;
        DataCacheProvider.getInstance().storeIntWithKey(DataCacheProvider.KEY_APP_ACCESS_MODE, accessMode.ordinal());
    }

    public App_ACCESS_MODE getAccessMode() {
        return accessMode;
    }

    public boolean isLoggedIn() {
        return apiAccessToken != null && !apiAccessToken.isEmpty() && accessMode == App_ACCESS_MODE.VERIFIED;
    }

    // interfaces
    public interface DataRequestCallback {
        void onDataReady(ServerResult result, boolean success);
    }

    public interface DataStoreUpdateListener {
        void onDataStoreUpdate();
        void onUserLocationUpdate();
        void onNewEventNotificationsAvailable();

        void onLoginStateChange();
    }

    public interface DataStoreErrorListener {
        void onError(GENERIC_ERROR_TYPE error);
    }
}