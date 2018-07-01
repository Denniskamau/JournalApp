package com.example.dennis.journalapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dennis on 6/27/18.
 */

/**
 * API Contract for the Journal app.
 */
public final class JournalContract {

    private JournalContract(){}


    public static final String CONTENT_AUTHORITY = "com.example.dennis.journalapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_JOURNALS = "journals";

    public static final class JournalEntry implements BaseColumns{
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_JOURNALS);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOURNALS;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_JOURNALS;

        public static final String TABLE_NAME ="journals";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_HEADING ="heading";
        public static final String COLUMN_JOURNAL_ENTRY ="entry";
    }
}
