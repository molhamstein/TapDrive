package com.brain_socket.tapdrive.data;

import java.util.HashMap;

public interface FacebookProviderListener
{
    void onFacebookSessionOpened(String accessToken, String userId, HashMap<String, Object> map);

    void onFacebookSessionClosed();

    void onFacebookException(Exception exception);

/*	void onFacebookFriendsReceived(ArrayList<AppFacebookFriend> arrayFriends, ArrayList<String> arrayActiveIds);*/
}
