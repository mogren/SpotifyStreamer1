package com.northshine.spotifystreamer;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mArtistListViewAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //View artistView = inflater.inflate(R.layout.fragment_artist_list_item, container, false);
        List<String> artistList = new ArrayList<>();
        artistList.add("Nothing!");
        mArtistListViewAdapter = new ArrayAdapter<>(getActivity(), R.layout.fragment_text, R.id.someText, artistList);
        ListView lv = (ListView) rootView.findViewById(R.id.artistListView);
        lv.setAdapter(mArtistListViewAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String artistText = mArtistListViewAdapter.getItem(position);
                Toast.makeText(getActivity(), artistText, Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_artist_refresh) {
            FetchArtistInfoTask artistInfoTask = new FetchArtistInfoTask();
            artistInfoTask.execute("Refused");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class FetchArtistInfoTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchArtistInfoTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {

            // If there's no artist string, just skip the search
            if (params.length == 0) {
                return null;
            }

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                    .build();

            SpotifyService spotify = restAdapter.create(SpotifyService.class);
            spotify.searchArtists("artist:" + params[0], new Callback<ArtistsPager>() {
                @Override
                public void success(ArtistsPager artistsPager, Response response) {
                    Log.v(LOG_TAG, "ArtistsPager returned " + artistsPager.artists.total);
                    mArtistListViewAdapter.clear();
                    for (Artist artist : artistsPager.artists.items) {
                        String info = artist.id + " " + artist.name + " (" + artist.images.size() +")";
                        Log.v(LOG_TAG, " - " + info);
                        mArtistListViewAdapter.add(info);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(LOG_TAG, "API search error " + error);
                }
            });

            return null;
        }

    }
}
