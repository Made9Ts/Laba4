package com.example.laba4;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "songs.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_SONGS = "songs";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ARTIST = "artist";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SONGS_TABLE = "CREATE TABLE " + TABLE_SONGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ARTIST + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_SONGS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SONGS);
        onCreate(db);
    }

    public void addSong(String artist, String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ARTIST, artist);
        values.put(COLUMN_TITLE, title);
        db.insert(TABLE_SONGS, null, values);
        db.close();
    }

    public Cursor getAllSongs() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SONGS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC", null);
    }

    public String getLastSong() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_TITLE + " FROM " + TABLE_SONGS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT 1", null);
        String lastSong = "";
        if (cursor.moveToFirst()) {
            lastSong = cursor.getString(0);
        }
        cursor.close();
        return lastSong;
    }
}
