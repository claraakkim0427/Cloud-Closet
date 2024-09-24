package com.example.a5_sample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.a5_sample.databinding.FragmentProfileBinding;
import com.example.a5_sample.ui.closet.ClothingItem;
import com.example.a5_sample.ui.user.FirstPageActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private Button saveButton;
    private EditText name;
    private EditText email;
    private DatabaseReference userRef;
    private Button logoutButton;

    private ImageView imageViewMostWorn;
    private ImageView imageViewLeastWorn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        saveButton = binding.profilesaveButton;
        name = binding.profileeditTextName;
        email = binding.profileeditTextEmail;
        imageViewMostWorn = binding.imageViewMostWorn;
        imageViewLeastWorn = binding.imageViewLeastWorn;

        Context context = requireContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        if (userId != null) {
            userRef = FirebaseDatabase.getInstance().getReference().child(userId);
            loadProfileData(userRef);

            saveButton.setOnClickListener(v -> {
                String newName = name.getText().toString();
                String newEmail = email.getText().toString();

                if (!newName.isEmpty() && !newEmail.isEmpty()) {
                    userRef.child("name").setValue(newName);
                    userRef.child("email").setValue(newEmail);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("UserName", newName);
                    editor.putString("UserEmail", newEmail);
                    editor.apply();

                    Toast.makeText(getActivity(), "Profile updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Name and email cannot be empty", Toast.LENGTH_SHORT).show();
                }
            });
            DatabaseReference closetRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("closet");
            closetRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    List<ClothingItem> clothingItems = new ArrayList<>();

                    for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                        ClothingItem item = itemSnapshot.getValue(ClothingItem.class);
                        if (item != null) {
                            clothingItems.add(item);
                        }
                    }

                    if (!clothingItems.isEmpty()) {
                        Collections.sort(clothingItems, Comparator.comparingInt(ClothingItem::getUses));
                        ClothingItem mostWornItem = clothingItems.get(clothingItems.size() - 1);
                        Picasso.get().load(mostWornItem.getImageUrl()).into(imageViewMostWorn);
                        ClothingItem leastWornItem = clothingItems.get(0);
                        Picasso.get().load(leastWornItem.getImageUrl()).into(imageViewLeastWorn);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "No user ID found. Please log in again.", Toast.LENGTH_SHORT).show();
        }
        logoutButton = binding.logoutButton;
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Logout Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), FirstPageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return root;
    }

    private void loadProfileData(DatabaseReference userRef) {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nameValue = dataSnapshot.child("name").getValue(String.class);
                    String emailValue = dataSnapshot.child("email").getValue(String.class);

                    if (nameValue != null) {
                        name.setText(nameValue);
                    }
                    if (emailValue != null) {
                        email.setText(emailValue);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
