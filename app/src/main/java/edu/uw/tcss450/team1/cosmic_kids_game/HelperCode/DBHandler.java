/**
 * @Class DBHandler
 * @Version 1.0.0
 * @Author Justin Burch
 * @Author Brandon Chambers
 *
 * This class provides a convenient way to handle calls to the SQLiteDatabase that is used to store
 * the words used in the Spelling Bee.
 */
package edu.uw.tcss450.team1.cosmic_kids_game.HelperCode;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBHandler {
    private Context context;
    private SQLiteDatabase db;

    /**
     * Constructor which stores the context.
     *
     * @param context Context to store for later use
     */
    public DBHandler(Context context) {
        this.context = context;
    }

    /**
     * Create the inner Database.
     *
     * @return The DBHandler that contains the database
     * @throws SQLException Exception thrown by bad database creation
     */
    public DBHandler open() throws SQLException {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Retrieve a Cursor object containing the set of all rows in the table.
     *
     * @return Cursor object at the start of the set
     */
    public Cursor fetch() {
        String[] columns = new String[]
                {DatabaseHelper.KEY_ID, DatabaseHelper.COL_WORD, DatabaseHelper.COL_GRADE};
        Cursor cursor =
                db.query(DatabaseHelper.TABLE_NAME, columns, null, null, null, null, null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    /**
     * Retrieve a Cursor object containing the set of all rows in the table in the grade range.
     *
     * @param startGrade Minimum grade to retrieve
     * @param endGrade Maximum grade to retrieve
     * @return Set of all rows within the specified range
     */
    public Cursor fetch(int startGrade, int endGrade) {
        String sb = "SELECT " +
                DatabaseHelper.COL_WORD +
                ", " +
                DatabaseHelper.COL_GRADE +
                " FROM " +
                DatabaseHelper.TABLE_NAME +
                " WHERE " +
                DatabaseHelper.COL_GRADE +
                " BETWEEN ? AND ?;";
        Cursor cursor = startGrade <= endGrade ? db.rawQuery(
                sb,
                new String[] {
                        Integer.toString(startGrade),
                        Integer.toString(endGrade)
                }
        ) : null;
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /**
     * Delete the entire database
     */
    public void deleteDatabase(){
        context.deleteDatabase(DatabaseHelper.DB_NAME);
    }
}
