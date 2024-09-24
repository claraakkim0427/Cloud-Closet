package com.example.a5_sample.ui.closet;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.example.a5_sample.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import androidx.annotation.NonNull;






import java.util.ArrayList;


public class FilterActivity extends AppCompatActivity {
    //TODO check that the x actually closes filter
    private RadioButton grey, black, white, purple, pink, brown, green, light_blue, yellow,
            dark_blue, red, orange;


    private CheckBox shorts, jacket, springOption, summerOption, fallOption, winterOption,
            longSleeveBlouse, sweatshirtSweater, pants, tShirtOption, bagOption,
            leggings, skirt, shoesOption, tankTopOption, dress, accessoriesOption;
    private Switch switch1;
    private Button applyFilterButton;
    private ArrayList<ClothingItem> originalItemList; // list of clothing items?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        setContentView(R.layout.popup_filter);


        // Initialize UI elements
        grey = findViewById(R.id.grey);
        black = findViewById(R.id.black);
        white = findViewById(R.id.white);
        purple = findViewById(R.id.purple);
        pink = findViewById(R.id.pink);
        brown = findViewById(R.id.brown);
        light_blue = findViewById(R.id.light_blue);
        yellow = findViewById(R.id.yellow);
        dark_blue = findViewById(R.id.dark_blue);
        red = findViewById(R.id.red);
        orange = findViewById(R.id.orange);
        green = findViewById(R.id.green);


        springOption = findViewById(R.id.spring_option);
        summerOption = findViewById(R.id.summer_option);
        fallOption = findViewById(R.id.fall_option);
        winterOption = findViewById(R.id.winter_option);


        shorts = findViewById(R.id.shorts);
        jacket = findViewById(R.id.jacket);
        longSleeveBlouse = findViewById(R.id.long_sleeve_blouse);
        sweatshirtSweater = findViewById(R.id.sweatshirt_sweater);
        pants = findViewById(R.id.pants);
        tShirtOption = findViewById(R.id.t_shirt_option);
        bagOption = findViewById(R.id.bag_option);
        leggings = findViewById(R.id.leggings);
        skirt = findViewById(R.id.skirt);
        shoesOption = findViewById(R.id.shoes_option);
        tankTopOption = findViewById(R.id.tank_top_option);
        dress = findViewById(R.id.dress);
        accessoriesOption = findViewById(R.id.accessories_option);
        switch1 = findViewById(R.id.switch1);
        applyFilterButton = findViewById(R.id.filter_button);


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.gravity = Gravity.CENTER;
        params.dimAmount = 0.6f;
        getWindow().setAttributes(params);


        Button closeButton = findViewById(R.id.button2);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        //  original list of items from somewhere?
        originalItemList = new ArrayList<ClothingItem>();
        // Load clothing items from Firebase
        loadFirebaseData();
        applyFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyFilters();
            }
        });
    }


    private void loadFirebaseData() {


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);


        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference databaseRef = usersRef.child("users").child(userId).child("closet");




        databaseRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ClothingItem item = snapshot.getValue(ClothingItem.class);
                    if (item != null) {
                        originalItemList.add(item);
                        Log.d("issue5", item.getImageUrl());
                    }
                }
                Log.d("issue5", originalItemList.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FilterActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void applyFilters() {
        // Get the selected filters
        boolean includeGrey = grey.isChecked();
        boolean includeBlack = black.isChecked();
        boolean includeWhite = white.isChecked();
        boolean includePurple = purple.isChecked();
        boolean includePink = pink.isChecked();
        boolean includeBrown = brown.isChecked();
        boolean includeGreen = green.isChecked();
        boolean includeLightBlue = light_blue.isChecked();
        boolean includeYellow = yellow.isChecked();
        boolean includeDarkBlue = dark_blue.isChecked();
        boolean includeRed = red.isChecked();
        boolean includeOrange = orange.isChecked();


        boolean includeSpring = springOption.isChecked();
        boolean includeSummer = summerOption.isChecked();
        boolean includeFall = fallOption.isChecked();
        boolean includeWinter = winterOption.isChecked();


        boolean includeShorts = shorts.isChecked();
        boolean includeJacket = jacket.isChecked();
        boolean includeLongSleeveBlouse = longSleeveBlouse.isChecked();
        boolean includeSweatshirtSweater = sweatshirtSweater.isChecked();
        boolean includePants = pants.isChecked();
        boolean includeTShirt = tShirtOption.isChecked();
        boolean includeBag = bagOption.isChecked();
        boolean includeLeggings = leggings.isChecked();
        boolean includeSkirt = skirt.isChecked();
        boolean includeShoes = shoesOption.isChecked();
        boolean includeTankTop = tankTopOption.isChecked();
        boolean includeDress = dress.isChecked();
        boolean includeAccessories = accessoriesOption.isChecked();
        boolean isLaundry = switch1.isChecked();


        // Perform filtering logic
        ArrayList<ClothingItem> colorFilteredItems = new ArrayList<>();
        ArrayList<ClothingItem> categoryFilteredItems = new ArrayList<>();
        ArrayList<ClothingItem> seasonFilteredItems = new ArrayList<>();
        ArrayList<ClothingItem> laundryFilteredItems = new ArrayList<>();


        //check if color selected


        boolean colorSelected = false;
        boolean categorySelected = false;
        boolean seasonSelected = false;


        //check color
        if (includeGrey || includeBlack || includeWhite || includePurple || includePink ||
                includeBrown || includeGreen || includeLightBlue || includeYellow ||
                includeDarkBlue || includeRed || includeOrange) {
            colorSelected = true;
            //from original, if item matches in color, add it to colorSelected
            for (ClothingItem item : originalItemList) {
                // Check if the item matches the selected filter
                if ((includeGrey && item.getColor().equals("Grey")) ||
                        (includeBlack && item.getColor().equals("Black")) ||
                        (includeWhite && item.getColor().equals("White")) ||
                        (includePurple && item.getColor().equals("Purple")) ||
                        (includePink && item.getColor().equals("Pink")) ||
                        (includeBrown && item.getColor().equals("Brown")) ||
                        (includeGreen && item.getColor().equals("Green")) ||
                        (includeLightBlue && item.getColor().equals("Light Blue")) ||
                        (includeYellow && item.getColor().equals("Yellow")) ||
                        (includeDarkBlue && item.getColor().equals("Dark Blue")) ||
                        (includeRed && item.getColor().equals("Red")) ||
                        (includeOrange && item.getColor().equals("Orange"))) {
                    // Add the item to the filtered list
                    colorFilteredItems.add(item);
                }
            }
        }
        //if category selected
        if (includeShorts || includeJacket || includeSweatshirtSweater || includePants || includeTShirt ||
                includeBag || includeLeggings || includeSkirt || includeShoes ||
                includeTankTop || includeDress || includeAccessories) {
            categorySelected = true;
            //if color filter present, load from sorted color
            if (colorSelected) {
                for (ClothingItem item : colorFilteredItems) {
                    // Check if the item matches the selected filter
                    if ((includeShorts && item.getCategory().equals("Shorts")) ||
                            (includeJacket && item.getCategory().equals("Jacket")) ||
                            (includeLongSleeveBlouse && item.getCategory().equals("Long Sleeves/Blouse")) ||
                            (includeSweatshirtSweater && item.getCategory().equals("Sweatshirt/Sweater")) ||
                            (includePants && item.getCategory().equals("Pants")) ||
                            (includeTShirt && item.getCategory().equals("T-Shirt")) ||
                            (includeBag && item.getCategory().equals("Bag")) ||
                            (includeLeggings && item.getCategory().equals("Leggings")) ||
                            (includeSkirt && item.getCategory().equals("Skirt")) ||
                            (includeShoes && item.getCategory().equals("Shoes")) ||
                            (includeTankTop && item.getCategory().equals("Tank Top")) ||
                            (includeDress && item.getCategory().equals("Dress")) ||
                            (includeAccessories && item.getCategory().equals("Accessories"))) {
                        // Add the item to the filtered list
                        categoryFilteredItems.add(item);
                    }
                }
            } else { //else load from original
                for (ClothingItem item : originalItemList) {
                    // Check if the item matches the selected filter
                    if ((includeShorts && item.getCategory().equals("Shorts")) ||
                            (includeJacket && item.getCategory().equals("Jacket")) ||
                            (includeLongSleeveBlouse && item.getCategory().equals("Long Sleeves/Blouse")) ||
                            (includeSweatshirtSweater && item.getCategory().equals("Sweatshirt/Sweater")) ||
                            (includePants && item.getCategory().equals("Pants")) ||
                            (includeTShirt && item.getCategory().equals("T-Shirt")) ||
                            (includeBag && item.getCategory().equals("Bag")) ||
                            (includeLeggings && item.getCategory().equals("Leggings")) ||
                            (includeSkirt && item.getCategory().equals("Skirt")) ||
                            (includeShoes && item.getCategory().equals("Shoes")) ||
                            (includeTankTop && item.getCategory().equals("Tank Top")) ||
                            (includeDress && item.getCategory().equals("Dress")) ||
                            (includeAccessories && item.getCategory().equals("Accessories"))) {
                        // Add the item to the filtered list
                        categoryFilteredItems.add(item);
                    }
                }
            }
        }
        Log.d("filterIssue", "here3");
        //check seasons
        if (includeSummer || includeFall || includeSpring || includeWinter) {
            seasonSelected = true;
            //if color and category selected or just category, load from categoryFiltered
            //TODO: MAKE THE YELLOW GO AWAY
            if ((categorySelected)) {
                for (ClothingItem item : categoryFilteredItems) {
                    // Check if the item matches the selected filter
                    if ((includeSpring && item.getSeason().equals("Spring")) ||
                            (includeSummer && item.getSeason().equals("Summer")) ||
                            (includeFall && item.getSeason().equals("Fall")) ||
                            (includeWinter && item.getSeason().equals("Winter"))) {
                        // Add the item to the filtered list
                        seasonFilteredItems.add(item);
                    }
                }
            }
            //if only color and not category, load from colorFiltered
            if (colorSelected && (!categorySelected)) {
                for (ClothingItem item : colorFilteredItems) {
                    // Check if the item matches the selected filter
                    if ((includeSpring && item.getSeason().equals("Spring")) ||
                            (includeSummer && item.getSeason().equals("Summer")) ||
                            (includeFall && item.getSeason().equals("Fall")) ||
                            (includeWinter && item.getSeason().equals("Winter"))) {
                        // Add the item to the filtered list
                        seasonFilteredItems.add(item);
                    }
                }
            }
            //if neither, load from original
            if (!colorSelected && !categorySelected) {
                for (ClothingItem item : originalItemList) {
                    // Check if the item matches the selected filter
                    if ((includeSpring && item.getSeason().equals("Spring")) ||
                            (includeSummer && item.getSeason().equals("Summer")) ||
                            (includeFall && item.getSeason().equals("Fall")) ||
                            (includeWinter && item.getSeason().equals("Winter"))) {
                        // Add the item to the filtered list
                        seasonFilteredItems.add(item);
                    }
                }
            }
        }
        Log.d("filterIssue", "here2");
        // add the laundry code
        if (colorSelected && !categorySelected && !seasonSelected){
            //in color but not in the other three
            for (ClothingItem item : colorFilteredItems) {
                // Check if the item matches the selected filter
                if ((!isLaundry || (isLaundry && item.isLaundry()))) {
                    // Add the item to the filtered list
                    laundryFilteredItems.add(item);
                }
            }
        } else if (categorySelected && !seasonSelected){
            //in category but not in season
            for (ClothingItem item : categoryFilteredItems) {
                // Check if the item matches the selected filter
                if ((!isLaundry || (isLaundry && item.isLaundry()))) {
                    // Add the item to the filtered list
                    laundryFilteredItems.add(item);
                }
            }
        } else if (seasonSelected) {
            // in season
            for (ClothingItem item : seasonFilteredItems) {
                // Check if the item matches the selected filter
                if ((!isLaundry || (isLaundry && item.isLaundry()))) {
                    // Add the item to the filtered list
                    laundryFilteredItems.add(item);
                }
            }
        } else if (!colorSelected && !categorySelected && !seasonSelected) {
            //nothing selected
            for (ClothingItem item : originalItemList) {
                // Check if the item matches the selected filter
                if ((!isLaundry || (isLaundry && item.isLaundry()))) {
                    // Add the item to the filtered list
                    laundryFilteredItems.add(item);
                }
            }
        }

        Log.d("issue78", laundryFilteredItems.toString());
        Log.d("issue78", laundryFilteredItems.get(0).getImageUrl());
        Log.d("filterIssue", "here1");

        

        // TODO: Display the filtered items
        // Pass filtered items back to ClosetFragment
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra("filteredItems", laundryFilteredItems);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}

