package com.example.a5_sample.ui.plan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a5_sample.R;
import com.example.a5_sample.ui.closet.ClothingItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PlanFragment extends Fragment {
    private static final String TAG = "PlanFragment";
    private RecyclerView recyclerView;
    private OutfitItemAdapter outfitItemAdapter;
    private FloatingActionButton addItemButton;
    private Button btnSeeHistory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.fragment_plan, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOutfits);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        outfitItemAdapter = new OutfitItemAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(outfitItemAdapter);

        addItemButton = view.findViewById(R.id.addplanButton);
        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddOutfitActivity.class);
            startActivity(intent);
        });
        btnSeeHistory = view.findViewById(R.id.btnSeeHistory);
        btnSeeHistory.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_history);
        });

        loadOutfitItems();

        return view;
    }

    private void loadOutfitItems() {
        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        Log.d(TAG, "User ID: " + userId);

        if (userId != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

            userRef.child("outfit_plans").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Data loaded from Firebase");
                    List<OutfitItem> outfitItems = new ArrayList<>();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    dateFormat.setLenient(false);

                    // Get today's date
                    Date today = new Date();

                    for (DataSnapshot planSnapshot : dataSnapshot.getChildren()) {
                        OutfitItem outfitItem = planSnapshot.getValue(OutfitItem.class);
                        if (outfitItem == null) {
                            Log.w(TAG, "OutfitItem is null for snapshot: " + planSnapshot);
                            continue;
                        }
                        outfitItem.setId(planSnapshot.getKey());

                        try {
                            Date outfitDate = dateFormat.parse(outfitItem.getDate());
                            if (outfitDate.compareTo(today) >= 0) {
                                outfitItems.add(outfitItem);
                            } else {
                                Log.d(TAG, "Skipping outfit item with past date: " + outfitItem.getDate());
                            }
                        } catch (ParseException e) {
                            Log.e(TAG, "Date parsing error: " + e.getMessage());
                        }
                    }

                    Log.d(TAG, "Number of future or current outfit items loaded: " + outfitItems.size());

                    sortOutfitItemsByDate(outfitItems);
                    loadClothingItems(userId, outfitItems);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(TAG, "Error loading outfit plans: " + databaseError.getMessage());
                }
            });
        } else {
            Log.e(TAG, "User ID is null in SharedPreferences.");
        }
    }

    private void sortOutfitItemsByDate(List<OutfitItem> outfitItems) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);
        Collections.sort(outfitItems, (o1, o2) -> {
            try {
                Date date1 = dateFormat.parse(o1.getDate());
                Date date2 = dateFormat.parse(o2.getDate());
                return date1.compareTo(date2);
            } catch (ParseException e) {
                Log.e(TAG, "Date parsing error: " + e.getMessage());
                return 0;
            }
        });
    }

    private void loadClothingItems(String userId, List<OutfitItem> outfitItems) {
        if (outfitItems == null || outfitItems.isEmpty()) {
            Log.e("PlanFragment", "Outfit items list is null or empty.");
            return;
        }

        DatabaseReference closetRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("users")
                .child(userId)
                .child("closet");

        closetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot closetSnapshot) {
                List<ClothingItem> allClothingItems = new ArrayList<>();
                for (DataSnapshot itemSnapshot : closetSnapshot.getChildren()) {
                    ClothingItem clothingItem = itemSnapshot.getValue(ClothingItem.class);
                    allClothingItems.add(clothingItem);
                }

                Log.d("PlanFragment", "Number of clothing items loaded: " + allClothingItems.size());
                for (OutfitItem outfitItem : outfitItems) {
                    List<ClothingItem> relevantClothingItems = new ArrayList<>();
                    List<String> clothingItemIds = outfitItem.getClothingItemIds();

                    if (clothingItemIds == null) {
                        Log.e("PlanFragment", "Clothing item IDs list is null for outfit plan: " + outfitItem.getEventName());
                        continue;
                    }

                    Log.d("PlanFragment", "Outfit plan " + outfitItem.getEventName() + " has " + clothingItemIds.size() + " clothing IDs");

                    for (String clothingId : clothingItemIds) {
                        for (ClothingItem clothingItem : allClothingItems) {
                            if (clothingItem.getName().equals(clothingId)) {
                                relevantClothingItems.add(clothingItem);
                            }
                        }
                    }

                    outfitItem.setClothingItems(relevantClothingItems);
                }

                outfitItemAdapter.setOutfitItems(outfitItems);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("PlanFragment", "Error loading closet items: " + databaseError.getMessage());
            }
        });
    }

}
