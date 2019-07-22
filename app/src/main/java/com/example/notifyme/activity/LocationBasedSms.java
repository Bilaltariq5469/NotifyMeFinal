package com.example.notifyme.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notifyme.R;
import com.example.notifyme.service.Service_Connection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationBasedSms extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    Button btn_next;
    FloatingActionButton fab;
    EditText et_cordinates;
    private BottomSheetBehavior bottomSheetBehavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_based_sms);
        changeStatusBarColor();
        initializeViews();
        setClickListeners();
    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    public void initializeViews()
    {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btn_next = (Button) findViewById(R.id.next);
        et_cordinates = (EditText) findViewById(R.id.cordinates);


        // Initializing Objects
        fab = findViewById(R.id.fab);
        final View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    public void setClickListeners()
    {
        btn_next.setOnClickListener(this);
        fab.setOnClickListener(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setTrafficEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btn_next.getId())
        {
            if(et_cordinates.getText().toString().length() > 0) {
                    SharedPreferences sharedPreferences = getSharedPreferences("LocationBasedSms",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cordinates", et_cordinates.getText().toString());
                    editor.commit();
                    Intent intent = new Intent(LocationBasedSms.this, LocationBasedSmsNext.class);
                    startActivity(intent);
                    // Save it in Shared Preferences and Start Service if it was in radius then silent phone
//                  // final AudioManager mode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//                  // mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);

            }
            else
            {
                Toast.makeText(this, "Select Location First", Toast.LENGTH_SHORT).show();
            }
        }
        else if(view.getId() == fab.getId())
        {
            expandorcollapseBottomSheet();
        }
    }

    public void expandorcollapseBottomSheet() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            rotateFabBackward();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            rotateFabForward();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void rotateFabForward() {
        ViewCompat.animate(fab)
                .rotation(180.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(5.0F))
                .start();
    }

    public void rotateFabBackward() {
        ViewCompat.animate(fab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(5.0F))
                .start();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
        mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);
        et_cordinates.setText(String.valueOf(latLng.latitude) + "/" + String.valueOf(latLng.longitude));
    }
}
