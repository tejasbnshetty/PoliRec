package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserNotificationsRequestsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapters;
    private ArrayList<String> requestDisplayList;
    private ArrayList<String> requestIdList;
    private ArrayList<String> requestStatusList;

    private RecyclerView recyclerView;
    private RequestStatusAdapter adapter;
    private ArrayList<RequestItem> requestItems;


    private DatabaseReference requestsRef;
    private String currentUsername;

    TextView heading;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_updates);

        recyclerView = findViewById(R.id.notificationsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestItems = new ArrayList<>();
        adapter = new RequestStatusAdapter(requestItems, this::onRequestClicked);

        heading = findViewById(R.id.headingTextView);

        recyclerView.setAdapter(adapter);

        currentUsername = getIntent().getStringExtra("username");
        requestsRef = FirebaseDatabase.getInstance().getReference("requests");

        fetchUserRequests();
    }

    private void fetchUserRequests() {
        requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestItems.clear();

                for (DataSnapshot reqSnapshot : snapshot.getChildren()) {
                    String requestId = reqSnapshot.getKey();

                    String username = getStringValue(reqSnapshot, "username");
                    if (currentUsername == null || !currentUsername.equals(username)) continue;

                    String status = getStringValue(reqSnapshot, "status", "Pending");
                    String reason = getStringValue(reqSnapshot, "reason", "No reason");

                    String name = getStringValue(reqSnapshot, "name", "N/A");
                    String mobile = getStringValue(reqSnapshot, "mobileNumber", "N/A");
                    String email = getStringValue(reqSnapshot, "email", "N/A");
                    String insuranceProvider = getStringValue(reqSnapshot, "insuranceProvider", "N/A");
                    String address = getStringValue(reqSnapshot, "address", "N/A");

                    RequestItem item = new RequestItem(requestId, status, reason, name, mobile, email, insuranceProvider, address);
                    requestItems.add(item);
                }

                adapter.notifyDataSetChanged();

                if (requestItems.isEmpty()) {
                    heading.setText("You Have no requests");
                    Toast.makeText(UserNotificationsRequestsActivity.this, "No requests found", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserNotificationsRequestsActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private String getStringValue(DataSnapshot snapshot, String key) {
        return getStringValue(snapshot, key, "");
    }

    private String getStringValue(DataSnapshot snapshot, String key, String defaultValue) {
        Object val = snapshot.child(key).getValue();
        return val != null ? val.toString() : defaultValue;
    }

    private void onRequestClicked(RequestItem item) {
        if ("Rejected".equalsIgnoreCase(item.getStatus())) {
            Intent intent = new Intent(this, UserAppealsActivity.class);
            intent.putExtra("requestId", item.getRequestId());
            intent.putExtra("username", currentUsername);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Appeal allowed only for rejected requests", Toast.LENGTH_SHORT).show();
        }
    }
}