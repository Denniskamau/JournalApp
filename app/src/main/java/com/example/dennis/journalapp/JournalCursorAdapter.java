package com.example.dennis.journalapp;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dennis.journalapp.data.JournalContract;

/**
 * Created by dennis on 6/29/18.
 */

public class JournalCursorAdapter  extends CursorAdapter{
    public JournalCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.activity_journal_page,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Find individual views by id
        TextView headingTextView = (TextView) view.findViewById(R.id.tv_heading);
        TextView summaryTextView = (TextView) view.findViewById(R.id.tv_summary);

        // find the column for the journal attribute
        int headingColumnIndex = cursor.getColumnIndex(JournalContract.JournalEntry.COLUMN_HEADING);
        int bodyColumnIndex = cursor.getColumnIndex(JournalContract.JournalEntry.COLUMN_JOURNAL_ENTRY);

        //Read the data from the cursor
        String heading = cursor.getString(headingColumnIndex);
        String body = cursor.getString(bodyColumnIndex);
        //Update the textview with the data
        headingTextView.setText(heading);
        summaryTextView.setText(body);
    }

}
