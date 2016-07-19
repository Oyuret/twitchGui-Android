package com.yuris.dev.twitchapi.workers;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.yuris.dev.twitchapi.models.Stream;
import com.yuris.dev.utils.AsyncTaskResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yuris on 2016-07-17.
 */
public abstract class FollowingWorker extends AsyncTask<String, Void, AsyncTaskResult<List<Stream>>> {

    @Override
    protected AsyncTaskResult<List<Stream>> doInBackground(String... params) {
        String userName = params[0];
        Integer offset = Integer.parseInt(params[1]);

        AsyncTaskResult<List<Stream>> result;

        try {
            List<Stream> onlineAndOfflineStreams = getOnlineAndOfflineStreams(userName, offset);
            List<Stream> onlineStreams = getOnlineStreams(onlineAndOfflineStreams);

            List<Stream> streams = joinStreamLists(onlineAndOfflineStreams, onlineStreams);
            
            result = new AsyncTaskResult<List<Stream>>(streams);

        } catch (Exception e) {
            return new AsyncTaskResult<List<Stream>>(e);
        }

        return result;
    }

    private List<Stream> joinStreamLists(List<Stream> onlineAndOfflineStreams, List<Stream> onlineStreams) {
        List<Stream> joinedStreams = new ArrayList<Stream>();
        HashMap<String, Stream> streamsMap = new HashMap<String, Stream>();

        for(Stream onlineAndOfflineStream : onlineAndOfflineStreams) {
            streamsMap.put(onlineAndOfflineStream.getName(), onlineAndOfflineStream);
        }

        for(Stream onlineStream : onlineStreams) {
            streamsMap.put(onlineStream.getName(), onlineStream);
        }

        for(String streamName : streamsMap.keySet()) {
            joinedStreams.add(streamsMap.get(streamName));
        }

        return joinedStreams;
    }

    private List<Stream> getOnlineAndOfflineStreams(String userName, Integer offset) throws Exception {
        List<Stream> onlineAndOfflineChannels = new ArrayList<Stream>();
        String escapedUserName = URLEncoder.encode(userName, "utf-8");

        String url = String.format("https://api.twitch.tv/kraken/users/%s/follows/channels?limit=50&offset=%d",
                escapedUserName, offset);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            onlineAndOfflineChannels = parseOnlineAndOfflineStreams(response.body().string());
        } else {
            throw new Exception(response.body().string());
        }

        return onlineAndOfflineChannels;
    }

    private List<Stream> parseOnlineAndOfflineStreams(String jsonResult) throws Exception{
        List<Stream> onlineAndOfflineStreams = new ArrayList<Stream>();

        JSONObject jsonResponseObject = new JSONObject(jsonResult);
        JSONArray follows = jsonResponseObject.getJSONArray("follows");

        for(int i=0; i < follows.length(); i++) {
            JSONObject channel = follows.getJSONObject(i);
            Stream stream = parseStreamAsOffline(channel);
            onlineAndOfflineStreams.add(stream);
        }

        return onlineAndOfflineStreams;
    }
    
    private List<Stream> getOnlineStreams(List<Stream> onlineAndOfflineStreams) throws Exception {
        List<Stream> onlineStreams = new ArrayList<Stream>();

        String concatenatedStreamNames = joinStreamNames(onlineAndOfflineStreams);
        concatenatedStreamNames = URLEncoder.encode(concatenatedStreamNames, "utf-8");

        String url = String.format("https://api.twitch.tv/kraken/streams?limit=100&channel=%s",
                concatenatedStreamNames);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            onlineStreams = parseOnlineStreams(response.body().string());
        } else {
            throw new Exception(response.body().string());
        }

        return onlineStreams;
    }

    private String joinStreamNames(List<Stream> streams) {
        List<String> streamNames = new ArrayList<String>();
        for(Stream stream : streams) {
            streamNames.add(stream.getName());
        }

        return TextUtils.join(",",streamNames);
    }

    private List<Stream> parseOnlineStreams(String jsonResult) throws Exception{
        List<Stream> onlineStreams = new ArrayList<Stream>();

        JSONObject jsonResponseObject = new JSONObject(jsonResult);
        JSONArray follows = jsonResponseObject.getJSONArray("streams");

        for(int i=0; i < follows.length(); i++) {
            JSONObject channel = follows.getJSONObject(i);
            Stream stream = parseStreamAsOnline(channel);
            onlineStreams.add(stream);
        }

        return onlineStreams;
    }

    private Stream parseStreamAsOnline(JSONObject streamObj) throws Exception {
        Stream stream =  new Stream.StreamFactory()
                .withName(streamObj.getJSONObject("channel").getString("name"))
                .withDisplayName(streamObj.getJSONObject("channel").getString("display_name"))
                .withAverageFps(streamObj.optDouble("average_fps", 0))
                .withDelay(streamObj.optInt("delay", 0))
                .withFollowers(streamObj.getJSONObject("channel").getInt("followers"))
                .withGame(streamObj.optString("game", "no game"))
                .withLanguage(streamObj.getJSONObject("channel").optString("language", "no language"))
                .withMature(streamObj.getJSONObject("channel").optBoolean("mature", false))
                .withStatus(streamObj.getJSONObject("channel").optString("status", "no status"))
                .withViews(streamObj.getJSONObject("channel").getInt("views"))
                .withViewers(streamObj.optInt("viewers", 0))
                .withPreviewSmall(streamObj.getJSONObject("preview").optString("small"))
                .withPreviewMedium(streamObj.getJSONObject("preview").optString("medium"))
                .withPreviewLarge(streamObj.getJSONObject("preview").optString("large"))
                .withOnline(true)
                .build();

        return stream;
    }

    private Stream parseStreamAsOffline(JSONObject streamObj) throws Exception {
        Stream stream =  new Stream.StreamFactory()
                .withName(streamObj.getJSONObject("channel").getString("name"))
                .withDisplayName(streamObj.getJSONObject("channel").getString("display_name"))
                .withAverageFps(streamObj.optDouble("average_fps", 0))
                .withDelay(streamObj.optInt("delay", 0))
                .withFollowers(streamObj.getJSONObject("channel").getInt("followers"))
                .withGame(streamObj.optString("game", "no game"))
                .withLanguage(streamObj.getJSONObject("channel").optString("language", "no language"))
                .withMature(streamObj.getJSONObject("channel").optBoolean("mature", false))
                .withStatus(streamObj.getJSONObject("channel").optString("status", "no status"))
                .withViews(streamObj.getJSONObject("channel").getInt("views"))
                .withViewers(streamObj.optInt("viewers", 0))
                .withOnline(false)
                .build();

        return stream;
    }

}
