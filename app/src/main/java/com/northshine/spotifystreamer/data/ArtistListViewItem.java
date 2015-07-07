package com.northshine.spotifystreamer.data;

import android.graphics.Bitmap;

/**
 * Created by cm, 16/06 2015
 */
public class ArtistListViewItem {

    private String name;
    private Bitmap image; // drawable reference id

    public ArtistListViewItem(String name, Bitmap image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Bitmap getImage() {
        return image;
    }
}
