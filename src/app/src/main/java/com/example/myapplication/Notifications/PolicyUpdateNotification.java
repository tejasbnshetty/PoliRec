package com.example.myapplication.Notifications;

import android.content.Context;

public class PolicyUpdateNotification implements UserNotificationsInterface {
    @Override
    public void send(Context context, String message) {
        String title = "New Policy Added!";
        NotificationsService.showNotification(context, title, message);
    }
}
