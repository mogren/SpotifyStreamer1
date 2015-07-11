package com.northshine.spotifystreamer.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cm, 16/06 2015
 */
public class TopTrackViewItem implements Parcelable {

    private String id;
    private String artist;
    private String title;
    private String imageUrl;

    public TopTrackViewItem(String id, String artist, String title, String imageUrl) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    protected TopTrackViewItem(Parcel in) {
        id = in.readString();
        artist = in.readString();
        title = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(imageUrl);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TopTrackViewItem> CREATOR = new Parcelable.Creator<TopTrackViewItem>() {
        @Override
        public TopTrackViewItem createFromParcel(Parcel in) {
            return new TopTrackViewItem(in);
        }

        @Override
        public TopTrackViewItem[] newArray(int size) {
            return new TopTrackViewItem[size];
        }
    };
}