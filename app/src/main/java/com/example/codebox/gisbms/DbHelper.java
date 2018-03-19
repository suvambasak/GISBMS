package com.example.codebox.gisbms;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by codebox on 24/8/17.
 */

public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gisbms.db";
    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE gisInfo (issue TEXT, comment TEXT, time TEXT, date TEXT, lat TEXT, lng TEXT, accuracy TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS gisInfo";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
