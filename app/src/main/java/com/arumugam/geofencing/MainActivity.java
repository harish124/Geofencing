package com.arumugam.geofencing;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private TextView loc;
    private GoogleApiClient googleapiClient;
    private Location location;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loc = findViewById(R.id.location);

        checkingPermissions();

        googleapiClient= new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }

    private void checkingPermissions()
    {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission not granted..!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package",getPackageName(),null);
            intent.setData(uri);
            startActivity(intent);
        }

        if(!checkPlayServices())
        {
            Toast.makeText(this,"Google Play services app required.!",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"Google Play services is installed.!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            loc.setText("Location Unkown");
        } else {
            String s="Location : " + location.getLatitude() + location.getLongitude();
            loc.setText(s);
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000);
            } else {
                finish();
            }

            return false;
        }
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
            //checkingPermissions();
            location = LocationServices.FusedLocationApi.getLastLocation(googleapiClient);

            if(location!=null)
            {
                String s="location : "+location.getLatitude()+location.getLongitude();
                loc.setText(s);
            }
            startLocationUpdates();
    }

    private void startLocationUpdates() {

        //checkingPermissions();

        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleapiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(googleapiClient!=null)
        {
            googleapiClient.connect();
        }
        else{
            Toast.makeText(this,"Google API is not initialised",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //checkingPermissions();

        if(!googleapiClient.isConnected())
        {
            googleapiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(googleapiClient!=null && googleapiClient.isConnected())
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleapiClient,this);
            googleapiClient.disconnect();
        }
    }
}