package com.northshine.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.northshine.spotifystreamer1.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ArtistListItemActivityFragment extends Fragment {

    public ArtistListItemActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View artistView = inflater.inflate(R.layout.fragment_artist_list_item, container, false);
        List<String> artistList = getArtists("test");

        ListView lv = (ListView) artistView.findViewById(R.id.artistListView);
        lv.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.fragment_artist_list_item, R.id.artistTextView, artistList));

        return artistView;
    }

    private List<String> getArtists(String artist) {

        List<String> artistList = new ArrayList<>();
        artistList.add(artist + " 1");
        artistList.add(artist + " 2");
        artistList.add(artist + " 3");
        artistList.add(artist + " 4");

        return artistList;
    }
}
