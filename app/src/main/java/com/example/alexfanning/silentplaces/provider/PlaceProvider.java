package com.example.alexfanning.silentplaces.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by alex.fanning on 23/10/2017.
 */

public class PlaceProvider extends ContentProvider {
    public static final int PLACE = 100;
    public static final int PLACE_WITH_ID = 101;

    private static final String HASH_PATH = "/*";
    private static final String PLACE_SELECTION_ID = "place_id=?";
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(PlaceContract.CONTENT_AUTHORITY, PlaceContract.PATH_PLACE, PLACE);
        uriMatcher.addURI(PlaceContract.CONTENT_AUTHORITY, PlaceContract.PATH_PLACE + HASH_PATH, PLACE_WITH_ID);
        return uriMatcher;
    }
    private PlaceDbHelper mPlaceDbHelper;
    @Override
    public boolean onCreate() {
        mPlaceDbHelper = new PlaceDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mPlaceDbHelper.getWritableDatabase();
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case PLACE:

                long id = db.insert(PlaceContract.PlaceEntry.TABLE_NAME,null,contentValues);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(PlaceContract.PlaceEntry.CONTENT_URI,id);
                }else{
                    throw new SQLException("Failed to insert :" + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unkown Uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mPlaceDbHelper.getReadableDatabase();

        Cursor retCursor;
        switch (sUriMatcher.match(uri)){

            case PLACE:
                retCursor = db.query(PlaceContract.PlaceEntry.TABLE_NAME,projection,selection,selectionArgs,null,null,sortOrder);
                break;
            case PLACE_WITH_ID:
                String id = uri.getLastPathSegment();
                String[] mselectionArgs = new String[]{id};
                retCursor = db.query(PlaceContract.PlaceEntry.TABLE_NAME,projection,PLACE_SELECTION_ID,mselectionArgs,null,null,sortOrder);
                break;
            default :
                throw new UnsupportedOperationException("Unkown Uri: " + uri);
        }

        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }



    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mPlaceDbHelper.getWritableDatabase();

        int tasksDeleted;

        switch (sUriMatcher.match(uri)){
            case PLACE_WITH_ID:
                String id = uri.getLastPathSegment();
                tasksDeleted = db.delete(PlaceContract.PlaceEntry.TABLE_NAME, PLACE_SELECTION_ID, new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unkown Uri: " + uri);
        }

        if (tasksDeleted != 0)
            getContext().getContentResolver().notifyChange(uri,null);

        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mPlaceDbHelper.getWritableDatabase();

        int placesUpdated;

        switch (sUriMatcher.match(uri)){
            case PLACE_WITH_ID:
                String id = uri.getLastPathSegment();
                placesUpdated = db.update(PlaceContract.PlaceEntry.TABLE_NAME,contentValues, PLACE_SELECTION_ID, new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unkown Uri: " + uri);
        }
        if (placesUpdated != 0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return placesUpdated;
    }
}
