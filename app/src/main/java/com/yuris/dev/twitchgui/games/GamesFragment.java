package com.yuris.dev.twitchgui.games;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import com.yuris.dev.twitchapi.models.Game;
import com.yuris.dev.twitchapi.workers.GamesWorker;
import com.yuris.dev.twitchgui.R;
import com.yuris.dev.utils.AsyncTaskResult;
import com.yuris.dev.utils.EndlessScrollListener;

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
    private List<Game> games;
    private GamesAdapter gamesAdapter;

    private OnGameSelectedListener mListener;

    public GamesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GamesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GamesFragment newInstance() {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        games = new ArrayList<Game>();
        gamesAdapter = new GamesAdapter(getActivity(), games);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_games, container, false);

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

        gamesListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                loadData();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        loadData();
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

    private void loadData() {

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
        getGames.execute(new Integer[]{games.size()});
    }
}
