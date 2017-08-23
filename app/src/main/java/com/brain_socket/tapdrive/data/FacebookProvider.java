package com.brain_socket.tapdrive.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.brain_socket.tapdrive.utils.TapApp;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class FacebookProvider {
    private static FacebookProvider facebookProvider = null;

    private FacebookProviderListener listener = null;
    // sharing
    private FacebookSharingListener shareListener = null;

    private enum PendingAction {
        NONE, POST_PHOTO, POST_STATUS_UPDATE, SEND_INVITATION, GET_FRIENDS, POST_STORY
    }

    private PendingAction pendingAction = PendingAction.NONE;

    private static final List<String> PERMISSIONS = new ArrayList<String>() {
        {
            add("user_friends");
            add("public_profile");
            add("email");
        }
    };
    private static final String PUBLISH_PERMISSION = "publish_actions";
    private CallbackManager callbackManager;


    private FacebookCallback<LoginResult> loginCallback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            handlePendingAction();
            final AccessToken accessToken = loginResult.getAccessToken();
            //Profile profile = Profile.getCurrentProfile();
            if (accessToken != null & !accessToken.isExpired()){
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object,GraphResponse response) {
                                try{
                                    // Application code
                                    Log.v("LoginActivity", response.toString());
                                    HashMap<String, Object> map = new HashMap<String, Object>();
                                    JSONObject jsonResp = response.getJSONObject();
                                    try
                                    {
                                        map.put("name", jsonResp.get("name"));
                                    }catch(Exception ignored){
                                        map.put("name", "unknown");
                                    }
                                    try
                                    {
                                        map.put("email", jsonResp.get("email"));
                                    }catch(Exception ignored){
                                        map.put("email", "");
                                    }
                                    try
                                    {
                                        map.put("gender", jsonResp.get("gender"));
                                    }
                                    catch(Exception ignored){
                                        ignored.printStackTrace();
                                    }
                                    try
                                    {
                                        map.put("birthday", jsonResp.get("birthday"));
                                    }
                                    catch(Exception ex){
                                        ex.printStackTrace();
                                    }
                                    broadcastSessionOpened(accessToken.getToken(), accessToken.getUserId(), map);
                                }catch (Exception e) {
                                    broadcastFacebookException(null);
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }
        }

        @Override
        public void onCancel() {
            Log.d("FB", "login Canseled");
        }

        @Override
        public void onError(FacebookException exception) {
            broadcastFacebookException(exception);
        }
    };


    private FacebookProvider() {
        try {
            // add Access Token request from the Facebook SDK Settings
            // Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.CACHE);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.DEVELOPER_ERRORS);
            FacebookSdk.sdkInitialize(TapApp.getAppContext());
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,loginCallback);
        } catch (Exception ignored) {
        }
    }


    /**
     * Returns the singleton of the FacebookProviderListener class
     */
    public static FacebookProvider getInstance() {
        if (facebookProvider == null) {
            facebookProvider = new FacebookProvider();
        }
        return facebookProvider;
    }

    /**
     * Requests the Facebook SDK to login the current user.
     */
    public void requestFacebookLogin(Activity activity) {
        try {
            AccessToken token = AccessToken.getCurrentAccessToken();
            LoginManager.getInstance().logInWithReadPermissions(activity, PERMISSIONS);
            // LoginManager.getInstance().logInWithPublishPermissions(activity, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void requestFacebookLogout() {
        try {
            LoginManager.getInstance().logOut();
        } catch (Exception ignored) {
        }
    }

    public void registerShareListener(FacebookSharingListener shareListener) {
        this.shareListener = shareListener;
    }

    private boolean hasPublishPermission() {
        AccessToken at = AccessToken.getCurrentAccessToken();
        return at != null && at.getPermissions().contains(PUBLISH_PERMISSION);
    }

    private void handlePendingAction() {
        try {
            PendingAction previouslyPendingAction = pendingAction;
            // These actions may re-set pendingAction if they are still pending,
            // but
            // we assume they will succeed.
            pendingAction = PendingAction.NONE;

            switch (previouslyPendingAction) {
                case POST_PHOTO:
                    break;
                case POST_STATUS_UPDATE:
                    break;
                case SEND_INVITATION:
                    // sendInvitations(activiy, msg);
                    break;
                case POST_STORY:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void onActiviyResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * Register a {@link FacebookProviderListener} This must be called before
     * initializing FacebookProviderListener
     */
    public void registerListener(FacebookProviderListener listener) {
        try {
            this.listener = listener;
        } catch (Exception ignored) {
        }
    }

    /**
     * Removes registered listener
     */
    public void unregisterListener() {
        try {
            listener = null;
        } catch (Exception ignored) {
        }
    }

    /**
     * Calls the onFacebookSessionOpened of a registered LoginProviderListener
     * and sends along the accessToken and the userId of the current Session
     *
     * @param accessToken
     */
    private void broadcastSessionOpened(String accessToken, String userId, HashMap<String, Object> map) {
        try {
            if (listener != null) {
                listener.onFacebookSessionOpened(accessToken, userId, map);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Calls the onFacebookSessionClosed of a registered LoginProviderListener
     */
    private void broadcastSessionClosed() {
        try {
            if (listener != null) {
                listener.onFacebookSessionClosed();
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * Calls the onFacebookException of a registered LoginProviderListener
     *
     * @param exception
     *            The Facebook exception received from the SDK
     */
    private void broadcastFacebookException(Exception exception) {
        try {
            if (listener != null) {
                listener.onFacebookException(exception);
            }
        } catch (Exception ignored) {
        }
    }

}