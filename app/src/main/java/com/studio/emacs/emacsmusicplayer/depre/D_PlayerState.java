package com.studio.emacs.emacsmusicplayer.depre;

import android.net.Uri;

/**
 * Created by ganbhat on 9/16/2016.
 */

public abstract class D_PlayerState {
    private D_PlayerContext mPlayerContext;

    public D_PlayerState(D_PlayerContext context) {
        mPlayerContext = context;
    }

    public D_PlayerContext getContext(){
        return mPlayerContext;
    }

    public void prepare(Uri mediaUri){}
    public void stop(){}
    public void play(){}
    public void resume(){}
    public void focusDimmed(){}
    public void temporarilyLostFocus(){}
    public void playerErrored(){}
    public void audioFocusGain(){}
    public void onCompleted(){}
    public void onPrepared() {}

    public void init() {
        if(getContext().initPlayer()) {
            getContext().setState(new Initialized(getContext()));
        } else {
            getContext().setState(new NonInitialized(getContext()));
        }
    }

    public void audioFocusLoss(){
        //permanently lost audio focus
        getContext().cleanup();
        getContext().setState(new NonInitialized(getContext()));
    }


    /* different states of the state machine */
    public static class NonInitialized extends D_PlayerState {

        public NonInitialized(D_PlayerContext context) {
            super(context);
        }

        /** shouldnt happen, just an error handling scenario */
        public void prepare(Uri mediaUri){
            init();
            getContext().setState(new Initialized(getContext()));
            getContext().getState().prepare(mediaUri);
        }

    }

    public static class Initialized extends D_PlayerState {

        public  Initialized(D_PlayerContext context) {
            super(context);
        }

        public void prepare(Uri mediaUri){
            getContext().prepare(mediaUri);
        }

        @Override
        public void onPrepared() {
            getContext().setState(new Stopped(getContext()));
        }

    }

    public static class Stopped extends D_PlayerState {

        public  Stopped(D_PlayerContext context) {
            super(context);
        }
      }

    public static class Playing extends D_PlayerState {
        public  Playing(D_PlayerContext context) {
            super(context);
        }

    }

    public static class DimmedAudioFocus extends D_PlayerState {
        public  DimmedAudioFocus(D_PlayerContext context) {
            super(context);
        }

    }

    public static class Paused extends D_PlayerState {
        public  Paused(D_PlayerContext context) {
            super(context);
        }

    }


}