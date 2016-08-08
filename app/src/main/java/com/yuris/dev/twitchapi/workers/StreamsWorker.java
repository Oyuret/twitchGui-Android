package com.yuris.dev.twitchapi.workers;

import android.os.AsyncTask;

import com.yuris.dev.twitchapi.models.Stream;
import com.yuris.dev.utils.AsyncTaskResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yuris on 2016-07-16.
 */
public abstract class StreamsWorker extends AsyncTask<String, Void, AsyncTaskResult<List<Stream>>> {

    @Override
    protected AsyncTaskResult<List<Stream>> doInBackground(String... params) {
        String game = params[0];
        Integer offset = Integer.parseInt(params[1]);

        try {
            String result = makeCall(game, offset);
            List<Stream> streams = parseResult(result);

            return new AsyncTaskResult<List<Stream>>(streams);
        } catch (Exception e) {
            return new AsyncTaskResult<List<Stream>>(e);
        }

    }

    private String makeCall(String game, Integer offset) throws Exception {
        String escapedGame = URLEncoder.encode(game, "utf-8");
        String urlString = String.format("https://api.twitch.tv/kraken/streams?game=%s&limit=50&offset=%d", escapedGame, offset);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .header("Accept", TwitchApiConstants.TWITCH_API_VERSION)
                .header("Client-ID", TwitchApiConstants.TWITCH_API_CLIENT_ID)
                .build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new Exception(response.body().string());
        }
    }

    private List<Stream> parseResult(String result) throws Exception {
        List<Stream> streams = new ArrayList<Stream>(100);
        JSONObject jsonObject = new JSONObject(result);

        JSONArray streamsJsonArray = jsonObject.getJSONArray("streams");

        for(int i = 0; i < streamsJsonArray.length(); i++) {
            JSONObject streamObject = streamsJsonArray.getJSONObject(i);
            Stream stream = parseStream(streamObject);

            streams.add(stream);
        }

        return streams;
    }

    private Stream parseStream(JSONObject streamObj) throws Exception {
        Stream stream =  new Stream.StreamFactory()
                .withName(streamObj.getJSONObject("channel").getString("name"))
                .withDisplayName(streamObj.getJSONObject("channel").getString("display_name"))
                .withAverageFps(streamObj.getDouble("average_fps"))
                .withDelay(streamObj.optInt("delay", 0))
                .withFollowers(streamObj.getJSONObject("channel").getInt("followers"))
                .withGame(streamObj.getString("game"))
                .withLanguage(streamObj.getJSONObject("channel").getString("language"))
                .withMature(streamObj.getJSONObject("channel").optBoolean("mature", false))
                .withStatus(streamObj.getJSONObject("channel").getString("status"))
                .withViews(streamObj.getJSONObject("channel").getInt("views"))
                .withViewers(streamObj.getInt("viewers"))
                .withPreviewSmall(streamObj.getJSONObject("preview").getString("small"))
                .withPreviewMedium(streamObj.getJSONObject("preview").getString("medium"))
                .withPreviewLarge(streamObj.getJSONObject("preview").getString("large"))
                .withOnline(true)
                .build();

        return stream;
    }
}
