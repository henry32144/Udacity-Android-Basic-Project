package com.henry.android.tourguideapp;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.henry.android.tourguideapp.R.id.map;

public class DetailActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String locationName;
    private String locationInfo;
    private int imageResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        //Get data from extras
        Bundle extras = intent.getExtras();
        TextView title = (TextView) findViewById(R.id.place_title);

        //Check if the data has name
        if (extras.getString("name") != null) {
            locationName = extras.getString("name");
            title.setText(locationName);
        }

        //Check if  the data has image resource
        if (extras.getInt("image") != 0) {
            imageResource = extras.getInt("image");
            title.setBackgroundResource(imageResource);
        }

        TextView info = (TextView) findViewById(R.id.place_info);
        //Check if  the data has image resource
        if (extras.getString("info") != null) {
            locationInfo = extras.getString("info");
            info.setText(locationInfo);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String address;
        address = locationName;
        // get address in string for used location for the map

        /* get latitude and longitude from the adderress */
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocationName(address, 5);
            if (addresses.size() > 0) {
                Double lat = (double) (addresses.get(0).getLatitude());
                Double lon = (double) (addresses.get(0).getLongitude());

                final LatLng user = new LatLng(lat, lon);

                 /*used marker for show the location */
                googleMap.addMarker(new MarkerOptions()
                        .position(user)
                        .title(address));
                // Move the camera instantly to hamburg with a zoom of 10.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 10));
            }
            //If doesnt find the location, then locate at Thunghai university.
            else {
                 /*used marker for show the location */
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(24.181587, 120.603122))
                        .title("ThungHai University"));

                // Move the camera instantly to hamburg with a zoom of 10.
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.181587, 120.603122), 10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
