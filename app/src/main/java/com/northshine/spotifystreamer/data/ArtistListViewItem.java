package com.northshine.spotifystreamer.data;

import android.graphics.Bitmap;

/**
 * Created by cm, 16/06 2015
 */
public class ArtistListViewItem {

    private String name;
    private String id;
    private Bitmap image; // drawable reference id

    public ArtistListViewItem(String name, String id, Bitmap image) {
        this.name = name;
        this.id = id;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Bitmap getImage() {
        return image;
    }
}
