package com.example.notifyme;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.notifyme.activity.AlarmMainActivity;

public class HomePageActivity extends AppCompatActivity implements View.OnClickListener {

    CardView mix_and_match, location_based_alarm;
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
        mix_and_match = findViewById(R.id.mix_and_match);
        location_based_alarm = findViewById(R.id.location_based_alarm);
    }

    public void setClickListeners()
    {
        mix_and_match.setOnClickListener(this);
        location_based_alarm.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if(mix_and_match.getId() == view.getId())
        {
            Toast.makeText(this, "Mix and Match", Toast.LENGTH_SHORT).show();
            Intent mix_match = new Intent(HomePageActivity.this, AlarmMainActivity.class);
            startActivity(mix_match);
        }
        else if(location_based_alarm.getId() == view.getId())
        {
            Toast.makeText(this, "Location based alarm", Toast.LENGTH_SHORT).show();
            Intent locationbasedIntent = new Intent(HomePageActivity.this, LocationBasedService.class);
            startActivity(locationbasedIntent);
        }
    }
}
