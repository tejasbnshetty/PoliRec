package com.example.myapplication.Notifications;

import android.content.Context;


public class UpdateRequestStatusNotifications implements UserNotificationsInterface {

    @Override
    public void send(Context context, String notificationMessage) {
        String title = "Request Update!";
        String message = notificationMessage;
        NotificationsService.showNotification(context, title, message);
    }
}