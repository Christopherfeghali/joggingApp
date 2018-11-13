package com.example.runningapp;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.util.HashMap;

import static com.example.runningapp.myDatabaseHelper.TABLE_NAME;

/**
 * Created by Christopher Feghali on 24/12/2017.
 * Content provider is the actual content of the database and is responsible for any changes the database goes through including insert,delete,query,update,etc.
 */

public class MyContentProvider extends ContentProvider {

    static final String PROVIDER_NAME = "com.example.runningapp.MyContentProvider";
    static final String URL ="content://" + PROVIDER_NAME +"/" + TABLE_NAME;
    static final Uri CONTENT_URL = Uri.parse(URL);
    static final String id = myDatabaseHelper._ID;// primary key
    static final String name = myDatabaseHelper.RUN_TITLE;// the name of the session run in this case, the day of the week
    static final String distance = myDatabaseHelper.RUN_DISTANCE;// the distance that has been travelled
    static final int uriCode = 1;

    private  static HashMap<String,String> values;
    private SQLiteDatabase db;
    private int TotalRun;

    static final UriMatcher uriMatcher;
    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, TABLE_NAME, uriCode);
    }

    @Override
    public boolean onCreate() {
        myDatabaseHelper dbHelper = new myDatabaseHelper(getContext());
        db = dbHelper.getWritableDatabase();
        if(db != null)
        {
            return true;
        }
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);
        switch (uriMatcher.match(uri))
        {
            case uriCode:
                queryBuilder.setProjectionMap(values);
                break;
            default:
                throw new IllegalArgumentException("URI not supported" + uri );
        }
        Cursor cursor = queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri))
        {
            case uriCode:
                return "vnd.android.cursor.dir/" + TABLE_NAME;
            default:
                throw new IllegalArgumentException("URI not supported" + uri );
        }    }

    @Override
    public Uri insert(Uri uri, ContentValues values) { // insert values in database that uses contentProvider
        long rowID = db.insert(TABLE_NAME, null, values);
        if(rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(CONTENT_URL,rowID);
            getContext().getContentResolver().notifyChange(_uri,null);
            return _uri;
        }
        Toast.makeText(getContext(),"Row Insert Failed",Toast.LENGTH_LONG).show();
        return null;    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) { // delete values in database that uses contentProvider
        int rowsDeleted = 0;
        switch (uriMatcher.match(uri))
        {
            case uriCode:
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Uri not supported " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) { // updates values in database that uses contentProvider
        int rowsUpdated = 0;
        switch (uriMatcher.match(uri))
        {
            case uriCode:
                rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Uri not supported " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return rowsUpdated;
    }

    public Cursor rawQuery(String string, Object object) {
        // TODO Auto-generated method stub
        return null;
    }

}
