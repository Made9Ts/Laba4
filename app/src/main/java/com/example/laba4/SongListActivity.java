package com.example.laba4;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android. widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class SongListActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private ListView songListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_list);

        dbHelper = new DatabaseHelper(this);
        songListView = findViewById(R.id.songListView);

        loadSongs();
    }

    private void loadSongs() {
        Cursor cursor = dbHelper.getAllSongs();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[]{"artist", "title"},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );
        songListView.setAdapter(adapter);
    }
}