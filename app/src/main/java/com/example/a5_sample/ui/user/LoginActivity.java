package com.example.a5_sample.ui.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText email;
    private EditText password;
    private Button loginButton;
    private Button forgotpwButton;
    private ImageButton backButton;

    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.d(TAG, "onCreate called");

        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.loginButton);
        forgotpwButton = findViewById(R.id.forgotpwButton);

        usersRef = FirebaseDatabase.getInstance().getReference();

        backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Back button clicked");
                Intent intent = new Intent(LoginActivity.this, FirstPageActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usersEmail = email.getText().toString();
                String usersPassword = password.getText().toString();

                Log.d(TAG, "Login button clicked with email: " + usersEmail);

                if (!usersEmail.isEmpty() && !usersPassword.isEmpty()) {
                    checkRealtimeDatabase(usersEmail, usersPassword);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter email and password", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Email or password is empty");
                }
            }
        });

        forgotpwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Forgot password button clicked");
                Intent intent = new Intent(LoginActivity.this, FindPwActivity.class);
                startActivity(intent);
            }
        });
    }

    private void checkRealtimeDatabase(String email, String password) {
        Log.d(TAG, "Checking Realtime Database for email: " + email);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Database response received");

                boolean isUserFound = false;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    String dbPassword = userSnapshot.child("password").getValue(String.class);

                    Log.d(TAG, "Checking user with email: " + dbEmail);

                    if (email.equals(dbEmail) && password.equals(dbPassword)) {
                        String userId = userSnapshot.getKey();

                        Log.d(TAG, "User found, userId: " + userId);

                        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("UserId", userId);
                        editor.apply();

                        Log.d(TAG, "Navigating to MainActivity");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                }

                if (!isUserFound) {
                    Log.d(TAG, "Authentication failed - user not found or password incorrect");
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
