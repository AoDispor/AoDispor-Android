package pt.aodispor.android;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;

import java.util.List;

public class GeoLocation {

    //TODO shouldn't always get a new position
    //TODO see http://stackoverflow.com/questions/10524381/gps-android-get-positioning-only-once

    LocationManager locationManager;

    private String lat = "";
    private String lon = "";

    public String getLatitude() {
        return lat;
    }

    public String getLongitude() {
        return lon;
    }

    public void updateLatLon(Context context) {
        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location loc = locationManager.getLastKnownLocation(provider);
                if (loc != null) {
                    lat = "" + loc.getLatitude();
                    lon = "" + loc.getLongitude();
                    break;
                }
            }
        }
    }

}
