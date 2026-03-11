package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Notifications.NotificationsListenerService;


public class UserDetails extends AppCompatActivity {

    TextView textName, textPhone, textAddress, textEmail, textLicense, textLicenseType;
    ImageButton logoutButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);


        textName = findViewById(R.id.textName);
        textPhone = findViewById(R.id.textPhone);
        textAddress = findViewById(R.id.textAddress);
        textEmail = findViewById(R.id.textEmail);
        textLicense = findViewById(R.id.textLicense);
        textLicenseType = findViewById(R.id.textLicenseType);
        logoutButton = findViewById(R.id.logout_button);

        logoutButton.setOnClickListener(v -> {

            getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
                    .edit()
                    .remove("USERNAME")
                    .remove("LICENSE")
                    .apply();

            Intent stopIntent = new Intent(this, NotificationsListenerService.class);
            stopService(stopIntent);

            Intent intent = new Intent(UserDetails.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // clears activity stack
            startActivity(intent);
            finish();
        });

        // Get the username passed from DashboardActivity or Login
        String username = getIntent().getStringExtra("username");

        if (username == null || username.isEmpty()) {
            Toast.makeText(this, "No username provided", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDetailsDao userDetailsDao = new UserDetailsDao();
        userDetailsDao.getUserByUsername(username, new UserDetailsDao.UserFetchCallback() {
            @Override
            public void onUserFetched(HelperClass user) {
                textName.setText(user.getName());
                textPhone.setText(user.getPhno());
                textAddress.setText(user.getAddress());
                textEmail.setText(user.getEmail());
                textLicense.setText(user.getLicense());
                textLicenseType.setText(user.getLicenseType());
            }

            @Override
            public void onUserNotFound() {
                Toast.makeText(UserDetails.this, "User not found in database", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(UserDetails.this, "Database error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
