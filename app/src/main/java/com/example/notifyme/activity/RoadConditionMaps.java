package com.example.notifyme.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
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
import com.example.notifyme.database.DataBaseManager;
import com.example.notifyme.database.RoadConditionDb;
import com.example.notifyme.model.Alarm;
import com.example.notifyme.model.RoadCondition;
import com.example.notifyme.service.RoadConditionService;
import com.example.notifyme.service.TrackPointsService;
import com.example.notifyme.view.AlarmAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class RoadConditionMaps extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener {

    private GoogleMap mMap;
    RoadConditionDb roadConditionDb;
    EditText et_cordinates, et_message;
    Button btn_saveMarker;
    FloatingActionButton fab;
    BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_condition_maps);
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
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        et_cordinates = findViewById(R.id.cordinates);
        et_message = findViewById(R.id.message);
        btn_saveMarker = findViewById(R.id.save_marker);
        fab = findViewById(R.id.fab);

        // Initializing Objects
        roadConditionDb = new RoadConditionDb(this);
        final View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
    }

    public void setClickListeners() {
        btn_saveMarker.setOnClickListener(this);
        fab.setOnClickListener(this);
    }

    private void importData() {
        // if alarmAdapter null it's means data have not imported, yet or database is empty
        // initialize database manager
        // get RoadCondition ArrayList from database
        ArrayList<RoadCondition> roadCondition = roadConditionDb.getRoadList();
        for (int i = 0; i < roadCondition.size(); i++) {
            drawMarker(roadCondition.get(i).getMessage(), roadCondition.get(i).getCordinates());
        }
    }

    void drawMarker(String message, String cordinates) {
        LatLng latLng = new LatLng(Double.parseDouble(cordinates.split("/")[0]), Double.parseDouble(cordinates.split("/")[1]));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(message);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);
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
        mMap.setTrafficEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setMyLocationEnabled(true);
        importData();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Point");
        //mMap.clear();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(markerOptions);
        et_cordinates.setText(String.valueOf(latLng.latitude) + "/" + String.valueOf(latLng.longitude));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_marker) {
            if (et_cordinates.getText().length() > 0) {
                if (et_message.getText().length() > 0) {
                    RoadCondition roadCondition = new RoadCondition(et_message.getText().toString(), et_cordinates.getText().toString());
                    roadConditionDb.insert(roadCondition);
                    Toast.makeText(this, "Marker Added Successfully", Toast.LENGTH_SHORT).show();
                    et_cordinates.setText("");
                    et_message.setText("");
                    expandorcollapseBottomSheet();
                    Intent intent=new Intent(RoadConditionMaps.this, RoadConditionService.class);
                    startService(intent);

                } else {
                    Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Tap on Map to get Cordinates", Toast.LENGTH_SHORT).show();
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
}