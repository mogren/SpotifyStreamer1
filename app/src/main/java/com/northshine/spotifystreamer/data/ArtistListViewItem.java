package com.northshine.spotifystreamer.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cm, 16/06 2015
 */
public class ArtistListViewItem implements Parcelable {

    private String name;
    private String id;
    private String imageUrl;

    public ArtistListViewItem(String name, String id, String imageUrl) {
        this.name = name;
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    protected ArtistListViewItem(Parcel in) {
        name = in.readString();
        id = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(imageUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ArtistListViewItem> CREATOR = new Parcelable.Creator<ArtistListViewItem>() {
        @Override
        public ArtistListViewItem createFromParcel(Parcel in) {
            return new ArtistListViewItem(in);
        }

        @Override
        public ArtistListViewItem[] newArray(int size) {
            return new ArtistListViewItem[size];
        }
    };
}