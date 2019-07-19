package com.example.notifyme.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.widget.Toast;
import android.view.WindowManager;

import com.example.notifyme.R;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    CardView mix_and_match, location_based_alarm, set_alarm, send_message, location_based_sms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        changeStatusBarColor();
        initializeViews();
        setClickListeners();
    }

    public void changeStatusBarColor()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    public void initializeViews()
    {
        mix_and_match = (CardView)findViewById(R.id.mix_and_match);
        location_based_alarm = (CardView)findViewById(R.id.location_based_alarm);
        set_alarm = (CardView)findViewById(R.id.set_alarm);
        send_message = (CardView)findViewById(R.id.send_message);
        location_based_sms = (CardView)findViewById(R.id.location_based_sms);
    }

    public void setClickListeners()
    {
        mix_and_match.setOnClickListener(this);
        location_based_alarm.setOnClickListener(this);
        set_alarm.setOnClickListener(this);
        send_message.setOnClickListener(this);
        location_based_sms.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(set_alarm.getId() == view.getId())
        {
            Toast.makeText(this, "Set Alarm", Toast.LENGTH_SHORT).show();
            Intent setalarm = new Intent(HomePageActivity.this, AlarmMainActivity.class);
            startActivity(setalarm);
        }
        else if(location_based_alarm.getId() == view.getId())
        {
            Toast.makeText(this, "Location based alarm", Toast.LENGTH_SHORT).show();
            Intent locationbasedIntent = new Intent(HomePageActivity.this, LocationBasedService.class);
            startActivity(locationbasedIntent);
        }
        else if(mix_and_match.getId() == view.getId())
        {
            Toast.makeText(this, "Mix and Match", Toast.LENGTH_SHORT).show();
            Intent mixMatch = new Intent(HomePageActivity.this, MixandMatchActivity.class);
            startActivity(mixMatch);
        }
        else if(send_message.getId() == view.getId())
        {
            Toast.makeText(this, "Send Message", Toast.LENGTH_SHORT).show();
            Intent sendMessage = new Intent(HomePageActivity.this, SendMessage.class);
            startActivity(sendMessage );
        }
        else if(location_based_sms.getId() == view.getId())
        {
            Toast.makeText(this, "Location based Sms", Toast.LENGTH_SHORT).show();
            Intent locationbasedsendMessage = new Intent(HomePageActivity.this, LocationBasedSms.class);
            startActivity(locationbasedsendMessage);
        }
    }
}
