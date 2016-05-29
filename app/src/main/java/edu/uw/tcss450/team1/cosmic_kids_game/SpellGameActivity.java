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
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uw.tcss450.team1.cosmic_kids_game.HelperCode.DBHandler;

public class SpellGameActivity extends Activity implements View.OnClickListener {

    //Log TAG
    private static final String TAG = "SpellGameActivity class";
    /* Mock Difficulty Setting--Change to the max desired grade-level words */
    private static final int DIFFICULTY = 3;

    /* TEMP DB variables May Be Moved, but keep in place for now*/
    private DBHandler dbHandler;
    private Cursor cursor;
    //used to store <Word, Grade> key/value pairs in List of Maps:
    static ArrayList<HashMap<String, String>> wordsList = new ArrayList<>();
    private ImageView iv;
    //Animates our GIF for background
    AnimationDrawable ad;

    //Where the spelling word is entered by user
    EditText etWordEntry;


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
        ArrayList<String> chosenWords = new ArrayList<>();

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            HashMap<String, String> map = new HashMap<>();
            Log.d("DEBUG****", "id:" + cursor.getInt(0) + ";word:" + cursor.getString(1)
                    + ";grade:" + cursor.getString(2));
            map.put("Word", cursor.getString(1));
            map.put("Grade", cursor.getString(2));

            //store just the words we want in chosenWords List
            if(Integer.parseInt(cursor.getString(2)) <= DIFFICULTY) {
                chosenWords.add(cursor.getString(1));
            }

            wordsList.add(map);
            cursor.moveToNext();
        }

        /*For testing: print out all chosen words*/
        int y = 0;
        for(String str : chosenWords)
            Log.d(TAG + ", Difficulty:" + DIFFICULTY, " chosenWords(" + y++ + ")=" + str);

//                for(int x = 0; x < wordsList.size(); x++) {
//                    if(wordsList.get(x).containsValue("3")) {
//                        Log.d(TAG, "word, grade:" + wordsList.get(x).values());
//                        chosenWords.add(wordsList.get(x).k);
//                        Log.d(TAG, "chosenWords="+chosenWords.get(y++));
//                    }
//
//                }


        /*============================================================================*/

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
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {

    }
}
