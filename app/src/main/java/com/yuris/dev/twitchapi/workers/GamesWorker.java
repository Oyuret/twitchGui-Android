package com.yuris.dev.twitchapi.workers;

import android.os.AsyncTask;

import com.yuris.dev.twitchapi.models.Game;
import com.yuris.dev.utils.AsyncTaskResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yuris on 2016-07-13.
 */
public abstract class GamesWorker extends AsyncTask<Integer, Void, AsyncTaskResult<List<Game>>>{


    @Override
    protected AsyncTaskResult<List<Game>> doInBackground(Integer... params) {
        Integer offset = params[0];

        try {
            String result = makeCall(offset);
            List<Game> games = parseResult(result);

            return new AsyncTaskResult<List<Game>>(games);
        } catch (Exception e) {
            return new AsyncTaskResult<List<Game>>(e);
        }

    }

    private String makeCall(Integer offset) throws Exception {
        String urlString = String.format("https://api.twitch.tv/kraken/games/top?limit=50&offset=%d", offset);

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

    private List<Game> parseResult(String result) throws Exception {
        List<Game> games = new ArrayList<Game>(100);
        JSONObject jsonObject = new JSONObject(result);

        JSONArray top = jsonObject.getJSONArray("top");

        for(int i = 0; i < top.length(); i++) {
            JSONObject gameObject = top.getJSONObject(i);
            Game game = parseGame(gameObject);

            games.add(game);
        }

        return games;
    }

    private Game parseGame(JSONObject gameObj) throws Exception {
        Game game =  new Game.GameFactory()
                .withName(gameObj.getJSONObject("game").getString("name"))
                .withChannels(gameObj.getInt("channels"))
                .withViewers(gameObj.getInt("viewers"))
                .withLogo(gameObj.getJSONObject("game").getJSONObject("box").getString("medium"))
                .build();

        return game;
    }

}
