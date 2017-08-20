package com.studio.emacs.emacsmusicplayer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.studio.emacs.emacsmusicplayer.core.PlayerService;
import com.studio.emacs.emacsmusicplayer.sections.SongQueueAdapter;

import java.util.ArrayList;
import java.util.List;

public class PlayerQueueActivity extends AppCompatActivity {

    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_queue);
        Toolbar toolbar = (Toolbar) findViewById(R.id.queue_toolbar);
        toolbar.setNavigationIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_back, null));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle("Queue");

        setSupportActionBar(toolbar);

        mListView = (ListView)findViewById(R.id.queue_listView);
        List<Song> queue = new ArrayList<>(PlayerService.mSongsQueue);
        mListView.setAdapter(new SongQueueAdapter(this, 0,queue));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_player_queue_cab,menu);
        return true;
    }


}
