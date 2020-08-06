package com.example.gpslocator;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText locationEt;
    private TextView addressLineTv, cityTv, regionTv, countryTv;
    private Geocoder geocoder;
    private String myAddress, myCity, myState, myCountry;
    private LatLng myLatLongCoordinates;
    private GPSTracker gpsTracker;


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
        gpsTracker = new GPSTracker(getApplicationContext(), this);

        updateGPS();
    }

    private void updateGPS() {
        if (gpsTracker != null) {
            Location location = gpsTracker.getLocation();
            if (location != null) {
                convertToAddress(location);
            }else {
                location = gpsTracker.fetchLocation();
                convertToAddress(location);
            }
        }
    }


    private void convertToAddress(Location myLoc) {
        try {
            double latitude = myLoc.getLatitude();
            double longitude = myLoc.getLongitude();
            myLatLongCoordinates = new LatLng(latitude, longitude);
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);

            if (!addressList.isEmpty()) {
                myAddress = addressList.get(0).getAddressLine(0);
                myCity = addressList.get(0).getLocality();
                myState = addressList.get(0).getAdminArea();
                myCountry = addressList.get(0).getCountryName();
                Log.d("MY ADDRES", myAddress);
                if (!myAddress.isEmpty()) {
                    updateUI();

                }else {
                    Toast.makeText(this, "EMPTY ADDRES", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(this, "EMPTY ADDRESLIST", Toast.LENGTH_LONG).show();
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
}
