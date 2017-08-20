package com.studio.emacs.emacsmusicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.studio.emacs.emacsmusicplayer.core.PlayerService;

import java.lang.ref.WeakReference;
import java.text.DecimalFormat;

/**
 * Represents the actual music player
 */
public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "PlayerActivity";
    private static final long MEDIA_PROGRESS_UPDATE_DURATION = 5000 ;

    private Animation fab_open;
    private Animation fab_close;
    private Animation rotate_forward;
    private Animation rotate_backward;

    private LinearLayout mFavoriteActionFab;
    private LinearLayout mEditMetadataFab;
    private LinearLayout mActionLyricsFab;
    private LinearLayout mActionEqualizerFab;
    private FloatingActionButton mHoverActionFab;


    private boolean isFabOpen = false;
    private ImageView bottomBarPlay;
    private ImageView bottomBarNext;
    private ImageView bottomBarPrevious;
    private ImageView bottomBarShuffle;
    private ImageView bottomBarRepete;

    private BroadcastReceiver playerStateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updatePlayerControls();
                }
            });
        }
    };

    private BroadcastReceiver playerProgressUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    long currentProgress = intent.getLongExtra(PlayerService.CURRENT_PROGRESS, 0);
                    double progressInMins = ((double)(currentProgress))/(1000*60 );
                    progressTv.setText(new DecimalFormat("##.##").format(progressInMins));
                }
            });
        }
    };



    private ImageView albumArt;
    private TextView subTitileTv;
    private TextView titleTv;
    private RatingBar ratingBarRb;
    private SeekBar trackSeekbarSb;
    private TextView durationTv;
    private TextView progressTv;

    private void updatePlayerControls() {
        if(PlayerService.mPlayerState == PlayerService.PlayerState.PLAYING) {
            bottomBarPlay.setImageResource(R.drawable.lb_ic_pause);
        } else {
            bottomBarPlay.setImageResource(R.drawable.lb_ic_play);
        }

        if(PlayerService.mCurrentlyPlayingTrack!=null && PlayerService.mCurrentlyPlayingTrack.mAlbumId>0){
            new AlbumArtLoader(this, albumArt).execute((Void[])null);
            titleTv.setText(PlayerService.mCurrentlyPlayingTrack.mTitile);
            subTitileTv.setText(PlayerService.mCurrentlyPlayingTrack.mAlbum);
            double durationInMins = ((double)(PlayerService.mCurrentlyPlayingTrack.mDuration))/(1000*60 );
            durationTv.setText(new DecimalFormat("##.##").format(durationInMins));
        }
    }


    private static class AlbumArtLoader extends AsyncTask<Void,Void,Bitmap>{

        WeakReference<ImageView> mAlbumArtImageView;
        WeakReference<Context> mContext;

        AlbumArtLoader(Context context, ImageView albumArtIv) {
            mContext = new WeakReference<Context>(context);
            mAlbumArtImageView = new WeakReference<ImageView>(albumArtIv);
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap bitmap = Utils.getAlbumThumb(mContext.get(),PlayerService.mCurrentlyPlayingTrack.mAlbumId, 600,600 );
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView iv = mAlbumArtImageView.get();
            if(iv!=null && bitmap!=null) {
                iv.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player);
        super.onCreate(savedInstanceState);
        albumArt = (ImageView) findViewById(R.id.player_album_art);
        titleTv = (TextView) findViewById(R.id.player_title);
        subTitileTv = (TextView) findViewById(R.id.player_subtitile);
        durationTv = (TextView) findViewById(R.id.player_track_duration);
        progressTv = (TextView) findViewById(R.id.player_track_progress);
        ratingBarRb = (RatingBar) findViewById(R.id.player_ratingbar);
        trackSeekbarSb = (SeekBar) findViewById(R.id.player_track_seeker);


        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.roate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        mFavoriteActionFab = (LinearLayout)findViewById(R.id.favoriteActionFabHolder);
        mEditMetadataFab = (LinearLayout)findViewById(R.id.editMetadataFabHolder);
        mActionLyricsFab = (LinearLayout)findViewById(R.id.actionLyricsFabHolder);
        mActionEqualizerFab = (LinearLayout)findViewById(R.id.actionEqualizerFabHolder);
        mHoverActionFab = (FloatingActionButton)findViewById(R.id.hoverActionFab);

        mFavoriteActionFab.setOnClickListener(this);
        mEditMetadataFab.setOnClickListener(this);
        mActionLyricsFab.setOnClickListener(this);
        mActionEqualizerFab.setOnClickListener(this);
        mHoverActionFab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.playerToolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_back, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        bottomBarPlay = (ImageView)findViewById(R.id.bottomBarPlay);
        bottomBarPlay.setOnClickListener(this);
        bottomBarNext = (ImageView)findViewById(R.id.bottomBarNext);
        bottomBarNext.setOnClickListener(this);
        bottomBarPrevious = (ImageView)findViewById(R.id.bottomBarPrevious);
        bottomBarPrevious.setOnClickListener(this);
        bottomBarShuffle = (ImageView)findViewById(R.id.bottomBarShuffle);
        bottomBarShuffle.setOnClickListener(this);
        bottomBarRepete=(ImageView)findViewById(R.id.bottomBarRepete);
        bottomBarRepete.setOnClickListener(this);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_player, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.menu_queue:
                Intent intent = new Intent(this,PlayerQueueActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void animateFAB(){

        if(isFabOpen){

            mHoverActionFab.startAnimation(rotate_backward);
            mFavoriteActionFab.startAnimation(fab_close);
            mEditMetadataFab.startAnimation(fab_close);
            mActionLyricsFab.startAnimation(fab_close);
            mActionEqualizerFab.startAnimation(fab_close);


            mFavoriteActionFab.setClickable(false);
            mEditMetadataFab.setClickable(false);
            mActionLyricsFab.setClickable(false);
            mActionEqualizerFab.setClickable(false);

            isFabOpen = false;
            Log.d(TAG, "close");

        } else {

            mHoverActionFab.startAnimation(rotate_forward);
            mFavoriteActionFab.startAnimation(fab_open);
            mEditMetadataFab.startAnimation(fab_open);
            mActionLyricsFab.startAnimation(fab_open);
            mActionEqualizerFab.startAnimation(fab_open);


            mFavoriteActionFab.setClickable(true);
            mEditMetadataFab.setClickable(true);
            mActionLyricsFab.setClickable(true);
            mActionEqualizerFab.setClickable(true);
            isFabOpen = true;
            Log.d(TAG,"open");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favoriteActionFab:
                animateFAB();
                break;
            case R.id.editMetadataFab:
                animateFAB();
                break;
            case R.id.actionLyricsFab:
                animateFAB();
                break;
            case R.id.actionEqualizerFab:
                animateFAB();
                break;
            case R.id.hoverActionFab:
                animateFAB();
                break;
            case R.id.bottomBarPlay:

                playPauseMusic();
                break;
            case R.id.bottomBarPrevious:
                previousSong();
                break;
            case R.id.bottomBarNext:
                nextSong();
                break;
            case R.id.bottomBarRepete:

                break;
            case R.id.bottomBarShuffle:
                shuffleSongs();
                break;
        }
    }

    private void shuffleSongs() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_SHUFFLE);
        startService(intent);
    }

    private void nextSong() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_NEXT);
        startService(intent);
    }

    private void playPauseMusic() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PLAY);
        startService(intent);
    }

    private void previousSong() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PREVIOUS);
        startService(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updatePlayerControls();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayerService.ACTION_PLAYER_STATE_CHANGED);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(playerStateChangeReceiver, filter);


        IntentFilter filter2 = new IntentFilter();
        filter2.addAction(PlayerService.ACTION_PLAYER_PROGRESS_UPDATE);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(playerProgressUpdateReceiver, filter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(playerStateChangeReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(playerProgressUpdateReceiver);
    }
}
