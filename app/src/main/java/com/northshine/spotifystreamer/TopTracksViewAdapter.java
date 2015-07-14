package com.northshine.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.northshine.spotifystreamer.data.TopTrackViewItem;

import java.util.List;

/**
 * Created by cm on 07/07/2015
 */
public class TopTracksViewAdapter extends ArrayAdapter<TopTrackViewItem> {

    // A holder will hold the reference to your views.
    ViewHolder holder;

    public TopTracksViewAdapter(Context context, List<TopTrackViewItem> topTrackViewItems) {
        super(context, 0, topTrackViewItems);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        final TopTrackViewItem artistListViewItem = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_top_track_item, parent, false);
            holder = new ViewHolder();
            holder.albumImage = (ImageView) convertView.findViewById(R.id.albumImageView);
            holder.trackTitle = (TextView) convertView.findViewById(R.id.trackTitle);
            holder.artistName = (TextView) convertView.findViewById(R.id.trackArtistName);
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
                .into(holder.albumImage);

        holder.trackTitle.setText(artistListViewItem.getTitle());
        holder.artistName.setText(artistListViewItem.getArtist());
        return convertView;
    }

    class ViewHolder {
        ImageView albumImage;
        TextView trackTitle;
        TextView artistName;
    }
}
