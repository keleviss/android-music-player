package com.viss.mymusicplayerfirst;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<Song> songsList = new ArrayList<>();
    ImageView songIcon;
    TextView songTitle;
    ImageView playPauseBtn;
    Song currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        songIcon = findViewById(R.id.songPlayingIcon);
        songTitle = findViewById(R.id.songPlayingTitle);
        Typeface typeface = ResourcesCompat.getFont(this, R.font.gothammedium);
        songTitle.setTypeface(typeface);
        playPauseBtn = findViewById(R.id.playPause);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.action_title));

        if(!checkPermission()) {
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };

        //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        @SuppressLint("Recycle") Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null,null);
        while (cursor.moveToNext()) {
            Song songData = new Song(cursor.getString(1),cursor.getString(0), cursor.getString(2));
            if (new File(songData.getPath()).exists())
                songsList.add(songData);
        }

        if (songsList.size() == 0) {
            noMusicTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new SongListAdapter(songsList, getApplicationContext()));
        }

        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    if (mediaPlayer.isPlaying()) {
                        if (songTitle.getText() != songsList.get(MyMediaPlayer.currentIndex).title ) {
                            currentSong = songsList.get(MyMediaPlayer.currentIndex);
                            songTitle.setText(currentSong.getTitle());
                        }
                        if (!songTitle.isSelected())
                            songTitle.setSelected(true);
                        playPauseBtn.setOnClickListener(v -> pausePlay());
                        playPauseBtn.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                        songIcon.setRotation(x++);
                    } else {
                        if (songTitle.isSelected())
                            songTitle.setSelected(false);
                        playPauseBtn.setOnClickListener(v -> pausePlay());
                        playPauseBtn.setImageResource(R.drawable.baseline_play_circle_outline_24);
                        songIcon.setRotation(0);
                        x = 0;
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });
    }

    boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED;

    }

    void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Read permission is required, please allow from settings", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView != null) {
            recyclerView.setAdapter(new SongListAdapter(songsList, getApplicationContext()));
        }
    }

    void pausePlay() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        else
            mediaPlayer.start();
    }

}
