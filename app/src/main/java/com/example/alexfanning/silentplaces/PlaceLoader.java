package com.example.alexfanning.silentplaces;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.alexfanning.silentplaces.provider.PlaceContract;
import com.google.android.gms.location.places.Place;

/**
 * Created by alex.fanning on 23/10/2017.
 */

public class PlaceLoader extends AsyncTaskLoader<SilentPlace[]> {
private static final String TAG = PlaceLoader.class.getSimpleName();
    public static final int PLACE_LOADER_ID = 22;
    private Context c;
    public PlaceLoader(Context c){
        super(c);
        this.c = c;
    }


    @Override
    public SilentPlace[] loadInBackground() {
        Cursor cursor;
        try{
            cursor = c.getContentResolver().query(PlaceContract.PlaceEntry.CONTENT_URI,null,null,null,null);
        }catch (Exception e){
            Log.e(TAG, "loadInBackground: "+ e.getMessage());
            return null;
        }
        int INDEX_PLACE_ID = cursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_PLACE_ID);
        int INDEX_DESC = cursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_DESCRIPTION);
        int INDEX_SILENT_MODE = cursor.getColumnIndex(PlaceContract.PlaceEntry.COLUMN_SILENT_MODE);

        SilentPlace[] places = new SilentPlace[cursor.getCount()];
        int i = 0;
        try{
            while(cursor.moveToNext()){
                String placeId = cursor.getString(INDEX_PLACE_ID);
                String desc = cursor.getString(INDEX_DESC);
                int sm = cursor.getInt(INDEX_SILENT_MODE);
                places[i] = new SilentPlace(placeId,desc,sm);
                i++;
            }
        }finally {
            cursor.close();
        }
        return places;

    }
}
