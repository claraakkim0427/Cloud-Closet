package com.example.a5_sample.ui.closet;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.a5_sample.MainActivity;
import com.example.a5_sample.R;
import com.example.a5_sample.ui.plan.AddOutfitActivity;
import com.example.a5_sample.ui.plan.choose.ChooseActivity;

import java.util.ArrayList;
import java.util.List;

public class ClothingItemAdapter extends RecyclerView.Adapter<ClothingItemAdapter.ViewHolder> {

    private List<ClothingItem> items;

    private String id;

    public ClothingItemAdapter() {

        this.items = new ArrayList<>();
        this.id = null;
    }

    public ClothingItemAdapter(String id) {

        this.items = new ArrayList<>();
        this.id = id;
        Log.d("AdapterConstructed", "here");
        Log.d("AdapterConstructed", id);
    }

    public void addItem(ClothingItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    public void addId(String item) {
        id = item;
        notifyDataSetChanged();
    }

    public String getId() {
        return id;
    }

    public List<ClothingItem> getItems() {
        return items;
    }

    public void setItems(List<ClothingItem> itemList) {
        items.clear();
        items.addAll(itemList);
        notifyDataSetChanged();
    }

    public void clearItems() {
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clothing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClothingItem item = items.get(position);
        Glide.with(holder.imageView.getContext()).load(item.getImageUrl()).into(holder.imageView);

        holder.imageView.setOnClickListener(v -> {
            Log.d("contextIssue", v.getContext().toString());
            if(v.getContext().toString().contains("MainActivity")) {
                Intent intent = new Intent(v.getContext(), EditItemActivity.class);
                intent.putExtra("clothing_item_name", item.getName());
                v.getContext().startActivity(intent);
            } else {
                Intent intent = new Intent(v.getContext(), ChooseActivity.class);
                intent.putExtra("clothing_item_name", item.getName());
                intent.putExtra("id", this.getId());
                Log.d("hereAdapter", this.id);
                //Log.d("hereAdapter", this.getId());
                v.getContext().startActivity(intent);
            }

        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageView;

        ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView_cloth);
        }
    }
}
