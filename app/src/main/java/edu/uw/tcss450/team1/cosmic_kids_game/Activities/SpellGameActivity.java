/**
 * @Class SpellGameActivity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 * This Activity represents the Spelling Bee game. Words and their associated grade levels
 * are stored locally in a SQLite Database.  Based on the app's
 * Difficulty setting, an ArrayList of words is chosen and held in memory to be randomly chosen
 * and the TextToSpeech engine will speak the word to the user.  The user will have a limited
 * amount of time, based on the difficulty setting within the options menu,
 * to type in the correct spelling of the word.  If the word is spelled incorrectly, no points
 * are awarded at the easier difficulties, and points are deducted at the highest level.  If it is
 * correct, a formula containing the length of the word and the grade level associated with the
 * word will produce a score.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.DBHandler;
import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.DatabaseHelper;
import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.General;
import edu.uw.tcss450.team1.cosmic_kids_game.Models.Word;
import edu.uw.tcss450.team1.cosmic_kids_game.R;

public class SpellGameActivity extends Activity {
    private static final String TAG = "debugSGA";
    private static final String ID = "UtteranceID";
    private static final int TIME_LIMIT = 60;
    private static final int WORD_LIMIT = 5;

    //Animates our GIF for background
    private AnimationDrawable ad;

    //Where the spelling word is entered by user
    EditText etWordEntry;
    public String result = null;
    public int pointSum = 0;
    //Speak spelling words to user
    TextToSpeech toSpeech;
    Button btnRepeatWord, btnSubmitWord;

    //ProgressBar to show countdownTimer
    ProgressBar progressBar;
    CountDownTimer timer;


    /* Class-level Variables */
    ArrayList<Word> potentialWords;
    int numberOfWords = 20;
    Word myWord;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_game);
        setTheme(R.style.FullscreenTheme);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(Color.CYAN, PorterDuff.Mode.DARKEN);
        progressBar.setMax(100);

        btnRepeatWord = (Button) findViewById(R.id.btnRepeatWord);
        btnSubmitWord = (Button) findViewById(R.id.btnSubmitWord);
        etWordEntry = (EditText) findViewById(R.id.etWordEntry);
        numberOfWords = 0;
        SharedPreferences sp = General.GetPrefs(this);


        username = sp.getString(getResources().getString(R.string.username), "Guest");
        int difficulty = sp.getInt(getResources().getString(R.string.difficulty), 1);
        final int timeLimit = TIME_LIMIT - (25 * difficulty);
        int[] grades = Word.GetGrades(difficulty);

        DBHandler dbHandler = new DBHandler(this);
        dbHandler.open();

        Cursor cursor = dbHandler.fetch(grades[0], grades[1]);

        potentialWords = new ArrayList<>();
        while(!cursor.isAfterLast()) {
            try {
                potentialWords.add(new Word(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COL_WORD)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COL_GRADE))));
            } catch(Exception e) {
                Log.d(TAG, cursor.toString());
            }
            cursor.moveToNext();
        }

        if (potentialWords.size() > 0) {

            //initialize TextToSpeech
            initTTS();

            btnRepeatWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    speak();
                }
            });

            final int timeMs = timeLimit * 1000;
            timer = new CountDownTimer(timeMs, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    int rounded = (int) (100 * (((timeMs - millisUntilFinished) * 1.0) / timeMs));
                    progressBar.setProgress(rounded);
                }

                @Override
                public void onFinish() {
                    nextWord();
                }
            };

            btnSubmitWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    result = etWordEntry.getText().toString();
                    if (result == null) {
                        return;
                    } else if (myWord.isCorrect(result)) {
                        int newPoints = myWord.getPoints();
                        pointSum += newPoints;
                        General.ToastTop(v.getContext(), "Earned " + newPoints + " points!");
                        nextWord();
                    } else {
                        int toDeduct = myWord.toDeduct(pointSum);
                        pointSum -= toDeduct;
                        General.ToastTop(v.getContext(), "Incorrect! You've lost " + toDeduct +
                                " points!");
                    }
                }
            });
        } else {
            General.Toast(this, "Not enough words to play at this difficulty!");
            finish();
        }
    }

    /**
     * sets up the TextToSpeech Engine
     */
    private void initTTS() {
        toSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    toSpeech.getDefaultEngine();
                    toSpeech.setLanguage(Locale.getDefault());
                    toSpeech.setPitch(1);
                    toSpeech.setSpeechRate(0);
                    toSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {}
                        @Override
                        public void onError(String utteranceId) {}
                        @Override
                        public void onDone(String utteranceId) {
                            if(progressBar.getProgress() == 0) {
                                timer.start();
                            }
                        }
                    });
                }
            }
        });
    }

    private void speak() {
        Log.d(TAG, "speak: " + toSpeech.getEngines().size());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, ID);
        toSpeech.speak(myWord.getWord(), TextToSpeech.QUEUE_FLUSH, params);
    }

    private void nextWord() {
        numberOfWords++;
        progressBar.setProgress(0);
        timer.cancel();
        etWordEntry.setText("");
        if(numberOfWords > WORD_LIMIT || potentialWords.size() == 0) {
            finishGame();
        } else {
            final Random random = new Random(SystemClock.currentThreadTimeMillis());
            int index = random.nextInt(potentialWords.size());
            myWord = potentialWords.get(index);
            potentialWords.remove(index);
            speak();
        }
    }

    private void finishGame() {

        General.Toast(this, "Game finished");

        try{
            OutputStreamWriter out = new OutputStreamWriter(
                    openFileOutput(getString(R.string.SCORES_FILE),
                            Context.MODE_PRIVATE));
            out.write(username + " ; " + pointSum);
            out.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(SpellGameActivity.this, EndGameTransitionActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            if (ad != null && !ad.isRunning()) {
                ad.start();
            }
        } else {
            if (ad != null && ad.isRunning()) {
                ad.stop();
            }
            if (toSpeech != null) {
                if (toSpeech.isSpeaking()) {
                    toSpeech.stop();
                }
                toSpeech.shutdown();
            }
        }
    }

    @Override
    public void onPause() {
        if (ad != null && ad.isRunning()) {
            ad.stop();
        }
        if(toSpeech != null) {
            if (toSpeech.isSpeaking()) {
                toSpeech.stop();
            }
            toSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        SharedPreferences sp = General.GetPrefs(this);
        if (sp.getBoolean(getString(R.string.enableAnims), true)) {
            if (ad == null || !ad.isRunning()) {
                ImageView iv = (ImageView) findViewById(R.id.imgBGSpaceGIF02);
                iv.setBackgroundResource(R.drawable.spacegif);
                ad = (AnimationDrawable) iv.getBackground();
            }
        } else {
            if (ad != null && ad.isRunning()) {
                ad.stop();
            }
            ad = null;
        }
        super.onResume();
        nextWord();
    }
}
