package com.yuris.dev.twitchgui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.yuris.dev.twitchapi.models.Game;
import com.yuris.dev.twitchapi.workers.SearchWorker;
import com.yuris.dev.twitchgui.games.GamesAdapter;
import com.yuris.dev.utils.AsyncTaskResult;
import com.yuris.dev.utils.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SearchFragment.OnSearchResultSelectedListener} interface
 * to handle interaction events.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {
    private List<Game> games;
    private GamesAdapter gamesAdapter;
    private boolean searchInProgress = false;

    private OnSearchResultSelectedListener mListener;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_search, container, false);


        GridView gamesListView = (GridView) view.findViewById(R.id.searchResultsGridView);
        gamesListView.setAdapter(gamesAdapter);

        gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Game game = (Game) parent.getItemAtPosition(position);
                mListener.onGameSelected(game.getName());
            }

        });

        final SearchView searchView = (SearchView) view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                makeSearch(query);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSearchResultSelectedListener) {
            mListener = (OnSearchResultSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSearchResultSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        final SearchView searchView = (SearchView) getView().findViewById(R.id.searchView);
        searchView.clearFocus();
    }

    private void makeSearch(String searchTerm) {
        if(searchInProgress) {
            mListener.notifyUser("Awaiting earlier search results, please wait and search again");
            return;
        }

        searchInProgress = true;
        games.clear();
        gamesAdapter.notifyDataSetChanged();
        SearchWorker searchGames = new SearchWorker() {
            @Override
            protected void onPostExecute(AsyncTaskResult<List<Game>> listAsyncTaskResult) {
                if(listAsyncTaskResult.hasError()) {
                    mListener.notifyUser("Search failed");
                } else {
                    games.addAll(listAsyncTaskResult.getResult());
                    gamesAdapter.notifyDataSetChanged();
                }
                searchInProgress = false;
            }
        };
        searchGames.execute(new String[]{searchTerm});
    }

    public interface OnSearchResultSelectedListener {
        void onGameSelected(String gameName);
        void notifyUser(String message);
    }
}
