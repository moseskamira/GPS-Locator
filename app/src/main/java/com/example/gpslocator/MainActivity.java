package com.example.gpslocator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText locationEt;
    private TextView addressLineTv, cityTv, regionTv, countryTv;
    private static final int REQUEST_CODE = 10;
    private Geocoder geocoder;
    private double latitude, longitude;
    private String myAddress, myCity, myState, knownName, myCountry;
    private LatLng myLatLongCoordinates;
    ProgressDialog progressDialog;


    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationEt = findViewById(R.id.location_et);
        addressLineTv = findViewById(R.id.address_tv);
        cityTv = findViewById(R.id.city_tv);
        regionTv = findViewById(R.id.region_tv);
        countryTv = findViewById(R.id.country_tv);
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        locationCallback = new LocationCallback();
        locationRequest = new LocationRequest();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        getLocationPermission();
    }

    private void getLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager
                    .PERMISSION_GRANTED && checkSelfPermission(Manifest.permission
                    .ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission
                        .ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET}, REQUEST_CODE);
            }else {
                updateLocation();
            }

        }else {
            updateLocation();
        }
    }

    private void updateLocation() {
        progressDialog.setMessage("Fetching Location");
        progressDialog.show();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(
                getApplicationContext());

        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                task.addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            progressDialog.dismiss();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            myLatLongCoordinates = new LatLng(latitude, longitude);
                            convertToAddress(latitude, longitude);

                        }else {
                            Toast.makeText(getApplicationContext(),"Unable To Fetch Location",Toast.LENGTH_LONG).show();
                        }

                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        e.printStackTrace();

                    }
                });

            }
        });
    }

    private void convertToAddress(double myLatitude, double myLongitude) {
        try {

            List<Address> addressList = geocoder.getFromLocation(myLatitude, myLongitude, 1);

            if (!addressList.isEmpty()) {
                myAddress = addressList.get(0).getAddressLine(0);
                myCity = addressList.get(0).getLocality();
                myState = addressList.get(0).getAdminArea();
                knownName = addressList.get(0).getFeatureName();
                myCountry = addressList.get(0).getCountryName();

                Log.d("MY LATITUDE", "" + myLatitude);
                Log.d("MY LONGITUDE", "" + myLongitude);

                Log.d("MY ADDRESS", myAddress);
                Log.d("MY CITY", myCity);
                Log.d("MY STATE", myState);
                Log.d("KNOWN NAME", knownName);
                Log.d("my COUNTRY", myCountry);

                updateUI();

            }else {
                Log.d("EMPTY", "LIST");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        locationEt.setText(String.valueOf(myLatLongCoordinates));
        addressLineTv.setText(myAddress);
        cityTv.setText(myCity);
        regionTv.setText(myState);
        countryTv.setText(myCountry);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantedResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantedResults.length > 0 && grantedResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateLocation();
                }
        }
    }
}
