package com.SDH3.VCA;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class VoiceManager {

    private int speechCode;

    private Activity activity;
    private MainActivity main;

    public VoiceManager(Activity activity, MainActivity main, int code){
        this.activity = activity;
        this.main = main;
        speechCode = code;
    }

    /**
     * Showing google speech input dialog
     */
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.speech_prompt);
        try {
            activity.startActivityForResult(intent, speechCode);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(activity.getApplicationContext(), R.string.speech_not_supported,
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    public void processResult(int resultCode,  Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            main.setSpeechText(result.get(0));
            voiceCommand(result.get(0));
        }
    }

    public void voiceCommand(String command){
        if(command.contains("weather")){
            Toast.makeText(activity, R.string.acquiringWeather, Toast.LENGTH_SHORT).show();
            main.weatherReport();
        }
        else if(command.contains("scan")){
            Toast.makeText(activity, R.string.connectingOneSheeld, Toast.LENGTH_SHORT).show();
            main.scan();
        }
        else if(command.contains("lights")){
            if(main.getConnectedToSheeld()) {
                if (command.contains("on")) {
                    Toast.makeText(activity, R.string.turningOnLights, Toast.LENGTH_SHORT).show();
                    main.getSheeld().digitalWrite(4, true);
                } else if (command.contains("off")) {
                    Toast.makeText(activity, R.string.turningOffLights, Toast.LENGTH_SHORT).show();
                    main.getSheeld().digitalWrite(4, false);
                }
            }
            else
                Toast.makeText(activity, R.string.noConnectionLights, Toast.LENGTH_SHORT).show();
        }
        else if(command.contains("heating")){
            if(main.getConnectedToSheeld()) {
                if (command.contains("on")) {
                    Toast.makeText(activity, R.string.turningOnHeating, Toast.LENGTH_SHORT).show();
                    main.getSheeld().digitalWrite(3, true);
                } else if (command.contains("off")) {
                    Toast.makeText(activity, R.string.turningOffHeating, Toast.LENGTH_SHORT).show();
                    main.getSheeld().digitalWrite(3, false);
                }
            }
            else
                Toast.makeText(activity, R.string.noConnectionHeating, Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(activity, R.string.unknownVoiceCommand, Toast.LENGTH_SHORT).show();
    }
}
