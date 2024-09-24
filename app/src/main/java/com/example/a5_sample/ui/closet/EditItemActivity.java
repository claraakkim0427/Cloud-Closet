package com.example.a5_sample.ui.closet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.a5_sample.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class EditItemActivity extends AppCompatActivity {
    private ImageButton backButton;
    private Spinner clothesCategory;
    private Button addItemButton;
    private EditText clothesName;
    private ImageView clothesPic;
    private String imageUrl;
    private Uri imageUri;
    private StorageReference storageReference;
    private String clothesNameNew;
    private int position;
    private String[] categories = {
            "Tank Top", "T-Shirt", "Long Sleeves/Blouse", "Sweatshirt/Sweater",
            "Jacket", "Dress", "Pants", "Leggings", "Skirt", "Shorts", "Shoes", "Bag", "Accessories"
    };
    private ImageButton deleteItemButton;
    private Switch laundrySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        // Retrieve the Intent that started this activity
        Intent intent = getIntent();

        // Extract the data using the same key used to put it in
        String itemName = intent.getStringExtra("clothing_item_name");

        backButton = findViewById(R.id.edititemBackButton);
        clothesCategory = findViewById(R.id.editspinner);
        clothesName = findViewById(R.id.editeditTextName);
        addItemButton = findViewById(R.id.edit_item_add_button);
        clothesPic = findViewById(R.id.imageViewEdit);
        deleteItemButton = findViewById(R.id.deleteItemButton);
        Button plusButton = findViewById(R.id.add_button);
        Button subButton = findViewById(R.id.minus_button);
        TextView timesWorn = findViewById(R.id.editTimesWorn);

        CheckBox red = findViewById(R.id.red_edit);
        CheckBox orange = findViewById(R.id.orange_edit);
        CheckBox yellow = findViewById(R.id.yellow_edit);
        CheckBox green = findViewById(R.id.green_edit);
        CheckBox light_blue = findViewById(R.id.light_blue_edit);
        CheckBox dark_blue = findViewById(R.id.dark_blue_edit);
        CheckBox brown = findViewById(R.id.brown_edit);
        CheckBox pink = findViewById(R.id.pink_edit);
        CheckBox purple = findViewById(R.id.purple_edit);
        CheckBox grey = findViewById(R.id.grey_edit);
        CheckBox black = findViewById(R.id.black_edit);
        CheckBox white = findViewById(R.id.white_edit);

        CheckBox winterCheck = findViewById(R.id.editwinter_option);
        CheckBox fallCheck = findViewById(R.id.editfall_option);
        CheckBox springCheck = findViewById(R.id.editspring_option);
        CheckBox summerCheck = findViewById(R.id.editsummer_option);

        laundrySwitch = findViewById(R.id.laundry_switch);

        //back button functionality
        backButton.setOnClickListener(view -> finish());

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference closetRef = usersRef.child("users").child(userId).child("closet").child(itemName);
        //set the image to given image
        DatabaseReference imageRef = closetRef.child("imageUrl");
        imageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (EditItemActivity.this.isFinishing()) { // Check if the activity is finishing
                    return; // Exit if the activity is finishing to avoid loading images into a destroyed context
                }
                imageUrl = snapshot.getValue(String.class);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(EditItemActivity.this)
                            .load(imageUrl)
                            .into(clothesPic);
                } else {
                    clothesPic.setImageDrawable(null); // Clear image if no URL is present
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        //set the name to given name
        DatabaseReference nameRef = closetRef.child("name");
        nameRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nameTemp = snapshot.getValue(String.class);
                clothesName.setText(nameTemp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        //set the laundry status to given laundry status
        DatabaseReference laundryRef = closetRef.child("laundry");
        laundryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean inLaundry = snapshot.getValue(Boolean.class);
                if (inLaundry != null) {
                    laundrySwitch.setChecked(inLaundry);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        //set uses to given uses
        DatabaseReference usesRef = closetRef.child("uses");
        usesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer usesTemp = snapshot.getValue(Integer.class);
                timesWorn.setText(usesTemp.toString());  // Safely calling toString on a non-null Integer

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EditItemActivity", "Failed to read uses data.", error.toException());
            }
        });


        //set color to given color
        DatabaseReference colorRef = closetRef.child("color");
        colorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String colorTemp = snapshot.getValue(String.class);
                if (colorTemp != null) {
                    List<String> selectedColors = Arrays.asList(colorTemp.split(","));
                    setSelectedColors(selectedColors, red, orange, yellow, green, light_blue, dark_blue, brown, pink, purple, grey, black, white);
                } else {
                    Log.d("EditItemActivity", "No color data available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EditItemActivity", "Failed to read color data.", error.toException());
            }
        });

        //set the season to given season
        DatabaseReference seasonRef = closetRef.child("season");
        seasonRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String seasonTemp = snapshot.getValue(String.class);
                if (seasonTemp != null) {
                    List<String> selectedSeasons = Arrays.asList(seasonTemp.split(","));
                    setSelectedSeasons(selectedSeasons, fallCheck, winterCheck, springCheck, summerCheck);
                } else {
                    Log.d("EditItemActivity", "No season data available.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("EditItemActivity", "Failed to read season data.", error.toException());
            }
        });

        clothesCategory = findViewById(R.id.editspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clothesCategory.setAdapter(adapter);

        DatabaseReference categoryRef = closetRef.child("category");
        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String categoryTemp = snapshot.getValue(String.class);
                int position = adapter.getPosition(categoryTemp);
                clothesCategory.setSelection(position);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        clothesPic.setOnClickListener(v -> choosePicture());

        clothesCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditItemActivity.this.position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        clothesName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clothesNameNew = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer temp = Integer.parseInt(timesWorn.getText().toString());
                temp++;
                timesWorn.setText(temp.toString());
            }
        });

        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer temp = Integer.parseInt(timesWorn.getText().toString());
                temp--;
                if(temp < 0){
                    Toast.makeText(EditItemActivity.this, "Number of times worn impossible", Toast.LENGTH_SHORT).show();

                } else {
                    timesWorn.setText(temp.toString());
                }

            }
        });

        addItemButton.setOnClickListener(view -> {
            String itemNameNew = clothesName.getText().toString().trim();
            if (itemNameNew.isEmpty()) {
                Toast.makeText(this, "Please enter an item name", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> selectedColors = getSelectedColors(red, orange, yellow, green, light_blue, dark_blue, brown, pink, purple, grey, black, white);
            if (selectedColors.isEmpty()) {
                Toast.makeText(this, "Please select at least one color", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedColorString = TextUtils.join(",", selectedColors);

            List<String> selectedSeasons = getSelectedSeasons(fallCheck, winterCheck, springCheck, summerCheck);
            if (selectedSeasons.isEmpty()) {
                Toast.makeText(this, "Please select a season", Toast.LENGTH_SHORT).show();
                return;
            }
            String selectedSeasonString = TextUtils.join(",", selectedSeasons);

            Integer numUses = Integer.parseInt(timesWorn.getText().toString());

            closetRef.child("name").setValue(itemNameNew);
            closetRef.child("category").setValue(categories[position]);
            closetRef.child("color").setValue(selectedColorString);
            closetRef.child("season").setValue(selectedSeasonString);
            closetRef.child("laundry").setValue(laundrySwitch.isChecked());
            closetRef.child("uses").setValue(numUses);


            if(!(itemNameNew.equals(itemName))) {
                DatabaseReference newKeyRef = usersRef.child("users").child(userId).child("closet").child(itemNameNew);
                newKeyRef.setValue(itemNameNew);
                closetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Object data = dataSnapshot.getValue();
                            newKeyRef.setValue(data, (databaseError, databaseReference) -> {
                                if (databaseError == null) {
                                    closetRef.removeValue();
                                } else {
                                    System.err.println("Error writing to new key: " + databaseError.getMessage());
                                }
                            });
                        } else {
                            System.err.println("Old key does not exist.");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.err.println("Error reading old key: " + databaseError.getMessage());
                    }
                });
            }

            Toast.makeText(this, "Item updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });

        //deletes item
        deleteItemButton.setOnClickListener(view -> {
            closetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        closetRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    runOnUiThread(() -> {
                                        Toast.makeText(EditItemActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                                        clothesPic.setImageDrawable(null);
                                        clothesName.setText("");
                                        laundrySwitch.setChecked(false);
                                        CheckBox[] checkBoxes = {red, orange, yellow, green, light_blue, dark_blue, brown, pink, purple, grey, black, white};
                                        for (CheckBox checkBox : checkBoxes) {
                                            checkBox.setChecked(false);
                                        }
                                        finish();
                                    });
                                })
                                .addOnFailureListener(e -> {
                                    // Run UI update on main thread
                                    runOnUiThread(() -> {
                                        Toast.makeText(EditItemActivity.this, "Failed to delete item: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(EditItemActivity.this, "Item not found", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Run UI update on main thread
                    runOnUiThread(() -> {
                        Toast.makeText(EditItemActivity.this, "Error checking item: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            clothesPic.setImageURI(imageUri);
            uploadPicture(null, null);
        }
    }

    private void uploadPicture(final String clothingName, final DatabaseReference closetRef) {
        if (imageUri == null) {
            Toast.makeText(this, "No image selected. Please add a picture.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                if (closetRef != null) {
                                    closetRef.child("imageUrl").setValue(uri.toString());
                                }
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload the image.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setSelectedColors(List<String> selectedColors, CheckBox red, CheckBox orange, CheckBox yellow, CheckBox green, CheckBox light_blue,
                                   CheckBox dark_blue, CheckBox brown, CheckBox pink, CheckBox purple, CheckBox grey,
                                   CheckBox black, CheckBox white) {
        for (String color : selectedColors) {
            switch (color) {
                case "Red":
                    red.setChecked(true);
                    break;
                case "Orange":
                    orange.setChecked(true);
                    break;
                case "Yellow":
                    yellow.setChecked(true);
                    break;
                case "Green":
                    green.setChecked(true);
                    break;
                case "Light Blue":
                    light_blue.setChecked(true);
                    break;
                case "Dark Blue":
                    dark_blue.setChecked(true);
                    break;
                case "Brown":
                    brown.setChecked(true);
                    break;
                case "Pink":
                    pink.setChecked(true);
                    break;
                case "Purple":
                    purple.setChecked(true);
                    break;
                case "Grey":
                    grey.setChecked(true);
                    break;
                case "Black":
                    black.setChecked(true);
                    break;
                case "White":
                    white.setChecked(true);
                    break;
            }
        }
    }

    private void setSelectedSeasons(List<String> selectedSeasons, CheckBox fallCheck, CheckBox winterCheck, CheckBox springCheck, CheckBox summerCheck) {
        for (String season : selectedSeasons) {
            switch (season) {
                case "Fall":
                    fallCheck.setChecked(true);
                    break;
                case "Winter":
                    winterCheck.setChecked(true);
                    break;
                case "Spring":
                    springCheck.setChecked(true);
                    break;
                case "Summer":
                    summerCheck.setChecked(true);
                    break;
            }
        }
    }

    private List<String> getSelectedColors(CheckBox red, CheckBox orange, CheckBox yellow, CheckBox green, CheckBox light_blue,
                                           CheckBox dark_blue, CheckBox brown, CheckBox pink, CheckBox purple, CheckBox grey,
                                           CheckBox black, CheckBox white) {
        List<String> selectedColors = new ArrayList<>();
        if (red.isChecked()) {
            selectedColors.add("Red");
        }
        if (orange.isChecked()) {
            selectedColors.add("Orange");
        }
        if (yellow.isChecked()) {
            selectedColors.add("Yellow");
        }
        if (green.isChecked()) {
            selectedColors.add("Green");
        }
        if (light_blue.isChecked()) {
            selectedColors.add("Light Blue");
        }
        if (dark_blue.isChecked()) {
            selectedColors.add("Dark Blue");
        }
        if (brown.isChecked()) {
            selectedColors.add("Brown");
        }
        if (pink.isChecked()) {
            selectedColors.add("Pink");
        }
        if (purple.isChecked()) {
            selectedColors.add("Purple");
        }
        if (grey.isChecked()) {
            selectedColors.add("Grey");
        }
        if (black.isChecked()) {
            selectedColors.add("Black");
        }
        if (white.isChecked()) {
            selectedColors.add("White");
        }
        return selectedColors;
    }

    private List<String> getSelectedSeasons(CheckBox fallCheck, CheckBox winterCheck, CheckBox springCheck, CheckBox summerCheck) {
        List<String> selectedSeasons = new ArrayList<>();
        if (fallCheck.isChecked()) {
            selectedSeasons.add("Fall");
        }
        if (winterCheck.isChecked()) {
            selectedSeasons.add("Winter");
        }
        if (springCheck.isChecked()) {
            selectedSeasons.add("Spring");
        }
        if (summerCheck.isChecked()) {
            selectedSeasons.add("Summer");
        }
        return selectedSeasons;
    }

}
