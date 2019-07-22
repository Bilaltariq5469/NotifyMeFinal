package com.example.notifyme.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.notifyme.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationEngineListener, MapboxMap.OnMapClickListener {

    String tag = "Directions Activity : ";
    private static int DISPLACEMENT = 10;
    private static final long UPDATE_INTERVAL = 1000, FASTEST_INTERVAL = 1000;
    private FusedLocationProviderClient mFusedLocationClient;
    Location currentlocation;

    private MapView mapView;
    LocationEngine locationEngine;
    LocationLayerPlugin locationLayerPlugin;
    private MapboxMap map;
    private Location originLocation;


    private Point originPosition;
    NavigationMapRoute navigationMapRoute;

    LocationCallback updatelocationcallback;
    private LocationRequest mLocationRequest;
    // For Google Api Client
    Status status;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    Marker mCurrent;
    MapboxNavigation navigation;
    Button navigation_button;
    private Marker destinationMarker;
    private Point destinationPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Mapbox.getInstance(this, "pk.eyJ1IjoiYmlsYWx0YXJpcSIsImEiOiJjanFveDlwaWEwYXkxNDNtaXU1ZjYwaWFqIn0.D_BjRBIcOyFHZwrhliXhXg");
        setContentView(R.layout.activity_get_direction);
        navigation = new MapboxNavigation(this, "pk.eyJ1IjoiYmlsYWx0YXJpcSIsImEiOiJjanFveDlwaWEwYXkxNDNtaXU1ZjYwaWFqIn0.D_BjRBIcOyFHZwrhliXhXg");
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        initializeCallbacks();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        initializeCallbacks();
        initializeGoogleApiClient();
        settingintentforgpspermission();
        initializeobjects();
    }


    public void getRoute(Point origin, Point destination) {
        NavigationRoute.builder().accessToken(Mapbox.getAccessToken()).origin(origin).destination(destination).build().getRoute(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (response.body() == null) {
                    Log.e(tag, "No Routes Found Check user and Access Token");
                    return;
                } else if (response.body().routes().size() == 0) {
                    Log.e(tag, "No routes found");
                    return;
                }
                DirectionsRoute currentRoute = response.body().routes().get(0);
                if (navigationMapRoute != null) {
                    navigationMapRoute.removeRoute();
                } else {
                    navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                }
                navigationMapRoute.addRoute(currentRoute);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Log.d(tag, "Error " + t.getMessage());
            }
        });

    }

    private void initializeobjects() {
        navigation_button = findViewById(R.id.navigation_button);
        navigation_button.setOnClickListener(this);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.map = mapboxMap;
        map.addOnMapClickListener(this);
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationEngine.getLastLocation() != null) {
            originLocation = locationEngine.getLastLocation();
        } else {
            locationEngine.addLocationEngineListener(DirectionsActivity.this);
        }
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!checkPlayServices()) {
            Toast.makeText(this, "You need to install Google Play Services to use the App properly", Toast.LENGTH_SHORT).show();
        }
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
        mapView.onStart();
    }


    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.navigation_button) {
            startnavigation();
        }
    }


    public void initializeGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();
    }

    private void settingintentforgpspermission() {
        LocationRequest locationrequest = new LocationRequest();
        locationrequest.setInterval(10000);
        locationrequest.setFastestInterval(5000);
        locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationrequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {

                status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        if (ActivityCompat.checkSelfPermission(DirectionsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DirectionsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        //mFusedLocationClient.requestLocationUpdates(mLocationRequest, updatelocationcallback, Looper.myLooper());
                        Toast.makeText(DirectionsActivity.this, "You are now online", Toast.LENGTH_SHORT).show();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult(DirectionsActivity.this, 15283);

                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 15283:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        try {
                            status.startResolutionForResult(DirectionsActivity.this, 15283);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setuplocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }
        return true;
    }

    @Override
    public void onConnected() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //Toast.makeText(this, "Updating Location", Toast.LENGTH_SHORT).show();
            originLocation = location;
            originPosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
            LatLng point1 = new LatLng(location.getLatitude(), location.getLongitude());
            if (mCurrent != null) {
                map.removeMarker(mCurrent);
            }
            mCurrent = map.addMarker(new MarkerOptions()
                    .position(point1));
            CameraPosition position = new CameraPosition.Builder()
                    .target(point1) // Sets the new camera position
                    .zoom(15) // Sets the zoom
                    .tilt(-90).build(); // Creates a CameraPosition from the builder

            map.moveCamera(CameraUpdateFactory
                    .newCameraPosition(position));
        }
        else
        {
            originPosition = Point.fromLngLat( 74.339521,31.557448);
        }
    }

    private void startnavigation() {
        NavigationLauncherOptions options = NavigationLauncherOptions.builder().origin(originPosition).destination(destinationPosition).build();
        NavigationLauncher.startNavigation(this, options);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, updatelocationcallback, Looper.myLooper());
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private void setuplocation() {
        createLocationRequest();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentlocation = location;
                } else {
                    Toast.makeText(DirectionsActivity.this, "Can't get your location", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initializeCallbacks() {
        updatelocationcallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult.getLocations().get(0) != null) {
                    currentlocation = locationResult.getLocations().get(0);
                }
            }
        };
    }

    @Override
    public void onMapClick(@NonNull LatLng point1) {
        if (destinationMarker != null) {
            map.removeMarker(destinationMarker);
        }
        destinationMarker = map.addMarker(new MarkerOptions()
                .position(point1));
        destinationPosition = Point.fromLngLat(point1.getLongitude(),point1.getLatitude());
        //originPosition = Point.fromLngLat(originLocation.getLongitude(),originLocation.getLatitude());
        getRoute(originPosition,destinationPosition);
        NavigationLauncherOptions options = NavigationLauncherOptions.builder().origin(originPosition).destination(destinationPosition).build();
        NavigationLauncher.startNavigation(this,options);
    }
}