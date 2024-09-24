package com.example.a5_sample.ui.plan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.example.a5_sample.ui.closet.ClothingItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RandomActivity extends AppCompatActivity {
    private ImageButton backButton;
    private Button generateRandomButton;
    private Button generateAgainButton;
    private Button saveFitButton;
    private Spinner seasonSpinner;
    private Spinner colorSpinner;
    private ImageView topImageView;
    private ImageView bottomImageView;
    private ImageView shoesImageView;
    private ImageView accessoriesImageView;
    private ImageView bagImageView;

    private List<ClothingItem> clothingItems;
    private Random random;
    private List<ClothingItem> generatedOutfit;
    private String eventName;
    private String location;
    private String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random);

        eventName = getIntent().getStringExtra("eventName");
        location = getIntent().getStringExtra("location");
        date = getIntent().getStringExtra("date");

        backButton = findViewById(R.id.backtoChooseOutfitButton);
        generateRandomButton = findViewById(R.id.generateRandomButton);
        generateAgainButton = findViewById(R.id.generateAgainButton);
        saveFitButton = findViewById(R.id.saveFitButton);
        seasonSpinner = findViewById(R.id.seasonSpinner);
        colorSpinner = findViewById(R.id.colorSpinner);
        topImageView = findViewById(R.id.topImageView);
        bottomImageView = findViewById(R.id.bottomImageView);
        shoesImageView = findViewById(R.id.shoesImageView);
        accessoriesImageView = findViewById(R.id.accessoriesImageView);
        bagImageView = findViewById(R.id.bagImageView);

        random = new Random();
        generatedOutfit = new ArrayList<>();

        generateRandomButton.setVisibility(View.VISIBLE);
        generateAgainButton.setVisibility(View.GONE);
        saveFitButton.setVisibility(View.GONE);

        loadClothingItems();
        setupSpinners();

        backButton.setOnClickListener(v -> finish());

        generateRandomButton.setOnClickListener(v -> {
            clearGeneratedItems();
            generateOutfit();
            generateRandomButton.setVisibility(View.GONE);
            generateAgainButton.setVisibility(View.VISIBLE);
            saveFitButton.setVisibility(View.VISIBLE);
        });

        generateAgainButton.setOnClickListener(v -> {
            clearGeneratedItems();
            generateOutfit();
        });

        saveFitButton.setOnClickListener(v -> {
            saveOutfitToDatabase();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("target_fragment", "PlanFragment");
            startActivity(intent);
            finish();

        });
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> seasonAdapter = new ArrayAdapter<>(
                this,
                R.layout.random_dropdown,
                getResources().getStringArray(R.array.seasons)
        );
        seasonAdapter.setDropDownViewResource(R.layout.random_dropdown);
        seasonSpinner.setAdapter(seasonAdapter);

        ArrayAdapter<CharSequence> colorAdapter = new ArrayAdapter<>(
                this,
                R.layout.random_dropdown,
                getResources().getStringArray(R.array.colors)
        );
        colorAdapter.setDropDownViewResource(R.layout.random_dropdown);
        colorSpinner.setAdapter(colorAdapter);
    }

    private void clearGeneratedItems() {
        topImageView.setImageResource(0);
        bottomImageView.setImageResource(0);
        shoesImageView.setImageResource(0);
        accessoriesImageView.setImageResource(0);
        bagImageView.setImageResource(0);
    }

    private void loadClothingItems() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        DatabaseReference closetRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("closet");

        closetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ClothingItem> items = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClothingItem item = snapshot.getValue(ClothingItem.class);
                    if (item != null) {
                        items.add(item);
                    }
                }
                clothingItems = items;
                if (clothingItems.isEmpty()) {
                    Toast.makeText(RandomActivity.this, "No items found in closet.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RandomActivity.this, "Failed to load closet items: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateOutfit() {
        if (clothingItems == null || clothingItems.isEmpty()) {
            Toast.makeText(this, "No items found in closet.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedSeason = seasonSpinner.getSelectedItem().toString();
        String selectedColor = colorSpinner.getSelectedItem().toString();

        List<ClothingItem> potentialTops = new ArrayList<>();
        List<ClothingItem> potentialBottoms = new ArrayList<>();
        List<ClothingItem> potentialShoes = new ArrayList<>();
        List<ClothingItem> potentialAccessories = new ArrayList<>();
        List<ClothingItem> potentialBags = new ArrayList<>();

        for (ClothingItem item : clothingItems) {
            boolean seasonMatch = "Select Season".equals(selectedSeason) || item.getSeason().equals(selectedSeason);
            boolean colorMatch = "Select Color".equals(selectedColor) || item.getColor().equals(selectedColor);

            if (seasonMatch && colorMatch) {
                switch (item.getCategory()) {
                    case "Tank Top":
                    case "T-Shirt":
                    case "Long Sleeves/Blouse":
                    case "Jacket":
                    case "Dress":
                        potentialTops.add(item);
                        break;
                    case "Pants":
                    case "Leggings":
                    case "Skirt":
                    case "Shorts":
                        potentialBottoms.add(item);
                        break;
                    case "Shoes":
                        potentialShoes.add(item);
                        break;
                    case "Bag":
                        potentialBags.add(item);
                        break;
                    case "Accessories":
                        potentialAccessories.add(item);
                        break;
                }
            }
        }

        ClothingItem top = getRandomItem(potentialTops);
        ClothingItem bottom = null;

        if (top != null && !top.getCategory().equals("Dress")) {
            bottom = getRandomItem(potentialBottoms);
        }

        ClothingItem shoes = getRandomItem(potentialShoes);
        ClothingItem bag = getRandomItem(potentialBags);
        ClothingItem accessories = getRandomItem(potentialAccessories);

        if (top != null) {
            Picasso.get().load(top.getImageUrl()).into(topImageView);
        }

        if (bottom != null) {
            Picasso.get().load(bottom.getImageUrl()).into(bottomImageView);
        } else {
            bottomImageView.setImageResource(0);
        }

        if (shoes != null) {
            Picasso.get().load(shoes.getImageUrl()).into(shoesImageView);
        }

        if (bag != null && random.nextBoolean()) {
            Picasso.get().load(bag.getImageUrl()).into(bagImageView);
        }

        if (accessories != null && random.nextBoolean()) {
            Picasso.get().load(accessories.getImageUrl()).into(accessoriesImageView);
        }
        generatedOutfit.clear();
        if (top != null) {
            generatedOutfit.add(top);
        }
        if (bottom != null) {
            generatedOutfit.add(bottom);
        }
        if (shoes != null) {
            generatedOutfit.add(shoes);
        }
        if (bag != null) {
            generatedOutfit.add(bag);
        }
        if (accessories != null) {
            generatedOutfit.add(accessories);
        }
    }

    private ClothingItem getRandomItem(List<ClothingItem> items) {
        if (items != null && !items.isEmpty()) {
            return items.get(random.nextInt(items.size()));
        }
        return null;
    }

    private void saveOutfitToDatabase() {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference outfitPlansRef = database.getReference("users").child(userId).child("outfit_plans");

        String outfitPlanId = UUID.randomUUID().toString();

        Map<String, Object> outfitPlan = new HashMap<>();
        outfitPlan.put("eventName", eventName);
        outfitPlan.put("location", location);
        outfitPlan.put("date", date);

        List<String> clothingItemIds = new ArrayList<>();
        if (generatedOutfit != null) {
            for (ClothingItem item : generatedOutfit) {
                clothingItemIds.add(item.getName());
            }
        }
        outfitPlan.put("clothingItemIds", clothingItemIds);

        outfitPlansRef.child(outfitPlanId).setValue(outfitPlan)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Outfit saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save outfit.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}