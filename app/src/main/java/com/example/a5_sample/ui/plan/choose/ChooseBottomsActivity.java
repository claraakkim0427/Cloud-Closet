package com.example.a5_sample.ui.plan.choose;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.R;
import com.example.a5_sample.ui.closet.ClothingItem;
import com.example.a5_sample.ui.closet.ClothingItemAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChooseBottomsActivity extends AppCompatActivity {

    private RecyclerView recyclerBottomsView;
    private ClothingItemAdapter adapterBottoms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bottom);

        // Initialize views
        recyclerBottomsView = findViewById(R.id.recyclerView4);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        if(id == null) {
            Log.d("chooseBag", "idNull");
        }

        // Initialize adapter and set RecyclerView layout manager
        adapterBottoms = new ClothingItemAdapter(id);
        recyclerBottomsView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerBottomsView.setAdapter(adapterBottoms);

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);


        if (userId != null) {
            loadFirebaseData(userId);
        } else {
            Log.e("ChooseBottomsActivity", "No user ID found in SharedPreferences.");
        }
    }
    private void loadFirebaseData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapterBottoms.clearItems();
                DataSnapshot closetSnapshot = dataSnapshot.child("closet");
                for (DataSnapshot itemSnapshot : closetSnapshot.getChildren()) {
                    ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                    if (item != null && ("Shorts".equalsIgnoreCase(item.getCategory()) || "Pants".equalsIgnoreCase(item.getCategory()) || "Skirt".equalsIgnoreCase(item.getCategory()) || "Leggings".equalsIgnoreCase(item.getCategory()))) {
                        adapterBottoms.addItem(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChooseBottomsActivity", "Error loading closet data: " + databaseError.getMessage());
            }
        });
    }
}
