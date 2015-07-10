package com.northshine.spotifystreamer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.northshine.spotifystreamer.data.TopTrackViewItem;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
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

    private TopTracksViewAdapter mTopTracksViewAdapter;

    private ArtistListViewAdapter mArtistListViewAdapter;

    private String searchText = "Refused";

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

        SearchView searchView = (SearchView) rootView.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getActivity(), "Searching for : " + s, Toast.LENGTH_SHORT).show();
                searchText = s;
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
        mTopTracksViewAdapter = new TopTracksViewAdapter(getActivity(), new ArrayList<TopTrackViewItem>());

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
                Toast.makeText(getActivity(), artistText, Toast.LENGTH_SHORT).show();
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
                        FetchArtistThumbnailTask fetchArtistThumbnailTask = new FetchArtistThumbnailTask();
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

    public class FetchArtistThumbnailTask extends AsyncTask<String, Void, Bitmap> {

        private final String LOG_TAG = FetchArtistThumbnailTask.class.getSimpleName();

        final int THUMBNAIL_SIZE = 64;

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.v(LOG_TAG, "Fetching image for url " + params[0]);
            Bitmap bm = null;
            if (params[0] != null) {
                try {
                    URL aURL = new URL(params[0]);
                    URLConnection conn = aURL.openConnection();
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(is);
                    Bitmap bigbm = BitmapFactory.decodeStream(bis);
                    bm = Bitmap.createScaledBitmap(bigbm, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
                    bis.close();
                    is.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error getting bitmap", e);
                }
            }
            return bm;
        }

    }

    public class FetchArtistSongsTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchArtistSongsTask.class.getSimpleName();

        @Override
        protected Void doInBackground(String... params) {

            // If there's no artist string, just skip the search
            if (params.length == 0) {
                return null;
            }

            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
                    .build();
            final String artistName = params[0];
            SpotifyService spotify = restAdapter.create(SpotifyService.class);
            spotify.searchAlbums("artist:" + artistName, new Callback<AlbumsPager>() {
                @Override
                public void success(AlbumsPager albumsPager, Response response) {
                    Log.v(LOG_TAG, "AlbumsPager returned " + albumsPager.albums.total);
                    mTopTracksViewAdapter.clear();
                    if (albumsPager.albums.items.isEmpty()) {
                        Toast.makeText(getActivity(), "No search result found!", Toast.LENGTH_SHORT).show();
                    }
                    for (AlbumSimple albumSimple : albumsPager.albums.items) {
                        String name = albumSimple.name + " (" + albumSimple.id + ")";
                        Log.v(LOG_TAG, " - " + name);
                        final String imageUrl = albumSimple.images.size() > 0 && albumSimple.images.get(0) != null ? albumSimple.images.get(0).url : null;
                        FetchArtistThumbnailTask fetchArtistThumbnailTask = new FetchArtistThumbnailTask();
                        Bitmap thumb = null;
                        try {
                            thumb = fetchArtistThumbnailTask.execute(imageUrl).get(10, TimeUnit.SECONDS);
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            // ignore
                        }
                        mTopTracksViewAdapter.add(new TopTrackViewItem(albumSimple.id, artistName, albumSimple.name, thumb));
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
