package com.example.dennis.journalapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by dennis on 6/27/18.
 */

public class JournalProvider extends ContentProvider{

    /** Tag for the log messages */
    public static final String LOG_TAG = JournalProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the Journals table */
    public static final int JOURNALS = 100;

    /** URI matcher code for the content URI for a single journal in the journal table */
    public static final int JOURNALS_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.journal/journal" will map to the
        // integer code {@link #JOURNAL}. This URI is used to provide access to MULTIPLE rows
        // of the journal table.
        sUriMatcher.addURI(JournalContract.CONTENT_AUTHORITY, JournalContract.PATH_JOURNALS, JOURNALS);

        // The content URI of the form "content://com.example.android.journal/journals/#" will map to the
        // integer code {@link #JOURNAL_ID}. This URI is used to provide access to ONE single row
        // of the journals table.

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.journals/journals/3" matches, but
        // "content://com.example.android.journals/journals" (without a number at the end) doesn't match.
        sUriMatcher.addURI(JournalContract.CONTENT_AUTHORITY, JournalContract.PATH_JOURNALS + "/#", JOURNALS_ID);
    }


    /** Database helper object */
    private JournalDbHelper mDbHelper;



    /**
     * Initialize the provider and the database helper object.
     */

    @Override
    public boolean onCreate() {
        mDbHelper = new JournalDbHelper(getContext());

        return false;
    }


    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {


        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case JOURNALS:
                cursor = database.query(JournalContract.JournalEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);

                break;
            case JOURNALS_ID:
                selection = JournalContract.JournalEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the journal table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(JournalContract.JournalEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */




    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case JOURNALS:
                return insertJournal(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private Uri insertJournal(Uri uri, ContentValues values){
        // Check that the heading is not null
        String heading = values.getAsString(JournalContract.JournalEntry.COLUMN_HEADING);
        if (heading == null) {
            throw new IllegalArgumentException("Heading is requires ");
        }
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new journal with the given values
        long id = database.insert(JournalContract.JournalEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        return ContentUris.withAppendedId(uri,id);
    }

    @Override
    public int delete( Uri uri,  String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match =  sUriMatcher.match(uri);
        switch (match){
            case JOURNALS:
                // Delete all rows that match the selection and selection args
                return database.delete(JournalContract.JournalEntry.TABLE_NAME, selection, selectionArgs);

            case JOURNALS_ID:

                // Delete a single row given by the ID in the URI
                selection = JournalContract.JournalEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return database.delete(JournalContract.JournalEntry.TABLE_NAME, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case JOURNALS:
                return updateJournal(uri, values, selection, selectionArgs);
            case JOURNALS_ID:
                selection = JournalContract.JournalEntry._ID + "=?";
                selectionArgs =new String[] {String.valueOf(ContentUris.parseId(uri))};
                return updateJournal(uri,values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for "+ uri);
        }

    }

    private int updateJournal(Uri uri,ContentValues values,String selection, String[] selectionArgs){
        if(values.containsKey(JournalContract.JournalEntry.COLUMN_HEADING)){
            String heading = values.getAsString(JournalContract.JournalEntry.COLUMN_HEADING);
            if(heading == null){
                throw new IllegalArgumentException("Journal heading is required");
            }
        }

        if(values.containsKey(JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY)){
            String body = values.getAsString(JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY);
        }
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Returns the number of database rows affected by the update statement
        return database.update(JournalContract.JournalEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri){

        final int match = sUriMatcher.match(uri);
        switch (match){
            case JOURNALS:
                return JournalContract.JournalEntry.CONTENT_LIST_TYPE;
            case JOURNALS_ID:
                return JournalContract.JournalEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }

    }
}
