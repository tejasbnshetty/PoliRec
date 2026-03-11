package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// the class responsible for adding policies as admin
public class AddPolicyActivityAdmin extends AppCompatActivity {

    private EditText etPolicyInput;
    private EditText etPolicyno;
    private MaterialButton btnSubmitPolicy;
    private MaterialButton btncancel;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private DatabaseReference userNotificationsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_policies_admin);

        etPolicyno = findViewById(R.id.etPolicyno);
        etPolicyInput = findViewById(R.id.etPolicyInput);
        btnSubmitPolicy = findViewById(R.id.btnSubmitPolicy);
        btncancel = findViewById(R.id.btnCancelPolicy);

        // Set initial color (light blue)
        btnSubmitPolicy.setBackgroundTintList(getResources().getColorStateList(R.color.light_blue));

        // Add input change listeners
        TextWatcher inputWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                toggleSubmitButtonColor();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etPolicyno.addTextChangedListener(inputWatcher);
        etPolicyInput.addTextChangedListener(inputWatcher);

        btnSubmitPolicy.setOnClickListener(view -> {
            String policyNo = etPolicyno.getText().toString().trim();
            String policyText = etPolicyInput.getText().toString().trim();

            if (!policyNo.isEmpty() && !policyText.isEmpty()) {
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("policies"); // refering to policies section in database

                HelperPolicy helper = new HelperPolicy(policyNo, policyText);  // the helper class file for adding new policies or to refer back to them
                reference.child(policyNo).setValue(helper).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userNotificationsRef = database.getReference("user_notifications");
                        userNotificationsRef.get().addOnCompleteListener(snapshotTask -> {
                            if (snapshotTask.isSuccessful() && snapshotTask.getResult() != null) {
                                for (DataSnapshot userSnap : snapshotTask.getResult().getChildren()) {
                                    String licenseKey = userSnap.getKey();
                                    userNotificationsRef.child(licenseKey)
                                            .child("policy_" + policyNo)
                                            .setValue(false);
                                }// the policy addition process
                                Toast.makeText(AddPolicyActivityAdmin.this, "Policy added and notifications updated!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddPolicyActivityAdmin.this, PoliciesActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("admin", true);// admin is declared as true to be able to view policies addition button
                                startActivity(intent);
                                finish();// policy successfully added
                            } else {
                                Toast.makeText(AddPolicyActivityAdmin.this, "Policy added, but failed to update notifications.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(AddPolicyActivityAdmin.this, "Failed to add policy.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(AddPolicyActivityAdmin.this, "Please enter a policy.", Toast.LENGTH_SHORT).show();
            }
        });

        btncancel.setOnClickListener(v -> {
            Intent intent = new Intent(AddPolicyActivityAdmin.this, PoliciesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("admin", true);// admin is declared as true to be able to view policies addition button
            startActivity(intent);
            finish();
        });
    }

    private void toggleSubmitButtonColor() {
        String policyNo = etPolicyno.getText().toString().trim();
        String policyText = etPolicyInput.getText().toString().trim();

        if (!policyNo.isEmpty() || !policyText.isEmpty()) {
            btnSubmitPolicy.setBackgroundTintList(getResources().getColorStateList(R.color.primary_blue));
        } else {
            btnSubmitPolicy.setBackgroundTintList(getResources().getColorStateList(R.color.light_blue));
        }
    }
}