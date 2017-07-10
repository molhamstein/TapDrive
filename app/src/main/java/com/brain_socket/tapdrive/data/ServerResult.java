package com.brain_socket.tapdrive.data;

import java.util.HashMap;

public class ServerResult {

    private static String[] errors = {
            ServerAccess.USER_EXIST_BEFORE,
            ServerAccess.USER_NOT_EXIST,
            ServerAccess.UNKNOWN_EXCEPTION ,
            ServerAccess.MODEL_NOT_FOUND,
            ServerAccess.VALIDATION_ERROR,
            ServerAccess.USER_NOT_FOUND,
    };

    private int statusCode;
    private String apiError;
    private HashMap<String, Object> pairs;

//	public ServerResult(int statusCode, int flag) {
//		this.statusCode = statusCode;
//		this.flag = flag;
//		pairs = new HashMap<String, Object>();
//	}

    public ServerResult() {
        pairs = new HashMap<String, Object>();
    }

    public HashMap<String, Object> getPairs() {
        return pairs;
    }

    public Object get(String key){
        if(pairs != null && pairs.containsKey(key))
            return pairs.get(key);
        return  null;
    }

    public void setPairs(HashMap<String, Object> pairs) {
        this.pairs = pairs;
    }

//	public int getFlag() {
//		return flag;
//	}

    public int getRequestStatusCode(){
        return  statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setApiError(String apiError) {
        this.apiError = apiError;
    }

    public String getApiError() {
        return apiError;
    }

    public void addPair(String key, Object value) {
        this.pairs.put(key, value);
    }

    public Object getValue(String key) {
        return this.pairs.get(key);
    }

    public boolean isValid() {
        if(apiError == "")
            return true;
        for(String error : errors) {
            if(apiError.equals(error))
                return false;
        }
        return true;
    }

    public boolean connectionFailed (){
        //return  apiError != "" || statusCode >= 600 ;
        return  statusCode >= 600 ;
    }
}