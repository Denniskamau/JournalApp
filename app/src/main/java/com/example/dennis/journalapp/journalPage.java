package com.example.dennis.journalapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dennis.journalapp.data.JournalContract;

public class journalPage extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the journal data loader */
    private static final int JOURNAL_LOADER = 0;

    /** Adapter for the ListView */
    JournalCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_page);

        // Find the ListView which will be populated with the journal data
        //ListView journalList = findViewById(R.id.list);
        ListView journalListView = (ListView) findViewById(R.id.list);


        // Setup an Adapter to create a list item for each row of journal data in the Cursor.
        mCursorAdapter = new JournalCursorAdapter(this, null);
        journalListView.setAdapter(mCursorAdapter);


        // Setup the item click listener
        journalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link WriteJournal}
                Intent intent = new Intent(journalPage.this, WriteJournal.class);

                // Form the content URI that represents the specific pet that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                Uri currentJournalUri = ContentUris.withAppendedId(JournalContract.JournalEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentJournalUri);

                // Launch the {@link WriteJournal} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(JOURNAL_LOADER, null, this);


    }


     @Override
    public boolean onCreateOptionsMenu(Menu menu){
         getMenuInflater().inflate(R.menu.menu,menu);
         return true;
     }

    private void deleteAllJournals() {
        int rowsDeleted = getContentResolver().delete(JournalContract.JournalEntry.CONTENT_URI, null, null);
        //Log.d("CatalogActivity", rowsDeleted + " rows deleted from journal database");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
         switch (item.getItemId()){
             case R.id.action_new:
                 Intent intent = new Intent(this, WriteJournal.class);
                 this.startActivity(intent);
                 //insertJournal();
                 return true;
             case R.id.action_delete_all_entries:
                 deleteAllJournals();
                 return true;
         }
        return super.onOptionsItemSelected(item);
     }

    /**
     * Helper method to insert hardcoded journal data into the database. For debugging purposes only.
     */
    private void insertJournal() {
        // Create a ContentValues object where column names are the keys,

        ContentValues values = new ContentValues();
        values.put(JournalContract.JournalEntry.COLUMN_HEADING, "Codding");
        values.put(JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY, "It was a fun day");

        Uri newUri = getContentResolver().insert(JournalContract.JournalEntry.CONTENT_URI, values);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                JournalContract.JournalEntry._ID,
                JournalContract.JournalEntry.COLUMN_HEADING,
                JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                JournalContract.JournalEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link JournalCursorAdapter} with this new cursor containing updated journal data
        mCursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }

}
