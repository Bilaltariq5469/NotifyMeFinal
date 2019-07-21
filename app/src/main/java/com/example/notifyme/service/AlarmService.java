package com.example.notifyme.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.notifyme.activity.AddAlarmActivity;
import com.example.notifyme.database.DataBaseManager;
import com.example.notifyme.model.Alarm;
import com.example.notifyme.receiver.AlarmReceiver;
import com.example.notifyme.ultil.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AlarmService extends Service {
    MediaPlayer mediaPlayer; // this object to manage media
    private NotificationManager alarmNotificationManager;
    DataBaseManager dataBaseManager;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO: processing on and off ringtone
        // get string from intent
        String on_Off = intent.getExtras().getString("ON_OFF");
        switch (on_Off) {
            case Constants.ADD_INTENT: // if string like this set start media
                // this is system default alarm alert uri
                Uri uri = Settings.System.DEFAULT_ALARM_ALERT_URI;

                // create mediaPlayer object
                mediaPlayer = MediaPlayer.create(this, uri);

                SharedPreferences sharedPreferences = getSharedPreferences("myprefs", MODE_PRIVATE);
                boolean vibrate = sharedPreferences.getBoolean("vibrate",false);
                boolean ringtone = sharedPreferences.getBoolean("ringtone",false);
                boolean silent = sharedPreferences.getBoolean("silent",false);

                if(ringtone)
                    mediaPlayer.start();
                if(vibrate)
                    Vibrate(10000);
                if(silent)
                    Silent();

                Date currentTime = Calendar.getInstance().getTime();

                dataBaseManager = new DataBaseManager(this);
                // get Alarm ArrayList from database
                Alarm alarm = dataBaseManager.getdata(currentTime.getHours(), currentTime.getMinutes());
                sendNotification(alarm.getAlarm_Name());
//                SharedPreferences sharedPreferences = getSharedPreferences("AlarmMessage",MODE_PRIVATE);
//                int count = sharedPreferences.getInt("alarm_no",0);
//                for(int i=0; i<=count && i!=0; i++)
//                {
//                    String savedtime = sharedPreferences.getString("time_"+i,"");
//                    String []split = savedtime.split(":");
//                    if(Integer.parseInt(split[0]) == currentTime.getHours() && Integer.parseInt(split[1]) == currentTime.getMinutes())
//                    {
//                        sendNotification(sharedPreferences.getString("message_"+i,""));
//                    }
//                }
                break;
            case Constants.OFF_INTENT:
                // this check if user pressed cancel
                // get the alarm cancel id to check if equal the
                // pendingIntent'trigger id(pendingIntent request code)
                // the AlarmReceiver.pendingIntentId is taken from AlarmReceiver
                // when one pendingIntent trigger
                int alarmId = intent.getExtras().getInt("AlarmId");
                // check if mediaPlayer created or not and if media is playing and id of
                // alarm and trigger pendingIntent is same  then stop music and reset it
                if (mediaPlayer != null && mediaPlayer.isPlaying() && alarmId == AlarmReceiver.pendingId) {
                    // stop media
                    mediaPlayer.stop();
                    // reset it
                    mediaPlayer.reset();
                }
                break;


        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO: Xử lý logic tắt nhạc chuông
        mediaPlayer.stop();
        mediaPlayer.reset();
    }
    private void sendNotification(String msg) {
        Log.d("AlarmService", "Preparing to send notification...: " + msg);
        alarmNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_channel_03",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            alarmNotificationManager.createNotificationChannel(channel);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AddAlarmActivity.class), 0);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(android.R.drawable.arrow_down_float)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);


        alamNotificationBuilder.setContentIntent(contentIntent);
        alarmNotificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void Vibrate(int duration)
    {
        Vibrator vibs = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibs.vibrate(duration);
    }
    public void Silent()
    {
        try {
            final AudioManager mode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
            mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }catch (Exception ex)
        {
            Log.d("OKOK",ex.getMessage());
        }

    }
}