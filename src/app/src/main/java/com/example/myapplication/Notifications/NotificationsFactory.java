package com.example.myapplication.Notifications;


public class NotificationsFactory {

    public static UserNotificationsInterface createNotification(String type) {
        if ("update_request_status".equals(type)) {
            return new UpdateRequestStatusNotifications();
        } else if("update_appeal_status".equals(type)){
            return new UpdateRequestStatusNotifications();
        } else if ("policy_update".equals(type)) {
            return new PolicyUpdateNotification();
        }
        return null;
    }
}