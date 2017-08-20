package com.studio.emacs.emacsmusicplayer.sections;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.studio.emacs.emacsmusicplayer.core.PlayerService;
import com.studio.emacs.emacsmusicplayer.R;
import com.studio.emacs.emacsmusicplayer.RecyclerTouchListener;
import com.studio.emacs.emacsmusicplayer.Song;

/**
 * Created by ganbhat on 6/26/2016.
 */

public class FragmentTab extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, RecyclerTouchListener.ClickListener {

    private static final int LOADER_ID = 0x20;
    private RecyclingCursorAdapter mAdapter;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_view,null);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);

        return view;
    }

    public FragmentTab(){
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        int displayType = bundle.getInt(TabSectionsAdapter.DISPLAY_TYPE);


        mAdapter = new RecyclingCursorAdapter(getActivity(), null);
        if(displayType == TabSectionsAdapter.DISPLAY_GRID) {
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
            recyclerView.setLayoutManager(mLayoutManager);
        } else if(displayType == TabSectionsAdapter.DISPLAY_LIST) {
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(0,0,20));
        recyclerView.setAdapter(mAdapter);
        RecyclerTouchListener listener = new RecyclerTouchListener(getActivity(), recyclerView, this);
        recyclerView.addOnItemTouchListener(listener);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String where = MediaStore.Audio.Media.IS_MUSIC + "=1";
        return new CursorLoader(getActivity(),
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                , null, where, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


    @Override
    public void onClick(View view, int position) {
        addToQueue((Song) view.getTag());
    }

    @Override
    public void onLongClick(View view, int pos){
        //do nothing
    }

    private void addToQueue(Song songData) {
        if(PlayerService.mSongsQueue.isEmpty()){

        }
        Toast.makeText(getActivity(),"Song added to queue",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), PlayerService.class);
        intent.setAction(PlayerService.ACTION_ENQUEE);
        intent.putExtra(PlayerService.TRACK, songData);
        getActivity().startService(intent);
    }

    public void setTypeAsList() {
        recyclerView = (RecyclerView)getActivity().findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mAdapter.notifyDataSetChanged();
    }

    public void setTypeAsGrid() {

        recyclerView = (RecyclerView)getActivity().findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(0,0,20));
        mAdapter.notifyDataSetChanged();
    }



}
