package com.example.alexfanning.silentplaces.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by alex.fanning on 23/10/2017.
 */

public class PlaceContract {

    public static final String CONTENT_AUTHORITY = "com.example.alexfanning.silentplaces";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_PLACE = "place";

    public static final class PlaceEntry implements BaseColumns{
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLACE).build();

        public static final String TABLE_NAME = "places";

        public static final String COLUMN_PLACE_ID = "place_id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_SILENT_MODE = "silent_mode";
        public static Uri buildPlaceUriWithId(int id){
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
        }
    }
}
