package com.example.a5_sample.ui.plan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.example.a5_sample.ui.closet.ClothingItem;
import com.example.a5_sample.ui.plan.choose.ChooseActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddOutfitActivity extends AppCompatActivity {
    private ImageButton backButton;
    private Button randomButton;
    private Button chooseButton;
    private Button saveFitButton;

    private TextInputEditText eventNameInput;
    private TextInputEditText locationInput;
    private TextInputEditText dateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_outfit);

        backButton = findViewById(R.id.addoutfitBackButton);
        randomButton = findViewById(R.id.randomBotton);
        chooseButton = findViewById(R.id.chooseBotton);
        saveFitButton = findViewById(R.id.saveFitBotton);

        eventNameInput = findViewById(R.id.eventNameInput);
        locationInput = findViewById(R.id.locationInput);
        dateInput = findViewById(R.id.textInputEditText);

        Intent intent = getIntent();
        String planId = intent.getStringExtra("id");
        if(planId != null) {
            Log.d("addOutfit", planId);
        }

        //String planId = UUID.randomUUID().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        DatabaseReference userRef = database.getReference("users").child(userId).child("outfit_plans");

        ArrayList<String> clothingItems = new ArrayList<String>();


        saveFitButton.setOnClickListener(v -> {
            if (isFormValid1(planId)) {
                saveOutfit(userRef, planId);
                Intent intent1 = new Intent(this, MainActivity.class);
                startActivity(intent1);
                finish();
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());

        randomButton.setOnClickListener(v -> {
            if (isFormValid()) {
                Intent intent1 = new Intent(this, RandomActivity.class);
                intent1.putExtra("eventName", eventNameInput.getText().toString().trim());
                intent1.putExtra("location", locationInput.getText().toString().trim());
                intent1.putExtra("date", dateInput.getText().toString().trim());
                intent1.putExtra("id", planId);
                startActivity(intent1);
                //startActivity(intent);
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });

        chooseButton.setOnClickListener(v -> {
            if (isFormValid()) {
                Intent intent1 = new Intent(this, ChooseActivity.class);
                intent1.putExtra("eventName", eventNameInput.getText().toString().trim());
                intent1.putExtra("location", locationInput.getText().toString().trim());
                intent1.putExtra("date", dateInput.getText().toString().trim());
                //intent.putExtra("id", planId);
                startActivity(intent1);
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isFormValid() {
        String eventName = eventNameInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        return !eventName.isEmpty() && !location.isEmpty() && !date.isEmpty();
    }

    private boolean isFormValid1(String planId) {
        String eventName = eventNameInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();

        boolean bool = planId != null;

        return !eventName.isEmpty() && !location.isEmpty() && !date.isEmpty() && bool;
    }

    private void saveOutfit(DatabaseReference userRef, String planId) {
        String eventName = eventNameInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();
        String date = dateInput.getText().toString().trim();





        Map<String, Object> outfitPlan = new HashMap<>();
        outfitPlan.put("eventName", eventName);
        outfitPlan.put("location", location);
        outfitPlan.put("date", date);



        userRef.child(planId).setValue(outfitPlan);
        //userRef.child(planId).child("eventName").setValue(eventName);
        //userRef.child(planId).child("location").setValue(location);
        //userRef.child(planId).child("date").setValue(date);


        Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
    }
}