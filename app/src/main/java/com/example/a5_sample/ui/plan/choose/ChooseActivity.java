package com.example.a5_sample.ui.plan.choose;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.example.a5_sample.ui.closet.EditItemActivity;
import com.example.a5_sample.ui.plan.AddOutfitActivity;
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

public class ChooseActivity extends AppCompatActivity {
    private ImageButton backButton;
    private Button chooseJacketButton;
    private Button chooseTopDressButton;
    private Button chooseBottomButton;
    private Button chooseShoesButton;
    private Button chooseBagButton;
    private Button chooseAccButton;
    private Button saveButton;

    private String eventName;
    private String location;
    private String date;

    String planId;

    //String planId = UUID.randomUUID().toString();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        backButton = findViewById(R.id.chooseBackButton);
        backButton.setOnClickListener(v -> finish());

        Intent intent = getIntent();

        // Extract the data using the same key used to put it in
        String itemName = intent.getStringExtra("clothing_item_name");
        //String eventName = intent.getStringExtra("eventName");
        //String location = intent.getStringExtra("location");
        //String date = intent.getStringExtra("date");

        eventName = getIntent().getStringExtra("eventName");
        location = getIntent().getStringExtra("location");
        date = getIntent().getStringExtra("date");
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();



        //String itemLoc = intent.getStringExtra("location");
        String planId = intent.getStringExtra("id");
        if (itemName == null) {
            planId = UUID.randomUUID().toString();
        } else if (planId == null){
            Log.d("here", "planId is null");
        }

        ImageView jacket = findViewById(R.id.jacketIcon);
        ImageView top = findViewById(R.id.top_dressIcon);
        ImageView bottom = findViewById(R.id.bottomIcon);
        ImageView shoes = findViewById(R.id.shoesIcon);
        ImageView bag = findViewById(R.id.bagIcon);
        ImageView acc = findViewById(R.id.accIcon);

        FirebaseDatabase database = FirebaseDatabase.getInstance();


        // Get userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please login.", Toast.LENGTH_SHORT).show();
            return; // Exit the activity
        }


        //final long[] count = {0};
        Log.d("here1", "child");
        DatabaseReference outfitPlansRef = usersRef.child("users").child(userId).child("outfit_plans").child(planId).child("clothingItemIds");

        if (itemName == null) {
            DatabaseReference outfitDetailsRef = usersRef.child("users").child(userId).child("outfit_plans").child(planId);
            outfitDetailsRef.child("eventName").setValue(eventName);
            outfitDetailsRef.child("location").setValue(location);
            outfitDetailsRef.child("date").setValue(date);
        }
        outfitPlansRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    String itemId = childSnapshot.getValue(String.class);
                    if (itemId != null) {
                        Log.d("here2", "child");
                        DatabaseReference closetRef = usersRef.child("users").child(userId).child("closet").child(itemId);
                        Log.d("here3", "child");
                        DatabaseReference imageRef = closetRef.child("imageUrl");
                        imageRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                DatabaseReference categoryRef = closetRef.child("category");
                                final String[] category = new String[1];
                                String imageUrl = snapshot.getValue(String.class);
                                categoryRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        category[0] = snapshot.getValue(String.class);

                                        String categoryTemp = category[0];
                                        if (categoryTemp.equals("Jacket")) {
                                            usersRef.child("users").child(userId).child("outfit_plans");
                                            Glide.with(ChooseActivity.this)
                                                    .load(imageUrl)
                                                    .into(jacket);
                                        } else if (categoryTemp.equals("Shoes")) {
                                            Glide.with(ChooseActivity.this)
                                                    .load(imageUrl)
                                                    .into(shoes);
                                        } else if (categoryTemp.equals("Bag")) {
                                            Glide.with(ChooseActivity.this)
                                                    .load(imageUrl)
                                                    .into(bag);
                                        } else if (categoryTemp.equals("Accessories")) {
                                            Glide.with(ChooseActivity.this)
                                                    .load(imageUrl)
                                                    .into(acc);
                                        } else if (categoryTemp.equals("T-Shirt") || categoryTemp.equals("Long Sleeves/Blouse") ||
                                                categoryTemp.equals("Sweatshirt/Sweater") || categoryTemp.equals("Dress")) {
                                            Glide.with(ChooseActivity.this)
                                                    .load(imageUrl)
                                                    .into(top);
                                        } else {
                                            Glide.with(ChooseActivity.this)
                                                    .load(imageUrl)
                                                    .into(bottom);
                                        }
                                    }


                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle onCancelled
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle onCancelled
                            }
                        });
                        System.out.println("Clothing Item ID: " + itemId);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Error: " + databaseError.getMessage());
            }
        });


        //NEED TO DISPLAY THE ITEMS ALREADY IN THE OUTFIT!!!

        if (itemName != null) {
            Log.d("here4", "child");
            DatabaseReference closetRef = usersRef.child("users").child(userId).child("closet").child(itemName);


            outfitPlansRef.push().setValue(itemName)
                    .addOnSuccessListener(aVoid -> {
                        System.out.println("Successfully added item to the array");
                    })
                    .addOnFailureListener(e -> {
                        System.err.println("Failed to add item: " + e.getMessage());
                    });

            DatabaseReference imageRef = closetRef.child("imageUrl");
            imageRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DatabaseReference categoryRef = closetRef.child("category");
                    final String[] category = new String[1];
                    String imageUrl = snapshot.getValue(String.class);
                    categoryRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            category[0] = snapshot.getValue(String.class);

                            String categoryTemp = category[0];
                            if (categoryTemp.equals("Jacket")) {
                                usersRef.child("users").child(userId).child("outfit_plans");
                                Glide.with(ChooseActivity.this)
                                        .load(imageUrl)
                                        .into(jacket);
                            } else if (categoryTemp.equals("Shoes")) {
                                Glide.with(ChooseActivity.this)
                                        .load(imageUrl)
                                        .into(shoes);
                            } else if (categoryTemp.equals("Bag")) {
                                Glide.with(ChooseActivity.this)
                                        .load(imageUrl)
                                        .into(bag);
                            } else if (categoryTemp.equals("Accessories")) {
                                    Glide.with(ChooseActivity.this)
                                            .load(imageUrl)
                                            .into(acc);
                            } else if (categoryTemp.equals("T-Shirt") || categoryTemp.equals("Long Sleeves/Blouse") ||
                                    categoryTemp.equals("Sweatshirt/Sweater") || categoryTemp.equals("Dress")) {
                                Glide.with(ChooseActivity.this)
                                        .load(imageUrl)
                                        .into(top);
                            } else {
                                Glide.with(ChooseActivity.this)
                                        .load(imageUrl)
                                        .into(bottom);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle onCancelled
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });
        }


        chooseJacketButton = findViewById(R.id.chooseJacketButton);
        chooseTopDressButton = findViewById(R.id.chooseTopDressButton);
        chooseBottomButton = findViewById(R.id.chooseBottomButton);
        chooseShoesButton = findViewById(R.id.chooseShoesButton);
        chooseBagButton = findViewById(R.id.chooseBagButton);
        chooseAccButton = findViewById(R.id.chooseAccessoriesButton);
        saveButton = findViewById(R.id.saveOutfitButton);

        // Firebase reference
        //FirebaseDatabase database = FirebaseDatabase.getInstance();

        // Get userId from SharedPreferences
        //SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), MODE_PRIVATE);
        //String userId = sharedPreferences.getString("UserId", null);

        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please login.", Toast.LENGTH_SHORT).show();
            return; // Exit the activity
        }
        //TODO:alter
        DatabaseReference userRef = database.getReference("users").child(userId).child("outfit_plans");


        String finalPlanId = planId;
        Log.d("null", "here");
        Log.d("null", finalPlanId);
        chooseJacketButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ChooseJacketActivity.class);
            intent1.putExtra("id", finalPlanId);
            startActivity(intent1);
        });
        chooseTopDressButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ChooseTopDressActivity.class);
            intent1.putExtra("id", finalPlanId);
            startActivity(intent1);
        });
        chooseBottomButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ChooseBottomsActivity.class);
            intent1.putExtra("id", finalPlanId);
            startActivity(intent1);
        });

        chooseShoesButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ChooseShoesActivity.class);
            intent1.putExtra("id", finalPlanId);
            startActivity(intent1);
        });
        chooseBagButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ChooseBagActivity.class);
            intent1.putExtra("id", finalPlanId);
            startActivity(intent1);
        });
        chooseAccButton.setOnClickListener(v -> {
            Intent intent1 = new Intent(this, ChooseAccessoriesActivity.class);
            intent1.putExtra("id", finalPlanId);
            startActivity(intent1);
        });

        String finalPlanId1 = planId;
        saveButton.setOnClickListener(v -> {
            List<String> clothingItemIds = new ArrayList<>();

            Map<String, Object> outfitPlan = new HashMap<>();
            /*outfitPlan.put("eventName", eventName);
            Log.d("is null?", "null");
            Log.d("is null?", eventName);
            outfitPlan.put("location", location);
            outfitPlan.put("date", date);*/

            outfitPlansRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // Loop through each child in the snapshot
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        // Extract the value of each child
                        String clothingItemId = childSnapshot.getValue(String.class);
                        clothingItemIds.add(clothingItemId);
                        //System.out.println("Clothing Item ID: " + clothingItemId);
                    }
                    outfitPlan.put("clothingItemIds", clothingItemIds);

                    DatabaseReference outfitPlansRef1 = database.getReference("users").child(userId).child("outfit_plans");

                    outfitPlansRef1.child(finalPlanId1).child("clothingItemIds").setValue(clothingItemIds);
                    /*outfitPlansRef1.child(finalPlanId1).setValue(outfitPlan)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ChooseActivity.this, "Outfit saved!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ChooseActivity.this, "Failed to save outfit.", Toast.LENGTH_SHORT).show();
                                }
                            });*/
                    Intent intent2 = new Intent(ChooseActivity.this, MainActivity.class);
                    intent2.putExtra("target_fragment", "PlanFragment");
                    startActivity(intent2);
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.err.println("Error: " + databaseError.getMessage());
                }
            });


        });

    }
}
