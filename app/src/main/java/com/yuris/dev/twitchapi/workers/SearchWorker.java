package com.yuris.dev.twitchapi.workers;

import android.os.AsyncTask;

import com.yuris.dev.twitchapi.models.Game;
import com.yuris.dev.utils.AsyncTaskResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yuris on 2016-07-19.
 */
public abstract class SearchWorker extends AsyncTask<String, Void, AsyncTaskResult<List<Game>>> {

    @Override
    protected AsyncTaskResult<List<Game>> doInBackground(String... params) {
        String unsafeSearchTerm = params[0];

        try {
            String safeSearchTerm = URLEncoder.encode(unsafeSearchTerm, "utf-8");
            String result = makeCall(safeSearchTerm);
            List<Game> games = parseResults(result);

            return new AsyncTaskResult<List<Game>>(games);

        } catch (Exception e) {
            return new AsyncTaskResult<List<Game>>(e);
        }
    }

    private String makeCall(String searchTerm) throws Exception {

        String url = String.format("https://api.twitch.tv/kraken/search/games?q=%s&type=suggest&live=true",
                searchTerm);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if(response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new Exception(response.body().string());
        }

    }

    private List<Game> parseResults(String jsonResult) throws Exception {
        List<Game> games = new ArrayList<Game>();
        JSONObject jsonObject = new JSONObject(jsonResult);

        JSONArray top = jsonObject.getJSONArray("games");

        for(int i = 0; i < top.length(); i++) {
            JSONObject gameObject = top.getJSONObject(i);
            Game game = parseGame(gameObject);
            games.add(game);
        }

        return games;
    }


    private Game parseGame(JSONObject gameObj) throws Exception {
        Game game =  new Game.GameFactory()
                .withName(gameObj.getString("name"))
                .withLogo(gameObj.getJSONObject("box").getString("medium"))
                .build();

        return game;
    }
}
