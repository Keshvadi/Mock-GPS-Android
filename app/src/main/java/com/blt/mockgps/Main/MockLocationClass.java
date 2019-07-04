package com.blt.mockgps.Main;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import static android.location.LocationManager.GPS_PROVIDER;

public class MockLocationClass {

    private Context context;
    private String TAG = MockLocationClass.class.getSimpleName();

    public MockLocationClass(Context context){
        this.context = context;
    }

    public void setMock(double latitude, double longitude, float accuracy) {

        try {
            Log.i(TAG,"Location Change Command");

            LocationManager locMgr = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

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

            try{
                String strMessage = "latitude: "+latitude+"\n longitude: "+ longitude;
                Log.i(TAG,strMessage);
            }catch (Exception e){e.printStackTrace();}

        } catch (Exception e) {
            e.printStackTrace();
            try{Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show();}catch (Exception ex){ex.printStackTrace();}
        }
    }

//

}
