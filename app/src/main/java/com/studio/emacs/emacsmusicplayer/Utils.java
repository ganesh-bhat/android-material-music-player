package com.studio.emacs.emacsmusicplayer;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;

/**
 * Created by ganbhat on 9/14/2016.
 */

public class Utils {
    public static Bitmap getAlbumThumb(Context context, long albumId, int sizeX, int sizeY) {
        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");
        //Uri sArtworkUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(
                    context.getContentResolver(), albumArtUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, sizeX, sizeY, true);

        } catch (FileNotFoundException exception) {

            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_album_art_placeholder);
            Log.e("Player","Exception getting the album FileNotFoundException");
        } catch (Exception e) {
            bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_album_art_placeholder);
            Log.e("Player","Exception getting the album other exception");
        }
        return bitmap;
    }
}
