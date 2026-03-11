package com.example.myapplication.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.myapplication.R;


public class NotificationsService {

    private static final String CHANNEL_ID = "request_status_channel";
    public static void showNotification(Context context, String title, String message) {
        createNotificationChannel(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        try {
            if (androidx.core.app.ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify((int) System.currentTimeMillis(), builder.build());
            } else {
                // Avoiding crash with logging
                android.util.Log.e("UserNotificationService", "POST_NOTIFICATIONS permission not granted");
            }
        } catch (SecurityException e) {
            android.util.Log.e("UserNotificationService", "SecurityException while sending notification: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            android.util.Log.e("UserNotificationService", "Unexpected exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Request Status Updates";
            String description = "Notifications for user request status changes";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}
