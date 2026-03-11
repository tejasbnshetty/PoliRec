package com.example.myapplication.Notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.myapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NotificationsListenerService extends Service {
    private static final String CHANNEL_ID = "notification_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private DatabaseReference requestsRef;
    private DatabaseReference appealsRef;
    private DatabaseReference userNotificationsRef;
    private String username;
    private String license;
    private ValueEventListener requestsListener;
    private ValueEventListener appealsListener;
    private ValueEventListener policyListener;

    @Override
    public void onCreate() {
        super.onCreate();

        username = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
                .getString("USERNAME", null);
        license = getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
                .getString("LICENSE", null);

        if (username != null || license != null) {
            requestsRef = FirebaseDatabase.getInstance().getReference("requests");
            appealsRef = FirebaseDatabase.getInstance().getReference("appeals");
            userNotificationsRef = FirebaseDatabase.getInstance().getReference("user_notifications");
            listenForStatusUpdates();
        }
        if ("admin".equalsIgnoreCase(username)) {
            stopSelf(); // Service will stop itself immediately if it's admin
            return;
        }
        startForegroundService();
    }

    private void startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Request Status Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Notification Listener Active")
                .setContentText("You’ll be notified about requests, appeals and policies.")
                .setSmallIcon(R.drawable.app_notifications_icon)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void listenForStatusUpdates() {
        requestsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (username == null) return;

                for (DataSnapshot reqSnapshot : snapshot.getChildren()) {
                    String reqID = reqSnapshot.getKey();
                    String reqUsername = reqSnapshot.child("username").getValue(String.class);
                    String status = reqSnapshot.child("status").getValue(String.class);
                    Boolean notified = reqSnapshot.child("notified").getValue(Boolean.class);
                    if (notified == null) notified = false;
                    if (reqUsername != null && reqUsername.equals(username) && !notified) {
                        if ("Approved".equalsIgnoreCase(status) || "Rejected".equalsIgnoreCase(status)) {
                            // Sending the notification
                            UserNotificationsInterface notification = NotificationsFactory.createNotification("update_request_status");
                            if (notification != null) {
                                notification.send(NotificationsListenerService.this, username + ", your request (ID: " + reqID + ") has been " + status);
                            }
                            requestsRef.child(reqID).child("notified").setValue(true);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotificationListenerService", "Error while listening to Firebase data: " + error.getMessage());
            }
        };
        requestsRef.addValueEventListener(requestsListener);

        // Notifications for appeals
        appealsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (username == null) return;
                for (DataSnapshot reqSnapshot : snapshot.getChildren()) {
                    String appealID = reqSnapshot.getKey();
                    String appealUsername = reqSnapshot.child("username").getValue(String.class);
                    String appealStatus = reqSnapshot.child("status").getValue(String.class);
                    Boolean appealNotified = reqSnapshot.child("notified").getValue(Boolean.class);
                    if (appealNotified == null) appealNotified = false;

                    if (appealUsername != null && appealUsername.equals(username) && !appealNotified) {
                        if ("Approved".equalsIgnoreCase(appealStatus) || "Rejected".equalsIgnoreCase(appealStatus)) {
                            // Send notification
                            UserNotificationsInterface appealNotification = NotificationsFactory.createNotification("update_appeal_status");
                            if (appealNotification != null) {
                                appealNotification.send(NotificationsListenerService.this, username + ", your appeal (ID: " + appealID + ") has been " + appealStatus);
                            }
                            appealsRef.child(appealID).child("notified").setValue(true);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("NotificationListenerService", "Error while listening to Firebase data: " + error.getMessage());
            }
        };
        appealsRef.addValueEventListener(appealsListener);

        // Policy notifications
        if(license != null){
            policyListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot policySnap : snapshot.getChildren()) {
                        String policyKey = policySnap.getKey();
                        Boolean seen = policySnap.getValue(Boolean.class);
                        if (seen != null && !seen) {
                            UserNotificationsInterface notification = NotificationsFactory.createNotification("policy_update");
                            if (notification != null) {
                                String policyNo = policyKey.replace("policy_", "");
                                notification.send(NotificationsListenerService.this,
                                        "New policy (" + policyNo + ") has been added. Please review.");
                            }
                            userNotificationsRef.child(license).child(policyKey).setValue(true);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("NotificationService", "Policy listener error: " + error.getMessage());
                }
            };
            userNotificationsRef.child(license).addValueEventListener(policyListener);
        }else {
            Log.e("NotificationsService", "License is null — cannot listen for policy notifications.");
        }
    }
    // Cleaning up Firebase listeners for other user/admin login in same phone
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (requestsRef != null && requestsListener != null) {
            requestsRef.removeEventListener(requestsListener);
        }
        if (appealsRef != null && appealsListener != null) {
            appealsRef.removeEventListener(appealsListener);
        }
        if (license != null && userNotificationsRef != null && policyListener != null) {
            userNotificationsRef.child(license).removeEventListener(policyListener);
        }
        Log.d("NotificationService", "Firebase listeners removed in onDestroy");
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}