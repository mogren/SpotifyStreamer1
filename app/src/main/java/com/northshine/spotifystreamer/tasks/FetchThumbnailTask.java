package com.northshine.spotifystreamer.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by cm on 10/07/2015
 */
public class FetchThumbnailTask extends AsyncTask<String, Void, Bitmap> {

    private final String LOG_TAG = FetchThumbnailTask.class.getSimpleName();

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

