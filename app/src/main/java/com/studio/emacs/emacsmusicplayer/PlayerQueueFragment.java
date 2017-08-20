package com.studio.emacs.emacsmusicplayer;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.studio.emacs.emacsmusicplayer.core.PlayerService;
import com.studio.emacs.emacsmusicplayer.sections.SongQueueAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlayerQueueFragment extends Fragment {

    private ListView mListView;

    public PlayerQueueFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_queue,null);
        mListView = (ListView)view.findViewById(R.id.listView);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Song> queue = new ArrayList<>(PlayerService.mSongsQueue);
        mListView.setAdapter(new SongQueueAdapter(getContext(), 0,queue));
     }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    public interface FragmentInteractionLister{
        void onFragmentClose();
    }

    /**
     * This is for contextual menu
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.music_player_queue_cab,menu);
    }
}
