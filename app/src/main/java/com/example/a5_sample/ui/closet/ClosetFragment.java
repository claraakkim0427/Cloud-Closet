package com.example.a5_sample.ui.closet;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.a5_sample.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;


public class ClosetFragment extends Fragment {


    private RecyclerView recyclerView;
    private ClothingItemAdapter adapter;
    private ImageButton filtericonButton;
    private ImageButton sorticonButton;
    private FloatingActionButton addItemButton;
    private ActivityResultLauncher<Intent> filterLauncher;
    private ActivityResultLauncher<Intent> sortLauncher;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        filterLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Log.d("closetIssue", "here1");
                        Intent data = result.getData();
                        if (data != null) {
                            Log.d("closetIssue", "here2");
                            ArrayList<Parcelable> parcelableList = data.getParcelableArrayListExtra("filteredItems");
                            Log.d("closetIssue", "here2.5");
                            ArrayList<ClothingItem> filteredItems = new ArrayList<>();
                            if (parcelableList != null && !parcelableList.isEmpty()) {
                                Log.d("closetIssue", "here3");
                                for (Parcelable parcelable : parcelableList) {
                                    if (parcelable instanceof ClothingItem) {
                                        Log.d("closetIssue", "here4");
                                        filteredItems.add((ClothingItem) parcelable);
                                        Log.d("closetIssue", "here5");
                                    } else {
                                        // Log the unexpected data type
                                        Log.d("CastingError", "Unexpected type: " + parcelable.getClass().getName());
                                    }
                                }
                            } else {Log.d("closetIssue", "hello");}
                            Log.d("closetIssue", "here6");
                            Log.d("issue79", filteredItems.get(0).getImageUrl());
                            Log.d("issue79", filteredItems.toString());
                            adapter.setItems(filteredItems);
                        }
                    }
                }
        );
        sortLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            Log.d("closetIssue", "here2");
                            ArrayList<Parcelable> parcelableList = data.getParcelableArrayListExtra("sorted_clothes");
                            Log.d("closetIssue", "here2.5");
                            ArrayList<ClothingItem> sortedClothes = new ArrayList<>();
                            if (parcelableList != null && !parcelableList.isEmpty()) {
                                Log.d("closetIssue", "here3");
                                for (Parcelable parcelable : parcelableList) {
                                    if (parcelable instanceof ClothingItem) {
                                        Log.d("closetIssue", "here4");
                                        sortedClothes.add((ClothingItem) parcelable);
                                        Log.d("closetIssue", "here5");
                                    } else {
                                        // Log the unexpected data type
                                        Log.d("CastingError", "Unexpected type: " + parcelable.getClass().getName());
                                    }
                                }
                            } else {
                                Log.d("closetIssue", "hello");
                            }
                            Log.d("closetIssue", "here6");
                            Log.d("issue79", sortedClothes.get(0).getImageUrl());
                            Log.d("issue79", sortedClothes.toString());
                            adapter.setItems(sortedClothes);
                        }
                    }
                }
        );
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_closet, container, false);


        recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new ClothingItemAdapter();
        recyclerView.setAdapter(adapter);


        filtericonButton = root.findViewById(R.id.filterButton);
        sorticonButton = root.findViewById(R.id.sortButton);
        addItemButton = root.findViewById(R.id.additemButton);


        filtericonButton.setOnClickListener(v -> {
            showFilter();
        });


        sorticonButton.setOnClickListener(v -> {
            //Intent intent = new Intent(getActivity(), SortActivity.class);
            //startActivity(intent);
            showSort();
        });


        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddItemActivity.class);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Log.e("ClosetFragment", "No activity found to handle intent");
            }
        });


        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);


        if (userId != null) {
            loadFirebaseData(userId);
        } else {
            Log.e("ClosetFragment", "No user ID found in SharedPreferences.");
        }


        return root;
    }


    void showFilter() {
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        filterLauncher.launch(intent);
    }

    void showSort() {
        Intent intent = new Intent(getActivity(), SortActivity.class);
        sortLauncher.launch(intent);
    }


    private void loadFirebaseData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);


        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adapter.clearItems();
                DataSnapshot closetSnapshot = dataSnapshot.child("closet");
                for (DataSnapshot itemSnapshot : closetSnapshot.getChildren()) {
                    ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                    if (item != null) {
                        adapter.addItem(item);
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
