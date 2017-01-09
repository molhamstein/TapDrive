package com.brain_socket.tapdrive.data;

import android.provider.Settings;

import com.brain_socket.tapdrive.TapApp;
import com.brain_socket.tapdrive.model.AppCar;
import com.brain_socket.tapdrive.model.AppCarBrand;
import com.brain_socket.tapdrive.model.AppUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class ServerAccess {
    // GENERIC ERROR CODES

    public static final int ERROR_CODE_userExistsBefore = -2;
    public static final int ERROR_CODE_userNotExists = -3;
    public static final int ERROR_CODE_UNAUTHENTICATED = -4;
    public static final int ERROR_CODE_unknown = -1000;
    public static final int ERROR_CODE_appVersionInvalid= -121;
    public static final int ERROR_CODE_updateAvailable=  -122;

    public static final int RESPONCE_FORMAT_ERROR_CODE = 601 ;
    public static final int CONNECTION_ERROR_CODE = 600 ;
    public static final int REQUEST_SUCCESS_CODE = 0 ;

    // api
    static final String BASE_SERVICE_URL = "http://thageralrafedain.com/mobile_app/api";

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

    //////////////////
    // login
    /////////////////
    public ServerResult login(String phoneNum) {
        ServerResult result = new ServerResult();
        AppUser me  = null ;
        boolean isRegistered = false;
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("mobile_number", phoneNum);
            try{
                String deviceId = Settings.Secure.getString(TapApp.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                jsonPairs.put("imei", deviceId);
            } catch(Exception e){
                e.printStackTrace();
            }
            // url
            String url = BASE_SERVICE_URL + "/auth/mobileLogin";
            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = new JSONObject(apiResult.response);
            if (jsonResponse != null && apiResult.statusCode != 400) { // check if response is empty
                me = AppUser.fromJson(jsonResponse);
                isRegistered = true;
            }
            if(apiResult.statusCode == 400 && result.getApiError().equals("MODEL_NOT_FOUND")){
                isRegistered = false;
                result.setStatusCode(ERROR_CODE_userNotExists);
            }
        } catch (Exception e) {
            result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        result.addPair("appUser", me);
        result.addPair("isRegistered",isRegistered);
        return result;
    }

    /**
     * register a new user with UserName and phoneNumber
     */
    public ServerResult registerUser(String fName,String lName, String phoneNum, String countryCode, String versionId, String FBID) {
        ServerResult result = new ServerResult();
        AppUser me  = null ;
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("country_id", 1);
            jsonPairs.put("mobile_number", phoneNum);
            jsonPairs.put("name", fName);
            jsonPairs.put("country_code", countryCode);
            jsonPairs.put("facebook_token", FBID);

            try{
                String deviceId = Settings.Secure.getString(TapApp.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                jsonPairs.put("imei", deviceId);
            }catch(Exception e){
                e.printStackTrace();
            }
            // url
            String url = BASE_SERVICE_URL + "/auth/register";
            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs, "post", null);
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            JSONObject jsonResponse = new JSONObject(apiResult.response);
            if (jsonResponse != null) { // check if response is empty
                me = AppUser.fromJson(jsonResponse);
            }
            if(apiResult.statusCode == 409 && result.getApiError().equals("USER_EXIST_BEFORE")){
                result.setStatusCode(ERROR_CODE_userExistsBefore);
            }
        } catch (Exception e) {
            result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        result.addPair("appUser", me);

        return result;
    }

    public ServerResult requestVerificationMsg(String accessToken) {
        ServerResult result = new ServerResult();
        boolean msgSent = false ;
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("access_token", accessToken);
            // url
            String url = BASE_SERVICE_URL + "/index.php/verification_messages_api/send_verification_code";
            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs,"post",null);
            JSONObject jsonResponse = apiResult.getResponseJsonObject();
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            if (jsonResponse != null) { // check if response is empty
                if(apiResult.getStatusCode() == REQUEST_SUCCESS_CODE){
                    msgSent = true ;
                }
            }
        } catch (Exception e) {
            result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        result.addPair("msgSent", msgSent);

        return result;
    }

    public ServerResult verifyAccount(String accessToken, String verifMsg) {
        ServerResult result = new ServerResult();
        boolean verified = false ;
        try {
            // parameters
            JSONObject jsonPairs = new JSONObject();
            jsonPairs.put("access_token", accessToken);
            jsonPairs.put("verification_code", verifMsg);
            // url
            String url = BASE_SERVICE_URL + "/index.php/verification_messages_api/accept_verification_code";
            // send request
            ApiRequestResult apiResult = httpRequest(url, jsonPairs,"post",null);
            JSONObject jsonResponse = apiResult.getResponseJsonObject();
            result.setStatusCode(apiResult.getStatusCode());
            result.setApiError(apiResult.getApiError());
            if (jsonResponse != null) { // check if response is empty
                if(apiResult.getStatusCode() == REQUEST_SUCCESS_CODE){
                    verified = true ;
                }
            }
        } catch (Exception e) {
            result.setStatusCode(RESPONCE_FORMAT_ERROR_CODE);
        }
        result.addPair("verified", verified);
        return result;
    }


    public ServerResult getNearbyWorkshops(float centerLat, float centerLng, float radius, ArrayList<AppCarBrand> brands) {
        ServerResult result = new ServerResult();
        ArrayList<AppCar> workshops = null;
        try{
            JSONObject params = new JSONObject();

            params.put("lat",centerLat);
            params.put("lon",centerLng);
            params.put("dist",radius);

            if(brands != null) {
                ArrayList<String> brandsIdsArray = new ArrayList<>();
                for (AppCarBrand brand : brands) {
                    brandsIdsArray.add(brand.getId());
                }
                String commaSeperatedArray = brandsIdsArray.toString();
                commaSeperatedArray = commaSeperatedArray.replace("[", "").replace("]", "").replaceAll("\\s", "").trim();
                params.put("brands",commaSeperatedArray);
            }

            Random rand = new Random();
            workshops = new ArrayList<>();

            AppCar car = new AppCar();

            car.setLat(25.194f);
            car.setLng(55.278f);
            car.setType(0);
            workshops.add(car);

            car = new AppCar();
            car.setLat(55.2395999f);
            car.setLng(25.188200f);
            car.setType(1);
            workshops.add(car);

            car = new AppCar();
            car.setLat(25.188444f);
            car.setLng(55.2496729f);
            car.setType(2);
            workshops.add(car);

            car = new AppCar();
            car.setLat(25.188444f);
            car.setLng(55.2501729f);
            car.setType(3);
            workshops.add(car);

            car = new AppCar();
            car.setLat(25.187234f);
            car.setLng(55.2475229f);
            car.setType(1);
            workshops.add(car);

            car = new AppCar();
            car.setLat(25.188244f);
            car.setLng(55.2495429f);
            car.setType(3);
            workshops.add(car);

            car = new AppCar();
            car.setLat(25.168244f);
            car.setLng(55.2495529f);
            car.setType(3);
            workshops.add(car);

//            String url = BASE_SERVICE_URL+"/getNearby.php";
//            ApiRequestResult apiResult = httpRequest(url,params,"post",null);
//            JSONArray jsonResponse = apiResult.getResponseJsonArray();
//            if(jsonResponse != null){
//                workshops = new ArrayList<>();
//                for(int i=0;i<jsonResponse.length();i++){
//                    JSONObject ob = jsonResponse.getJSONObject(i);
//                    workshops.add(WorkshopModel.fromJson(ob));
//                }
//            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        result.addPair("workshops",workshops);
        return result;
    }

    /**
     * send Https request through connection
     * @param method  get or post
     */
    public ApiRequestResult httpRequest(String url, JSONObject jsonPairs, String method, JSONObject headers) {
        String result = null;
        int responseCode;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

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

    public static class ApiRequestResult{
        String response;
        int statusCode;
        int apiErrorCode;
        JSONObject jsonResponse;

        public boolean connectionSuccess(){
            return  statusCode <400;
        }

        public JSONObject getResponseJsonObject() throws JSONException {
            if (response != null && !response.equals("")) { // check if response is empty
                if(jsonResponse == null )
                    jsonResponse = new JSONObject(response);
                if(!jsonResponse.isNull("object"))
                    return jsonResponse.getJSONObject("object");
            }
            return null;
        }

        public JSONArray getResponseJsonArray() throws JSONException{
            JSONArray res = null;
            if (response != null && !response.equals("")) { // check if response is empty
                res = new JSONArray(response);
            }
            return res;
        }

        public int getStatusCode(){
            return statusCode;
        }

        public String getApiError() {
            try {
                if(jsonResponse == null )
                    jsonResponse = new JSONObject(response);
                if(jsonResponse.has("error"))
                    return jsonResponse.getString("error");
                else
                    return null;
            }catch (Exception e){}
            return "";
        }
    }




}