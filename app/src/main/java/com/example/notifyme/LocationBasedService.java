package com.example.notifyme;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.notifyme.Services.Service_Connection;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class LocationBasedService extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    EditText et_cordinates, et_radius;
    Button btn_set_marker;
    private FloatingActionButton fab;
    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_based_service);
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

    public void initializeViews() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        et_cordinates = (EditText) findViewById(R.id.cordinates);
        et_radius = (EditText) findViewById(R.id.radius);
        btn_set_marker = findViewById(R.id.set_marker);

        // Initializing Objects
        fab = findViewById(R.id.fab);
        final View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    public void setClickListeners() {
        btn_set_marker.setOnClickListener(this);
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
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
        // Moving to Faisalabad
//        LatLng currentlocation = new LatLng(31.4504,73.1350);//provider name is unnecessary
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 16));
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

    @Override
    public void onClick(View view) {
        if(view.getId() == btn_set_marker.getId())
        {
            if(et_cordinates.getText().toString().length() > 0) {
                if (et_radius.getText().toString().length() > 0) {
                    SharedPreferences sharedPreferences = getSharedPreferences("LocationBased",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("cordinates", et_cordinates.getText().toString());
                    editor.putString("radius", et_radius.getText().toString());
                    editor.commit();
                    if(!ismyservicerunning(Service_Connection.class))
                    {
                        Intent intent=new Intent(LocationBasedService.this,Service_Connection.class);
                        startService(intent);
                        Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();
                    }
                    // Save it in Shared Preferences and Start Service if it was in radius then silent phone
//                  // final AudioManager mode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//                  // mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                }
                else
                {
                    Toast.makeText(this, "Enter Radius", Toast.LENGTH_SHORT).show();
                }
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

    public boolean ismyservicerunning(Class<?> service)
    {
        ActivityManager manager= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo:manager.getRunningServices(Integer.MAX_VALUE))
        {
            if(service.getName().equals(serviceInfo.service.getClassName()))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
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
