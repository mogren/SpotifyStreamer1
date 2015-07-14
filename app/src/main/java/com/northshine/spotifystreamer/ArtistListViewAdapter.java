package com.northshine.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.northshine.spotifystreamer.data.ArtistListViewItem;

import java.util.List;

/**
 * Created by cm on 07/07/2015
 */
public class ArtistListViewAdapter extends ArrayAdapter<ArtistListViewItem> {

    public ArtistListViewAdapter(Context context, List<ArtistListViewItem> artistListViewItems) {
        super(context, 0, artistListViewItems);
    }

    // A holder will hold the reference to your views.
    ViewHolder holder;

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
            holder = new ViewHolder();
            holder.artistImage = (ImageView) convertView.findViewById(R.id.artistImageView);
            holder.artistName = (TextView) convertView.findViewById(R.id.artistTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String url = artistListViewItem.getImageUrl();
        Glide.with(getContext())
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.abc_spinner_mtrl_am_alpha)
                .crossFade()
                .into(holder.artistImage);

        holder.artistName.setText(artistListViewItem.getName());

        return convertView;
    }

    class ViewHolder {
        ImageView artistImage;
        TextView artistName;
    }
}
