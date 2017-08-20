package com.studio.emacs.emacsmusicplayer;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.studio.emacs.emacsmusicplayer.core.PlayerService;
import com.studio.emacs.emacsmusicplayer.sections.FragmentTab;
import com.studio.emacs.emacsmusicplayer.sections.TabSectionsAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0x25;
    private static final String TAG = "MainActivity";

    private boolean isFabOpen;
    private FloatingActionButton nextFab;
    private FloatingActionButton playFab;
    private FloatingActionButton prevFab;
    private FloatingActionButton nextNavigatorFab;
    private Animation fab_open;
    private Animation fab_close;
    private TextView bottomBarSongName;
    private TextView bottomBarAlbumName;
    private ImageView albumArtThumb;

    private boolean isChecked = false;
    private TabLayout tabLayout;
    private TabSectionsAdapter tabSectionsAdapter;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        toolbar.setLogo(R.drawable.ic_app_icon);

        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        //toolbar.setLogo();
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);


        tabSectionsAdapter = new TabSectionsAdapter(getSupportFragmentManager());
        if(android.os.Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //has permission
                viewPager.setAdapter(tabSectionsAdapter);
                tabLayout.setupWithViewPager(viewPager);

                Intent intent = new Intent(this, PlayerService.class);
                startService(intent);

            } else {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
            }
        } else if(android.os.Build.VERSION.SDK_INT < 23) {
            //already has permision
            viewPager.setAdapter(tabSectionsAdapter);
            tabLayout.setupWithViewPager(viewPager);

            Intent intent = new Intent(this, PlayerService.class);
            startService(intent);
        }

        nextFab = (FloatingActionButton) findViewById(R.id.nextFab);
        playFab = (FloatingActionButton) findViewById(R.id.playFab);
        prevFab = (FloatingActionButton) findViewById(R.id.previousFab);
        nextNavigatorFab = (FloatingActionButton) findViewById(R.id.nextNavigatorFab);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);


        bottomBarSongName = (TextView)findViewById(R.id.bottomBarSongName);
        bottomBarAlbumName = (TextView)findViewById(R.id.bottomBarAlbumName);
        albumArtThumb = (ImageView)findViewById(R.id.albumArtThumb);


        RelativeLayout currentlyPlaying = (RelativeLayout) findViewById(R.id.currentlyPlaying);


        nextFab.setOnClickListener(this);
        nextNavigatorFab.setOnClickListener(this);
        prevFab.setOnClickListener(this);
        playFab.setOnClickListener(this);
        currentlyPlaying.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent(this, PlayerService.class);
        startService(intent);

        updatePlayerControls();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayerService.ACTION_PLAYER_STATE_CHANGED);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(playerStateChangeReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(playerStateChangeReceiver);
    }

    private void updatePlayerControls(){
        if(PlayerService.mCurrentlyPlayingTrack !=null) {
            if(PlayerService.mCurrentlyPlayingTrack.mTitile!=null) {
                bottomBarSongName.setText(PlayerService.mCurrentlyPlayingTrack.mTitile);
            }
            if(PlayerService.mCurrentlyPlayingTrack.mAlbum!=null) {
                bottomBarAlbumName.setText(PlayerService.mCurrentlyPlayingTrack.mTitile);
            }

            if(PlayerService.mCurrentlyPlayingTrack.mAlbum!=null) {
                bottomBarAlbumName.setText(PlayerService.mCurrentlyPlayingTrack.mTitile);
            }
            if(PlayerService.mIsPlayerStatePlaying) {
                playFab.setImageResource(R.drawable.ic_action_pause);
            } else {
                playFab.setImageResource(R.drawable.ic_action_play);
            }
            Bitmap bm = Utils.getAlbumThumb(this,PlayerService.mCurrentlyPlayingTrack.mAlbumId, 50,50);
            albumArtThumb.setImageBitmap(bm);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission was granted!
                    // Perform the action

                } else {
                    // Permission was denied
                    // :(
                    // Gracefully handle the denial
                    Toast.makeText(this,"Permission denined, cant show music files",Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }

            // Add additional cases for other permissions you may have asked for
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_toggle_grid_list:
                if(isChecked) {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_toggle_list));
                    FragmentTab fragment = getTabFragment(tabLayout.getSelectedTabPosition());
                    if(fragment !=null) {
                        fragment.setTypeAsList();
                    }
                    Toast.makeText(this,"list mode",Toast.LENGTH_SHORT).show();
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_toggle_grid));
                    FragmentTab fragment = getTabFragment(tabLayout.getSelectedTabPosition());
                    if(fragment !=null) {
                        fragment.setTypeAsGrid();
                    }
                    Toast.makeText(this,"grid mode",Toast.LENGTH_SHORT).show();
                }
                isChecked = !isChecked;

                return true;
            case R.id.action_settings:
                return true;
            default:
                return true;
        }


    }

    private FragmentTab getTabFragment(int selectedTabPosition) {
        for(Fragment fragment:getVisibleFragments()){
            Bundle bundle = fragment.getArguments();
            if(bundle!=null && bundle.getInt(TabSectionsAdapter.POSITION,-1) == selectedTabPosition && fragment instanceof  FragmentTab ) {
                return (FragmentTab)fragment;
            }
        }
        return null;
    }

    public List<Fragment> getVisibleFragments() {
        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        if (allFragments == null || allFragments.isEmpty()) {
            return Collections.emptyList();
        }

        List<Fragment> visibleFragments = new ArrayList<Fragment>();
        for (Fragment fragment : allFragments) {
            if (fragment.isVisible()) {
                visibleFragments.add(fragment);
            }
        }
        return visibleFragments;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playFab:
                playMusic();
                break;
            case R.id.nextFab:
                nextSong();
                animateFAB();
                break;
            case R.id.previousFab:
                previousSong();
                animateFAB();
                break;
            case R.id.nextNavigatorFab:
                animateFAB();
                break;
            case R.id.currentlyPlaying:
                Intent intent = new Intent(this,PlayerActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void nextSong() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_NEXT);
        startService(intent);
    }

    private void playMusic() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PLAY);
        startService(intent);
    }

    private void previousSong() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PREVIOUS);
        startService(intent);
    }


    public void animateFAB(){

        if(isFabOpen){
            nextNavigatorFab.setImageResource(R.drawable.ic_action_next_song);
            nextFab.startAnimation(fab_close);
            prevFab.startAnimation(fab_close);

            nextFab.setClickable(false);
            prevFab.setClickable(false);


            isFabOpen = false;
            Log.d(TAG, "close");

        } else {

            nextNavigatorFab.setImageResource(R.drawable.ic_close_black);
            nextFab.startAnimation(fab_open);
            prevFab.startAnimation(fab_open);
            nextFab.setClickable(true);
            prevFab.setClickable(true);

            isFabOpen = true;
            Log.d(TAG,"open");

        }
    }
}
