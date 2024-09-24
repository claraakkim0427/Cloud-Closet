package com.example.a5_sample.ui.closet;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

public class ClothingItem implements Parcelable {
    private String category;
    private String color;
    private String imageUrl;
    private boolean laundry;
    private String name;
    private String season;

    private int uses;

    public ClothingItem() { // default constructor for Firebase
    }

    public ClothingItem(String category, String color, String imageUrl, boolean laundry, String name, String season, int uses) {
        this.category = category;
        this.color = color;
        this.imageUrl = imageUrl;
        this.laundry = laundry;
        this.name = name;
        this.season = season;
        this.uses = uses;
    }

    protected ClothingItem(Parcel in) {
        name = in.readString();
        color = in.readString();
        uses = in.readInt();
        category = in.readString();
        imageUrl = in.readString();
        season = in.readString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            laundry = in.readBoolean();
        } else {
            laundry = false;
        }



    }


    public static final Creator<ClothingItem> CREATOR = new Creator<ClothingItem>() {
        @Override
        public ClothingItem createFromParcel(Parcel in) {
            return new ClothingItem(in);
        }

        @Override
        public ClothingItem[] newArray(int size) {
            return new ClothingItem[size];
        }
    };

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        parcel.writeString(color);
        parcel.writeInt(uses);
        parcel.writeString(category);
        parcel.writeString(imageUrl);
        parcel.writeString(season);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            parcel.writeBoolean(laundry);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }
    public int getUses() { return uses; }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isLaundry() {
        return laundry;
    }

    /*public int getUses() {
        return uses;
    }*/

    public void setLaundry(boolean laundry) {
        this.laundry = laundry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }
}
