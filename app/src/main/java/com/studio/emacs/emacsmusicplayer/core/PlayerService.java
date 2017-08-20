package com.studio.emacs.emacsmusicplayer.core;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.studio.emacs.emacsmusicplayer.Song;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by ganbhat on 6/14/2016.
 */

public class PlayerService extends Service
        implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener{

    public static enum PlayerState {
        NON_INITIALIZED, INITIALIZED, STOPPED, PLAYING, PAUSED
    }

    private static final String TAG = "PlayerService";
    public static PlayerState mPlayerState;
    public static Song mCurrentlyPlayingTrack;
    public static long mCurrentProgress;


    public class PlayerBinder extends Binder {
        PlayerService getService() {
            return PlayerService.this;
        }
    }


    /* update notifications */
    public static final String CURRENT_PROGRESS = "CURRENT_PROGRESS";
    public static final String ACTION_PLAYER_PROGRESS_UPDATE = "ACTION_PLAYER_PROGRESS_UPDATE";
    public static final String ACTION_PLAYER_STATE_CHANGED = "ACTION_PLAYER_STATE_CHANGED";
    public static final String TRACK = "TRACK";

    /* music player controls */
    public static final String ACTION_PLAY = "PLAY";
    public static final String ACTION_PAUSE = "PAUSE";
    public static final String ACTION_STOP = "STOP";
    public static final String ACTION_NEXT = "NEXT";
    public static final String ACTION_PREVIOUS = "PREVIOUS";
    public static final String ACTION_SEEK = "SEEK";
    public static final String ACTION_ENQUEE = "ENQUEE";
    public static final String ACTION_DEQUEE = "ACTION_DEQUEE";
    public static final String ACTION_PLAY_FROM_PLAYLIST = "PLAY_FROM_PLAYLIST";
    public static final String ACTION_SHUFFLE = "SHUFFLE";
    public static final String ACTION_REPETE_ONCE = "REPETE_ONCE";
    public static final String ACTION_REPETE_ALL = "REPETE_ALL";
    public static final String ACTION_NO_REPETE = "NO_REPETE";

    /* ation data */
    public static final String DATA_SEEK_INDEX = "DATA_SEEK_INDEX";
    public static final String DATA_PLAYLIST_ID = "DATA_PLAYLIST_ID";
    public static final String DATA_SONG_URI = "DATA_SONG_URI";
    public static final String DATA_TITLE = "DATA_TITLE";
    public static final String DATA_DISPLAY_NAME = "DATA_DISPLAY_NAME";
    public static final String DATA_ALBUM = "DATA_ALBUM";
    
    private final IBinder playerBinder = new PlayerBinder();
    /* TODO, need to have a content provider and share this */
    public static List<Song> mSongsQueue = Collections.synchronizedList(new ArrayList<Song>());

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private AudioManager mAudioManager;
    private int mCurrentlyPlaying = 0;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return playerBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initMediaPlayer();
    }

    private void init() {
        //load the queue from persistant storage

    }

    /* managing input events */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null) {
            if(ACTION_PLAY.equals(intent.getAction())){
                playMusic();
            } else if(ACTION_PAUSE.equals(intent.getAction())){
                pauseMusic();
            } else if(ACTION_NEXT.equals(intent.getAction())){
                nextSong();
            }else if(ACTION_STOP.equals(intent.getAction())) {
                stopMusic();
            } else if(ACTION_SEEK.equals(intent.getAction())){
                int seekIndex = intent.getIntExtra(DATA_SEEK_INDEX,0);
                seekMusic(seekIndex);
            }else if(ACTION_PREVIOUS.equals(intent.getAction())) {
                previousSong();
            } else if(ACTION_NEXT.equals(intent.getAction())) {
                nextSong();
            } else if(ACTION_ENQUEE.equals(intent.getAction())) {
                Song song = (Song)intent.getParcelableExtra(TRACK);
                enqueeSong(song);
            }  else if(ACTION_DEQUEE.equals(intent.getAction())) {
                //TODO broken - datatype needs to be fixed
                Uri songUri = (Uri)intent.getParcelableExtra(DATA_SONG_URI);
                removeFromQueue(songUri);
            } else if(ACTION_PLAY_FROM_PLAYLIST.equals(intent.getAction())) {
                String playListId = intent.getStringExtra(DATA_PLAYLIST_ID);
                enqueePlaylist(playListId);
            } else if(ACTION_REPETE_ONCE.equals(intent.getAction())) {
                repeteCurrentSong();
            } else if(ACTION_REPETE_ALL.equals(intent.getAction())) {

            } else if(ACTION_NO_REPETE.equals(intent.getAction())) {

            }
            sendPlayerStateChangedIntent();
        }

        return START_STICKY;
    }

    /**
     * Initialization
     */
    public void initMediaPlayer() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnErrorListener(PlayerService.this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
    }

    /** control media playback based on queue - drives player*/
    @Override
    public void onCompletion(MediaPlayer mp) {
        mMediaPlayer.reset();
        mPlayerState = PlayerState.STOPPED;
        sendPlayerStateChangedIntent();
        nextSong();
    }


    /* methods to work on queue */
    public void enqueeSong(Song song) {
        if(mSongsQueue.isEmpty()) {
            mCurrentlyPlayingTrack = song;
        }
        mSongsQueue.add(song);
    }

    private void enqueePlaylist(String playListId) {
        //read playlist and add to queue

    }

    public void removeFromQueue(Uri songUri) {
        mSongsQueue.remove(songUri);
    }

    /* music player functionalities */

    public void playMusic() {
        if(mPlayerState == PlayerState.PLAYING) {
            /* in case play is pressed multiple times */
            pauseMusic();

        } else if(mPlayerState == PlayerState.PAUSED && mMediaPlayer!=null) {
            try{
                mMediaPlayer.start();
                mPlayerState = PlayerState.PLAYING;
                startNotificationSender();
            } catch(IllegalStateException e) {
                mMediaPlayer = null;
                initMediaPlayer();
                playMusic();
            }
        } else {
            if(!mSongsQueue.isEmpty()) {
                if(mSongsQueue.size() > mCurrentlyPlaying) {
                    Song song = mSongsQueue.get(mCurrentlyPlaying);
                    mCurrentlyPlayingTrack = song;
                    try {
                        mMediaPlayer.reset();
                        mMediaPlayer.setDataSource(getApplicationContext(), song.mUri);
                        mMediaPlayer.prepareAsync();
                    } catch (Exception e) {
                        Log.e(TAG, "Exception while preparing the song to play",e);
                    }
                } else {
                    Toast.makeText(this,"Current playing index "+mCurrentlyPlaying+" is more than queue size",Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this,"Please add songs to music queue, by clicking on them",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void stopNotificationSender() {

    }

    private void sendProgressNotification() {

    }


    private void startNotificationSender() {
        
    }

    /**
     * Play when ready, async ,media play
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if(mMediaPlayer!=null) {
                mMediaPlayer.start();
                mPlayerState = PlayerState.PLAYING;
                sendPlayerStateChangedIntent();
            } else {
                initMediaPlayer();
                playMusic();
            }
        } else {
            // could not get audio focus.
            //TODO - throw some toast indicating could not play. we can ask user if they want to play anyways
            //will wait for audio focus gain to start playing music
            cleanup();
            mPlayerState = PlayerState.NON_INITIALIZED;
        }
    }


    public void pauseMusic() {
        mMediaPlayer.pause();
        mPlayerState = PlayerState.PAUSED;
        sendProgressNotification();
        stopNotificationSender();
    }

    public void resumeMusic() {
        if(mPlayerState == PlayerState.PAUSED
                || mPlayerState == PlayerState.STOPPED
                || mPlayerState == PlayerState.INITIALIZED) {
            mMediaPlayer.start();
           mPlayerState = PlayerState.PLAYING;
            sendProgressNotification();
            startNotificationSender();
        }
    }


    public void stopMusic() {
        if(mPlayerState!=PlayerState.STOPPED) {
            if(mMediaPlayer!=null) {
                mMediaPlayer.stop();
            }
            mPlayerState = PlayerState.STOPPED;
            sendProgressNotification();
            stopNotificationSender();
        }
    }

    public void nextSong() {
        if(!mSongsQueue.isEmpty() && mSongsQueue.size()-1 > mCurrentlyPlaying) {
            mCurrentlyPlaying++;
            stopMusic();
            playMusic();
        }
    }

    public void previousSong() {
        if(!mSongsQueue.isEmpty() && mCurrentlyPlaying>0) {
            mCurrentlyPlaying--;
            stopMusic();
            playMusic();
        }
    }

    /** music player options */
    private void seekMusic(int seekIndex) {
        mMediaPlayer.seekTo(seekIndex);
        sendProgressNotification();
    }

    private void repeteCurrentSong() {
        mMediaPlayer.setLooping(true);
    }

    public void shuffle() {
        Collections.shuffle(mSongsQueue);
    }


    /* manage conditions related to system level probelms and errors */
    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                //You have lost the audio focus for a presumably long time. You must stop all audio playback
                //release media player
                if (mMediaPlayer.isPlaying())  {
                    mMediaPlayer.stop();
                    mPlayerState = PlayerState.NON_INITIALIZED;
                }
                mMediaPlayer.release();
                mMediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                //got back the audio focus
                if (mMediaPlayer == null) initMediaPlayer();
                else if (mPlayerState == PlayerState.PAUSED ||mPlayerState == PlayerState.STOPPED) {
                    mMediaPlayer.start();
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                    mPlayerState = PlayerState.PLAYING;
                } else if(mPlayerState == PlayerState.PLAYING){
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                    mPlayerState = PlayerState.PLAYING;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                //You have temporarily lost audio focus, but should receive it back shortly. You must stop all audio playback, but you can keep your resources
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    //let the state be playing
                    mPlayerState = PlayerState.PAUSED;
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //play with lower voulme
                if (mPlayerState == PlayerState.PLAYING) {
                    mMediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        cleanup();
        mPlayerState = PlayerState.NON_INITIALIZED;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG,"Error playing music:"+what);
        mPlayerState = PlayerState.NON_INITIALIZED;
        mp.release();
        cleanup();
        return false;
    }

    public void cleanup(){
        if(mMediaPlayer !=null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    
    public void sendPlayerStateChangedIntent() {
        Intent intent = new Intent(ACTION_PLAYER_STATE_CHANGED);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}
