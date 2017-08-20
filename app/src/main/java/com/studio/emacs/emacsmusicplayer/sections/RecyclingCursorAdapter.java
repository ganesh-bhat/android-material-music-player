package com.studio.emacs.emacsmusicplayer.sections;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.studio.emacs.emacsmusicplayer.R;
import com.studio.emacs.emacsmusicplayer.Song;
import com.studio.emacs.emacsmusicplayer.Utils;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * Created by ganbhat on 6/25/2016.
 */


public class RecyclingCursorAdapter extends RecyclerView.Adapter<RecyclingCursorAdapter.ViewHolder> {

    public static final String TITLE = "TITLE";
    public static final String SONG_ID = "SONG_ID";
    public static final String ALBUM = "ALBUM";
    public static final String ARTIST = "ARTIST";
    public static final String ALBUM_ID = "ALBUM_ID";
    public static final String DURATION = "DURATION";
    public static final String URI = "URI";


    // Because RecyclerView.Adapter in its current form doesn't natively
    // support cursors, we wrap a CursorAdapter that will do all the job
    // for us.
    CursorAdapter mCursorAdapter;

    Context mContext;

    public RecyclingCursorAdapter(Context context, Cursor c) {

        mContext = context;

        mCursorAdapter = new CursorAdapter(mContext, c, 0) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflator = LayoutInflater.from(context);
                View simpleItem = inflator.inflate(R.layout.tab_item_row,parent,false);
                return simpleItem;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView titleTv = (TextView)view.findViewById(R.id.row_item_title);
                TextView albumTv = (TextView)view.findViewById(R.id.row_item_sub_title);
                ImageView albumArtThumb = (ImageView)view.findViewById(R.id.row_album_art_thumb);

                String[] columanNames = cursor.getColumnNames();
                Log.i("ColumnNames", Arrays.asList(columanNames).toString());

                int songIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int albumIdIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int titileIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int albumIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artistIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int durationIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                long albumId = cursor.getLong(albumIdIndex);
                long songId = cursor.getLong(songIdIndex);
                String title = cursor.getString(titileIndex);
                String album = cursor.getString(albumIndex);
                String artist = cursor.getString(artistIndex);
                long duration = cursor.getLong(durationIndex);


                Bitmap bitmap = Utils.getAlbumThumb(context, albumId,50,50);
                albumArtThumb.setImageBitmap(bitmap);
                titleTv.setText(title);
                albumTv.setText(album);

                long thisId = cursor.getLong(idColumn);
                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, thisId);

                Song song = new Song(title,songId,artist,album,albumId,duration,contentUri);
                view.setTag(song);
            }
        };
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTv;
        TextView albumTv;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView)itemView.findViewById(R.id.row_item_title);
            albumTv = (TextView)itemView.findViewById(R.id.row_item_sub_title);
        }
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Passing the binding operation to cursor loader
        mCursorAdapter.getCursor().moveToPosition(position);
        mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Passing the inflater job to the cursor-adapter
        View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
        return new ViewHolder(v);
    }

    public void swapCursor(Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

}


