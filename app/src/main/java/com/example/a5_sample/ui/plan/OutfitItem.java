package com.example.a5_sample.ui.plan;

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
import com.example.a5_sample.ui.closet.ClothingItem;
import com.example.a5_sample.ui.plan.choose.ChooseAccessoriesActivity;
import com.example.a5_sample.ui.plan.choose.ChooseBagActivity;
import com.example.a5_sample.ui.plan.choose.ChooseBottomsActivity;
import com.example.a5_sample.ui.plan.choose.ChooseJacketActivity;
import com.example.a5_sample.ui.plan.choose.ChooseShoesActivity;
import com.example.a5_sample.ui.plan.choose.ChooseTopDressActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class OutfitItem {
    private String id;
    private String eventName;
    private String location;
    private String date;
    private List<String> clothingItemIds;
    private List<ClothingItem> clothingItems;

    public OutfitItem() {
    }

    public OutfitItem(String id, String eventName, String location, String date, List<String> clothingItemIds) {
        this.id = id;
        this.eventName = eventName;
        this.location = location;
        this.date = date;
        this.clothingItemIds = clothingItemIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getClothingItemIds() {
        return clothingItemIds;
    }

    public void setClothingItemIds(List<String> clothingItemIds) {
        this.clothingItemIds = clothingItemIds;
    }

    public List<ClothingItem> getClothingItems() {
        return clothingItems;
    }

    public void setClothingItems(List<ClothingItem> clothingItems) {
        this.clothingItems = clothingItems;
    }

    public static class EditOutfitActivity extends AppCompatActivity {
        private ImageButton backButton;
        private Button saveFitButton;
        private TextInputEditText eventNameInput;
        private TextInputEditText locationInput;
        private TextInputEditText dateInput;
        private ImageView shoeImage, bagImage, accImage, topImage, jacketImage, bottomImage;
        private List<ClothingItem> clothingItems = new ArrayList<>();
        private DatabaseReference userRef;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_outfit);

            ImageView shoeImage= findViewById(R.id.shoesIcon);
            ImageView bagImage= findViewById(R.id.bagIcon);
            ImageView accImage= findViewById(R.id.accIcon);
            ImageView topImage= findViewById(R.id.top_dressIcon);
            ImageView jacketImage= findViewById(R.id.jacketIcon);
            ImageView bottomImage= findViewById(R.id.bottomIcon);

            Button shoeButton = findViewById(R.id.editchooseShoesButton);
            Button bagButton = findViewById(R.id.editchooseBagButton);
            Button accButton = findViewById(R.id.editchooseAccessoriesButton);
            Button topButton = findViewById(R.id.editchooseTopDressButton);
            Button jacketButton = findViewById(R.id.editchooseJacketButton);
            Button bottomButton = findViewById(R.id.editchooseBottomButton);

            /*
            Button shoeDone = findViewById(R.id.closeShoes);
            Button bagDone = findViewById(R.id.closeBag);
            Button accDone = findViewById(R.id.closeAccessories);
            Button topDone = findViewById(R.id.closeTop);
            Button jacketDone = findViewById(R.id.closeJacket);
            Button bottomDone = findViewById(R.id.closeBottom);*/

            backButton = findViewById(R.id.addoutfitBackButton);
            saveFitButton = findViewById(R.id.saveFitBotton);

            eventNameInput = findViewById(R.id.eventNameInput);
            locationInput = findViewById(R.id.locationInput);
            dateInput = findViewById(R.id.textInputEditText);

            String outfitId = getIntent().getStringExtra("id");

            if (outfitId == null) {
                finish();
            }
            FirebaseDatabase database = FirebaseDatabase.getInstance();

            // Get userId from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), MODE_PRIVATE);
            String userId = sharedPreferences.getString("UserId", null);

            if (userId == null) {
                Toast.makeText(this, "User ID not found. Please login.", Toast.LENGTH_SHORT).show();
                return; // Exit the activity
            }

            DatabaseReference userRef = database.getReference("users").child(userId).child("outfit_plans").child(outfitId);
            DatabaseReference nameRef = userRef.child("eventName");
            nameRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String nameTemp = snapshot.getValue(String.class);
                    eventNameInput.setText(nameTemp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });

            DatabaseReference locationRef = userRef.child("location");
            locationRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String locationTemp = snapshot.getValue(String.class);
                    locationInput.setText(locationTemp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });

            DatabaseReference dateRef = userRef.child("date");
            dateRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String dateTemp = snapshot.getValue(String.class);
                    dateInput.setText(dateTemp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle onCancelled
                }
            });

            //userRef.child("clothingItemIds");
            ArrayList<String> clothingItems = new ArrayList<String>();

            //BEGIN FIREBASE TESTING
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //adapter.clearItems();
                    DataSnapshot closetSnapshot = dataSnapshot.child("clothingItemIds");
                    for (DataSnapshot itemSnapshot : closetSnapshot.getChildren()) {
                        String item = itemSnapshot.getValue(String.class);
                        if (item != null) {
                            clothingItems.add(item);
                            DatabaseReference closetRef = database.getReference("users").child(userId);
                            ArrayList<ClothingItem> items = new ArrayList<>();
                            closetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    //adapter.clearItems();
                                    DataSnapshot closetSnapshot = dataSnapshot.child("closet");
                                    for (DataSnapshot itemSnapshot : closetSnapshot.getChildren()) {
                                        ClothingItem clothItem = itemSnapshot.getValue(ClothingItem.class);
                                        if(clothItem != null && clothItem.getName().equals(item)) {
                                            if(clothItem.getCategory().equals("Shoes")) {
                                                Glide.with(shoeImage.getContext())
                                                        .load(clothItem.getImageUrl())
                                                        .into(shoeImage);
                                            } else if(clothItem.getCategory().equals("Jacket")) {
                                                Glide.with(jacketImage.getContext())
                                                        .load(clothItem.getImageUrl())
                                                        .into(jacketImage);
                                            } else if(clothItem.getCategory().equals("Bag")) {
                                                Glide.with(bagImage.getContext())
                                                        .load(clothItem.getImageUrl())
                                                        .into(bagImage);
                                            } else if(clothItem.getCategory().equals("Accessories")) {
                                                Glide.with(accImage.getContext())
                                                        .load(clothItem.getImageUrl())
                                                        .into(accImage);
                                            } else if(clothItem.getCategory().equals("T-Shirt") || clothItem.getCategory().equals("Long Sleeves/Blouse") ||
                                                    clothItem.getCategory().equals("Sweatshirt/Sweater") || clothItem.getCategory().equals("Dress")) {
                                                Glide.with(topImage.getContext())
                                                        .load(clothItem.getImageUrl())
                                                        .into(topImage);
                                            } else {
                                                Glide.with(bottomImage.getContext())
                                                        .load(clothItem.getImageUrl())
                                                        .into(bottomImage);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e("ClosetFragment", "Error loading closet data: " + databaseError.getMessage());
                                }
                            });
                        }
                    }
                    Log.d("issueEditOutfit1", clothingItems.toString());
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ClosetFragment", "Error loading outfit data: " + databaseError.getMessage());
                }
            });

            /*
            shoeDone.setOnClickListener(v -> {
                remove("Shoes");
            });
            bottomDone.setOnClickListener(v -> {
                remove("Shorts");
                remove("Pants");
                remove("Skirt");
                remove("Leggings");
            });
            accDone.setOnClickListener(v -> {
                remove("Accessories");
            });
            topDone.setOnClickListener(v -> {
                remove("Long Sleeves/Blouse");
                remove("Sweatshirt/Sweater");
                remove("T-Shirt");
                remove("Tank Top");
                remove("Dress");
            });
            jacketDone.setOnClickListener(v -> {
                remove("Jacket");
            });
            bagDone.setOnClickListener(v -> {
                remove("Bag");
            });*/

            shoeButton.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, ChooseShoesActivity.class);
                intent1.putExtra("id", outfitId);
                startActivity(intent1);
            });
            jacketButton.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, ChooseJacketActivity.class);
                intent1.putExtra("id", outfitId);
                startActivity(intent1);
            });
            bottomButton.setOnClickListener(v -> {
                //userRef.child("clothingItemIds").
                Intent intent1 = new Intent(this, ChooseBottomsActivity.class);
                intent1.putExtra("id", outfitId);
                startActivity(intent1);
            });
            topButton.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, ChooseTopDressActivity.class);
                intent1.putExtra("id", outfitId);
                startActivity(intent1);
            });
            bagButton.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, ChooseBagActivity.class);
                intent1.putExtra("id", outfitId);
                startActivity(intent1);
            });
            accButton.setOnClickListener(v -> {
                Intent intent1 = new Intent(this, ChooseAccessoriesActivity.class);
                intent1.putExtra("id", outfitId);
                startActivity(intent1);
            });
            saveFitButton.setOnClickListener(v -> {
                // Get inputs
                String eventName = eventNameInput.getText().toString().trim();
                String location = locationInput.getText().toString().trim();
                String date = dateInput.getText().toString().trim();

                if (eventName.isEmpty() || location.isEmpty() || date.isEmpty()) {
                    Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AtomicBoolean eventBool = new AtomicBoolean(false);
                AtomicBoolean dateBool = new AtomicBoolean(false);
                AtomicBoolean locBool = new AtomicBoolean(false);

                // Firebase operations
                userRef.child("eventName").setValue(eventName)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                eventBool.set(true);

                                userRef.child("date").setValue(date)
                                        .addOnCompleteListener(taskDate -> {
                                            if (taskDate.isSuccessful()) {
                                                dateBool.set(true);

                                                userRef.child("location").setValue(location)
                                                        .addOnCompleteListener(taskLoc -> {
                                                            if (taskLoc.isSuccessful()) {
                                                                locBool.set(true);

                                                                // Now, after all tasks are complete, navigate
                                                                Intent intent = new Intent(EditOutfitActivity.this, MainActivity.class);
                                                                intent.putExtra("target_fragment", "PlanFragment");
                                                                startActivity(intent);
                                                                finish();
                                                            } else {
                                                                Toast.makeText(EditOutfitActivity.this, "Failed to save location.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(EditOutfitActivity.this, "Failed to save date.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(EditOutfitActivity.this, "Failed to save event name.", Toast.LENGTH_SHORT).show();
                            }
                        });
            });

            // Handle back button
            backButton.setOnClickListener(v -> finish());


        }
        private void remove(String category) {
            Iterator<ClothingItem> iterator = clothingItems.iterator();
            while (iterator.hasNext()) {
                ClothingItem item = iterator.next();
                if (category.equalsIgnoreCase(item.getCategory()) && item.getName() != null) {
                    DatabaseReference itemRef = userRef.child("clothingItems").child(item.getName()); // Adjust this if the path is incorrect
                    itemRef.removeValue().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditOutfitActivity.this, "Item removed successfully", Toast.LENGTH_SHORT).show();
                            clearImageView(category);
                            iterator.remove(); // Safely remove from list
                        } else {
                            Toast.makeText(EditOutfitActivity.this, "Failed to remove item", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break; // Stop loop once the first match is found and removed
                }
            }

        }
        private void clearImageView(String category) {
            switch (category) {
                case "Shoes":
                    Glide.with(getApplicationContext()).clear(shoeImage);
                    break;
                case "Jacket":
                    Glide.with(getApplicationContext()).clear(jacketImage);
                    break;
                case "Long Sleeves/Blouse":
                    Glide.with(getApplicationContext()).clear(topImage);
                    break;
            }
        }

    }
}
