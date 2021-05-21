package com.namastey.listeners;

public interface AuthenticationListener {
    void onTokenReceived(String auth_token);
}