package com.example.myapplication.data.model;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AVLTreeManager {
    private static AVLTreeManager instance;
    private AVLTree avlTree;

    public AVLTreeManager() {
        avlTree = new AVLTree();
    }

    public static AVLTreeManager getInstance() {
        if (instance == null) {
            instance = new AVLTreeManager();
        }
        return instance;
    }
    // Adding this setter only for tests (used in VehicleSearchTest.java)
    public static void setInstance(AVLTreeManager mockInstance) {
        instance = mockInstance;
    }

    public AVLTree getAvlTree() {
        return avlTree;
    }

    //Observer for changes in user. Updates tree when database is changed
    public void observeUsersAndLoadTree(Context context) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                avlTree.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String license = userSnapshot.child("license").getValue(String.class);
                    if (license != null) {
                        HashMap<String, Object> userInfo = (HashMap<String, Object>) userSnapshot.getValue();
                        avlTree.insert(license, userInfo);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(context, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public List<HashMap<String, Object>> searchByFilters(String username, Map<String, String> filters) {
        return avlTree.searchByFilters( filters);
    }
    // Adding this for junit logic tests
    public void insert(String license, HashMap<String, Object> userInfo) {
        avlTree.insert(license, userInfo);
    }
}
