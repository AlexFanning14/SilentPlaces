package com.example.alexfanning.silentplaces.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by alex.fanning on 23/10/2017.
 */

public class PlaceDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME  = "place.db";
    private static final String DROP_TABLE_IF_EXISTS ="DROP TABLE IF EXISTS ";

    private static final int DATABASE_VERSION = 1;

    public PlaceDbHelper(Context c){super(c, DATABASE_NAME,null,DATABASE_VERSION);}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_PLACE_TABLE =
                "CREATE TABLE " + PlaceContract.PlaceEntry.TABLE_NAME + " (" +
                        PlaceContract.PlaceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        PlaceContract.PlaceEntry.COLUMN_PLACE_ID + " TEXT NOT NULL, " +
                        PlaceContract.PlaceEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                        PlaceContract.PlaceEntry.COLUMN_SILENT_MODE + " INTEGER NOT NULL);";
        sqLiteDatabase.execSQL((SQL_CREATE_PLACE_TABLE));
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(DROP_TABLE_IF_EXISTS + PlaceContract.PlaceEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
