package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    //LOG TAG
    public static final String TAG = "DatabaseHelper CLASS";

    // Database Version and Name
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "WORDS.DB";

    // Words table name
    public static final String TABLE_NAME = "tableofwords";

    // Words Table Column names
    public static final String KEY_ID = "_id";
    public static final String COL_GRADE = "grade";
    public static final String COL_WORD = "word";

    //Create table query String
   private static final String CREATE_TABLE =
            "create table " + TABLE_NAME + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COL_WORD + " TEXT NOT NULL UNIQUE, "
                    + COL_GRADE + " TEXT);";

    /**
     * Constructor
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Create Tables
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    /**
     * Make changes to DB and Tables, create new db
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}