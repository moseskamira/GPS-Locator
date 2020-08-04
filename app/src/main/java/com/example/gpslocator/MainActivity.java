package com.example.gpslocator;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText locationEt;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static final int REQUEST_CODE = 10;

    private Geocoder geocoder;
    private List<Address> addressList;
    private double latitude, longitude;
    private String myLocation, myAddress, myCity, myState, knownName, myCountry;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationEt = findViewById(R.id.location_et);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        geocoder = new Geocoder(this, Locale.getDefault());

        instantiateLocationListner();
        checkBuildVersion();

    }

    private void instantiateLocationListner() {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                myLocation = latitude +" , "+ longitude;

                convertToAddress(latitude, longitude);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };
    }

    private void checkBuildVersion() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                    .PERMISSION_GRANTED && checkSelfPermission(Manifest.permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                        .ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, REQUEST_CODE);

                return;
            } else {
                fetchCurrentLocation();

            }
        }else {
            fetchCurrentLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantedResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantedResults.length > 0 && grantedResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchCurrentLocation();
                    return;
                }
        }

    }


    public void fetchCurrentLocation() {

        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);


    }

    private void convertToAddress(double myLatitude, double myLongitude) {
        try {
            addressList = geocoder.getFromLocation(myLatitude, myLongitude, 1);
            myAddress = addressList.get(0).getAddressLine(0);
            myCity = addressList.get(0).getLocality();
            myState = addressList.get(0).getAdminArea();
            knownName = addressList.get(0).getFeatureName();
            myCountry = addressList.get(0).getCountryName();

            Log.d("MY LATITUDE", ""+myLatitude);
            Log.d("MY LONGITUDE", ""+myLongitude);

            Log.d("MY ADDRESS", myAddress);
            Log.d("MY CITY", myCity);
            Log.d("MY STATE", myState);
            Log.d("KNOWN NAME", knownName);
            Log.d("my COUNTRY", myCountry);

            setToViews();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setToViews() {
        locationEt.setText(myLocation);

    }
}
