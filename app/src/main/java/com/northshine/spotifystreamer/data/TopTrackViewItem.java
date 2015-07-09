package com.northshine.spotifystreamer.data;

import android.graphics.Bitmap;

/**
 * Created by cm, 16/06 2015
 */
public class TopTrackViewItem {

    private String id;
    private String artist;
    private String title;
    private Bitmap image; // drawable reference id

    public TopTrackViewItem(String id, String artist, String title, Bitmap image) {
        this.id = id;
        this.artist = artist;
        this.title = title;
        this.image = image;
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

    public Bitmap getImage() {
        return image;
    }
}
