package com.northshine.spotifystreamer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.northshine.spotifystreamer.data.TopTrackViewItem;
import com.northshine.spotifystreamer.tasks.FetchThumbnailTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class TopTracksActivityFragment extends Fragment {

    private TopTracksViewAdapter mTopTracksViewAdapter;

    private final String LOG_TAG = TopTracksActivityFragment.class.getSimpleName();

    public TopTracksActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_tracks, container, false);

        List<TopTrackViewItem> trackViewItems = new ArrayList<>();
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String artistId = intent.getStringExtra(Intent.EXTRA_TEXT);
            FetchArtistSongsTask fetchArtistSongsTask = new FetchArtistSongsTask();
            fetchArtistSongsTask.execute(artistId);
        }
        ListView trackListView = (ListView) rootView.findViewById(R.id.trackListView);
        mTopTracksViewAdapter = new TopTracksViewAdapter(getActivity(), trackViewItems);
        trackListView.setAdapter(mTopTracksViewAdapter);
        return rootView;
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
            final String artistId = params[0];
            SpotifyService spotify = restAdapter.create(SpotifyService.class);
            Map<String, Object> options = new HashMap<>();
            options.put("country", "SE");
            Log.v(LOG_TAG, "Trying for id " + artistId);
            spotify.getArtistTopTrack(artistId, options, new Callback<Tracks>() {

                @Override
                public void success(Tracks tracks, Response response) {
                    Log.v(LOG_TAG, "Tracks returned " + tracks.tracks.size());
                    mTopTracksViewAdapter.clear();
                    if (tracks.tracks.size() < 1) {
                        Toast.makeText(getActivity(), "No top tracks found!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (Track track : tracks.tracks) {
                        String name = track.name + " (" + track.id + ")";
                        Log.v(LOG_TAG, " - " + name);
                        final String imageUrl = track.album.images.size() > 0 && track.album.images.get(0) != null ? track.album.images.get(0).url : null;
                        FetchThumbnailTask fetchArtistThumbnailTask = new FetchThumbnailTask();
                        Bitmap thumb = null;
                        try {
                            thumb = fetchArtistThumbnailTask.execute(imageUrl).get(10, TimeUnit.SECONDS);
                        } catch (InterruptedException | ExecutionException | TimeoutException e) {
                            // ignore
                        }
                        mTopTracksViewAdapter.add(new TopTrackViewItem(track.id, track.artists.get(0).name, track.name, thumb));
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
