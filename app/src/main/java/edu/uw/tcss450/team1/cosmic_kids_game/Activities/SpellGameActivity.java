/**
 * @Class SpellGameActivity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 * <p/>
 * This handles the Spelling Bee game Activity.  Words and there associated grade levels
 * (currently 3rd through 6th) are stored locally in a SQLite Database.  Based on the app's
 * Difficulty setting, an ArrayList of words is chosen and held in memory to be randomly chosen
 * and the TextToSpeech engine will speak the word to the user.  The user will have 15 seconds
 * to typed in the correct spelling of the word.  If the word is spelled incorrectly, no points
 * are awarded.  If it is correct, then the following formula is applied to determine the current
 * games final score: SUM(word.length * gradeLevelOfWord).
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

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
    private static final String TAG = "SpellGameActivity class";
    private static final String ID = "UtteranceID";
    private static final int TIME_LIMIT = 60;
    private static final int WORD_LIMIT = 3;

    //Animates our GIF for background
    private AnimationDrawable ad;

    //Where the spelling word is entered by user
    EditText etWordEntry;
    public String result = null;
    public int pointSum = 0;
    //Speak spelling words to user
    TextToSpeech toSpeech;
    public String strToSpeak;
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

        /*Grab ImageView by id and use the animation-list created inside
        animation xml to string together frames of a GIF (Android does
        not provide native GIF support)*/
        ImageView iv = (ImageView) findViewById(R.id.imgBGSpaceGIF02);
        iv.setBackgroundResource(R.drawable.spacegif);
        ad = (AnimationDrawable) iv.getBackground();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(Color.CYAN, PorterDuff.Mode.DARKEN);
        progressBar.setMax(100);

        //button that repeats the current spelling word
        btnRepeatWord = (Button) findViewById(R.id.btnRepeatWord);
        //button that sends the user's spelling for checking
        btnSubmitWord = (Button) findViewById(R.id.btnSubmitWord);
        //user's word entry to be checked for spelling
        etWordEntry = (EditText) findViewById(R.id.etWordEntry);
        numberOfWords = 0;
        SharedPreferences sp = General.GetPrefs(this);


        username = sp.getString("username", "Guest");
        int difficulty = sp.getInt("difficulty", 1);
        final int timeLimit = TIME_LIMIT - (20 * difficulty);
        int[] grades = Word.GetGrades(difficulty);

        DBHandler dbHandler = new DBHandler(this);
        dbHandler.open();

        Cursor cursor = dbHandler.fetch(grades[0], grades[1]);

        potentialWords = new ArrayList<>();
        while(!cursor.isAfterLast()) {
            try {
                int wcol = cursor.getColumnIndex(DatabaseHelper.COL_WORD);
                int icol = cursor.getColumnIndex(DatabaseHelper.COL_GRADE);
                potentialWords.add(new Word(cursor.getString(wcol),
                        cursor.getInt(icol)));
            } catch(Exception e) {
                Log.d(TAG, cursor.toString());
            }
            cursor.moveToNext();
        }

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
/*                long elapsed = timeMs - millisUntilFinished;
                double pct = (elapsed * 1.0) / timeMs;
                double rounder = pct * 100;*/
                int rounded = (int)(100 * (((timeMs - millisUntilFinished) * 1.0) / timeMs));
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
                int grade = myWord.getGrade();
                int length = strToSpeak.length();
                if(result == null) {
                    return;
                } else if(result.equals(strToSpeak)) {
                    int newPoints = length * grade;
                    pointSum += newPoints;
                    General.Toast(v.getContext(), "Earned " + newPoints + " points!");
                    nextWord();
                } else if(grade > 4 && pointSum > length) {
                    pointSum -= length;
                    General.Toast(v.getContext(), "Incorrect! You've lost " + length + " points!");
                } else {
                    General.Toast(v.getContext(), "Incorrect!");
                }
            }
        });
        nextWord();
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
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                            if(progressBar.getProgress() == 0) {
                                timer.start();
                            }
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }
                    });
                }
            }
        });
    }

    /**
     * use TTS engine to speak a word
     */
    private void speak() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, ID);
        toSpeech.speak(strToSpeak, TextToSpeech.QUEUE_FLUSH, params);
    }

    /**
     * randomly select a word from a pool of difficulty-level based words
     */
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
            strToSpeak = myWord.getWord();
            speak();
        }
    }

    /**
     * send user to the score displaying transitional screen after completion
     */
    private void finishGame() {

        General.Toast(this, "Game finished");

        try{
            OutputStreamWriter out = new OutputStreamWriter(
                    openFileOutput(getString(R.string.SCORES_FILE), Context.MODE_PRIVATE));
            out.write("User: " + username + " => ");
            out.write("Score: " + pointSum);
            out.close();
            Log.d(TAG, "Stored User+Score successfully!");
        } catch(Exception e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, EndGameTransitionActivity.class);
        intent.putExtra("USER_SCORE", pointSum);
        startActivity(intent);
    }

    /**
     * Starts the AnimationDrawable to string together frames of a GIF
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus && ad != null) {
            ad.start();
        } else {
            if (ad != null && ad.isRunning()) {
                ad.stop();
            }
            if(toSpeech != null) {
                if (toSpeech.isSpeaking()) {
                    toSpeech.stop();
                }
                toSpeech.shutdown();
            }
        }
    }

    /**
     * Shutdown TextToSpeech engine when idle
     */
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
        super.onResume();
        
    }
}
