package com.northshine.spotifystreamer;

import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.northshine.spotifystreamer.data.ArtistListViewItem;

import java.util.ArrayList;

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

    private ArtistListViewAdapter mArtistListViewAdapter;

    private ArrayList<ArtistListViewItem> artistListViewItems;

    private SearchView searchView;

    private String searchText = "";

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        setRetainInstance(true);
        restoreArtistItems(savedInstanceState);
    }

    private void restoreArtistItems(Bundle savedInstanceState) {
        if (savedInstanceState == null || !savedInstanceState.containsKey("artists")) {
            artistListViewItems = new ArrayList<>();
        } else {
            artistListViewItems = savedInstanceState.getParcelableArrayList("artists");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("artists", artistListViewItems);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        searchView = (SearchView) rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getActivity(), "Searching for : " + s, Toast.LENGTH_SHORT).show();
                searchText = s;
                searchView.clearFocus();
                FetchArtistInfoTask artistInfoTask = new FetchArtistInfoTask();
                artistInfoTask.execute(searchText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        if (artistListViewItems == null) {
            Log.v(LOG_TAG, "No artistListViewItems");
        }
        mArtistListViewAdapter = new ArtistListViewAdapter(getActivity(), artistListViewItems);
        setOnClickForArtist(rootView);
        return rootView;
    }

    private void setOnClickForArtist(View rootView) {
        ListView lv = (ListView) rootView.findViewById(R.id.artistListView);
        lv.setAdapter(mArtistListViewAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ArtistListViewItem artistListViewItem = mArtistListViewAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), TopTracksActivity.class).putExtra(Intent.EXTRA_TEXT, artistListViewItem.getId());
                startActivity(intent);
            }
        });
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
            artistInfoTask.execute(searchText);
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
                    if (artistsPager.artists.items.isEmpty()) {
                        Toast.makeText(getActivity(), "No search result found!", Toast.LENGTH_SHORT).show();
                    }
                    for (Artist artist : artistsPager.artists.items) {
                        final String imageUrl = artist.images.size() > 0 && artist.images.get(0) != null ? artist.images.get(0).url : null;
                        mArtistListViewAdapter.add(new ArtistListViewItem(artist.name, artist.id, imageUrl));
                    }
                    // TODO: reset on click?
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
