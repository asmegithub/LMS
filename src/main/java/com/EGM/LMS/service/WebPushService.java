package com.EGM.LMS.service;

import com.EGM.LMS.model.User;

import java.util.List;

public interface WebPushService {
    String getPublicKey();
    void upsertSubscription(User user, String endpoint, String p256dh, String auth);
    void removeSubscription(User user, String endpoint);
    void sendPushToUsers(List<User> users, String title, String body, String actionUrl);
}