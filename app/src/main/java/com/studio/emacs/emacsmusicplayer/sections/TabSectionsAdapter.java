package com.studio.emacs.emacsmusicplayer.sections;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by ganbhat on 6/26/2016.
 */

public class TabSectionsAdapter extends FragmentPagerAdapter {

    // Tab Titles
    private String tabtitles[] = new String[] { "Recommendations","Songs", "Folder", "Album", "Artist","Genre", "Composer", "Years" };
    private static final int RECOMMENDATIONS_INDEX = 0;
    private static final int SONGS_INDEX = 1;
    private static final int FOLDER_INDEX = 2;
    private static final int ALBUM_INDEX = 3;
    private static final int ARTIST_INDEX = 4;
    private static final int GENRE_INDEX = 5;
    private static final int COMPOSER_INDEX = 6;
    private static final int YEARS_INDEX = 7;

    final int PAGE_COUNT = tabtitles.length;
    public static final int DISPLAY_GRID = 1;
    public static final int DISPLAY_LIST = 2;
    public static final String DISPLAY_TYPE = "DISPLAY_TYPE";
    public static final String POSITION = "POSITION";

    private int displayType = DISPLAY_LIST;

    public TabSectionsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        Fragment fragment = null;
        switch (position) {
            case RECOMMENDATIONS_INDEX:
            case SONGS_INDEX:
            case FOLDER_INDEX:
            case ALBUM_INDEX:
            case ARTIST_INDEX:
            case GENRE_INDEX:
            case COMPOSER_INDEX:
            case YEARS_INDEX:
                fragment = new FragmentTab();
                Bundle bundle = new Bundle();
                bundle.putInt(DISPLAY_TYPE, displayType);
                bundle.putInt(POSITION, position);
                fragment.setArguments(bundle);
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

}
