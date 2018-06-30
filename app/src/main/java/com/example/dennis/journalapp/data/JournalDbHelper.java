package com.example.dennis.journalapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.dennis.journalapp.data.JournalContract.JournalEntry;

/**
 * Created by dennis on 6/27/18.
 */


/**
 * Database helper for Journal app. Manages database creation and version management.
 */
public class JournalDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = JournalDbHelper.class.getSimpleName();
    // name of the db
    private static final String DATABASE_NAME = "journals.db";

    //Db version
    private static final int DATABASE_VERSION = 1;

    public JournalDbHelper(Context context){

        super(context,DATABASE_NAME,null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the pets table
        String SQL_CREATE_JOURNAL_TABLE =  "CREATE TABLE " + JournalEntry.TABLE_NAME + " ("
                + JournalEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + JournalEntry.COLUMN_HEADING + " TEXT NOT NULL, "
                + JournalEntry.COLUMN_JOURNAL_ENTRY + " TEXT);";
        //Execute the sql statement
        db.execSQL(SQL_CREATE_JOURNAL_TABLE);

    }
    /**
     * This is called when the database needs to be upgraded.
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
