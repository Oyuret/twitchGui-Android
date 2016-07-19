package com.yuris.dev.twitchgui.streams;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yuris.dev.twitchapi.models.Stream;
import com.yuris.dev.twitchapi.workers.StreamsWorker;
import com.yuris.dev.twitchgui.R;
import com.yuris.dev.utils.AsyncTaskResult;
import com.yuris.dev.utils.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StreamsFragment.OnStreamSelectedListener} interface
 * to handle interaction events.
 * Use the {@link StreamsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StreamsFragment extends Fragment {
    private static final String ARG_GAMENAME = "gameName";

    private String gameName;

    List<Stream> streams;
    StreamsAdapter streamsAdapter;

    private OnStreamSelectedListener mListener;

    public StreamsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param gameName The name of the game which we will show streams for
     * @return A new instance of fragment StreamsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StreamsFragment newInstance(String gameName) {
        StreamsFragment fragment = new StreamsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAMENAME, gameName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gameName = getArguments().getString(ARG_GAMENAME);
        }
        streams = new ArrayList<Stream>();
        streamsAdapter = new StreamsAdapter(getActivity(), streams);
        loadData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_streams, container, false);
        ListView streamsListView = (ListView) view.findViewById(R.id.streams_list_view);
        streamsListView.setAdapter(streamsAdapter);

        streamsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Stream stream = (Stream) parent.getItemAtPosition(position);
                mListener.onStreamSelected(stream.getName());
            }

        });

        streamsListView.setOnScrollListener(new EndlessScrollListener() {
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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnStreamSelectedListener) {
            mListener = (OnStreamSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnStreamSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadData() {

        StreamsWorker getSteams = new StreamsWorker() {
            @Override
            protected void onPostExecute(AsyncTaskResult<List<Stream>> listAsyncTaskResult) {
                if(listAsyncTaskResult.hasError()) {
                    mListener.notifyUser("Failed to load Streams");
                } else {
                    streams.addAll(listAsyncTaskResult.getResult());
                    streamsAdapter.notifyDataSetChanged();
                }
            }
        };

        getSteams.execute(new String[]{gameName, String.valueOf(streams.size())});
    }

    public interface OnStreamSelectedListener {
        void onStreamSelected(String streamName);
        void notifyUser(String message);
    }
}
