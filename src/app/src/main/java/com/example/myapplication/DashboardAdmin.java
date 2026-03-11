package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.data.model.AVLTree;
import com.example.myapplication.data.model.AVLTreeManager;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.HashMap;


public class DashboardAdmin extends AppCompatActivity {

    ImageButton adminProfile, requestsButton;
    MaterialButton searchButton, policyButton, advancedButton;
    EditText searchBar;
    AVLTree avlTree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard_admin);

        // Getting AVL Tree with the user data
        avlTree = AVLTreeManager.getInstance().getAvlTree();

        // Bind views
        adminProfile = findViewById(R.id.user_profile_admin);
        requestsButton = findViewById(R.id.notification_admin);
        searchButton = findViewById(R.id.search_button_admin);
        policyButton = findViewById(R.id.policy_admin);
        searchBar = findViewById(R.id.search_bar_admin);
        advancedButton = findViewById(R.id.search_button_advanced);

        // Welcome message
        String username = getIntent().getStringExtra("username");
        TextView welcome = findViewById(R.id.Welcome_admin);
        welcome.setText("Welcome Admin");

        //listeners
        //Vehicle Search/Advanced search
        advancedButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardAdmin.this, VehicleSearch.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Profile button
        adminProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardAdmin.this, UserDetails.class);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        // Requests button
        requestsButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardAdmin.this, AdminRequestsActivity.class);
            startActivity(intent);
        });

        // Policies button
        policyButton.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardAdmin.this, PoliciesActivity.class);
            intent.putExtra("admin", true);
            startActivity(intent);
        });

        // Search button
        searchButton.setOnClickListener(v -> {
            String licenseQuery = searchBar.getText().toString().trim();
            if (licenseQuery.isEmpty()) {
                searchBar.setError("Please enter a license number");//if empty field
                return;
            }

            HashMap<String, Object> userInfo = avlTree.search(licenseQuery);//search for entries that match license number
            if (userInfo != null) {
                Intent intent = new Intent(DashboardAdmin.this, DisplayPostSearch.class);
                intent.putExtra("userInfo", (Serializable) userInfo);
                intent.putExtra("name", userInfo.get("name").toString());
                startActivity(intent);
            } else {
                searchBar.setError("License not found!");//incorrect input
            }
        });

        // Change search button color on typing
        int lightBlue = ContextCompat.getColor(this, R.color.light_blue);
        int darkBlue = ContextCompat.getColor(this, R.color.primary_blue);
        searchButton.setBackgroundColor(lightBlue);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchButton.setBackgroundColor(darkBlue);
                } else {
                    searchButton.setBackgroundColor(lightBlue);
                }
            }
        });

        // System padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });
    }
}

