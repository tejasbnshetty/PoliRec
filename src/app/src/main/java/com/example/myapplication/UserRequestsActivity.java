package com.example.myapplication;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.myapplication.data.model.UpdateRequests;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;



public class UserRequestsActivity extends AppCompatActivity {

    private EditText editName, editMobile, editAddress, editEmail, editInsurance, editReason;
    private Button buttonSubmit, buttonCancel;
    DatabaseReference requestsRef;
    String username;
    String status = "Pending";
    boolean notified = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_requests);
        requestsRef = FirebaseDatabase.getInstance().getReference("requests");
        username = getIntent().getStringExtra("username");

        // Bind fields
        editName = findViewById(R.id.editTextName);
        editMobile = findViewById(R.id.editTextMobile);
        editAddress = findViewById(R.id.editTextAddress);
        editEmail = findViewById(R.id.editTextEmail);
        editInsurance = findViewById(R.id.editTextInsurance);
        editReason = findViewById(R.id.editTextReason);

        buttonSubmit = findViewById(R.id.buttonSubmitRequest);
        buttonCancel = findViewById(R.id.buttonCancelRequest);

        // Default button colors
        int lightBlue = ContextCompat.getColor(this, R.color.light_blue);
        int darkBlue = ContextCompat.getColor(this, R.color.primary_blue);
        buttonSubmit.setBackgroundColor(lightBlue);
        buttonSubmit.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!editReason.getText().toString().trim().isEmpty() && anyFieldFilled()) {
                    buttonSubmit.setBackgroundColor(darkBlue);
                    buttonSubmit.setEnabled(true);
                } else {
                    buttonSubmit.setBackgroundColor(lightBlue);
                    buttonSubmit.setEnabled(false);
                }
            }
        };

        editName.addTextChangedListener(watcher);
        editMobile.addTextChangedListener(watcher);
        editAddress.addTextChangedListener(watcher);
        editEmail.addTextChangedListener(watcher);
        editInsurance.addTextChangedListener(watcher);
        editReason.addTextChangedListener(watcher);

        buttonSubmit.setOnClickListener(v -> submitRequest());
        buttonCancel.setOnClickListener(v -> finish());
    }

    boolean anyFieldFilled() {
        return !editName.getText().toString().trim().isEmpty()
                || !editMobile.getText().toString().trim().isEmpty()
                || !editAddress.getText().toString().trim().isEmpty()
                || !editEmail.getText().toString().trim().isEmpty()
                || !editInsurance.getText().toString().trim().isEmpty();
    }

    private void submitRequest() {
        String name = editName.getText().toString().trim();
        String mobile = editMobile.getText().toString().trim();
        String address = editAddress.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        String insurance = editInsurance.getText().toString().trim();
        String reason = editReason.getText().toString().trim();

        String reqID = generateRequestId();
        UpdateRequests request = new UpdateRequests(reqID, username, name, mobile, address, email, insurance, status, notified, reason);

        requestsRef.child(reqID).setValue(request).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Request submitted! Your Request ID: " + reqID, Toast.LENGTH_LONG).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to submit request", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String generateRequestId() {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder id = new StringBuilder("req");
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            id.append(chars.charAt(random.nextInt(chars.length())));
        }
        return id.toString();
    }
}