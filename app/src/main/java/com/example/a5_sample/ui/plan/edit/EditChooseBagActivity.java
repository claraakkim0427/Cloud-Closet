package com.example.a5_sample.ui.plan.edit;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.R;
import com.example.a5_sample.ui.closet.ClothingItemAdapter;
import com.example.a5_sample.ui.closet.ClothingItem;


public class EditChooseBagActivity extends AppCompatActivity {
    private RecyclerView recyclerBagView;
    private ClothingItemAdapter adapterBag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_bag);

        // Initialize views
        recyclerBagView = findViewById(R.id.recyclerView3);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        if(id == null) {
            Log.d("chooseBag", "idNull");
        }
        // Initialize adapter and set RecyclerView layout manager
        adapterBag = new ClothingItemAdapter(id);
        recyclerBagView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerBagView.setAdapter(adapterBag);

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);


        if (userId != null) {
            loadFirebaseData(userId);
        } else {
            Log.e("EditChooseBagActivity", "No user ID found in SharedPreferences.");
        }
    }
    private void loadFirebaseData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapterBag.clearItems();
                DataSnapshot closetSnapshot = dataSnapshot.child("closet");
                for (DataSnapshot itemSnapshot : closetSnapshot.getChildren()) {
                    ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                    if (item != null && "bag".equalsIgnoreCase(item.getCategory())) {
                        adapterBag.addItem(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("EditChooseBagActivity", "Error loading closet data: " + databaseError.getMessage());
            }
        });
    }
}
