/**
 * @Class SpellGameActivity
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
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

import android.annotation.TargetApi;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    /* Static Variables */
    private static final String TAG = "debugSGA";
    private static final String ID = "UtteranceID";
    private static final int TIME_LIMIT = 60;
    private static final int WORD_LIMIT = 10;

    /* Class-level Variables */
    private AnimationDrawable ad;
    private Animation animBtn;
    private EditText etWordEntry;
    private Button btnRepeatWord, btnSubmitWord;
    private TextToSpeech toSpeech;
    private ProgressBar progressBar;
    private CountDownTimer timer;
    private String username;
    private ArrayList<Word> potentialWords;
    private Word myWord;
    private int numberOfWords;
    private boolean started;

    public String result;
    public int pointSum;

    /**
     * Initialize components and set their listeners, as well as retrieve the word list.
     * @param savedInstanceState
     */
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

        // Animated button to attract user to start game
        animBtn = AnimationUtils.loadAnimation(SpellGameActivity.this, R.anim.fade);
        btnRepeatWord.startAnimation(animBtn);

        SharedPreferences sp = General.getPrefs(this);
        username = General.getUsername(this);
        int difficulty = sp.getInt(getResources().getString(R.string.difficulty), 1);
        final int timeLimit = TIME_LIMIT - (22 * difficulty);
        Word.GradeRange grades = Word.GetGrades(difficulty);

        DBHandler dbHandler = new DBHandler(this).open();

        Cursor cursor = dbHandler.fetch(grades.getMin(), grades.getMax());

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

        if (potentialWords.size() == 0) {
            General.toast(this, "Not enough words to play at this difficulty!");
            finish();
        } else {
            initTTS();

            btnRepeatWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!started) {
                        btnRepeatWord.clearAnimation();
                        btnRepeatWord.setText(R.string.repeat_word);
                        started = true;
                    }
                    speak();
                }
            });

            final int timeMs = timeLimit * 1000;
            timer = new CountDownTimer(timeMs, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
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
                    if (result.length() > 0) {
                        if (myWord.isCorrect(result)) {
                            int newPoints = myWord.getPoints();
                            pointSum += newPoints;
                            General.toastTop(v.getContext(), "Earned " + newPoints + " points!");
                            nextWord();
                        } else {
                            int toDeduct = myWord.toDeduct(pointSum);
                            pointSum -= toDeduct;
                            General.toastTop(v.getContext(), "Incorrect! You've lost " + toDeduct +
                                    " points!");
                        }
                    }
                }
            });
        }
    }

    /**
     * Create the TextToSpeech object and set a listener that will activate the game timer as
     * soon as the word has finished being spoken for the first time.
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

    /**
     * Speak the word that is currently set.
     */
    private void speak() {
        if (android.os.Build.VERSION.SDK_INT > 20) {
            speak21();
        } else {
            speakOld();
        }
    }

    @TargetApi(21)
    private void speak21() {
        toSpeech.speak(myWord.getWord(), TextToSpeech.QUEUE_FLUSH, null, ID);
    }

    private void speakOld() {
        HashMap<String, String> params = new HashMap<>();
        params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, ID);
        //noinspection deprecation
        toSpeech.speak(myWord.getWord(), TextToSpeech.QUEUE_FLUSH, params);
    }

    /**
     * Reset the components and check if the game should continue: go to next screen if not,
     * retrieve the next word if so.
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
            speak();
        }
    }

    /**
     * Write the score to a local file and start the EndGame screen that allows for score sharing.
     */
    private void finishGame() {
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
        if(hasFocus && ad != null && !ad.isRunning()) {
            ad.start();
        }
    }

    @Override
    public void onResume() {
        SharedPreferences sp = General.getPrefs(this);
        if (sp.getBoolean(getString(R.string.enableAnims), true)) {
            if (ad == null || !ad.isRunning()) {
                ImageView iv = (ImageView) findViewById(R.id.imgBGSpaceGIF02);
                iv.setBackgroundResource(R.drawable.spacegif);
                ad = (AnimationDrawable) iv.getBackground();
            }
        } else { // if user made change after this was initially created
            if (ad != null && ad.isRunning()) {
                ad.stop();
            }
            ad = null;
        }
        super.onResume();
        nextWord();
    }

    private void closeResources() {
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

    @Override
    public void onPause() {
        closeResources();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        closeResources();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        closeResources();
        super.onStop();
    }
}
