package com.gas.go.mostafa.gogas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, OnMapReadyCallback {

    private static final int ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private boolean isDist;
    private boolean isFirst = true;
    private boolean mLocationPermissionGranted;
    private double lng = 0.0;
    private double lat = 0.0;
    private Marker customerMarker;

    private DatabaseReference mDatabase;
    private Map<String, Object> markersData;
    long currentTime;

    private Button btRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        final Calendar currentDate = Calendar.getInstance();
        currentTime = currentDate.getTimeInMillis();
        btRequest = findViewById(R.id.bt_request);

        // render the button of request if the user is customer only
        // check if the user is dist or customer
        Log.d("type", LoginActivity.getUserData().get("type") + "");
        isDist = LoginActivity.getUserData().get("type").equals("Distributors");
        if (isDist) {
            btRequest.setVisibility(View.GONE);
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

        //checks for permission using the Support library before enabling the My Location layer
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION);
        }
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        // get camera to current location
        LocationManager locationManager = (LocationManager) MapsActivity.this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, new LocationListener() {
            public void onLocationChanged(Location location) {
                double currentLongitude = location.getLongitude();
                double currentLatitude = location.getLatitude();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 17));

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                locationRequest();
            }

        });

        if (isDist) {
            this.getDistData();
        } else {
            this.customerRequest();
        }

    }

    private void locationRequest(){
        LocationRequest mlocationRequest = LocationRequest.create();
        mlocationRequest.setInterval(10000);
        mlocationRequest.setFastestInterval(5000);
        mlocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mlocationRequest);

//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
    }

    private void getDistData() {

        // get delivering locations
        DatabaseReference ref = mDatabase.child("requests/map");

        // database listener
        ValueEventListener userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Map dataSnapshotValue = (Map) dataSnapshot.getValue();
                markersData = new HashMap();

                if(dataSnapshotValue != null) {
                    for (Object i : dataSnapshotValue.keySet()) {
                        i = i.toString();

                        Log.d("current time", currentTime + "");

                        if (currentTime <= Long.parseLong((String) i)) {
                            continue;
                        }

                        Log.d("i", i + "");
                        Map data = (Map) dataSnapshotValue.get(i);

                        double lat = (double) data.get("lat");
                        double lng = (double) data.get("lng");
                        String name = (String) data.get("name");

                        // add marker
                        LatLng latLng = new LatLng(lat, lng);
                        String id = mMap.addMarker(new MarkerOptions().position(latLng).title(name)).getId();

                        markersData.put(id, data);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("cancel", "load:onCancelled", databaseError.toException());
                // ...
            }
        };
        ref.addValueEventListener(userListener);


        // marker onclick handler
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            public void onInfoWindowClick(Marker marker) {

                String markerId = marker.getId();
                Log.d("marker tag", marker.getId());
                Map data = (Map) markersData.get(markerId);

                double lat = (double) data.get("lat");
                double lng = (double) data.get("lng");
                String name = (String) data.get("name");
                String date = (String) data.get("date");
                String qty = (String) data.get("qty");
                String type = (String) data.get("type");
                String uid = (String) data.get("uid");
                long timeStamp = (long) data.get("timeStamp");

                Intent intent = new Intent(MapsActivity.this, MapDetailsActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("qty", qty);
                intent.putExtra("type", type);
                intent.putExtra("timeStamp", timeStamp);
                intent.putExtra("date", date);
                intent.putExtra("isManual", false);
                startActivity(intent);
            }
        });
    }

    private void customerRequest() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng arg0) {
                lng = arg0.longitude;
                lat = arg0.latitude;

                Log.d("langlat:", lat + ", " + lng);

                LatLng latlng = new LatLng(lat, lng);

                if (isFirst) {
                    customerMarker = mMap.addMarker(new MarkerOptions().position(latlng).title("Delivering location"));
                    isFirst = false;
                } else {
                    customerMarker.setPosition(latlng);
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
            }
        });
    }

    public void buttonRequest(View view) {
        Intent intent = new Intent(MapsActivity.this, BookingActivity.class);
        if (lng != 0.0) {
            intent.putExtra("lng", lng);
            intent.putExtra("lat", lat);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Tap on your on map to select your location", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                        return;
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
//        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
//        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

}
