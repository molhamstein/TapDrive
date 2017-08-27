package com.brain_socket.tapdrive.data;

public interface FacebookSharingListener
{
    void onShareResult(boolean success);

    void onShareError(String error);

    void onShareCancelled();
}