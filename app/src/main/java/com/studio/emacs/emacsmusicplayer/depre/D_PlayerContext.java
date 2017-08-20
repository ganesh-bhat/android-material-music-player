package com.studio.emacs.emacsmusicplayer.depre;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Created by ganbhat on 9/14/2016.
 */

public class D_PlayerContext implements MediaPlayer.OnErrorListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "MediaPlayerContext";
    private static final int SYSTEM_AUDIO_BUSY = 1<<2;
    private D_PlayerState mPlayerState = new D_PlayerState.NonInitialized(this);
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private AudioManager mAudioManager;
    private WeakReference<Context> mContext;
    private Uri mCurrentSongUri;

    public D_PlayerContext(Context context) {
        mContext = new WeakReference<Context>(context);
    }

    public Context getSystemContext() {
        if(mContext!=null) {
            return mContext.get();
        }
        return null;
    }

    public int requestFocus(){
        if(mAudioManager == null) {
            Context context = getSystemContext();
            if(context!=null) {
                mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                int result = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
                return result;
            }
        }
        return AudioManager.AUDIOFOCUS_REQUEST_FAILED;
    }

    public boolean initPlayer() {
        Context context = getSystemContext();
        if(context!=null) {
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setWakeMode(context , PowerManager.PARTIAL_WAKE_LOCK);
            mMediaPlayer.setOnErrorListener(this);
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnCompletionListener(this);
            return true;
        }
        return false;
    }


    public void setState(D_PlayerState state) {
        mPlayerState = state;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlayerState.onPrepared();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mPlayerState.onCompleted();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                mPlayerState.audioFocusLoss();

                break;
            case AudioManager.AUDIOFOCUS_GAIN:
                mPlayerState.audioFocusGain();

                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                //You have temporarily lost audio focus, but should receive it back shortly. You must stop all audio playback, but you can keep your resources
                mPlayerState.temporarilyLostFocus();
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                //play with lower voulme
                mPlayerState.focusDimmed();
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG,"Error playing music:"+what);
        mPlayerState.playerErrored();
        return false;
    }

    public void play(Uri mediaUri) {
        mPlayerState.prepare(mediaUri);
    }

    public void sendNotification(Intent intent){
        LocalBroadcastManager.getInstance(getSystemContext()).sendBroadcast(intent);
    }


    public void cleanup() {
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void prepare(Uri songUri) {
        if(mMediaPlayer!=null){
            try {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(getSystemContext(), songUri);
                mMediaPlayer.prepareAsync();
            } catch(IOException ioe) {
                Log.e(TAG,"IO exceptiion",ioe);
            }
        } else {
            initPlayer();
            prepare(songUri);
        }
    }

    public D_PlayerState getState() {
        return mPlayerState;
    }
}
