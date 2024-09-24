package com.example.a5_sample.ui.plan.edit;

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

public class EditChooseAccessoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerAccessoriesView;
    private ClothingItemAdapter adapterAccessories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_accessories);
        // Initialize views
        recyclerAccessoriesView = findViewById(R.id.recyclerAccessoriesView);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        if(id == null) {
            Log.d("chooseBag", "idNull");
        }
        // Initialize adapter and set RecyclerView layout manager
        adapterAccessories = new ClothingItemAdapter(id);
        recyclerAccessoriesView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerAccessoriesView.setAdapter(adapterAccessories);

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);


        if (userId != null) {
            loadFirebaseData(userId);
        } else {
            Log.e("EditChooseAccessoriesActivity", "No user ID found in SharedPreferences.");
        }
    }
    private void loadFirebaseData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapterAccessories.clearItems();
                DataSnapshot closetSnapshot = dataSnapshot.child("closet");
                for (DataSnapshot itemSnapshot : closetSnapshot.getChildren()) {
                    ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                    if (item != null && "Accessories".equalsIgnoreCase(item.getCategory())) {
                        adapterAccessories.addItem(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("EditChooseAccessoriesActivity", "Error loading closet data: " + databaseError.getMessage());
            }
        });
    }

}
