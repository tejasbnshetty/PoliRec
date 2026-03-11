package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;



public class UserAppealsActivity extends AppCompatActivity {

    EditText appealUsername, appealReqID, appealDetailsInput;
    Button submitAppealButton, appealCancelButton;
    String reqID;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_appeals);

        username = getIntent().getStringExtra("username");
        reqID = getIntent().getStringExtra("requestId");

        appealUsername = findViewById(R.id.appealedUser);
        appealReqID = findViewById(R.id.appealReqID);
        appealDetailsInput = findViewById(R.id.appealDetailsInput);
        submitAppealButton = findViewById(R.id.submitAppealButton);
        appealCancelButton = findViewById(R.id.appealCancelButton);

        // Pre-fill and disable username & request ID fields
        appealReqID.setText(reqID);
        appealReqID.setEnabled(false);
        appealUsername.setText(username);
        appealUsername.setEnabled(false);

        // Define colors
        int lightBlue = ContextCompat.getColor(this, R.color.light_blue);
        int darkBlue = ContextCompat.getColor(this, R.color.primary_blue);

        // Set initial state
        submitAppealButton.setEnabled(false);
        submitAppealButton.setBackgroundColor(lightBlue);

        // Enable the appeal button only when reason is entered
        appealDetailsInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean hasReason = !s.toString().trim().isEmpty();
                submitAppealButton.setEnabled(hasReason);
                submitAppealButton.setBackgroundColor(hasReason ? darkBlue : lightBlue);
            }
        });

        submitAppealButton.setOnClickListener(v -> {
            String details = appealDetailsInput.getText().toString().trim();

            String appealID = generateRandomAppealID();
            DatabaseReference reqRef = FirebaseDatabase.getInstance().getReference("requests").child(reqID);
            DatabaseReference appealsRef = FirebaseDatabase.getInstance().getReference("appeals").child(appealID);

            reqRef.child("status").setValue("Appealed");
            reqRef.child("reason").setValue(details);
            appealsRef.child("username").setValue(username);
            appealsRef.child("requestId").setValue(reqID);
            appealsRef.child("reason").setValue(details);
            appealsRef.child("status").setValue("Pending");
            appealsRef.child("notified").setValue(false);

            Intent intent = new Intent(UserAppealsActivity.this, DashboardUser.class);
            intent.putExtra("username", username);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
        appealCancelButton.setOnClickListener(v -> finish());
    }

    private String generateRandomAppealID() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder id = new StringBuilder("ap");
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        return id.toString();
    }
}
