package com.example.notifyme.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.example.notifyme.R;
import com.example.notifyme.database.TrackPointsDb;
import com.example.notifyme.model.RoadCondition;
import com.example.notifyme.model.TrackPoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ViewPointsMaps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    TrackPointsDb trackPointsDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_points_maps);
        changeStatusBarColor();
        initializeViews();

    }

    public void initializeViews() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        trackPointsDb = new TrackPointsDb(this);
    }

    public void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }
    }

    private void importData() {
        // if alarmAdapter null it's means data have not imported, yet or database is empty
        // initialize database manager
        // get RoadCondition ArrayList from database
        ArrayList<TrackPoints> trackPoints = trackPointsDb.getTrackList();
        for (int i = 0; i < trackPoints.size(); i++) {
            if (trackPoints.get(i).getColor().equals("GREEN")) {
                drawMarker("GREEN", trackPoints.get(i).getCordinates());
            } else {
                drawMarker("RED", trackPoints.get(i).getCordinates());
            }
        }
    }

    void drawMarker(String color, String cordinates) {
        LatLng latLng = new LatLng(Double.parseDouble(cordinates.split("/")[0]), Double.parseDouble(cordinates.split("/")[1]));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        if (color.equals("GREEN")) {
            markerOptions.title("Visited");
            mMap.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        } else {
            markerOptions.title("Not Visited");
            mMap.addMarker(markerOptions).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
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
        mMap.setMyLocationEnabled(true);
        importData();
    }
}
