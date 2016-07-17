package com.yuris.dev.twitchgui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yuris.dev.twitchapi.models.Stream;
import com.yuris.dev.twitchapi.workers.FollowingWorker;
import com.yuris.dev.twitchgui.streams.StreamsAdapter;
import com.yuris.dev.utils.AsyncTaskResult;
import com.yuris.dev.utils.EndlessScrollListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FollowingFragment.OnFollowedStreamSelectedListener} interface
 * to handle interaction events.
 * Use the {@link FollowingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FollowingFragment extends Fragment {

    private static final String ARG_USERNAME = "userName";

    private String userName;

    List<Stream> streams;
    StreamsAdapter streamsAdapter;

    private OnFollowedStreamSelectedListener mListener;

    public FollowingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userName The username on Twitch.
     * @return A new instance of fragment FollowingFragment.
     */
    public static FollowingFragment newInstance(String userName) {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(ARG_USERNAME);
        }
        streams = new ArrayList<Stream>();
        streamsAdapter = new StreamsAdapter(getActivity(), streams);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_following, container, false);

        ListView streamsListView = (ListView) view.findViewById(R.id.following_list_view);
        streamsListView.setAdapter(streamsAdapter);

        streamsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final Stream stream = (Stream) parent.getItemAtPosition(position);
                mListener.onFollowedStreamSelected(stream.getName());
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
    public void onStart() {
        super.onStart();
        loadData();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFollowedStreamSelectedListener) {
            mListener = (OnFollowedStreamSelectedListener) context;
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

    private void loadData() {

        FollowingWorker getFollowedStreams = new FollowingWorker() {
            @Override
            protected void onPostExecute(AsyncTaskResult<List<Stream>> listAsyncTaskResult) {
                if(listAsyncTaskResult.hasError()) {
                    Log.e("Following", listAsyncTaskResult.getError().getMessage());
                } else {
                    streams.addAll(listAsyncTaskResult.getResult());
                    streamsAdapter.getFilter().filter("");
                }
            }
        };

        getFollowedStreams.execute(new String[]{userName, String.valueOf(streams.size())});
    }

    public interface OnFollowedStreamSelectedListener {
        void onFollowedStreamSelected(String streamName);
    }
}
