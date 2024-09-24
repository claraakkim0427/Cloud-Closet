package com.example.a5_sample.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindPwActivity extends AppCompatActivity {
    private EditText name;
    private EditText email;
    private Button findpwButton;
    private Button loginButton;
    private ImageButton backButton;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_findpw);

        name = findViewById(R.id.editTextpwName);
        email = findViewById(R.id.editTextpwEmail);
        findpwButton = findViewById(R.id.findpwButton);
        loginButton = findViewById(R.id.findpwloginButton);
        backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

        findpwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usersName = name.getText().toString();
                String usersEmail = email.getText().toString();

                if (!usersName.isEmpty() && !usersEmail.isEmpty()) {
                    findPassword(usersName, usersEmail);
                } else {
                    Toast.makeText(FindPwActivity.this, "Please enter name and email", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FindPwActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void findPassword(String usersName, String usersEmail) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;

                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String nameInDb = userSnapshot.child("name").getValue(String.class);
                    String emailInDb = userSnapshot.child("email").getValue(String.class);

                    if (nameInDb != null && emailInDb != null &&
                            nameInDb.equals(usersName) && emailInDb.equals(usersEmail)) {
                        userFound = true;
                        String password = userSnapshot.child("password").getValue(String.class);

                        if (password != null) {
                            Toast.makeText(FindPwActivity.this, "Password: " + password, Toast.LENGTH_LONG).show();
                            loginButton.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(FindPwActivity.this, "Password not found for " + usersName, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }

                if (!userFound) {
                    Toast.makeText(FindPwActivity.this, "User does not exist.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(FindPwActivity.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
