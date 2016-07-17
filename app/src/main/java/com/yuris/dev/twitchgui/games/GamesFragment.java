package com.yuris.dev.twitchgui.games;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.yuris.dev.twitchapi.models.Game;
import com.yuris.dev.twitchapi.workers.GamesWorker;
import com.yuris.dev.twitchgui.R;
import com.yuris.dev.utils.AsyncTaskResult;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnGameSelectedListener} interface
 * to handle interaction events.
 * Use the {@link GamesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GamesFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    List<Game> games;
    GamesAdapter gamesAdapter;

    private OnGameSelectedListener mListener;

    public GamesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GamesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GamesFragment newInstance(String param1, String param2) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        games = new ArrayList<Game>();
        gamesAdapter = new GamesAdapter(getActivity(), games);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_games, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridView gamesListView = (GridView) view.findViewById(R.id.gamesListView);
        gamesListView.setAdapter(gamesAdapter);

        gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Game game = (Game) parent.getItemAtPosition(position);
                mListener.onGameSelected(game.getName());
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();

        GamesWorker getGames = new GamesWorker() {
            @Override
            protected void onPostExecute(AsyncTaskResult<List<Game>> listAsyncTaskResult) {
                if(listAsyncTaskResult.hasError()) {
                    Log.e("Games", "Error loading games");
                } else {
                    games.addAll(listAsyncTaskResult.getResult());
                    gamesAdapter.notifyDataSetChanged();
                }
            }
        };
        getGames.execute(new Integer[]{Integer.parseInt("0")});
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGameSelectedListener) {
            mListener = (OnGameSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnGameSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnGameSelectedListener {
        void onGameSelected(String gameName);
    }
}
