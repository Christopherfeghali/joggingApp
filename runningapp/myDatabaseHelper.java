package com.example.runningapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Christopher Feghali on 24/12/2017.
 * Class myDatabasehelper is used to create our database where we store our values obtained from the service class
 */

public class myDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "names";
    public static final String DATABASE_NAME = "myRun";

    public static final String _ID = "_id";
    public static final String RUN_TITLE = "session";
    public static final String RUN_DISTANCE = "distance";

    static final int DATABASE_VERSION = 1;

    private static final String CREATE_TABLE = "create table " + TABLE_NAME + "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " // creates table with following coloumns
            + RUN_TITLE + " TEXT NOT NULL, " + RUN_DISTANCE + " TEXT" + ")";

    myDatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) { // on creation execute sql function of creating the specified table above
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
