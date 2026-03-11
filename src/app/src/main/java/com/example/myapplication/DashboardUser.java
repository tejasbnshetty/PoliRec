package com.example.myapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.os.Build;
import androidx.annotation.NonNull;
import com.example.myapplication.data.model.AVLTree;
import com.example.myapplication.data.model.AVLTreeManager;
import com.google.android.material.button.MaterialButton;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;
import com.example.myapplication.Notifications.NotificationsListenerService;


public class DashboardUser extends AppCompatActivity {

    // Variables for UI
    String username;
    TextView welcome;
    ImageButton user_profile;
    ImageButton notifications;
    MaterialButton policies;
    MaterialButton details_form;
    EditText search_bar;
    MaterialButton search_button;
    AVLTree avlTree;
    Button LLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_user);

        //getting the same tree
        avlTree = AVLTreeManager.getInstance().getAvlTree();

        // welcome message
        username = getIntent().getStringExtra("username");
        welcome = findViewById(R.id.Welcome);
        String wel_user = "Welcome " + username;
        welcome.setText(wel_user);

        // UI element bindings
        user_profile = findViewById(R.id.user_profile_button);
        notifications = findViewById(R.id.notifications_button_user);
        policies = findViewById(R.id.user_policies_button);
        details_form = findViewById(R.id.user_details_form_button);
        search_bar = findViewById(R.id.user_search_bar);
        search_button = findViewById(R.id.user_search_button);
        LLM = findViewById(R.id.LLMbutton);

        // Storing it in SharedPreferences for the use of notifications
        getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
                .edit()
                .putString("USERNAME", username)
                .apply();
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Finding license and storing in shared preferences for policy notifications
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String currentUsername = userSnapshot.child("username").getValue(String.class);
                    if (currentUsername != null && currentUsername.equals(username)) {
                        String license = userSnapshot.getKey(); // The key is the license
                        if (license != null) {
                            getSharedPreferences("MY_APP_PREFS", MODE_PRIVATE)
                                    .edit()
                                    .putString("LICENSE", license)
                                    .apply();
                            Log.d("DashboardUser", "License for logged in user is found and stored: " + license);

                            Intent serviceIntent = new Intent(DashboardUser.this, NotificationsListenerService.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(serviceIntent);
                            } else {
                                startService(serviceIntent);
                            }
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardUser", "Error fetching users: " + error.getMessage());
            }
        });
        // Check if the app has permission to post notifications (for Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Request the permission
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101); // 101 is request code
            }
        }

        //listeners
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //user profile
        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardUser.this, UserDetails.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //update details
        details_form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardUser.this, UserRequestsActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        //notifications/requests activity
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardUser.this, UserNotificationsRequestsActivity.class);
                intent.putExtra("username",username);
                startActivity(intent);
            }
        });

        //policies activity
        policies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardUser.this, PoliciesActivity.class);
                intent.putExtra("admin","false");
                startActivity(intent);
            }
        });

        //chatbot - PolyRecBot
        LLM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardUser.this, LLMActivity.class);
                startActivity(intent);
            }
        });

        //search by rego num
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rego = search_bar.getText().toString().trim();

                if (rego.isEmpty()) {
                    search_bar.setError("Please enter a registration number");//empty
                    return;
                }

                HashMap<String, Object> result = avlTree.searchRegoByUsername(username, rego);//search for valid entry by rego num
                if (result != null) {
                    Intent intent = new Intent(DashboardUser.this, DisplayPostSearch.class);
                    intent.putExtra("vehicleInfo", (Serializable) result);
                    intent.putExtra("name", username);
                    startActivity(intent);
                } else {
                    search_bar.setError("You do not own this vehicle");//incorrect input
                }
            }
        });

        // Dynamic color change for search button
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty()) {
                    search_button.setBackgroundTintList(ContextCompat.getColorStateList(DashboardUser.this, R.color.primary_blue));
                } else {
                    search_button.setBackgroundTintList(ContextCompat.getColorStateList(DashboardUser.this, R.color.light_blue));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "POST_NOTIFICATIONS permission granted");
            } else {
                Log.d("Permission", "POST_NOTIFICATIONS permission denied");
                Toast.makeText(this, "Notification permission is needed to receive updates.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
