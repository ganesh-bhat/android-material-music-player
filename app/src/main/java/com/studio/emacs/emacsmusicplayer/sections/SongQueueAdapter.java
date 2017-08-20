package com.studio.emacs.emacsmusicplayer.sections;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.emacs.emacsmusicplayer.core.PlayerService;
import com.studio.emacs.emacsmusicplayer.R;
import com.studio.emacs.emacsmusicplayer.Song;
import com.studio.emacs.emacsmusicplayer.Utils;

import java.util.List;

/**
 * Created by ganbhat on 6/27/2016.
 */

public class SongQueueAdapter extends ArrayAdapter<Song> {
    private final LayoutInflater mInflator;

    public SongQueueAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
        mInflator = LayoutInflater.from(context);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            view =  mInflator.inflate(R.layout.queue_list_item,parent,false);
        }

        view.setBackgroundColor(getContext().getResources().getColor(R.color.cardview_light_background));

        ImageView albumThumb = (ImageView)view.findViewById(R.id.queueAlbumThumb);
        TextView songName = (TextView)view.findViewById(R.id.queueSongTv);
        TextView albumName = (TextView)view.findViewById(R.id.queueAlbumTv);
        ImageView currentlyPlaying = (ImageView)view.findViewById(R.id.queue_currently_playing);

        Song song = getItem(position);

        Bitmap bitmap = Utils.getAlbumThumb(getContext(),song.mAlbumId,50,50);
        songName.setText(song.mTitile);
        albumName.setText(song.mAlbum);
        albumThumb.setImageBitmap(bitmap);

        if(song != PlayerService.mCurrentlyPlayingTrack) {
            currentlyPlaying.setVisibility(View.INVISIBLE);
        }

        return view;
    }




}
