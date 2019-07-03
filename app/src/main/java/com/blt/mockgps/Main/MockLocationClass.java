package com.blt.mockgps.Main;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import static android.location.LocationManager.GPS_PROVIDER;

public class MockLocationClass {

    private View parentview;
    private AppCompatActivity appCompatActivity;

    public MockLocationClass(AppCompatActivity appCompatActivity){
        this.appCompatActivity = appCompatActivity;
        parentview = appCompatActivity.findViewById(android.R.id.content);
    }

    public void setMock(double latitude, double longitude, float accuracy) {

        try {

            LocationManager locMgr = (LocationManager)
                    appCompatActivity.getSystemService(Context.LOCATION_SERVICE);

            locMgr.addTestProvider(GPS_PROVIDER,
                    "requiresNetwork" == "",
                    "requiresSatellite" == "",
                    "requiresCell" == "",
                    "hasMonetaryCost" == "",
                    "supportsAltitude" == "",
                    "supportsSpeed" == "",
                    "supportsBearing" == "",
                    android.location.Criteria.POWER_LOW,
                    android.location.Criteria.ACCURACY_FINE);

            Location newLocation = new Location(GPS_PROVIDER);

            newLocation.setLatitude(latitude);
            newLocation.setLongitude(longitude);
            newLocation.setAccuracy(accuracy);
            newLocation.setAltitude(0);
            newLocation.setAccuracy(500);
            newLocation.setTime(System.currentTimeMillis());
            newLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());

            locMgr.setTestProviderEnabled(GPS_PROVIDER, true);

            locMgr.setTestProviderStatus(GPS_PROVIDER,
                    LocationProvider.AVAILABLE,
                    null, System.currentTimeMillis());

            locMgr.setTestProviderLocation(GPS_PROVIDER, newLocation);

        } catch (Exception e) {
            e.printStackTrace();
            Snackbar.make(parentview, e.toString(), Snackbar.LENGTH_LONG);

        }
    }

//

}
