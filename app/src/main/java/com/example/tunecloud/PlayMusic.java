package com.example.tunecloud;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayMusic extends AppCompatActivity {

    Button previousButton,pauseButton,nextButton;
    TextView songnameTextview;
    SeekBar seekBar;

    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> songs;
    Thread threadSeekBar;
    String songName;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Now Playing");

        previousButton = (Button)findViewById(R.id.previousBtn);
        pauseButton = (Button)findViewById(R.id.pauseBtn);
        nextButton = (Button)findViewById(R.id.nextBtn);
        songnameTextview = (TextView) findViewById(R.id.txtSongLabel);
        seekBar = (SeekBar)findViewById(R.id.seekBar);


        threadSeekBar=new Thread(){
            @Override
            public void run() {
                int duration=mediaPlayer.getDuration();
                int position=0;

                while (position<duration){
                    try{
                        sleep(500);
                        position=mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(position);
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        };




        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i = getIntent();
        Bundle b = i.getExtras();


        songs = (ArrayList) b.getParcelableArrayList("songs");

        songName = songs.get(position).getName().toString();

        String SongName = i.getStringExtra("songname");
        songnameTextview.setText(SongName);
        songnameTextview.setSelected(true);

        position = b.getInt("pos",0);
        Uri u = Uri.parse(songs.get(position).toString());

        mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        threadSeekBar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i,
                                                                                boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setMax(mediaPlayer.getDuration());
                if(mediaPlayer.isPlaying()){
                    pauseButton.setBackgroundResource(R.drawable.playred);
                    mediaPlayer.pause();

                }
                else {
                    pauseButton.setBackgroundResource(R.drawable.pausered);
                    mediaPlayer.start();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%songs.size());
                Uri u = Uri.parse(songs.get( position).toString());
                // songNameText.setText(getSongName);
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);

                songName = songs.get(position).getName().toString();
                songnameTextview.setText(songName);

                try{
                    mediaPlayer.start();
                }catch(Exception e){}

            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //songNameText.setText(getSongName);
                mediaPlayer.stop();
                mediaPlayer.release();

                position=((position-1)<0)?(songs.size()-1):(position-1);
                Uri u = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(),u);
                songName = songs.get(position).getName().toString();
                songnameTextview.setText(songName);
                mediaPlayer.start();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
