package com.example.alexfanning.silentplaces;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.alexfanning.silentplaces.activities.MainActivity;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

/**
 * Created by alex.fanning on 10/11/2017.
 */

public class GeofenceBroadcastReceiver extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()){
            Log.e(TAG, String.format("Error code : %d", geofencingEvent.getErrorCode()));
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
//        List<Geofence> gs = geofencingEvent.getTriggeringGeofences();
//        Geofence a = gs.get(0);

        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER){
            setRingerMode(context,AudioManager.RINGER_MODE_SILENT);
        }else if(geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT){
            setRingerMode(context,AudioManager.RINGER_MODE_NORMAL);
        }else{
            Log.e(TAG, "Error");
            return;
        }
        sendNotification(context,geofenceTransition);

    }


    private void sendNotification(Context context, int transitionType){
        Intent notificationIntent = new Intent(context, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent pi = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER){
            builder.setContentTitle("Silent Mode Activated");
        }else if (transitionType == Geofence.GEOFENCE_TRANSITION_EXIT){
            builder.setContentTitle("Back to normal");
        }

        builder.setContentText("Touch to launch app");
        builder.setContentIntent(pi);

        builder.setAutoCancel(true);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(0,builder.build());
    }





    private void setRingerMode(Context context, int mode){
        NotificationManager nm = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT < 24 || (Build.VERSION.SDK_INT >= 24 && !nm.isNotificationPolicyAccessGranted())){
            AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setRingerMode(mode);
        }
    }

}
