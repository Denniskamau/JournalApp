package com.example.dennis.journalapp;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dennis.journalapp.data.JournalContract;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by dennis on 6/29/18.
 */

public class JournalCursorAdapter  extends CursorAdapter{
    Cursor cursor;
    Context context;

    public JournalCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
        cursor = c;
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.journal_entry_row,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.e("Column Count", cursor.getColumnCount()+"");
        for (String name : cursor.getColumnNames())
            Log.e("Column",name);


        //Find individual views by id
        TextView headingTextView = view.findViewById(R.id.row_journalHeader);
        TextView summaryTextView = view.findViewById(R.id.row_JournalContent);
        TextView dateTextView = view.findViewById(R.id.row_JournalDate);

        // find the column for the journal attribute
        int headingColumnIndex = cursor.getColumnIndex(JournalContract.JournalEntry.COLUMN_HEADING);
        int bodyColumnIndex = cursor.getColumnIndex(JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY);
       // int dateColumnIndex = cursor.getColumnIndex(JournalContract.JournalEntry.COLUMN_DATE);

        //Read the data from the cursor
        String heading = cursor.getString(headingColumnIndex);
        String body = cursor.getString(bodyColumnIndex);
        //String date = cursor.getString(dateColumnIndex);

        SimpleDateFormat sdf = new SimpleDateFormat("EE dd MM, yyyy HH:mm", Locale.ENGLISH);
        // give a timezone reference for formatting (see comment at the bottom)

        //String formattedDate = sdf.format(date);

        //Update the textview with the data
        headingTextView.setText(heading);
        summaryTextView.setText(body);
//        dateTextView.setText(formattedDate);

    }

    public int getCursorSize(){
       Cursor c = context.getContentResolver().query(
                JournalContract.JournalEntry.CONTENT_URI, null, null, null, null
       );

       return c.getCount();
    }




}
