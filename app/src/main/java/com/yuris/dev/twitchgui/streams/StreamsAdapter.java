package com.yuris.dev.twitchgui.streams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yuris.dev.twitchapi.models.Stream;
import com.yuris.dev.twitchgui.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yuris on 2016-07-17.
 */
public class StreamsAdapter extends ArrayAdapter<Stream> implements Filterable {
    private final Context context;
    private List<Stream> values;

    public StreamsAdapter(Context context, List<Stream> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Stream getItem(int position) {
        return values.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.streams_list_layout, parent, false);
        TextView displayName = (TextView) rowView.findViewById(R.id.display_name);
        TextView streamStatus = (TextView) rowView.findViewById(R.id.stream_status);
        TextView streamViewers = (TextView) rowView.findViewById(R.id.stream_viewers);
        ImageView streamPreview = (ImageView) rowView.findViewById(R.id.stream_preview);

        displayName.setText(values.get(position).getDisplayName());
        streamStatus.setText(values.get(position).getStatus());
        streamViewers.setText(values.get(position).getViewers().toString());

        Picasso.with(context)
                .load(values.get(position).getPreviewMedium())
                .memoryPolicy(MemoryPolicy.NO_CACHE )
                .networkPolicy(NetworkPolicy.NO_CACHE)//
                .placeholder(R.drawable.preview_missing) //
                .error(R.drawable.preview_missing) //
                .tag(context) //
                .into(streamPreview);

        return rowView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<Stream> newValues = new ArrayList<Stream>();

                for(Stream stream : values) {
                    if(stream.getOnline()) {
                        newValues.add(stream);
                    }
                }

                results.values = newValues;
                results.count = newValues.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    values = (List<Stream>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }
}
