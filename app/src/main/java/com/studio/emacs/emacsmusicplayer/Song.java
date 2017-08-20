package com.studio.emacs.emacsmusicplayer;

import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ganbhat on 6/26/2016.
 */

public class Song implements Parcelable {
    public long mSongId;
    public String mTitile;
    public String mArtist;
    public String mAlbum;
    public long mAlbumId;
    public long mDuration;
    public Uri mUri;

    public Song(String title, long songId, String artist, String album, long albumId , long duration, Uri uri) {
        mTitile = title;
        mSongId = songId;
        mArtist = artist;
        mAlbum=album;
        mAlbumId = albumId;
        mDuration = duration;
        mUri = uri;
    }

    protected Song(Parcel in) {
        mSongId = in.readLong();
        mTitile = in.readString();
        mArtist = in.readString();
        mAlbum = in.readString();
        mAlbumId = in.readLong();
        mDuration = in.readLong();
        mUri = in.readParcelable(Uri.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mSongId);
        dest.writeString(mTitile);
        dest.writeString(mArtist);
        dest.writeString(mAlbum);
        dest.writeLong(mAlbumId);
        dest.writeLong(mDuration);
        dest.writeParcelable(mUri, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };


}
