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
package edu.uw.tcss450.team1.cosmic_kids_game;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.DBHandler;

public class SpellGameActivity extends Activity implements View.OnClickListener {

    //Log TAG
    private static final String TAG = "SpellGameActivity class";
    /* Mock Difficulty Setting--Change to the max desired grade-level words */
    private static final int DIFFICULTY = 6;

    /* TEMP DB variables May Be Moved, but keep in place for now*/
    private DBHandler dbHandler;
    private Cursor cursor;
    //create pool of potential words:
    private static ArrayList<String> potentialWords;
    //out of the above pool, get 20 words for current game
    private static LinkedHashSet<String> gameSet;
    private ImageView iv;
    //Animates our GIF for background
    private AnimationDrawable ad;

    //Where the spelling word is entered by user
    EditText etWordEntry;
    public String result = null;
    public int pointSum = 0;
    //Speak spelling words to user
    TextToSpeech toSpeech;
    public String strToSpeak;
    Button btnRepeatWord, btnSubmitWord, btnNextWord;

    //ProgressBar to show countdownTimer
    ProgressBar progressBar;
    CountDownTimer timer;
    int countDown = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_game);
        setTheme(R.style.FullscreenTheme);

        /*Grab ImageView by id and use the animation-list created inside
        animation xml to string together frames of a GIF (Android does
        not provide native GIF support)*/
        iv = (ImageView) findViewById(R.id.imgBGSpaceGIF02);
        iv.setBackgroundResource(R.drawable.spacegif_02_animation);
        ad = (AnimationDrawable) iv.getBackground();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(Color.CYAN, PorterDuff.Mode.DARKEN);
        //button that repeats the current spelling word
        btnRepeatWord = (Button) findViewById(R.id.btnRepeatWord);
        //button that sends the user's spelling for checking
        btnSubmitWord = (Button) findViewById(R.id.btnSubmitWord);
        //Next word in game
        btnNextWord = (Button)findViewById(R.id.btnNextWord);
        btnNextWord.setVisibility(View.INVISIBLE);
        //user's word entry to be checked for spelling
        etWordEntry = (EditText) findViewById(R.id.etWordEntry);
        //*********************** SQLite Words DB code ******************************************

        dbHandler = new DBHandler(this);
        dbHandler.open();

        /*Database delete all data, close, then reopen logic, uncomment to nuke*/
//        int count = dbHandler.deleteAllRows();
//        Log.d(TAG, "number of row deleted: " + count);
//        dbHandler.open();

        /*...OR just nuke the entire Database */
//        dbHandler.deleteDatabase();

        //grab all data using Cursor
        cursor = dbHandler.fetch();

        /* Based on difficulty setting, we check each word's associated grade level, and
                * place words that have grade equal to or lower than DIFFICULTY*/
        potentialWords = new ArrayList<String>();

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Log.d("CURSOR -> DB****", "ID:" + cursor.getInt(0) + "; WORD:" + cursor.getString(1)
                    + "; GRADE:" + cursor.getString(2));

            //store the words we want in a potentialWords List
            //by checking the word's associated Grade value
            if(Integer.parseInt(cursor.getString(2)) <= DIFFICULTY) {
                potentialWords.add(cursor.getString(1));
            }

            cursor.moveToNext();
        }

        /*For testing: print out all chosen words*/
        int y = 0;
        for(String str : potentialWords)
            Log.d(TAG + ", Difficulty:" + DIFFICULTY, " potentialWords(" + y++ + ")=" + str);


        final Random random = new Random(SystemClock.currentThreadTimeMillis());
        /* Out of our list, 'potentialWords' of potential game words, chosen based
         * on DIFFICULTY, we only want < 20 > words per game, they need to be a random
          * selection, and it must be contained in a Set so we have no repeats.
          *
          * Using 'LinkedHashSet' because: This implementation spares its clients from
          * the unspecified, generally chaotic ordering provided by HashSet, without
          * incurring the increased cost associated with TreeSet.*/
        gameSet = new LinkedHashSet<>();

        /*add 20 unique words to the set, chosen randomly */
        while(!(gameSet.size() == 20)) {
            gameSet.add(potentialWords.get(random.nextInt(potentialWords.size())));
        }

        //testing only:
//        final Iterator<String> itr = gameSet.iterator();
//        y = 0;
//        while(itr.hasNext())
//            Log.d(TAG, "gameSet(" + y++ + "):" + itr.next());

        /*===========================================================================
        *      TextToSpeech + CountDownTimer + ProgressBar
        * */

        /*===================================================================
              *       TEXTTOSPEECH --
              *       here we initialize the TTS engine
              *
              *       I cannot get TTS to work by itself, meaning I have to
              *       attach it to a onClick for a button, etc...researching...
              * */

        toSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    toSpeech.getDefaultEngine();
                    toSpeech.setLanguage(Locale.getDefault());
                    toSpeech.setPitch(1);
                    toSpeech.setSpeechRate(0);
                }
            }
        });
        //===================================================================
        final Iterator<String> iter = gameSet.iterator();
        //Game Loop
        while(iter.hasNext()) {
            strToSpeak = iter.next();

            btnRepeatWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //repeat the current word stored inside strToSpeak
                    toSpeech.speak(strToSpeak, TextToSpeech.QUEUE_FLUSH, null);

                    progressBar.setProgress(countDown);
                    timer = new CountDownTimer(20000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
//                    Log.d("CountDownTimer.OnTick", "tick:" + countDown + millisUntilFinished);
                            countDown++;
                            progressBar.setProgress(countDown);
                        }

                        @Override
                        public void onFinish() {
                            //Reset the progressBar+CountDownTimer
                            countDown = 0;
                            progressBar.setProgress(countDown);
                            timer.cancel();
                            //Time is up, so take what the user has entered thus far, and check it
                            result = etWordEntry.getText().toString();
                        }
                    };//END CountDownTimer init

                    timer.start();

                }//end onClick method for RepeatWord

            });//END of RepeatWord button click listener

            btnSubmitWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //user has submitted the word, so stop the timer/progressBar
                    countDown = 0;
                    progressBar.setProgress(countDown);
                    timer.cancel();
                    //send their text entry to check for spelling
                    result = etWordEntry.getText().toString();
                    Log.d("RESULT STRING *********",result);
                    //now check the user's spelling to see if it is correct, then apply points scored
                    if(result != null && result.equals(strToSpeak)) {
                        //word spelled correctly--earn points!
                        pointSum += (strToSpeak.length() * DIFFICULTY);

                        Toast toast = new Toast(SpellGameActivity.this);
                        toast.makeText(SpellGameActivity.this, "Your spelling: " + result
                                + "\nActual spelling: " + strToSpeak + "\n"
                                + "***CORRECT!***", Toast.LENGTH_SHORT).show();

                    } else if(DIFFICULTY > 4 && pointSum > strToSpeak.length()
                            && !result.equals(strToSpeak)) {
                        //if DIFFICULTY = 5 or 6, the user loses points for incorrect spelling
                        pointSum -= strToSpeak.length();//only lose points equal to length of word
                        Toast toast = new Toast(SpellGameActivity.this);
                        toast.makeText(SpellGameActivity.this, "Your spelling: " + result
                                + "\nActual spelling: " + strToSpeak + "\n"
                                + "***WRONG!***", Toast.LENGTH_SHORT).show();
                    } //else: continue, doing nothing with points (DIFFICULTY is low)

                    Log.d(TAG, "******pointSum=" + pointSum);

                    /* Next Word Logic*/
                    btnNextWord.setVisibility(View.VISIBLE);
                    btnNextWord.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etWordEntry.setText("");
                            btnNextWord.setVisibility(View.INVISIBLE);
                            etWordEntry.requestFocus();
                            /**************************ITERATE HERE?******************************/
//                            if(iter.hasNext())
//                                strToSpeak = iter.next();
                        }
                    });
                    /* End Next Word Logic*/
                }
            });


        }//end outer while loop: iter.hasNext()
    }

    /**
     * Starts the AnimationDrawable to string together frames of a GIF
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            ad.start();
        }
    }

    /**
     * shutdown TextToSpeech engine when idle
     */
    public void onPause() {
        if(toSpeech != null) {
            toSpeech.stop();
            toSpeech.shutdown();
        }
        super.onPause();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }

}
