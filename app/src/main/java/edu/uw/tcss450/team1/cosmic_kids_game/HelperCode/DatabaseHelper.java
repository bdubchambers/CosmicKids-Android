package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.uw.tcss450.team1.cosmic_kids_game.R;

/**
 * Database table is populated via XML files:
 * grade3 and grade4_wordtable.xml
 *
 * Created by Brandon on 5/26/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper Class";
    private final Context context;

    DatabaseHelper(Context c) {
        super(c, "mydb", null, 1);
        context = c;
    }

    /**
     * autocreated Javadoc from implementing methods
     *
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE grade3_wordtable ("
                + "word TEXT"
                + ");");

        //add records found in corresponding xml to grade3_wordtable
        ContentValues contentValues = new ContentValues();
        //extract String Array from xml
        Resources resources = context.getResources();
        //store resources from xml into an array
        String[] grade3wordsArr = resources.getStringArray(R.array.grade3_wordtable_v1);
        //finally, iterate array, inserting values into db table
        for(int i = 0; i < grade3wordsArr.length; i++) {
            contentValues.put("word", grade3wordsArr[i]);
            db.insert("grade3_wordtable", null, contentValues);
        }
    }

    /**
     * autocreated Javadoc from implementing methods
     *
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.w(TAG, "Upgrading datbase from version "+oldVersion+" to "+newVersion
                +", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS grade3WordsArr;");
        onCreate(db);
    }
}
