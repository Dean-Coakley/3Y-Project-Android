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

public class MusicPlayer extends Activity{

//    private Activity activity;
//    private MainActivity main;

//    public MusicPlayer(Activity activity, MainActivity main){
//        this.activity = activity;
//        this.main = main;
//    }

//My Shitty attempt of OO that will not work thamk

    private Button play;
    private Button pause;
    private EditText urlET;
    public MediaPlayer mediaPlayer = null;

    public void playMusic(){

        urlET = (EditText)findViewById(R.id.music_link) ;
        final String url = urlET.getText().toString() ; // your URL here urlET.getText().toString()


        play = (Button) findViewById(R.id.play_music) ;
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Playing Mm888888888", url);
                try {
                    if(mediaPlayer != null && mediaPlayer.isPlaying())
                    {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                    else{
                        mediaPlayer =  new MediaPlayer();

                        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            SoundPool sp = new SoundPool.Builder().setAudioAttributes(new AudioAttributes.Builder()
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                    .setUsage(AudioAttributes.USAGE_MEDIA).build())
                                    .setMaxStreams(100).build();
                        }
                        mediaPlayer.setDataSource("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
                        mediaPlayer.prepare();
                        mediaPlayer.prepareAsync();
                        mediaPlayer.start();
                    }

                } catch (Exception e) {
                    e.printStackTrace();}
            }
        });
    }
}
