package com.brain_socket.tapdrive.data;

import android.util.Log;

import com.brain_socket.tapdrive.model.filters.Category;
import com.brain_socket.tapdrive.model.filters.CategoryField;
import com.brain_socket.tapdrive.model.filters.MapFilters;
import com.brain_socket.tapdrive.model.orders.Order;
import com.brain_socket.tapdrive.model.orders.ServerNotification;
import com.brain_socket.tapdrive.model.partner.Car;
import com.brain_socket.tapdrive.model.partner.Country;
import com.brain_socket.tapdrive.model.partner.Invoice;
import com.brain_socket.tapdrive.model.partner.Partner;
import com.brain_socket.tapdrive.model.user.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ServerAccess {
    // GENERIC ERROR CODES

    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String INCORRECT_EMAIL_OR_PASSWORD = "INCORRECT_EMAIL_OR_PASSWORD";
    public static final String USER_EXIST_BEFORE = "USER_EXIST_BEFORE";
    public static final String USER_NOT_EXIST = "USER_NOT_EXIST";
    public static final String UNAUTHENTICATED = "UNAUTHENTICATED";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String MODEL_NOT_FOUND = "MODEL_NOT_FOUND";
    public static final String UNKNOWN_EXCEPTION = "UNKNOWN_EXCEPTION";
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE";
    public static final String TOKEN_NOT_PROVIDED = "TOKEN_NOT_PROVIDED";
    public static final String TOKEN_EXPIRED = "TOKEN_EXPIRED";
    public static final String TOKEN_INVALID = "TOKEN_INVALID";
    public static final String INVALID_RESET_PASSWORD_TOKEN = "INVALID_RESET_PASSWORD_TOKEN";

    // api
    static final String BASE_SERVICE_URL = "http://tap-drive.com/tapdrive_api/public/index.php/api/v1";

    private static ServerAccess serverAccess = null;

    private ServerAccess() {

    }

    public static ServerAccess getInstance() {
        if (serverAccess == null) {
            serverAccess = new ServerAccess();
        }
        return serverAccess;
    }
    // API calls // ------------------------------------------------

    public ServerResult login(String email, String password, String name, String socialId, String socialPlatform) {
        ServerResult result = new ServerResult();
        UserModel me = null;
        boolean isRegistered = false;
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("email", email);
            jsonPairs.put("password", password);
            jsonPairs.put("social_id", socialId);
            jsonPairs.put("social_platform", socialPlatform);
            if(name != null && !name.isEmpty())
                jsonPairs.put("username", name);

            // url
            String url = BASE_SERVICE_URL + "/auth/login";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = apiResult.getResponseJsonObject(); //new JSONObject(apiResult.response);
            if (jsonResponse != null && apiResult.statusCode == 200) { // check if response is empty
                me = UserModel.fromJson(jsonResponse);
                isRegistered = true;
            }
            if (apiResult.statusCode == 200 && result.getApiError().equals(MODEL_NOT_FOUND)) {
                isRegistered = false;
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        result.addPair("appUser", me);
        result.addPair("isRegistered", isRegistered);
        return result;
    }

    public ServerResult partnerLogin(String email, String password) {
        ServerResult result = new ServerResult();
        UserModel me = null;
        boolean isRegistered = false;
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("email", email);
            jsonPairs.put("password", password);

            // url
            String url = BASE_SERVICE_URL + "/partners/login";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = apiResult.getResponseJsonObject(); //new JSONObject(apiResult.response);
            if (jsonResponse != null && apiResult.statusCode == 200) { // check if response is empty
                me = UserModel.fromJson(jsonResponse);
                isRegistered = true;
            }
            if (apiResult.statusCode == 200 && result.getApiError().equals(MODEL_NOT_FOUND)) {
                isRegistered = false;
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        result.addPair("appUser", me);
        result.addPair("isRegistered", isRegistered);
        return result;
    }

    /**
     * register a new user with UserName and phoneNumber
     */
    public ServerResult registerUser(String email, String password, String fullName,
                                     String phone, String gender, String birthday,
                                     String countryId, String socialId, String socialPlatform) {
        ServerResult result = new ServerResult();
        UserModel me = null;
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("email", email);
            jsonPairs.put("password", password);
            jsonPairs.put("full_name", fullName);
            jsonPairs.put("phone", phone);
            jsonPairs.put("gender", gender.toLowerCase());
            jsonPairs.put("birthday", birthday);
            jsonPairs.put("country_id", countryId);
            jsonPairs.put("social_id", socialId);
            jsonPairs.put("social_platform", socialPlatform);

            // url
            String url = BASE_SERVICE_URL + "/auth/register";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = apiResult.getResponseJsonObject();
            if (jsonResponse != null) { // check if response is empty
                me = UserModel.fromJson(jsonResponse);
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        result.addPair("appUser", me);

        return result;
    }

    public ServerResult updateUser(String email, String fullName,
                                   String phone, String gender, String birthday,
                                   String countryId, String filePath) {
        ServerResult result = new ServerResult();
        UserModel me = null;
        try {

            // url
            String url = BASE_SERVICE_URL + "/users";
            String results;
            Response response;

            if (filePath != null) {
                MediaType MEDIA_TYPE = MediaType.parse("image/jpg");

                String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
                if (extension.equalsIgnoreCase("png")) {
                    MEDIA_TYPE = MediaType.parse("image/png");
                }

                Log.d("EYAD", "updateUser: " + filePath);
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("photo", filePath, RequestBody.create(MEDIA_TYPE, new File(filePath)))
                        .addFormDataPart("full_name", fullName)
                        .addFormDataPart("phone", phone)
                        .addFormDataPart("gender", gender.toLowerCase())
                        .addFormDataPart("birthday", birthday)
                        .addFormDataPart("country_id", countryId)
                        .addFormDataPart("_method", "PUT")
                        .build();
                Request request = new Request.Builder().url(url).addHeader("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN)).post(body).build();
                response = client.newCall(request).execute();
                results = response.body().string();
            } else {

                Log.d("EYAD", "updateUser: " + filePath);
                OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
                RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("full_name", fullName)
                        .addFormDataPart("phone", phone)
                        .addFormDataPart("gender", gender.toLowerCase())
                        .addFormDataPart("birthday", birthday)
                        .addFormDataPart("country_id", countryId)
                        .addFormDataPart("_method", "PUT")
                        .build();
                Request request = new Request.Builder().url(url).addHeader("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN)).post(body).build();
                response = client.newCall(request).execute();
                results = response.body().string();

            }

            result.setStatusCode(response.code());
            result.setApiError("");
            JSONObject jsonResponse = new JSONObject(results);
            Log.d("EYAD", "updateUser: " + jsonResponse);
            if (jsonResponse != null) { // check if response is empty
                me = UserModel.fromJson(jsonResponse.getJSONObject("data"));
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
            e.printStackTrace();
        }
        result.addPair("appUser", me);

        return result;
    }

    public ServerResult forgetUserPassword(String email) {
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("email", email);

            // url
            String url = BASE_SERVICE_URL + "/auth/forget_password";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = apiResult.getResponseJsonObject();
            if (jsonResponse != null) { // check if response is empty

            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }

        return result;
    }

    public ServerResult forgetPartnerPassword(String email) {
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("email", email);

            // url
            String url = BASE_SERVICE_URL + "/partners/forget_password";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = apiResult.getResponseJsonObject();
            if (jsonResponse != null) { // check if response is empty

            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }

        return result;
    }

    public ServerResult resetUserPassword(String email, String token, String newPsw) {
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("email", email);
            jsonPairs.put("token", token);
            jsonPairs.put("password", newPsw);
            jsonPairs.put("password_confirmation", newPsw);

            // url
            String url = BASE_SERVICE_URL + "/auth/reset_password";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = apiResult.getResponseJsonObject();
            if (jsonResponse != null) { // check if response is empty

            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }

        return result;
    }

    public ServerResult resetPartnerPassword(String email, String token, String newPsw){
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("email", email);
            jsonPairs.put("token", token);
            jsonPairs.put("password", newPsw);
            jsonPairs.put("password_confirmation", newPsw);

            // url
            String url = BASE_SERVICE_URL + "/partners/reset_password";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = apiResult.getResponseJsonObject();
            if (jsonResponse != null) { // check if response is empty

            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }

        return result;
    }

    public ServerResult bookItem(String startDate, String endDate, String itemId, String partnerId) {
        ServerResult result = new ServerResult();
        try {
            // parametersn
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("start_date", startDate);
            jsonPairs.put("end_date", endDate);
            jsonPairs.put("item_id", itemId);
            jsonPairs.put("partner_id", partnerId);

            JSONObject headers = new JSONObject();
            headers.put("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN));


            // url
            String url = BASE_SERVICE_URL + "/orders";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", headers);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        return result;

    }

    public ServerResult changeItemStatus(int orderId, String status) {
        ServerResult result = new ServerResult();
        try {

            Log.d("EYAD", "changeItemStatus: " + status);

            // parametersn
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("order_id", orderId);
            jsonPairs.put("status", status);
            jsonPairs.put("_method", "PUT");

            JSONObject headers = new JSONObject();
            headers.put("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN));

            Log.d("EYAD", "changeItemStatus: " + DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN));
            Log.d("EYAD", "changeItemStatus: " + jsonPairs.toString());

            // url
            String url = BASE_SERVICE_URL + "/orders/change_status";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", headers);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());

            JSONObject jsonObject = apiResult.getResponseJsonObject();
            Log.d("EYAD", "changeItemStatus: " + apiResult.getStatusCode());
            Log.d("EYAD", "changeItemStatus: " + jsonObject);
            Order order = Order.fromJson(jsonObject);
            result.addPair("order", order);

        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        return result;

    }

    public ServerResult setFCMId(String fcmToken) {
        ServerResult result = new ServerResult();
        try {

            // parametersn
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("fcm_id", fcmToken);
            jsonPairs.put("_method", "PUT");

            JSONObject headers = new JSONObject();
            headers.put("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN));

            // url
            String url = BASE_SERVICE_URL + "/base_user/set_fcm_id";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", headers);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());

            JSONObject jsonObject = apiResult.getResponseJsonObject();
            Log.d("EYAD", "setFCMId: " + jsonObject);

        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        return result;

    }

    public ServerResult requestVerificationMsg(String accessToken) {
        ServerResult result = new ServerResult();
//        boolean msgSent = false ;
//        try {
//            // parameters
//            JSONObject jsonPairs = new JSONObject();
//            jsonPairs.put("access_token", accessToken);
//            // url
//            String url = BASE_SERVICE_URL + "/index.php/verification_messages_api/send_verification_code";
//            // send request
//            ApiRequestResult apiResult = httpRequest(url, jsonPairs,"post",null);
//            JSONObject jsonResponse = apiResult.getResponseJsonObject();
//            result.setStatusCode(apiResult.getStatusCode());
//            result.setApiError(apiResult.getApiError());
//            if (jsonResponse != null) { // check if response is empty
//                if(apiResult.getStatusCode() == REQUEST_SUCCESS_CODE){
//                    msgSent = true ;
//                }
//            }
//        } catch (Exception e) {
//            result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
//        }
//        result.addPair("msgSent", msgSent);

        return result;
    }

    public ServerResult verifyAccount(String accessToken, String verifMsg) {
        ServerResult result = new ServerResult();
//        boolean verified = false ;
//        try {
//            // parameters
//            JSONObject jsonPairs = new JSONObject();
//            jsonPairs.put("access_token", accessToken);
//            jsonPairs.put("verification_code", verifMsg);
//            // url
//            String url = BASE_SERVICE_URL + "/index.php/verification_messages_api/accept_verification_code";
//            // send request
//            ApiRequestResult apiResult = httpRequest(url, jsonPairs,"post",null);
//            JSONObject jsonResponse = apiResult.getResponseJsonObject();
//            result.setStatusCode(apiResult.getStatusCode());
//            result.setApiError(apiResult.getApiError());
//            if (jsonResponse != null) { // check if response is empty
//                if(apiResult.getStatusCode() == REQUEST_SUCCESS_CODE){
//                    verified = true ;
//                }
//            }
//        } catch (Exception e) {
//            result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
//        }
//        result.addPair("verified", verified);
        return result;
    }

    public ServerResult getCategories() {
        ServerResult result = new ServerResult();

        try {
            // url
            String url = BASE_SERVICE_URL + "/categories";

            // send request
            ApiRequestResult apiResult = httpRequest(url, null, "get", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONArray jsonResponse = apiResult.getResponseJsonArray();
            if (jsonResponse != null) { // check if response is empty
                ArrayList<Category> categories = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    categories.add(Category.fromJson(jsonResponse.getJSONObject(i)));
                }
                result.addPair("categories", categories);
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }

        return result;

    }

    public ServerResult getNearbyPartners(int categoryId, final float latitude, final float longitude, final int radius, final MapFilters mapFilters) {
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("category_id", categoryId);
            jsonPairs.put("radius", radius);
            jsonPairs.put("longitude", longitude);
            jsonPairs.put("latitude", latitude);
            jsonPairs.put("price_from", mapFilters.getPriceFrom());
            jsonPairs.put("price_to", mapFilters.getPriceTo());
            jsonPairs.put("category_options_ids", mapFilters.getCategoryOptionsIds());


            // url
            String url = BASE_SERVICE_URL + "/partners/nearby";

            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONArray jsonResponse = apiResult.getResponseJsonArray();
            if (jsonResponse != null) { // check if response is empty
                ArrayList<Partner> partners = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    partners.add(Partner.fromJson(jsonResponse.getJSONObject(i)));
                }
                result.addPair("partners", partners);
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        return result;

    }

    public ServerResult getNearbyPartners(final float latitude, final float longitude, final int radius, final MapFilters mapFilters) {
        return getNearbyPartners(1, latitude, longitude, radius, mapFilters);
    }

    public ServerResult getOrders() {
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject headers = new JSONObject();
            headers.put("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN));

            // url
            String url = BASE_SERVICE_URL + "/orders";

            // send request
            ApiRequestResult apiResult = httpRequest(url, null, "get", headers);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONArray jsonResponse = apiResult.getResponseJsonArray();
            if (jsonResponse != null) { // check if response is empty
                ArrayList<Order> orders = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    orders.add(Order.fromJson(jsonResponse.getJSONObject(i)));
                }
                result.addPair("orders", orders);
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
            e.printStackTrace();
        }
        return result;
    }

    public ServerResult getServerNotifications() {
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject headers = new JSONObject();
            headers.put("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN));

            // url
            String url = BASE_SERVICE_URL + "/notifications";

            // send request
            ApiRequestResult apiResult = httpRequest(url, null, "get", headers);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONArray jsonResponse = apiResult.getResponseJsonArray();
            if (jsonResponse != null) { // check if response is empty
                ArrayList<ServerNotification> serverNotifications = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    serverNotifications.add(ServerNotification.fromJson(jsonResponse.getJSONObject(i)));
                }
                result.addPair("server_notifications", serverNotifications);
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
            e.printStackTrace();
        }
        return result;
    }

    public ServerResult getPartnerCars() {
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject headers = new JSONObject();
            headers.put("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN));

            // url
            String url = BASE_SERVICE_URL + "/items";

            // send request
            ApiRequestResult apiResult = httpRequest(url, null, "get", headers);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONArray jsonResponse = apiResult.getResponseJsonArray();
            if (jsonResponse != null) { // check if response is empty
                ArrayList<Car> orders = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    orders.add(Car.fromJson(jsonResponse.getJSONObject(i)));
                }
                result.addPair("cars", orders);
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
            e.printStackTrace();
        }
        return result;
    }

    public ServerResult getPartnerInvoices() {
        ServerResult result = new ServerResult();
        try {
            // parameters
            JSONObject headers = new JSONObject();
            headers.put("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN));

            // url
            String url = BASE_SERVICE_URL + "/invoices";

            // send request
            ApiRequestResult apiResult = httpRequest(url, null, "get", headers);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONArray jsonResponse = apiResult.getResponseJsonArray();
            if (jsonResponse != null) { // check if response is empty
                ArrayList<Invoice> invoices = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    invoices.add(Invoice.fromJson(jsonResponse.getJSONObject(i)));
                }
                result.addPair("invoices", invoices);
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
            e.printStackTrace();
        }
        return result;
    }

    public ServerResult createCar(Car newCar, ArrayList<CategoryField> fields, ArrayList<String> selectedOptionsIds, String imagePath) {
        ServerResult result = new ServerResult();
        try {
            // url
            String url = BASE_SERVICE_URL + "/items";
            String results;
            Response response;

            String strOptions = "";
            String strFields = "";
            for (int i = 0; i < fields.size(); i++) {
                strOptions += strOptions + selectedOptionsIds.get(i)+",";
                strFields += strFields + fields.get(i).getId() + ",";
            }
            strFields = strFields.substring(0,strFields.length()-2);
            strOptions = strOptions.substring(0,strOptions.length()-2);

            MediaType MEDIA_TYPE = MediaType.parse("image/jpg");
            String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
            if (extension.equalsIgnoreCase("png")) {
                MEDIA_TYPE = MediaType.parse("image/png");
            }

            OkHttpClient client = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).build();
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("photo", imagePath, RequestBody.create(MEDIA_TYPE, new File(imagePath)))
                    .addFormDataPart("name_en", newCar.getEnglishName())
                    .addFormDataPart("name_ar", newCar.getArabicName())
                    .addFormDataPart("hourly_price", newCar.getHourlyPrice())
                    .addFormDataPart("daily_price", newCar.getDailyPrice())
                    .addFormDataPart("weekly_price", newCar.getWeeklyPrice())
                    .addFormDataPart("monthly_price", newCar.getMonthlyPrice())
                    .addFormDataPart("fields_ids", strFields)
                    .addFormDataPart("options_ids", strOptions)
                    .addFormDataPart("category_id", "1")
                    .build();
            Request request = new Request.Builder().url(url).addHeader("token", DataCacheProvider.getInstance().getStoredStringWithKey(DataCacheProvider.KEY_ACCESS_TOKEN)).post(body).build();
            response = client.newCall(request).execute();
            results = response.body().string();

            result.setStatusCode(response.code());
            result.setApiError("");
            JSONObject jsonResponse = new JSONObject(results);
            if (jsonResponse != null) { // check if response is empty
                //me = UserModel.fromJson(jsonResponse.getJSONObject("data"));
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
            e.printStackTrace();
        }
        //result.addPair("appUser", me);

        return result;
    }

    public ServerResult getCountries() {
        ServerResult result = new ServerResult();

        try {
            // url
            String url = BASE_SERVICE_URL + "/countries";

            // send request
            ApiRequestResult apiResult = httpRequest(url, null, "get", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONArray jsonResponse = apiResult.getResponseJsonArray();
            if (jsonResponse != null) { // check if response is empty
                ArrayList<Country> countries = new ArrayList<>();
                for (int i = 0; i < jsonResponse.length(); i++) {
                    countries.add(Country.fromJson(jsonResponse.getJSONObject(i)));
                }
                result.addPair("countries", countries);
            }
        } catch (Exception e) {
            //result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }

        return result;

    }

    /**
     * send Https request through connection
     *
     * @param method get or post
     */
    public ApiRequestResult httpRequest(String url, JSONObject jsonPairs, String method, JSONObject headers) {
        String result = null;
        int responseCode;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setConnectTimeout(10000);

            //configure connection
            con.setUseCaches(false);
            // add headers
            if (headers != null) {
                Iterator keys = headers.keys();
                while (keys.hasNext()) {
                    // loop to get the dynamic key
                    String key = (String) keys.next();
                    String value = headers.getString(key);
                    con.setRequestProperty(key, value);
                }
            }
            //con.setRequestProperty("Content-Type","application/json");
            //con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //application/x-www-form-urlencoded
            if (method.equalsIgnoreCase("post") || method.equalsIgnoreCase("delete")) {
                //String urlParameters = jsonPairs.toString();
                StringBuilder postData = new StringBuilder();
                if (jsonPairs != null) {
                    Iterator<String> keys = jsonPairs.keys();
                    while (keys.hasNext()) {
                        if (postData.length() != 0) postData.append('&');
                        String key = keys.next();
                        postData.append(URLEncoder.encode(key, "UTF-8"));
                        postData.append('=');
                        postData.append(URLEncoder.encode(String.valueOf(jsonPairs.get(key)), "UTF-8"));
                    }
                }
                con.setDoOutput(true); // implicitly set to POST
                con.setRequestMethod(method.toUpperCase());
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postData.toString());
                wr.flush();
                wr.close();
            }

            responseCode = con.getResponseCode();
            if (responseCode >= 400) {
                try {
                    // try to read returned error response
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    result = response.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                result = response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            responseCode = 600; // indicates connection failure
        }
        ApiRequestResult requestResult = new ApiRequestResult();
        requestResult.statusCode = responseCode;
        requestResult.response = result;
        return requestResult;
    }

    public static class ApiRequestResult {
        String response;
        int statusCode;
        int apiErrorCode;
        JSONObject jsonResponse;

        public boolean connectionSuccess() {
            return statusCode < 400;
        }

        public JSONObject getResponseJsonObject() throws JSONException {
            if (response != null && !response.equals("")) { // check if response is empty
                if (jsonResponse == null)
                    jsonResponse = new JSONObject(response);
                if (jsonResponse.has("data"))
                    return jsonResponse.getJSONObject("data");
            }
            return null;
        }

        public JSONArray getResponseJsonArray() throws JSONException {
            if (response != null && !response.equals("")) { // check if response is empty
                if (jsonResponse == null)
                    jsonResponse = new JSONObject(response);
                if (jsonResponse.has("data"))
                    return jsonResponse.getJSONArray("data");
            }
            return null;
        }

        public int getStatusCode() {
            return statusCode;
        }

        public String getApiError() {
            try {
                if (jsonResponse == null)
                    jsonResponse = new JSONObject(response);
                if (jsonResponse.has("error")) {
                    JSONObject errorJsonObject = jsonResponse.getJSONObject("error");
                    return errorJsonObject.getString("code");
                }
            } catch (Exception e) {
            }
            return "";
        }
    }


}