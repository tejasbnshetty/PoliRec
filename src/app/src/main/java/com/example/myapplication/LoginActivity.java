package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.data.model.AVLTreeManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    EditText loginUsername, loginPassword;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        //map UI
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        loginButton.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String usernameInput = loginUsername.getText().toString().trim();
                String passwordInput = loginPassword.getText().toString().trim();
                loginButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty());
            }
        };

        //listeners
        loginUsername.addTextChangedListener(watcher);
        loginPassword.addTextChangedListener(watcher);

        //initialise AVL tree
        AVLTreeManager.getInstance().observeUsersAndLoadTree(this);

        loginButton.setOnClickListener(view -> {
            if (!validateUsername() | !validatePassword()) return;
            checkUser();
        });
    }

    //method to check if username is empty
    public Boolean validateUsername() {
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) {
            loginUsername.setError("Username cannot be empty");
            return false;
        } else {
            loginUsername.setError(null);
            return true;
        }
    }

    //method to check if password is empty
    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    //method to check if the user is admin
    public void checkUser() {
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        //get from db to verify
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check if username is in db or not
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        String isAdmin = userSnapshot.child("admin").getValue(String.class);

                        //verify password for username
                        if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                            loginUsername.setError(null);

                            //check if admin or not and go to respective dahsboards
                            Intent intent;
                            if ("yes".equals(isAdmin)) {
                                intent = new Intent(LoginActivity.this, DashboardAdmin.class);
                            } else {
                                intent = new Intent(LoginActivity.this, DashboardUser.class);
                            }

                            intent.putExtra("username", userUsername);//send username to next activty
                            startActivity(intent);
                            finish();
                            return;
                        } else {
                            loginPassword.setError("Invalid credentials"); //password incorrect/ not matching
                            loginPassword.requestFocus();
                            return;
                        }
                    }
                } else {
                    loginUsername.setError("Invalid User");//USername not in db
                    loginUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
