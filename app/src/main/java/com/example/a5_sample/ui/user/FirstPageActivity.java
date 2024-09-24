package com.example.a5_sample.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.R;

public class FirstPageActivity extends AppCompatActivity {
    private Button firstloginButton;
    private Button firstsignupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);

        firstloginButton = findViewById(R.id.firstloginButton);
        firstsignupButton = findViewById(R.id.firstsignupButton);

        firstloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstPageActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
       firstsignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstPageActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
