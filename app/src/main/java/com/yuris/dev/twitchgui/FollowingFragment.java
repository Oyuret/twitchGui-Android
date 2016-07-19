package com.yuris.dev.twitchgui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceManager;
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
     * @return A new instance of fragment FollowingFragment.
     */
    public static FollowingFragment newInstance() {
        FollowingFragment fragment = new FollowingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        streams = new ArrayList<Stream>();
        streamsAdapter = new StreamsAdapter(getActivity(), streams);
        loadData();
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
        String userName = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("settings_twitch_username", "");

        if(userName.isEmpty()) {
            mListener.notifyUser("Twitch username is not set. Go to settings");
            return;
        }


        FollowingWorker getFollowedStreams = new FollowingWorker() {
            @Override
            protected void onPostExecute(AsyncTaskResult<List<Stream>> listAsyncTaskResult) {
                if(listAsyncTaskResult.hasError()) {
                    mListener.notifyUser("Failed to load Followed Streams");
                } else {
                    streams.addAll(listAsyncTaskResult.getResult());
                    streamsAdapter.getFilter().filter("");
                }
            }
        };

        getFollowedStreams.execute(new String[]{userName, String.valueOf(streams.size())});
    }

    public interface OnFollowedStreamSelectedListener {
        void onStreamSelected(String streamName);
        void notifyUser(String message);
    }
}
