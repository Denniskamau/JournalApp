package com.example.dennis.journalapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
//import android.support.v4.app.LoaderManager.LoaderCallbacks<D>;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dennis.journalapp.data.JournalContract;

public class WriteJournal extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_JOURNAL_LOADER = 0;

    private Uri mCurrentJournalUri;


    private boolean mJournalHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mJournalHasChanged= true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_journal);

        Intent intent = getIntent();

        mCurrentJournalUri = intent.getData();

        if (mCurrentJournalUri == null) {
            setTitle("Add a Journal");

            invalidateOptionsMenu();
        }else {
            setTitle(getString(R.string.title_edit));
            getLoaderManager().initLoader(EXISTING_JOURNAL_LOADER, null, this);
        }

        EditText heading = (EditText) findViewById(R.id.et_heading);
        EditText body = (EditText) findViewById(R.id.et_body);

        heading.setOnTouchListener(mTouchListener);
        body.setOnTouchListener(mTouchListener);



    }

    // get user input and save to db
    private void insertJournal(){
        // find user input by id
        EditText Heading = (EditText) findViewById(R.id.et_heading);
        EditText Body = (EditText) findViewById(R.id.et_body);




        // parse the values to variable
        // Use trim to eliminate any leading and trailing white space
        String heading = Heading.getText().toString().trim();
        String body  = Body.getText().toString();


        // Create a ContentValues object where column names are the keys,
        // and journal attributes from the editor are the values.

        ContentValues values = new ContentValues();
        values.put(JournalContract.JournalEntry.COLUMN_HEADING,heading);
        values.put(JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY,body);

        // Insert a new journal into the provider, returning the content URI for the new journal.
        Uri newUri = getContentResolver().insert(JournalContract.JournalEntry.CONTENT_URI, values);
        // Run a check to see if data is hitting the db
        // Show a toast message depending on whether or not the insertion was successful
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_journal_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_journal_successful),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        // Inflate the menu options from the res/menu/menu_write_journal.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_write_journal,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new journal, hide the "Delete" menu item.
        if (mCurrentJournalUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()){
            case R.id.action_save:
                //save to db
                insertJournal();
                //Exit Activity
               finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.home:
                //Navigate to the parent activity
                if (!mJournalHasChanged) {
                    NavUtils.navigateUpFromSameTask(WriteJournal.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(WriteJournal.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the journal hasn't changed, continue with handling back button press
        if (!mJournalHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                JournalContract.JournalEntry._ID,
                JournalContract.JournalEntry.COLUMN_HEADING,
                JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentJournalUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int headingColumnIndex = cursor.getColumnIndex(JournalContract.JournalEntry.COLUMN_HEADING);
            int bodyColumnIndex = cursor.getColumnIndex(JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY);

            // Extract out the value from the Cursor for the given column index
            String heading = cursor.getString(headingColumnIndex);
            String body = cursor.getString(bodyColumnIndex);

            // Update the views on the screen with the values from the database
            EditText Heading = (EditText) findViewById(R.id.et_heading);
            EditText Body = (EditText) findViewById(R.id.et_body);

            Heading.setText(heading);
            Body.setText(body);



        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.

        EditText Heading = (EditText) findViewById(R.id.et_heading);
        EditText Body = (EditText) findViewById(R.id.et_body);

        Heading.setText("");
        Body.setText("");


    }








    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the journal.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    /**
     * Prompt the user to confirm that they want to delete this journal.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the journal.
                deleteJournal();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the journal.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the journal in the database.
     */
    private void deleteJournal() {
        // Only perform the delete if this is an existing journal.
        if (mCurrentJournalUri != null) {
            // Call the ContentResolver to delete the journal at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentJournalUri
            // content URI already identifies the pet that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentJournalUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.delete_pet_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.delete_journal_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

}
