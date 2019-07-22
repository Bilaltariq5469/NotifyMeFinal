package com.example.notifyme.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.notifyme.R;
import com.example.notifyme.activity.AddAlarmActivity;
import com.example.notifyme.database.TrackPointsDb;
import com.example.notifyme.model.TrackPoints;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class TrackPointsService extends Service {
    Context context = this;
    MediaPlayer mediaPlayer;
//    static Double longitude=73.18347;
//    static Double latitude=31.56781;
    // run on another Thread to avoid crash

    static int x = 2, y = 0,z=0;
    LocationManager manager;
    LocationListener listener;
    TrackPointsDb trackPointsDb;
    private NotificationManager locationNotificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //onTaskRemoved(intent);
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        getlocation();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restart = new Intent(getApplicationContext(), this.getClass());
        restart.setPackage(getPackageName());
        startService(restart);
        super.onTaskRemoved(rootIntent);
    }

    @SuppressLint("MissingPermission")
    void getlocation()
    {
        trackPointsDb = new TrackPointsDb(this);
        manager= (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location currentlocation) {
                checkradius(currentlocation);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
                Toast.makeText(context,"gps not found",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProviderDisabled(String provider) {
                PendingIntent intent=PendingIntent.getActivity(context,0,new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),0);
                NotificationCompat.Builder builder=new NotificationCompat.Builder(context).setColor(Color.GREEN).setContentText("GPS Disabled")
                        .setContentTitle("Gps").setSmallIcon(R.mipmap.ic_launcher_round);
                builder.addAction(R.mipmap.ic_launcher,"Allow gps",intent);
                builder.setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_SOUND);
                NotificationManagerCompat managerCompat= NotificationManagerCompat.from(context);
                managerCompat.notify(2,builder.build());
            }
        };
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,0,listener);
        }
        else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,0,listener);
        }
        else
        {
            PendingIntent intent=PendingIntent.getActivity(context,0,new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),0);
            NotificationCompat.Builder builder=new NotificationCompat.Builder(context).setColor(Color.GREEN).setContentText("GPS Disabled")
                    .setContentTitle("Gps").setSmallIcon(R.mipmap.ic_launcher_round);
            builder.setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_SOUND);
            builder.addAction(R.mipmap.ic_launcher,"Allow gps",intent);
            NotificationManagerCompat managerCompat= NotificationManagerCompat.from(context);
            managerCompat.notify(1,builder.build());
        }
        if(manager!=null)
        {
            Location currentlocation=manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentlocation!=null)
            {
                checkradius(currentlocation);
            }
        }
    }

    public void checkradius(Location currentlocation)
    {
        //Toast.makeText(context, "Checking Cordinates for Track Points", Toast.LENGTH_SHORT).show();
        ArrayList<TrackPoints> trackPointsList = trackPointsDb.getTrackList();
        for(int i=0;i<trackPointsList.size();i++) {
            Location savedlocation = new Location("");
            savedlocation.setLongitude(Double.parseDouble(trackPointsList.get(i).getCordinates().split("/")[1]));
            savedlocation.setLatitude(Double.parseDouble(trackPointsList.get(i).getCordinates().split("/")[0]));
            if ((currentlocation.distanceTo(savedlocation) / 1000) <= 1) {
                // change its color from red to green
                //Toast.makeText(context, "Changing Color", Toast.LENGTH_SHORT).show();
                generateNotification();
                trackPointsDb.updateColor(trackPointsList.get(i).getCordinates());
            }
        }
    }

    public void generateNotification()
    {
        locationNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
//                new Intent(this, AddAlarmActivity.class), 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_channel_02",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            locationNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("NotifyMe").setSmallIcon(android.R.drawable.arrow_down_float)
                .setStyle(new NotificationCompat.BigTextStyle().bigText("You have visited a Place"))
                .setContentText("You have visited a Place");


        alamNotificationBuilder.setContentIntent(null);
        locationNotificationManager.notify(1, alamNotificationBuilder.build());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.reset();
    }
}