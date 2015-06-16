package com.northshine.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.northshine.spotifystreamer1.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistListItemActivityFragment extends Fragment {

    public ArtistListItemActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_artist_list_item, container, false);
    }
}
