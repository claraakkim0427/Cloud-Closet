package com.example.a5_sample.ui.plan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a5_sample.R;
import com.example.a5_sample.ui.closet.ClothingItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OutfitItemAdapter extends RecyclerView.Adapter<OutfitItemAdapter.ViewHolder> {
    private static final String TAG = "OutfitItemAdapter";

    private List<OutfitItem> outfitItems;
    private Context context;

    public OutfitItemAdapter(List<OutfitItem> outfitItems, Context context) {
        this.outfitItems = outfitItems;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return outfitItems.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_outfit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        OutfitItem outfitItem = outfitItems.get(position);

        Log.d(TAG, "Binding ViewHolder at position: " + position);

        holder.eventName.setText(outfitItem.getEventName());
        holder.location.setText(outfitItem.getLocation());
        holder.date.setText(outfitItem.getDate());

        holder.image1.setVisibility(View.GONE);
        holder.image2.setVisibility(View.GONE);
        holder.image3.setVisibility(View.GONE);

        List<ClothingItem> clothingItems = outfitItem.getClothingItems();

        if (clothingItems != null) {
            Log.d(TAG, "Number of clothing items in outfit: " + clothingItems.size());
            if (clothingItems.size() > 0) {
                String url = clothingItems.get(0).getImageUrl();
                Log.d(TAG, "Loading image 1 with URL: " + url);
                Glide.with(context)
                        .load(url)
                        .into(holder.image1);
                holder.image1.setVisibility(View.VISIBLE);
            }
            if (clothingItems.size() > 1) {
                String url = clothingItems.get(1).getImageUrl();
                Log.d(TAG, "Loading image 2 with URL: " + url);
                Glide.with(context)
                        .load(url)
                        .into(holder.image2);
                holder.image2.setVisibility(View.VISIBLE);
            }
            if (clothingItems.size() > 2) {
                String url = clothingItems.get(2).getImageUrl();
                Log.d(TAG, "Loading image 3 with URL: " + url);
                Glide.with(context)
                        .load(url)
                        .into(holder.image3);
                holder.image3.setVisibility(View.VISIBLE);
            }
        }

        holder.overflowMenu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.overflowMenu);
            popupMenu.inflate(R.menu.outfit_menu);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_edit:
                        // TODO
                        Intent intent = new Intent(context, OutfitItem.EditOutfitActivity.class);
                        intent.putExtra("id", outfitItem.getId());
                        context.startActivity(intent);
                        return true;
                    case R.id.menu_delete:
                        deleteOutfitPlan(outfitItem.getId(), position);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
        });
    }

    private void deleteOutfitPlan(String outfitPlanId, int position) {
        if (outfitPlanId == null) {
            Toast.makeText(context, "Error: Outfit plan ID is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.storage), Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("UserId", null);

        if (userId == null) {
            Toast.makeText(context, "Error: User ID is null.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        userRef.child("outfit_plans").child(outfitPlanId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                outfitItems.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Outfit plan deleted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Failed to delete outfit plan.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setOutfitItems(List<OutfitItem> newOutfitItems) {
        this.outfitItems = newOutfitItems;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventName, location, date;
        ImageView image1, image2, image3, overflowMenu;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.outfit_event_name);
            location = itemView.findViewById(R.id.outfit_location);
            date = itemView.findViewById(R.id.outfit_date);
            image1 = itemView.findViewById(R.id.outfit_image_1);
            image2 = itemView.findViewById(R.id.outfit_image_2);
            image3 = itemView.findViewById(R.id.outfit_image_3);
            overflowMenu = itemView.findViewById(R.id.outfit_overflow_menu);
        }
    }
}
