package com.example.notifyme.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.notifyme.R;
import com.google.android.gms.common.internal.FallbackServiceBroker;

import java.text.Format;

import belka.us.androidtoggleswitch.widgets.ToggleSwitch;

public class MixandMatchActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean vibrate,ringtone,silent;
    ToggleSwitch vibrateToggleSwitch,ringtoneToggleSwitch,silentToggleSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mixand_match);

        editor = getSharedPreferences("myprefs", MODE_PRIVATE).edit();
        sharedPreferences = getSharedPreferences("myprefs", MODE_PRIVATE);

//        intilizeValues();

        ringtoneToggleSwitch = (ToggleSwitch) findViewById(R.id.ringtone_switch);
        vibrateToggleSwitch = (ToggleSwitch) findViewById(R.id.vibration_switch);
        silentToggleSwitch = (ToggleSwitch) findViewById(R.id.silent_switch);

        ringtoneToggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                // Write your code ...
                ringtone = position == 1;
            }
        });
        vibrateToggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                // Write your code ...
                vibrate = position == 1;
            }
        });
        silentToggleSwitch.setOnToggleSwitchChangeListener(new ToggleSwitch.OnToggleSwitchChangeListener(){

            @Override
            public void onToggleSwitchChangeListener(int position, boolean isChecked) {
                // Write your code ...
                silent = position == 1;
            }
        });

    }

    private void setToggels(Boolean prev_ringtone, Boolean prev_vibrate, Boolean prev_silent) {
        if(prev_ringtone)
            ringtoneToggleSwitch.setCheckedTogglePosition(1);
        else
            ringtoneToggleSwitch.setCheckedTogglePosition(0);
        if(prev_vibrate)
            vibrateToggleSwitch.setCheckedTogglePosition(1);
        else
            vibrateToggleSwitch.setCheckedTogglePosition(0);
        if(prev_silent)
            silentToggleSwitch.setCheckedTogglePosition(1);
        else
            silentToggleSwitch.setCheckedTogglePosition(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Boolean prev_vibrate = sharedPreferences.getBoolean("vibrate",false);
        Boolean prev_ringtone = sharedPreferences.getBoolean("ringtone",false);
        Boolean prev_silent = sharedPreferences.getBoolean("silent",false);
        setToggels(prev_ringtone,prev_vibrate,prev_silent);
    }

    private void intilizeValues() {
        vibrate = sharedPreferences.getBoolean("vibrate",false);
        ringtone = sharedPreferences.getBoolean("ringtone",false);
        silent = sharedPreferences.getBoolean("silent",false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putBoolean("vibrate",vibrate);
        editor.putBoolean("ringtone",ringtone);
        editor.putBoolean("silent",silent);
        editor.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        editor.putBoolean("vibrate",vibrate);
        editor.putBoolean("ringtone",ringtone);
        editor.putBoolean("silent",silent);
        editor.commit();
    }
}
