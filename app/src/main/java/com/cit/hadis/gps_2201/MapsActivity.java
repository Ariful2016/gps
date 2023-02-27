package com.cit.hadis.gps_2201;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.cit.hadis.gps_2201.databinding.ActivityMapsBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    Location location;

    double lat,lng;

    List<Address> addressList;

    Address address;

    String myCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        addressList = new ArrayList<>();

        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {

                            location = getLocation();

                            mapFragment.getMapAsync(new OnMapReadyCallback() {
                                @Override
                                public void onMapReady(@NonNull GoogleMap googleMap) {

                                    if (location != null){

                                        lat = location.getLatitude();
                                        lng = location.getLongitude();


                                    }

                                    Log.i("TAG", "onMapReady: "+ lat + " "+ lng);

                                    mMap = googleMap;
                                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                    mMap.getUiSettings().setZoomControlsEnabled(true);

                                    // Add a marker in Sydney and move the camera
                                    LatLng myPoint = new LatLng(lat, lat);


                                    Geocoder geocoder = new Geocoder(MapsActivity.this);

                                    try {
                                        addressList = geocoder.getFromLocation(lat,lng, 1);

                                        address = addressList.get(0);

                                        myCurrentLocation = address.getAddressLine(0);

                                        Log.i("TAG", "onMapReady: "+myCurrentLocation);

                                        String[] token = myCurrentLocation.split(",");

                                        mMap.addMarker(new MarkerOptions().position(myPoint)
                                                .title(token[2])
                                                .snippet(token[0]+", "+token[3] +", "+ token[4]));
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPoint, 18));


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });


                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
                }).check();


    }


    public Location getLocation() {

        Location location = null;
        Location bestLocation = null;

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        List<String> provider = locationManager.getProviders(true);

        for (String p : provider) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            location = locationManager.getLastKnownLocation(p);

            if (location == null){
                continue;
            }

            if (bestLocation == null || location.getAccuracy() > bestLocation.getAccuracy()){

                bestLocation = location;
            }


        }

        return  bestLocation;

    }


}