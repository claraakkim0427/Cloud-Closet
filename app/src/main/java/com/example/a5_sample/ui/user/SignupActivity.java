package com.example.a5_sample.ui.user;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.view.View;

public class SignupActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private EditText password;
    private Button signupButton;
    private ImageButton backButton;

    private DatabaseReference rootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        name = findViewById(R.id.editeditTextName);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        signupButton = findViewById(R.id.signupButton);
        rootRef = FirebaseDatabase.getInstance().getReference();
        backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                if (!userName.isEmpty() && !userEmail.isEmpty() && !userPassword.isEmpty()) {
                    checkAndCreateAccount(userName, userEmail, userPassword);
                } else {
                    Toast.makeText(getApplicationContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkAndCreateAccount(String userName, String userEmail, String userPassword) {
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isUserFound = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String dbEmail = userSnapshot.child("email").getValue(String.class);
                    if (userEmail.equals(dbEmail)) {
                        isUserFound = true;
                        break;
                    }
                }

                if (isUserFound) {
                    Toast.makeText(SignupActivity.this, "User with this email already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    createNewUser(userName, userEmail, userPassword);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignupActivity.this, "Database error occurred.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNewUser(String userName, String userEmail, String userPassword) {
        DatabaseReference newUserRef = rootRef.push();
        String userId = newUserRef.getKey();

        if (userId != null) {
            newUserRef.child("name").setValue(userName);
            newUserRef.child("email").setValue(userEmail);
            newUserRef.child("password").setValue(userPassword);

            Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(SignupActivity.this, "Could not create new user.", Toast.LENGTH_SHORT).show();
        }
    }
}
