package com.example.myapplication;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;



public class UserDetailsDao {

    private final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");

    public interface UserFetchCallback {
        void onUserFetched(HelperClass user);
        void onUserNotFound();
        void onError(String error);
    }

    public void getUserByUsername(String username, UserFetchCallback callback) {

        //query directly instead of iterating
        Query query = userRef.orderByChild("username").equalTo(username);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        HelperClass user = userSnapshot.getValue(HelperClass.class);
                        if (user != null) {
                            callback.onUserFetched(user);
                        } else {
                            callback.onError("User data is corrupted");
                        }
                        return;
                    }
                } else {
                    callback.onUserNotFound();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }
}
