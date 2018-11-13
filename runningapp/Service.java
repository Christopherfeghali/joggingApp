package com.example.runningapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Christopher Feghali on 26/12/2017.
 * Service class is used to track location of the user as well as the elapsed time.
 * Even when the activity is closed the service still runs and tracks location
 * after tracking is done (onDestroy method) is called and the date is inserted into the database.
 */

public class Service extends android.app.Service {

    long tStart;
    long tEnd;
    long tDelta;

    double elapsedSeconds;
    double currentLatitude;
    double currentLongitude;
    double startLatitude;
    double startLongitude;
    double totalDis = 0;

    LatLng startlatLng;
    LatLng CurrlatLng;
    Calendar calendar;


    private Boolean Locpermission = false;
    private LocationManager locationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getStartLocation();
        getCurrentLocation();
        tStart = System.currentTimeMillis();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tEnd = System.currentTimeMillis();
        tDelta = tEnd - tStart;
        elapsedSeconds = tDelta / 1000.0;
        ContentValues values = new ContentValues();
        values.put(MyContentProvider.name,getDay()+"   Duration: "+ elapsedSeconds);
        values.put(MyContentProvider.distance, String.valueOf(totalDis));
        Uri uri = getContentResolver().insert(MyContentProvider.CONTENT_URL,values);
        Toast.makeText(getBaseContext(), "added run session", Toast.LENGTH_SHORT).show();

    }

    public void getStartLocation() // gets the initial co-ordinates of user's location
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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



        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                startLatitude = location.getLatitude();
                startLongitude = location.getLongitude();
                startlatLng = new LatLng(startLatitude,startLongitude);
                locationManager.removeUpdates(this);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

    }

    public void getCurrentLocation() // gets live feed of users movements according to the requestlocationupdate method and proceeds to calculate distance travelled with each change in gps location
    {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
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



        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                CurrlatLng = new LatLng(currentLatitude,currentLongitude);
                calcDistance();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        });

    }

    public void calcDistance() // equation used in locationchange to calculate the distance between longitude and latitude values
    {
        Double distance = SphericalUtil.computeDistanceBetween(startlatLng, CurrlatLng);
        totalDis = totalDis + distance;
    }

    public String getDay() // method used to find the day of the week
    {
        int dayString = calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        switch(dayString){
            case 0:
                return "Sunday";
            case 1:
                return "Monday";
            case 2:
                return "Tuesday";
            case 3:
                return "Wednesday";
            case 4:
                return "Thursday";
            case 5:
                return  "Friday";
            case 6:
                return "Saturday";
        }

        return "Wrong Day";
    }


    public void sendTime () // Broadcast to be used in future needs of the application
    {
        Intent intent = new Intent();
        intent.setAction("com.example.runningapp");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(intent);
    }


}


