package com.example.alexfanning.silentplaces;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

/**
 * Created by alex.fanning on 09/11/2017.
 */

public class Geofencing implements ResultCallback {

    private static final String TAG = Geofencing.class.getSimpleName();
    private static final float GEOFENCE_RADIUS = 50; //Read from shared pref
    private static final long GEOFENCE_TIMEOUT = 24* 60 * 60 * 1000; //24Hrs

    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mClient;
    private Context mContext;

    public Geofencing(Context context, GoogleApiClient client){
        mContext = context;
        mClient = client;
        mGeofencePendingIntent = null;
        mGeofenceList = new ArrayList<>();
    }

    @Override
    public void onResult(@NonNull Result result) {

    }

    public void updateList(PlaceBuffer places){
        mGeofenceList = new ArrayList<>();
        if (places == null || places.getCount() == 0) return;
        for (Place place :places){
            String placeID = place.getId();
            double lat = place.getLatLng().latitude;
            double lon = place.getLatLng().longitude;

            Geofence geofence = new Geofence.Builder()
                    .setRequestId(placeID)
                    .setExpirationDuration(GEOFENCE_TIMEOUT)
                    .setCircularRegion(lat,lon,GEOFENCE_RADIUS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build();
            mGeofenceList.add(geofence);

        }
    }

    private GeofencingRequest getGeofencingRequest(){
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    public void registerAllGeofences(){
        if (mClient == null || !mClient.isConnected() || mGeofenceList == null || mGeofenceList.size() == 0)
            return;

        try{
            LocationServices.GeofencingApi.addGeofences(mClient,getGeofencingRequest(),getGeofencePendingIntent()).setResultCallback(this);

        }catch (SecurityException se){
            Log.e(TAG, "Security Excepetion");
        }
    }


    public void unRegisterAllGeofences(){
        if (mClient == null || !mClient.isConnected()){
            return;
        }
        try{
            LocationServices.GeofencingApi.removeGeofences(mClient,getGeofencePendingIntent()).setResultCallback(this);
        }catch (SecurityException se){
            Log.e(TAG, "Security Exception");
        }
    }

    private PendingIntent getGeofencePendingIntent(){
        if (mGeofencePendingIntent != null){
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(mContext,GeofenceBroadcastReceiver.class);
        mGeofencePendingIntent = PendingIntent.getBroadcast(mContext,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }






}
