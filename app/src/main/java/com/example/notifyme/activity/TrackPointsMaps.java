package com.example.notifyme.activity;

import android.content.Intent;
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
import android.widget.Toast;

import com.example.notifyme.R;
import com.example.notifyme.database.TrackPointsDb;
import com.example.notifyme.model.TrackPoints;
import com.example.notifyme.service.Service_Connection;
import com.example.notifyme.service.TrackPointsService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class TrackPointsMaps extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    Button btn_saveMarkers, btn_clear;
    FloatingActionButton fab;
    BottomSheetBehavior bottomSheetBehavior;
    TrackPointsDb trackPointsDb;
    ArrayList<String> trackPointsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_points_maps);
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
        btn_saveMarkers = findViewById(R.id.save_markers);
        fab = findViewById(R.id.fab);
        btn_clear = findViewById(R.id.clear);

        // Initializing Objects
        trackPointsDb = new TrackPointsDb(this);
        final View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        trackPointsArrayList = new ArrayList<>();
    }

    public void setClickListeners()
    {
        btn_saveMarkers.setOnClickListener(this);
        fab.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
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
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == btn_saveMarkers.getId())
        {
            if(trackPointsArrayList.size() > 0) {
                for(int i=0;i<trackPointsArrayList.size();i++) {
                    TrackPoints trackPoints = new TrackPoints("RED",trackPointsArrayList.get(i));
                    trackPointsDb.insert(trackPoints);
                }
                mMap.clear();
                Toast.makeText(this, "Points Saved Successfully", Toast.LENGTH_SHORT).show();
                expandorcollapseBottomSheet();
                Intent intent=new Intent(TrackPointsMaps.this, TrackPointsService.class);
                startService(intent);
            }
            else
            {
                Toast.makeText(this, "Add Points First", Toast.LENGTH_SHORT).show();
            }
        }
        else if(btn_clear.getId() == view.getId())
        {
            mMap.clear();
        }
        else if(fab.getId() == view.getId())
        {
            expandorcollapseBottomSheet();
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);
        trackPointsArrayList.add(String.valueOf(latLng.latitude) + "/" + String.valueOf(latLng.longitude));
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
}
