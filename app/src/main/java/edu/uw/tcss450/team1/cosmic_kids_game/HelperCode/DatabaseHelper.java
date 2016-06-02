/**
 * @Class DatabaseHelper
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides the necessary methods to create and populate a SQLiteDatabase by parsing
 * the necessary XML file for the word lists.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import edu.uw.tcss450.team1.cosmic_kids_game.R;

public class DatabaseHelper extends SQLiteOpenHelper {

    private final Context context;

    public static final int DB_VERSION = 3;
    public static final String TAG = "debugDH";
    public static final String DB_NAME = "WORDS.DB";
    public static final String TABLE_NAME = "WORDS";
    public static final String KEY_ID = "_id";
    public static final String COL_GRADE = "grade";
    public static final String COL_WORD = "word";
    private static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_WORD + " TEXT NOT NULL UNIQUE, "
                    + COL_GRADE + " INTEGER);";

    /**
     * Constructor that saves the context for later use.
     * @param context Context to pass to the SQLiteDatabase in onCreate trigger
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     * Create Tables by executing SQL statement, then fill with words from words.xml resource file.
     * @param db The database that is to be filled
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        ContentValues cv = new ContentValues();
        Resources res = context.getResources();
        XmlResourceParser wordsXML = res.getXml(R.xml.words);
        try {
            int eventType = wordsXML.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && wordsXML.getName().equals("record")){
                    String word = wordsXML.getAttributeValue(null, DatabaseHelper.COL_WORD);
                    String grade = wordsXML.getAttributeValue(null, DatabaseHelper.COL_GRADE);
                    cv.put(DatabaseHelper.COL_WORD, word);
                    cv.put(DatabaseHelper.COL_GRADE, Integer.parseInt(grade));
                    db.insert(DatabaseHelper.TABLE_NAME, null, cv);
                }
                eventType = wordsXML.next();
            }
        } catch (Exception e) { // Expecting XML or IO Exceptions, both handled identically
            Log.e(TAG, e.getMessage(), e);
        } finally {
            wordsXML.close();
        }
    }

    /**
     * Checks if DB_VERSION has been changed (newer version) since the last run. If so, drop the
     * current table and call the onCreate method to re-import the values.
     * @param db The database that is to be dropped and re-filled
     * @param oldVersion The old version of the database
     * @param newVersion The new version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}