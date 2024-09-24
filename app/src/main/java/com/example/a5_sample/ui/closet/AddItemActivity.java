package com.example.a5_sample.ui.closet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class AddItemActivity extends AppCompatActivity {

    private Button addItemButton;
    private ImageButton backButton;
    private Spinner clothesCategory;
    private ImageView clothesPic;
    private Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private int position;
    private String newRef;


    private CheckBox winterCheck, fallCheck, springCheck, summerCheck;
    private String[] categories = {
            "Tank Top", "T-Shirt", "Long Sleeves/Blouse", "Sweatshirt/Sweater",
            "Jacket", "Dress", "Pants", "Leggings", "Skirt", "Shorts", "Shoes", "Bag", "Accessories"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        EditText clothesName = findViewById(R.id.addeditTextName);
        if (clothesName == null) {
            Log.e("AddItemActivity", "clothesName is null. Check the ID or layout setup.");
            return;
        }

        clothesPic = findViewById(R.id.imageViewAdd);
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        clothesPic = findViewById(R.id.imageViewAdd);
        clothesPic.setImageResource(R.drawable.camera_icon);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        winterCheck = findViewById(R.id.addwinter_option);
        fallCheck = findViewById(R.id.addfall_option);
        springCheck = findViewById(R.id.addspring_option);
        summerCheck = findViewById(R.id.addsummer_option);

        clothesCategory = findViewById(R.id.addspinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clothesCategory.setAdapter(adapter);

        final String[] name = new String[1];

        CheckBox red = findViewById(R.id.red_add);
        CheckBox orange = findViewById(R.id.orange_add);
        CheckBox yellow = findViewById(R.id.yellow_add);
        CheckBox green = findViewById(R.id.green_add);
        CheckBox light_blue = findViewById(R.id.light_blue_add);
        CheckBox dark_blue = findViewById(R.id.dark_blue_add);
        CheckBox brown = findViewById(R.id.brown_add);
        CheckBox pink = findViewById(R.id.pink_add);
        CheckBox purple = findViewById(R.id.purple_add);
        CheckBox grey = findViewById(R.id.grey_add);
        CheckBox black = findViewById(R.id.black_add);
        CheckBox white = findViewById(R.id.white_add);

        clothesName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                name[0] = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        clothesCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                AddItemActivity.this.position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addItemButton = findViewById(R.id.add_item_add_button);
        clothesPic.setOnClickListener(v -> choosePicture());

        addItemButton.setOnClickListener(v -> {
            if (name[0] == null || name[0].isEmpty()) {
                Toast.makeText(this, "Please enter a name for the clothing item.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (imageUri == null) {
                Toast.makeText(this, "Please select an image for the clothing item.", Toast.LENGTH_SHORT).show();
                return;
            }
            String clothingName = name[0];

            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference closetRef = usersRef.child("users").child(userId).child("closet").child(clothingName);
            closetRef.child("name").setValue(clothingName);

            String clothesColor = null;

            //please don't change this, the getCheckedRadioButton doesn't work
            if(red.isChecked()) {
                clothesColor = "Red";
            } else if(orange.isChecked()){
                clothesColor = "Orange";
            } else if(yellow.isChecked()){
                clothesColor = "Yellow";
            } else if(green.isChecked()){
                clothesColor = "Green";
            }else if(light_blue.isChecked()){
                clothesColor = "Light Blue";
            }else if(dark_blue.isChecked()){
                clothesColor = "Dark Blue";
            } else if(purple.isChecked()){
                clothesColor = "Purple";
            } else if(pink.isChecked()){
                clothesColor = "Pink";
            } else if(brown.isChecked()){
                clothesColor = "Brown";
            } else if(grey.isChecked()){
                clothesColor = "Grey";
            } else if(black.isChecked()){
                clothesColor = "Black";
            } else if(white.isChecked()){
                clothesColor = "White";
            }

            /*
            if (radioGroup.get() != null) {
                int checkedId = radioGroup.get().getCheckedRadioButtonId();
                if (checkedId != -1) {
                    RadioButton selectedRadio = findViewById(checkedId);
                    clothesColor = selectedRadio.getText().toString();
                }
            }*/

            if (clothesColor == null) {
                Toast.makeText(this, "Please select a color.", Toast.LENGTH_SHORT).show();
                return;
            }

            closetRef.child("color").setValue(clothesColor);
            Log.d("issue", clothesColor);

            closetRef.child("category").setValue(categories[position]);
            Log.d("issue", categories[position]);

            String season = null;
            if (winterCheck.isChecked()) {
                season = "Winter";
            } else if (fallCheck.isChecked()) {
                season = "Fall";
            } else if (springCheck.isChecked()) {
                season = "Spring";
            } else if (summerCheck.isChecked()) {
                season = "Summer";
            }

            if (season != null) {
                closetRef.child("season").setValue(season);
                Log.d("issue", season);
            } else {
                Toast.makeText(this, "Please select a season.", Toast.LENGTH_SHORT).show();
                return;
            }

            closetRef.child("laundry").setValue(false);
            closetRef.child("inLaundry").setValue(false);
            closetRef.child("uses").setValue(0);


            //closetRef.child("imageUrl").setValue

            //closetRef.child("uses").setValue(0);

            Log.d("clothes234", closetRef.toString());
            uploadPicture(clothingName, closetRef);
            closetRef.child("imageUrl").setValue(newRef);

            //uploadPicture(clothingName, closetRef);

            Intent intent = new Intent(AddItemActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
        backButton = findViewById(R.id.additemBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
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
            Log.d("clothes234", "imageUri is null?");
            Toast.makeText(this, "No image selected. Please add a picture.", Toast.LENGTH_SHORT).show();
            return;
        }

        final String randomKey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomKey);

        //DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference();
        //closetRef = usersRef.child("users").child(userId).child("closet").child(clothingName);


        riversRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //if (closetRef != null) {
                                    //closetRef.child("imageUrl").setValue(uri.toString());
                                    Log.d("issuePic", uri.toString());
                                    Log.d("issuePic2", riversRef.getDownloadUrl().toString());
                                    //Log.d("clothes234", uri.toString());
                                    //closetRef.child("imageUrl").setValue(uri.toString());
                                    //closetRef.child("imageUrl").setValue(uri.toString());
                                    newRef = uri.toString();
                                    //newRef = riversRef.getDownloadUrl().toString();
                                //}
                                //imageUri = uri;
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
        //newRef = riversRef.getDownloadUrl().toString();
    }
}
