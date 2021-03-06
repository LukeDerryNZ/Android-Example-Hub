package com.lukederrynz.application_Hub;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.lukederrynz.android_test.R;

import java.util.Locale;

/**
 * Created by Luke Derry on 23/08/2017
 *
 * Provides a basic Test To Speech application.
 * Future additions will include Speech To Text functionality.
 */
public class TextToSpeechActivity extends AppCompatActivity {

    private EditText editText;
    private TextToSpeech TTS;
    private Button button_Speak;
    private SeekBar seekBar_Pitch, seekBar_SpeechRate;
    private float pitch = 0f, speechRate = 0f;
    private int divisor = 10; // Divisor for speech pitch and rate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_speech);

        initControls();
        initTTS();
        initListeners();
    }

    /**
     * Initialize Text to speech engine.
     *
     */
    private void initTTS() {
        TTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        TTS.getVoice().getLocale();
                    } else {
                        TTS.setLanguage(Locale.UK); // Deprecated in version >= 21
                    }
                }
            }
        });
    }


    /**
     * Initialize our UI.
     *
     */
    private void initControls() {

        editText = (EditText)findViewById(R.id.TTS_editText);
        button_Speak = (Button)findViewById(R.id.TTS_button_Speak);
        seekBar_Pitch = (SeekBar)findViewById(R.id.seekBar_Pitch);
        seekBar_SpeechRate = (SeekBar)findViewById(R.id.seekBar_SpeechRate);

        /*  Set the editText colors
         *  Note that there are better methods to do this using themes or styles
         *  This method ONLY affects this drawable
         */
        Drawable d = editText.getBackground();
        d.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        editText.setBackground(d);
    }


    /**
     * Initialize our Listeners.
     *
     */
    private void initListeners() {
        // Speak Listener
        button_Speak.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String toSpeak = editText.getText().toString();

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                // Bail if we have an empty string
                if (toSpeak.isEmpty()) {
                    TTS.setPitch(1.0f); TTS.setSpeechRate(1.0f);
                    TTS.speak("Please Enter some text first", TextToSpeech.QUEUE_FLUSH, null);
                    return;
                }

                // Display the message spoken
                Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();

                // Set our vars from UI
                TTS.setPitch(pitch);
                TTS.setSpeechRate(speechRate);

                // Branch depending on API level
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    TTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    TTS.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        // Pitch Listener
        seekBar_Pitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // Divide by 10 to get speech rate between 0.0-1.0
                pitch = ((float)i+1) / divisor;
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Speech Rate Listener
        seekBar_SpeechRate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                speechRate = ((float)i+1) / divisor;
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }


    /**
     * Called when another activity takes focus.
     *
     */
    @Override public void onPause() {
        shutDownTTS();
        super.onPause();
    }


    /**
     * Called just before activity is destroyed.
     *
     */
    @Override public void onDestroy() {
        shutDownTTS();
        super.onDestroy();
    }


    /**
     * Shut down text to speech engine.
     *
     */
    private void shutDownTTS() {
        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
    }

}
