package com.yuris.dev.twitchgui.games;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yuris.dev.twitchapi.models.Game;
import com.yuris.dev.twitchgui.R;

import java.util.List;

import okhttp3.OkHttpClient;


/**
 * Created by yuris on 2016-07-16.
 */
public class GamesAdapter extends ArrayAdapter<Game> {
    private final Context context;
    private final List<Game> values;
    Picasso picasso;

    public GamesAdapter(Context context, List<Game> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();
        Picasso.Builder picassoBuilder = new Picasso.Builder(context);
        picassoBuilder.downloader(new OkHttp3Downloader(client));
        picasso = picassoBuilder.build();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.game_list_layout, parent, false);
        final TextView gameName = (TextView) rowView.findViewById(R.id.game_name);
        ImageView gameIcon = (ImageView) rowView.findViewById(R.id.game_icon);

        picasso.load(values.get(position).getLogo())//
                .placeholder(R.drawable.boxart_missing) //
                .error(R.drawable.boxart_missing) //
                .tag(context) //
                .into(gameIcon, new Callback() {
                    @Override
                    public void onSuccess() {
                        gameName.setText("");
                    }

                    @Override
                    public void onError() {
                        gameName.setText(values.get(position).getName());
                    }
                });

        return rowView;
    }
}
