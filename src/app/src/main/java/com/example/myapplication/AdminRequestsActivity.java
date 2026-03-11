package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class AdminRequestsActivity extends AppCompatActivity {

    private DatabaseReference dbRef, dbApp;
    private LinearLayout requestContainer;
    private int totalSources = 2;
    private int emptySources = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_requests);

        // mapping to UI
        requestContainer = findViewById(R.id.request_container);
        dbRef = FirebaseDatabase.getInstance().getReference("requests");
        dbApp = FirebaseDatabase.getInstance().getReference("appeals");
        Button backButton = findViewById(R.id.back_button);

        //listener for back button
        backButton.setOnClickListener(v -> finish());

        requestContainer.removeAllViews();
        loadPendingAppeals();
        loadPendingRequests();
    }

    //load all the requests with status as Pending
    private void loadPendingRequests() {
        dbRef.orderByChild("status").equalTo("Pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            emptySources++;
                            checkIfAllSourcesEmpty();
                            return;
                        }
                        //Itrearte through all the requests to obtain the request details
                        for (DataSnapshot requestSnap : snapshot.getChildren()) {
                            String reqID = requestSnap.getKey();
                            String name = requestSnap.child("name").getValue(String.class);
                            String username = requestSnap.child("username").getValue(String.class);
                            String email = requestSnap.child("email").getValue(String.class);
                            String address = requestSnap.child("address").getValue(String.class);
                            String mobileNumber = requestSnap.child("mobileNumber").getValue(String.class);
                            String insuranceProvider = requestSnap.child("insuranceProvider").getValue(String.class);
                            String reason = requestSnap.child("reason").getValue(String.class);

                            // inflate vehicle card with the data obatined
                            View card = LayoutInflater.from(AdminRequestsActivity.this)
                                    .inflate(R.layout.request_card, requestContainer, false);

                            ((TextView) card.findViewById(R.id.card_username)).setText("Username: " + username);

                            //logic for empty fields - no change required
                            if (name == null || name.trim().equalsIgnoreCase("") ) {
                                ((TextView) card.findViewById(R.id.card_name)).setText("Name: No Change");
                            } else {
                                ((TextView) card.findViewById(R.id.card_name)).setText("Name: " + name);
                            }

                            if (mobileNumber == null  || mobileNumber.trim().equalsIgnoreCase("")) {
                                ((TextView) card.findViewById(R.id.phno)).setText("Mobile Number: No Change");
                            } else {
                                ((TextView) card.findViewById(R.id.phno)).setText("Mobile Number: " + mobileNumber);
                            }

                            if (email == null || email.trim().equalsIgnoreCase("")) {
                                ((TextView) card.findViewById(R.id.card_email)).setText("Email: No Change");
                            } else {
                                ((TextView) card.findViewById(R.id.card_email)).setText("Email: " + email);
                            }

                            if (insuranceProvider == null  || insuranceProvider.trim().equalsIgnoreCase("")) {
                                ((TextView) card.findViewById(R.id.card_insurance)).setText("Insurance: No Change");
                            } else {
                                ((TextView) card.findViewById(R.id.card_insurance)).setText("Insurance: " + insuranceProvider);
                            }

                            if (address == null || address.trim().equalsIgnoreCase("")) {
                                ((TextView) card.findViewById(R.id.card_address)).setText("Address: No Change");
                            } else {
                                ((TextView) card.findViewById(R.id.card_address)).setText("Address: " + address);
                            }

                            if (reason == null || reason.trim().equalsIgnoreCase("")) {
                                ((TextView) card.findViewById(R.id.card_reason)).setText("Reason: No Change");
                            } else {
                                ((TextView) card.findViewById(R.id.card_reason)).setText("Reason: " + reason);
                            }


                            Button acceptBtn = card.findViewById(R.id.accept_btn);
                            Button rejectBtn = card.findViewById(R.id.reject_btn);

                            //action performed by the admin
                            acceptBtn.setOnClickListener(v -> updateStatus(reqID, "Approved", card));
                            rejectBtn.setOnClickListener(v -> updateStatus(reqID, "Rejected", card));

                            //inflate card
                            requestContainer.addView(card);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminRequestsActivity.this, "Error loading requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //update status depending on accepted or rejected
    private void updateStatus(String requestId, String newStatus, View cardView) {
        dbRef.child(requestId).child("status").setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Request " + newStatus, Toast.LENGTH_SHORT).show();

                    if ("Approved".equals(newStatus)) {
                        updateUserDetailsFromRequest(requestId);
                    }
                    requestContainer.removeView(cardView);
                    if (requestContainer.getChildCount() == 0) {
                        showEmptyMessage();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }

    //loading appeals which have status as pending
    private void loadPendingAppeals() {
        dbApp.orderByChild("status").equalTo("Pending")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (!snapshot.exists()) {
                            emptySources++;
                            checkIfAllSourcesEmpty();
                            return;
                        }

                        //Iterate through all the appeals to obtain the request details
                        for (DataSnapshot requestSnap : snapshot.getChildren()) {
                            String reqId = requestSnap.child("requestId").getValue(String.class);
                            String appID = requestSnap.getKey();
                            String username = requestSnap.child("username").getValue(String.class);
                            String reason = requestSnap.child("reason").getValue(String.class);

                            View card = LayoutInflater.from(AdminRequestsActivity.this)
                                    .inflate(R.layout.request_card, requestContainer, false);

                            TextView appealLabel = card.findViewById(R.id.appeal_label);
                            appealLabel.setVisibility(View.VISIBLE);

                            ((TextView) card.findViewById(R.id.card_username)).setText("Username: " + username);
                            if (reason == null || reason.trim().equalsIgnoreCase("")) {
                                ((TextView) card.findViewById(R.id.card_reason)).setText("Reason: No Change");
                            } else {
                                ((TextView) card.findViewById(R.id.card_reason)).setText("Reason: " + reason);
                            }

                            //obtain request details for which the appeal belongs to
                            DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests").child(reqId);
                            requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (!snapshot.exists()) return;

                                    String address = snapshot.child("address").getValue(String.class);
                                    String email = snapshot.child("email").getValue(String.class);
                                    String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);
                                    String name = snapshot.child("name").getValue(String.class);
                                    String insurance = snapshot.child("insuranceProvider").getValue(String.class);

                                    //create card for data
                                    ((TextView) card.findViewById(R.id.card_username)).setText("Username: " + username);

                                    if (name == null || name.trim().equalsIgnoreCase("") ) {
                                        ((TextView) card.findViewById(R.id.card_name)).setText("Name: No Change");
                                    } else {
                                        ((TextView) card.findViewById(R.id.card_name)).setText("Name: " + name);
                                    }

                                    if (mobileNumber == null  || mobileNumber.trim().equalsIgnoreCase("")) {
                                        ((TextView) card.findViewById(R.id.phno)).setText("Mobile Number: No Change");
                                    } else {
                                        ((TextView) card.findViewById(R.id.phno)).setText("Mobile Number: " + mobileNumber);
                                    }

                                    if (email == null || email.trim().equalsIgnoreCase("")) {
                                        ((TextView) card.findViewById(R.id.card_email)).setText("Email: No Change");
                                    } else {
                                        ((TextView) card.findViewById(R.id.card_email)).setText("Email: " + email);
                                    }

                                    if (insurance == null  || insurance.trim().equalsIgnoreCase("")) {
                                        ((TextView) card.findViewById(R.id.card_insurance)).setText("Insurance: No Change");
                                    } else {
                                        ((TextView) card.findViewById(R.id.card_insurance)).setText("Insurance: " + insurance);
                                    }

                                    if (address == null || address.trim().equalsIgnoreCase("")) {
                                        ((TextView) card.findViewById(R.id.card_address)).setText("Address: No Change");
                                    } else {
                                        ((TextView) card.findViewById(R.id.card_address)).setText("Address: " + address);
                                    }

                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(AdminRequestsActivity.this, "Failed to find user", Toast.LENGTH_SHORT).show();
                                }

                            });

                            //accept or reject logic, status to be updated in two tables/nodes
                            Button acceptBtn = card.findViewById(R.id.accept_btn);
                            Button rejectBtn = card.findViewById(R.id.reject_btn);

                            acceptBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateStatusAppeal(appID, "Approved", card);
                                    updateStatus(reqId,"Approved", card);
                                }
                            });

                            rejectBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    updateStatusAppeal(appID, "Rejected", card);
                                    updateStatus(reqId,"Appeal Rejected", card);
                                }
                            });

                            requestContainer.addView(card);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminRequestsActivity.this, "Error loading requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //method for appeal status updation
    private void updateStatusAppeal(String requestId, String newStatus, View cardView) {
        dbApp.child(requestId).child("status").setValue(newStatus)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Request " + newStatus, Toast.LENGTH_SHORT).show();

                    if(newStatus.equalsIgnoreCase("Rejected"))
                        requestContainer.removeView(cardView);
                    if (requestContainer.getChildCount() == 0) {
                        showEmptyMessage();
                    }

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show();
                });
    }

    //method to update user details if request or appeal is accepted
    private void updateUserDetailsFromRequest(String requestId) {
        DatabaseReference requestRef = FirebaseDatabase.getInstance().getReference("requests").child(requestId);
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String username = snapshot.child("username").getValue(String.class);
                String address = snapshot.child("address").getValue(String.class);
                String email = snapshot.child("email").getValue(String.class);
                String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);
                String name = snapshot.child("name").getValue(String.class);
                String insurance = snapshot.child("insuranceProvider").getValue(String.class);

                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    //only update those that are required
                    @Override
                    public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                        for (DataSnapshot userSnap : userSnapshot.getChildren()) {
                            String dbUsername = userSnap.child("username").getValue(String.class);
                            if (username != null && username.equals(dbUsername)) {
                                String userKey = userSnap.getKey();

                                if (address != null && !address.trim().isEmpty()) {
                                    usersRef.child(userKey).child("address").setValue(address);
                                }
                                if (email != null && !email.trim().isEmpty()) {
                                    usersRef.child(userKey).child("email").setValue(email);
                                }
                                if (mobileNumber != null && !mobileNumber.trim().isEmpty()) {
                                    usersRef.child(userKey).child("phno").setValue(mobileNumber);
                                }
                                if (name != null && !name.trim().isEmpty()) {
                                    usersRef.child(userKey).child("name").setValue(name);
                                }
                                if (insurance != null && !insurance.trim().isEmpty()) {
                                    usersRef.child(userKey).child("insurance").setValue(insurance);
                                }

                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(AdminRequestsActivity.this, "Failed to find user", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminRequestsActivity.this, "Failed to fetch request data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //mehtods to check if any requests or appeals are left
    private void checkIfAllSourcesEmpty() {
        if (emptySources == totalSources) {
            TextView empty = new TextView(AdminRequestsActivity.this);
            empty.setText("No pending requests or appeals.");
            requestContainer.addView(empty);
        }
    }

    private void showEmptyMessage() {
        TextView empty = new TextView(AdminRequestsActivity.this);
        empty.setText("No pending requests or appeals.");
        requestContainer.addView(empty);
    }
}