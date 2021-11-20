package com.styluslabs.write;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Date;

public class NotesDbAdapter {
    private static final String DATABASE_CREATE = "create table drawings (_id integer primary key autoincrement, title text, body text, sortorder integer, createtime integer, modifytime integer, tags text, thumbnail blob);";
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "drawings";
    private static final int DATABASE_VERSION = 3;
    public static final String KEY_BODY = "body";
    public static final String KEY_CREATETIME = "createtime";
    public static final String KEY_MODIFYTIME = "modifytime";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_SORTORDER = "sortorder";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_THUMBNAIL = "thumbnail";
    public static final String KEY_TITLE = "title";
    private static final String TAG = "NotesDbAdapter";
    private final Context mCtx;
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, NotesDbAdapter.DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, (int) NotesDbAdapter.DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(NotesDbAdapter.DATABASE_CREATE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion <= 2 && newVersion > 2) {
                db.execSQL("ALTER TABLE drawings ADD COLUMN tags DEFAULT '';");
            }
        }
    }

    public NotesDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }

    public NotesDbAdapter open() throws SQLException {
        this.mDbHelper = new DatabaseHelper(this.mCtx);
        this.mDb = this.mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        this.mDbHelper.close();
    }

    public void reset() {
        this.mDb.execSQL("DROP TABLE IF EXISTS drawings");
        this.mDb.execSQL(DATABASE_CREATE);
    }

    public long createNote(Long sortorder, String title, String body) {
        return createNote(sortorder, title, body, null, null, 0);
    }

    public long createNote(Long sortorder, String title, String body, String tags, byte[] thumb, long timestamp) {
        ContentValues initialValues = new ContentValues();
        if (sortorder != null) {
            initialValues.put(KEY_SORTORDER, sortorder);
        }
        if (title != null) {
            initialValues.put(KEY_TITLE, title);
        }
        initialValues.put(KEY_BODY, body);
        if (tags != null) {
            initialValues.put(KEY_TAGS, tags);
        }
        if (thumb != null && thumb.length > 0) {
            initialValues.put(KEY_THUMBNAIL, thumb);
        }
        if (timestamp <= 0) {
            timestamp = new Date().getTime();
        }
        initialValues.put(KEY_CREATETIME, Long.valueOf(timestamp));
        initialValues.put(KEY_MODIFYTIME, Long.valueOf(timestamp));
        return this.mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean deleteNote(long rowId) {
        return this.mDb.delete(DATABASE_TABLE, new StringBuilder("_id=").append(rowId).toString(), null) > 0;
    }

    public void deleteTag(String oldtag) {
        renameTag(oldtag, "");
    }

    public void renameTag(String oldtag, String newtag) {
        ContentValues args = new ContentValues();
        args.put(KEY_TAGS, newtag);
        this.mDb.update(DATABASE_TABLE, args, "tags = ?", new String[]{oldtag});
    }

    public Cursor fetchAllNotes(String orderby) {
        return this.mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_TAGS, KEY_THUMBNAIL}, null, null, null, null, orderby);
    }

    public Cursor fetchNotes(String tags, String orderby) {
        return this.mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_TAGS, KEY_THUMBNAIL}, "tags = ?", new String[]{tags}, null, null, orderby);
    }

    public Cursor fetchTags() {
        return this.mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TAGS}, null, null, null, null, null, null);
    }

    public Cursor fetchNote(long rowId) throws SQLException {
        Cursor mCursor = this.mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_TAGS}, "_id=" + rowId, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor fetchNoteWithBody(String body) throws SQLException {
        Cursor mCursor = this.mDb.query(true, DATABASE_TABLE, new String[]{KEY_ROWID, KEY_TITLE, KEY_BODY, KEY_TAGS}, "body='" + body + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public boolean updateNote(long rowId, String field, String value) {
        ContentValues args = new ContentValues();
        args.put(field, value);
        return updateRow(rowId, args);
    }

    public boolean updateNote(long rowId, String title, String body, String tags, byte[] thumb) {
        ContentValues args = new ContentValues();
        if (title != null) {
            args.put(KEY_TITLE, title);
        }
        if (body != null) {
            args.put(KEY_BODY, body);
        }
        if (tags != null) {
            args.put(KEY_TAGS, tags);
        }
        if (thumb != null && thumb.length > 0) {
            args.put(KEY_THUMBNAIL, thumb);
        }
        args.put(KEY_MODIFYTIME, Long.valueOf(new Date().getTime()));
        return updateRow(rowId, args);
    }

    public boolean updateRow(long rowId, ContentValues args) {
        if (args.size() <= 0 || this.mDb.update(DATABASE_TABLE, args, "_id=" + rowId, null) <= 0) {
            return false;
        }
        return true;
    }
}
