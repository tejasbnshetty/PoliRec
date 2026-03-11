package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**  this is the main policies class file that gets calls and gives calls to all other policy related files.
 *
 *
 *
 *
 *
 */
public class PoliciesActivity extends AppCompatActivity {

    private RecyclerView policyRecyclerView;
    private PolicyAdapter adapter;
    private ArrayList<Policy> policyList;

    // instance creater for policies that perform the main functions
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policies);
    // calling from xml layouts and getting ids of variables for same....like textfields
        policyRecyclerView = findViewById(R.id.policyRecyclerView);
        policyList = new ArrayList<>();
        adapter = new PolicyAdapter(policyList);
        policyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        policyRecyclerView.setAdapter(adapter);

        // the add policy button
        FloatingActionButton fabAddPolicy = findViewById(R.id.fab_add_policy);
        // Get admin flag passed via Intent
        boolean isAdmin = getIntent().getBooleanExtra("admin", false);

        // Create and apply user state
        UserSession userSession = new UserSession();
        if (isAdmin) {
            userSession.setState(new AdminState());
        } else {
            userSession.setState(new RegularUserState());
        }

        // Show/hide FAB based on permission
        fabAddPolicy.setVisibility(userSession.canAddPolicy() ? View.VISIBLE : View.GONE);

        // FAB click action
        fabAddPolicy.setOnClickListener(v -> {
            Intent intent = new Intent(PoliciesActivity.this, AddPolicyActivityAdmin.class);
            startActivity(intent);
        });

        // Fetch policies from Firebase
        fetchPoliciesFromFirebase();

        // Search bar input
        TextInputEditText searchView = findViewById(R.id.searchView);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPolicies(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        });
    }


    //function that fetches policies from database
    private void fetchPoliciesFromFirebase() {
        FirebaseDatabase.getInstance().getReference("policies")
                .orderByChild("policy_no")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        policyList.clear();
                        for (DataSnapshot policySnapshot : snapshot.getChildren()) {// traversing through each snapshot
                            String policyNumber = policySnapshot.child("policy_no").getValue(String.class);
                            String policyDescription = policySnapshot.child("policytxt").getValue(String.class);
                            if (policyNumber != null && policyDescription != null) {
                                policyList.add(new Policy(policyNumber, policyDescription));
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override// catching exceptions in calling data from database.
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(PoliciesActivity.this, "Error loading requests", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // tokenizing function that has its own grammar for filtering through data...policies
    private void filterPolicies(String query) {
        if (query == null || query.trim().isEmpty()) {
            adapter.updateList(new ArrayList<>(policyList));
            return;
        }

        String clean = query.replaceAll("[^a-zA-Z0-9 ]", "").toLowerCase();
        String[] rawTokens = clean.split("\\s+");

        Set<String> stopwords = new HashSet<>(Arrays.asList(
                "the", "is", "a", "of", "to", "and", "in", "on", "at"
        ));// removing stopwords from filtering by making a set for the same

        List<String> tokens = new ArrayList<>();
        for (String token : rawTokens) {
            if (!stopwords.contains(token)) {
                tokens.add(token);
            }
        }// removes stopwords

        ArrayList<Policy> filteredList = new ArrayList<>();
        for (Policy policy : policyList) {
            String combined = (policy.getPolicyNumber() + " " + policy.getPolicyDescription()).toLowerCase();
            boolean allMatch = true;
            for (String token : tokens) {
                if (!combined.contains(token)) {
                    allMatch = false;
                    break;
                }// the fucntion that gets the policies and go through them
            }
            if (allMatch) {
                filteredList.add(policy);
            }
        }

        adapter.updateList(filteredList);
    }
}
