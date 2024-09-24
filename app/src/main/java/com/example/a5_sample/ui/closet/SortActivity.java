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
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.a5_sample.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class SortActivity extends AppCompatActivity {

    private Button sortIcon;
    private Button doneExit;

    private RadioButton az;
    private RadioButton recent;

    private RadioButton most;

    private RadioButton least;

    FirebaseDatabase database;
    DatabaseReference closetRef;
    ArrayList<String> clothingItems;

    ArrayList<Integer> clothingUses;
    private ArrayList<ClothingItem> originalItemList;
    private ArrayList<ClothingItem> sortedClothes;

    public interface DataFetchCallback {
        void onDataFetched(ArrayList<ClothingItem> sortedItems);
    }


    public CompletableFuture<ArrayList<ClothingItem>> fetchAndSortByName() {
        CompletableFuture<ArrayList<ClothingItem>> future = new CompletableFuture<>();

        closetRef.orderByChild("name").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<ClothingItem> sortedClothes = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                    if (item != null) {
                        sortedClothes.add(item);
                    }
                }
                // Sort the list by name
                Collections.sort(sortedClothes, (item1, item2) -> item1.getName().compareToIgnoreCase(item2.getName()));

                // Complete the CompletableFuture with the sorted list
                future.complete(sortedClothes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error and complete with an exception
                Log.e("ClosetService", "Error fetching data: " + error.getMessage());
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }


    //public void fetchAndSortByName(DataFetchCallback callback) {

    public void fetchAndSortByName(ArrayList<ClothingItem> passed) {
        //sortedClothes = new ArrayList<ClothingItem>();
        sortedClothes = passed;
        clothingItems = new ArrayList<String>();
        Query query = closetRef.orderByChild("name");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                sortedClothes.clear();
                ArrayList<ClothingItem> sortedClothes = new ArrayList<>();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    //String name = itemSnapshot.child("name").getValue(String.class);
                    ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                    sortedClothes.add(item);
                    //clothingItems.add(name);
                }
                //should do the trick
               // Collections.sort(clothingItems);
                Collections.sort(sortedClothes, (item1, item2) -> item1.getName().compareToIgnoreCase(item2.getName()));
                Log.d("issueSortClothing", clothingItems.toString());
                Log.d("issueSortOriginal", originalItemList.toString());
                Log.d("issueSortOriginal", passed.toString());
                Log.d("issueSortOriginal", sortedClothes.toString());


                Intent intent = new Intent();
                /*if(sortedClothes.isEmpty()) {
                    Log.d("issueSort", "list is null");
                }*/
                Log.d("issueSort", "issue");
                intent.putParcelableArrayListExtra("sorted_clothes", sortedClothes);
                setResult(Activity.RESULT_OK, intent);
                finish();
/*
                for (String itemName : clothingItems) {
                    for (ClothingItem item : originalItemList) {
                        if (item.getName().equals(itemName)) {
                            sortedClothes.add(item);
                            //break;
                        }
                    }
                }
                //callback.onDataFetched(sortedClothes);*/
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SortActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                sortedClothes = originalItemList;
            }
        });
        /*
        if(!(sortedClothes.isEmpty())) {
            Log.d("issueSort1", sortedClothes.toString());
        } else {
            Log.d("issueSort1", "no sort");
        }
        return sortedClothes;*/
    }

    public void fetchAndSortByMost(ArrayList<ClothingItem> passed) {
        sortedClothes = passed;
        clothingItems = new ArrayList<String>();
        Query query = closetRef.orderByChild("uses");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clothingItems.clear();
                //clothingUses.clear();
                sortedClothes.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    //String name = itemSnapshot.child("name").getValue(String.class);
                    //int uses = itemSnapshot.child("uses").getValue(Integer.class);
                    //clothingItems.add(name);
                    //clothingUses.add(uses);
                    ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                    sortedClothes.add(item);
                }
                // handle
                /*
                for (int i = 0; i < clothingUses.size() - 1; i++) {
                    for (int j = 0; j < clothingUses.size() - i - 1; j++) {
                        if (clothingUses.get(j) < clothingUses.get(j + 1)) {
                            // Swap usage counts
                            int tempUses = clothingUses.get(j);
                            clothingUses.set(j, clothingUses.get(j + 1));
                            clothingUses.set(j + 1, tempUses);
                            // Swap corresponding clothing names
                            String tempName = clothingItems.get(j);
                            clothingItems.set(j, clothingItems.get(j + 1));
                            clothingItems.set(j + 1, tempName);
                        }
                    }
                }
                for (String itemName : clothingItems) {
                    for (ClothingItem item : originalItemList) {
                        if (item.getName().equals(itemName)) {
                            sortedClothes.add(item);
                            break;
                        }
                    }
                } */
                Collections.reverse(sortedClothes);
                Intent intent = new Intent();
                /*if(sortedClothes.isEmpty()) {
                    Log.d("issueSort", "list is null");
                }*/
                Log.d("issueSort", "issue");
                intent.putParcelableArrayListExtra("sorted_clothes", sortedClothes);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SortActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                sortedClothes = originalItemList;
            }
        });
        //return sortedClothes;

    }

    /*
    public ArrayList<ClothingItem> fetchAndSortByLeast(ArrayList<ClothingItem> passed) {
        //TODO:take a look at this line
        Query query = closetRef.orderByChild("uses");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            //@Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clothingItems.clear();
                clothingUses.clear();
                sortedClothes.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String name = itemSnapshot.child("name").getValue(String.class);
                    int uses = itemSnapshot.child("uses").getValue(Integer.class);
                    clothingItems.add(name);
                    clothingUses.add(uses);
                }
                // handle
                for (int i = 0; i < clothingUses.size() - 1; i++) {
                    for (int j = 0; j < clothingUses.size() - i - 1; j++) {
                        if (clothingUses.get(j) > clothingUses.get(j + 1)) {
                            // Swap usage counts
                            int tempUses = clothingUses.get(j);
                            clothingUses.set(j, clothingUses.get(j + 1));
                            clothingUses.set(j + 1, tempUses);
                            // Swap corresponding clothing names
                            String tempName = clothingItems.get(j);
                            clothingItems.set(j, clothingItems.get(j + 1));
                            clothingItems.set(j + 1, tempName);
                        }
                    }
                }
                for (String itemName : clothingItems) {
                    for (ClothingItem item : originalItemList) {
                        if (item.getName().equals(itemName)) {
                            sortedClothes.add(item);
                            break;
                        }
                    }
                }
                 @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SortActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                sortedClothes = originalItemList;
            }
        });
        return sortedClothes;
    }
            }*/

            public void fetchAndSortByLeast(ArrayList<ClothingItem> passed) {
                sortedClothes = passed;
                clothingItems = new ArrayList<String>();
                Query query = closetRef.orderByChild("uses");
                query.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //clothingItems.clear();
                        //clothingUses.clear();
                        sortedClothes.clear();
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            //String name = itemSnapshot.child("name").getValue(String.class);
                            //int uses = itemSnapshot.child("uses").getValue(Integer.class);
                            //clothingItems.add(name);
                            //clothingUses.add(uses);
                            ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                            sortedClothes.add(item);
                        }
                        // handle
                /*
                for (int i = 0; i < clothingUses.size() - 1; i++) {
                    for (int j = 0; j < clothingUses.size() - i - 1; j++) {
                        if (clothingUses.get(j) < clothingUses.get(j + 1)) {
                            // Swap usage counts
                            int tempUses = clothingUses.get(j);
                            clothingUses.set(j, clothingUses.get(j + 1));
                            clothingUses.set(j + 1, tempUses);
                            // Swap corresponding clothing names
                            String tempName = clothingItems.get(j);
                            clothingItems.set(j, clothingItems.get(j + 1));
                            clothingItems.set(j + 1, tempName);
                        }
                    }
                }
                for (String itemName : clothingItems) {
                    for (ClothingItem item : originalItemList) {
                        if (item.getName().equals(itemName)) {
                            sortedClothes.add(item);
                            break;
                        }
                    }
                } */

                        Intent intent = new Intent();
                /*if(sortedClothes.isEmpty()) {
                    Log.d("issueSort", "list is null");
                }*/
                        Log.d("issueSort", "issue");
                        intent.putParcelableArrayListExtra("sorted_clothes", sortedClothes);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SortActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                        sortedClothes = originalItemList;
                    }
                });
                //return sortedClothes;


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
                        //Log.d("issue5", item.getImageUrl());
                    }
                }
                Log.d("issue5", originalItemList.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SortActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        setContentView(R.layout.popup_sort);

        sortIcon = findViewById(R.id.applySortButton);
        doneExit = findViewById(R.id.closeSortMenu);
        az = findViewById(R.id.sortOption1);
        most = findViewById(R.id.sortOption3);
        least = findViewById(R.id.sortOption4);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.gravity = Gravity.CENTER;
        params.dimAmount = 0.6f;
        getWindow().setAttributes(params);

        originalItemList = new ArrayList<ClothingItem>();
        // Load clothing items from Firebase
        loadFirebaseData();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //originalItemList = new ArrayList<ClothingItem>();
        database = FirebaseDatabase.getInstance();

        //temporary until figure out how
        closetRef = database.getReference().child("users").child(userId).child("closet");
        clothingItems = new ArrayList<>();
        clothingUses = new ArrayList<>();


        doneExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        sortIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ClothingItem> sortedClothes = new ArrayList<>();

                if(az.isChecked()) {
                    //closetRef.orderByChild("name");

                    //sortedClothes = fetchAndSortByName(originalItemList);
                    fetchAndSortByName(originalItemList);

                    Log.d("issue6", "gotHere");
                } else if (most.isChecked()) {
                    fetchAndSortByMost(originalItemList);
                    closetRef.orderByChild("uses");
                } else if(least.isChecked()) {
                    //sortedClothes = fetchAndSortByLeast(originalItemList);
                    fetchAndSortByLeast(originalItemList);
                } else{
                    sortedClothes = originalItemList;
                    Log.d("issue6", "gotHere1");
                //Toast.makeText(, "no sort option selected", Toast.LENGTH_SHORT);
                }
                /*Intent intent = new Intent();
                if(sortedClothes.isEmpty()) {
                    Log.d("issueSort", "list is null");
                }
                Log.d("issueSort", "issue");
                intent.putParcelableArrayListExtra("sorted_clothes", sortedClothes);
                setResult(Activity.RESULT_OK, intent);
                finish();*/
            }
        });
    }

}
