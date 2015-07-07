package com.northshine.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.northshine.spotifystreamer.data.ArtistListViewItem;

import java.util.List;

/**
 * Created by cm on 07/07/2015
 */
public class ArtistListViewAdapter extends ArrayAdapter<ArtistListViewItem> {

    public ArtistListViewAdapter(Context context, List<ArtistListViewItem> androidFlavors) {
        super(context, 0, androidFlavors);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        ArtistListViewItem artistListViewItem = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_artist_list_item, parent, false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.artistImageView);
        image.setImageBitmap(artistListViewItem.getImage());

        TextView versionNameView = (TextView) convertView.findViewById(R.id.artistTextView);
        versionNameView.setText(artistListViewItem.getName());

        return convertView;
    }
}
