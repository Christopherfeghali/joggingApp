package com.example.runningapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

/*MapsActivity is responsible for showing our current location on the map and leaves markers on map to know where we were before our destination change
 * includes methods that allow the map to be manipulated such as zoom in/out, rotate, etc. */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    double currentLatitude;
    double currentLongitude;
    LatLng CurrlatLng;

    private GoogleMap mMap;
   private Boolean Locpermission = false;
    private LocationProvider locationProvider;
    private LocationManager locationManager;
    private static final int LOCATION_CODE_PERMISSION = 0000;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getPermission();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //getStartLocation();
        getCurrentLocation();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near where we are.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) { // on launch of map activity do the following
        mMap = googleMap;
        Toast.makeText(this,"Map is Ready",Toast.LENGTH_SHORT).show();
    }

    public void getPermission()//gets permission from the manifest file
    {
        String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
            Locpermission = true;
        }
        else
            {
                ActivityCompat.requestPermissions(this,permission,LOCATION_CODE_PERMISSION);
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) //requests permission from the user if they havent enables lcoation permissions yet
    {
        Locpermission = false;
        switch (requestCode) {
            case LOCATION_CODE_PERMISSION: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            Locpermission = false;
                            return;
                        }
                    }
                    Locpermission = true;
                }
            }
        }
    }


    public void getCurrentLocation()// shows live feed of users movements according to the requestlocationupdate method
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



        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {// listens for any change in location
            @Override
            public void onLocationChanged(Location location) {

                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();
                CurrlatLng = new LatLng(currentLatitude,currentLongitude);
                //calcDistance();

                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {

                    List<Address> addressList = geocoder.getFromLocation(currentLatitude,currentLongitude,5000);
                    String str = addressList.get(0).getLocality();
                    str += addressList.get(0).getCountryName() + " ";
                    mMap.addMarker(new MarkerOptions().position(CurrlatLng).title(str));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(CurrlatLng));

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}