package com.northshine.spotifystreamer;

import android.content.Intent;
import android.graphics.Bitmap;
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
import com.northshine.spotifystreamer.tasks.FetchThumbnailTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
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

        List<ArtistListViewItem> artistList = new ArrayList<>();
        mArtistListViewAdapter = new ArtistListViewAdapter(getActivity(), artistList);
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
                String artistText = artistListViewItem.getName();
                Log.v(LOG_TAG, "Toast " + artistText + " " + artistListViewItem.getId());
                Intent intent = new Intent(getActivity(), TopTracksActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, artistListViewItem.getId());
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
                        String name = artist.name + " (" + artist.id + ")";
                        Log.v(LOG_TAG, " - " + name);
                        final String imageUrl = artist.images.size() > 0 && artist.images.get(0) != null ? artist.images.get(0).url : null;
                        FetchThumbnailTask fetchArtistThumbnailTask = new FetchThumbnailTask();
                        Bitmap thumb = null;
                        try {
                            thumb = fetchArtistThumbnailTask.execute(imageUrl).get(10, TimeUnit.SECONDS);
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            // ignore
                        }
                        mArtistListViewAdapter.add(new ArtistListViewItem(name, artist.id, thumb));
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
