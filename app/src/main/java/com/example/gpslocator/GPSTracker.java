package com.example.gpslocator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;
public class GPSTracker implements LocationListener, ActivityCompat.OnRequestPermissionsResultCallback {

    private Context context;
    private Activity activity;
    private Location bestLocation = null;
    private static final int LOCATION_REQUEST_CODE = 23;

    GPSTracker(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    Location getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_REQUEST_CODE);

            }else {
                return fetchLocation();
            }
        }else {
            return fetchLocation();

        }
        return fetchLocation();
    }

    Location fetchLocation() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            List<String> providers = locationManager.getProviders(true);
            for (String provider : providers) {
                try {
                    Location location = locationManager.getLastKnownLocation(provider);
                    if (location != null) {
                        if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                            bestLocation = location;
                        }

                    }

                }catch (Exception exc) {
                    exc.printStackTrace();

                }

            }
            return bestLocation;
        }
        return bestLocation;
    }

    @Override
    public void onLocationChanged(Location location) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
        }
    }
}
