package com.SDH3.VCA;

import android.app.Activity;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;

/**
 * Created by Cian on 12/11/2017.
 */

public class MusicPlayer extends AppCompatActivity {

    private Activity activity;
    private MainActivity main;

    public MusicPlayer(Activity activity, MainActivity main) {
        this.activity = activity;
        this.main = main;
    }

//My Shitty attempt of OO that will not work thamk

//    Not really sure how to reference it in the main because methods need to be static when I'm calling it in onCreate..
//    but once playMusic is made static then all app actiity such as findViewById are unavailable. Halp.

    
    private Button pause;
    public MediaPlayer mediaPlayer = new MediaPlayer();

    private EditText urlET = (EditText) findViewById(R.id.music_link);
    final String url = urlET.getText().toString(); // your URL here urlET.getText().toString()
    private Button play = (Button) findViewById(R.id.play_music);

    public void playMusic(){

        play = (Button) findViewById(R.id.play_music) ;
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlET = (EditText)findViewById(R.id.music_link) ;
                String url = urlET.getText().toString() ; // your URL toString

                try {
                    //MediaPlayer will be in a play state but will give a IllegalStateException when asked to be played
                    if(mediaPlayer != null && mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    else{

                        //Instantiate your MediaPlayer
                        mediaPlayer = new MediaPlayer();
                        //This allows the device to be locked and to continue to play music
                        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            SoundPool sp = new SoundPool.Builder().setAudioAttributes(new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA).build())
                                    .setMaxStreams(1).build();
                        }
                        //Points the MediaPlayer at the link to play
                        mediaPlayer.setDataSource(url);//Url may look like this "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                        //Loads data source set abouve
                        mediaPlayer.prepare();

                        //Start Playing your .mp3 file
                        mediaPlayer.start();

                    }

                } catch (Exception e) {
                    e.printStackTrace();}
            }
        });
    }
}