package com.northshine.spotifystreamer;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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
public class MainActivityFragment extends Fragment {

    ArrayAdapter<String> mArtistListViewAdapter;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //View artistView = inflater.inflate(R.layout.fragment_artist_list_item, container, false);
        List<String> artistList = getArtists("test");
        mArtistListViewAdapter = new ArrayAdapter<>(getActivity(), R.layout.fragment_text, R.id.someText, artistList);
        ListView lv = (ListView) rootView.findViewById(R.id.artistListView);
        lv.setAdapter(mArtistListViewAdapter);

        return rootView;
    }

    private List<String> getArtists(String artist) {

        List<String> artistList = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            artistList.add(artist + " " + i);
        }
        return artistList;
    }
}
