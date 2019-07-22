package com.example.notifyme.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.notifyme.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,View.OnClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    Location currentLocation;
    boolean isFirst = true;
    boolean iszoomed = false;
    float currentZoon = 18;
    TextView addressTextview;
    Button confirmButton;
    double lat,log;

    public void centerMapOnLocation(Location location, String title) {
        if (location == null) {
            //Toast.makeText(this, "Hello", Toast.LENGTH_SHORT).show();
            return;
        }
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        lat = location.getLatitude();
        log = location.getLongitude();
        mMap.clear();
        addressTextview.setText("http://maps.google.com/?q=" + lat + "," + log);
        SharedPreferences sharedPreferences = getSharedPreferences("SelectedAddress", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Lat", String.valueOf(lat));
        editor.putString("Log", String.valueOf(log));
        editor.putString("Address", addressTextview.getText().toString());
        editor.commit();

        mMap.addMarker(new MarkerOptions().position(userLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, currentZoon));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        addressTextview = findViewById(R.id.select_address);
        confirmButton=findViewById(R.id.confirmlocation);
        confirmButton.setOnClickListener(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // userLocationFAB();

        // mMap.getUiSettings().setZoomControlsEnabled(true);
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
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Intent intent = getIntent();
        //Toast.makeText(this, intent.getIntExtra("placenumber", -1) + "", Toast.LENGTH_SHORT).show();

        //if (intent.getIntExtra("placenumber", 0) == 0) {
        //zoom in on user location
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        //checking location is on or not
//        boolean gps_enabled = false;
//        boolean network_enabled = false;
//        try {
//            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//        } catch(Exception ex) {}
//
//        try {
//            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//        } catch(Exception ex) {}
//        if(!gps_enabled && !network_enabled)
//        {
//            showSettingsAlert();
//        }
        //end of checking location on or not
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(isFirst)
                {
                    currentLocation=location;
                    isFirst=false;
                }
                //centerMapOnLocation(location, "Your Location");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }else {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                mMap.setMyLocationEnabled(true);
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                Location lastknownlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastknownlocation==null)
                {
                    //Toast.makeText(/, "", Toast.LENGTH_SHORT).show();
                    LocationManager locationManager2=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    locationManager2.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                    lastknownlocation=locationManager2.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                centerMapOnLocation(lastknownlocation,"Your Location");
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }

        //}
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLng center=mMap.getCameraPosition().target;
                Location location=new Location(LocationManager.NETWORK_PROVIDER);
                location.setLongitude(center.longitude);
                location.setLatitude(center.latitude);
                if(isFirst)
                {
                    currentLocation=location;
                }
                Log.i("CameraIdle Liner","Called");
                if(currentLocation.distanceTo(location)>10||isFirst==true)
                {
                    currentLocation = location;
                    centerMapOnLocation(currentLocation, "Your Location");
                    isFirst=false;
                }
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            //private float currentZoom = 18;

            @Override
            public void onCameraChange(CameraPosition position) {
                if (position.zoom != currentZoon){
                    iszoomed=true;
                    currentZoon=position.zoom;
                    //currentZoom = position.zoom;  // here you get zoom level
                }
            }
        });
        centerMapOnLocation(currentLocation, "Your Location");
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.confirmlocation)
        {
            onBackPressed();
        }
    }
}
