package com.brain_socket.tapdrive.utils.fcm;

import android.util.Log;

import com.brain_socket.tapdrive.data.DataCacheProvider;
import com.brain_socket.tapdrive.data.DataStore;
import com.brain_socket.tapdrive.data.ServerResult;
import com.brain_socket.tapdrive.model.user.UserModel;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by eyadmhanna on 8/18/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.

        UserModel me = DataCacheProvider.getInstance().getStoredObjectWithKey(DataCacheProvider.KEY_APP_USER_ME, UserModel.class);
        DataCacheProvider.getInstance().storeStringWithKey(DataCacheProvider.KEY_FCM_TOKEN, token);

        if (me != null) {

            DataStore.getInstance().setFCMId(token, new DataStore.DataRequestCallback() {
                @Override
                public void onDataReady(ServerResult result, boolean success) {

                }
            });

        }

    }
}
