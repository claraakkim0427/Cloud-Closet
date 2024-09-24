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

public class ChooseJacketActivity extends AppCompatActivity {
    private RecyclerView recyclerJacketView;
    private ClothingItemAdapter adapterJacket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_jacket);

        // Initialize views
        recyclerJacketView = findViewById(R.id.recyclerView5);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        if(id == null) {
            Log.d("chooseBag", "idNull");
        }

        // Initialize adapter and set RecyclerView layout manager
        adapterJacket = new ClothingItemAdapter(id);
        recyclerJacketView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerJacketView.setAdapter(adapterJacket);

        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);


        if (userId != null) {
            loadFirebaseData(userId, id);
        } else {
            Log.e("ChooseJacketActivity", "No user ID found in SharedPreferences.");
        }
    }
    private void loadFirebaseData(String userId, String id) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapterJacket.clearItems();
                DataSnapshot closetSnapshot = dataSnapshot.child("closet");
                for (DataSnapshot itemSnapshot : closetSnapshot.getChildren()) {
                    ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                    if (item != null && "Jacket".equalsIgnoreCase(item.getCategory())) {
                        adapterJacket.addItem(item);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ChooseJacketActivity", "Error loading closet data: " + databaseError.getMessage());
            }
        });
    }
}
