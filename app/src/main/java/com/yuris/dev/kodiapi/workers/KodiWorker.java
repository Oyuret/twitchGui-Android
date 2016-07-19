package com.yuris.dev.kodiapi.workers;

import android.os.AsyncTask;
import android.util.Log;

import com.yuris.dev.utils.AsyncTaskResult;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yuris on 2016-07-17.
 */
public abstract class KodiWorker extends AsyncTask<String, Void, AsyncTaskResult<String>> {

    @Override
    protected AsyncTaskResult<String> doInBackground(String... params) {
        String response;
        try {
            String streamName = params[0];
            Integer qualityCode = Integer.valueOf(params[1]);

            response = playStream(streamName, qualityCode);
        } catch(Exception e) {
            return new AsyncTaskResult<String>(e);
        }

        return new AsyncTaskResult<String>(response);
    }

    private String playStream(String streamName, Integer qualityCode) throws Exception {
        String requestBodyJsonString = getBodyJsonString(streamName, qualityCode);
        RequestBody body = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"),requestBodyJsonString);
        Request request = new Request.Builder()
                .url("http://192.168.0.10:8080/jsonrpc")
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        Response response = client.newCall(request).execute();

        if(!response.isSuccessful()) throw new Exception(response.body().string());

        return response.body().string();

    }

    private String getBodyJsonString(String streamName, Integer qualityCode) {
        JSONObject body = new JSONObject();
        String path = String
                .format("plugin://plugin.video.twitch/playLive/%s/%d", streamName, qualityCode);

        try {
            body.put("jsonrpc", "2.0");
            body.put("method", "Player.Open");
            body.put("id", 1);
            body.put("params", new JSONObject().put("item", new JSONObject().put("file", path)));
        } catch (JSONException e) {
            Log.e("Body Json", "Failed to created request body Json");
        }

        return body.toString();
    }
}
