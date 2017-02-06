package com.example.naval.crime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Toolbar toolbar;
    CheckBox checkBox;

    ActionMode actionMode;
    ArrayAdapter<String> adapter;

    DBHandler myDB;
    crime_incident incident_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDB();
        setContentView(R.layout.activity_maps);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crime Alert!");

        //toolbar.setNavigationIcon(R.drawable.crimealert);
        //toolbar.setNavigationContentDescription("Navigation Icon");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ArrayList<String> arraylocation  = myDB.getAllLocations();
        adapter = new ArrayAdapter<>(MapsActivity.this,android.R.layout.simple_list_item_1,arraylocation);
        //lv.setAdapter(adapter);
    }

    public void createDB() {
        myDB = new DBHandler(this);
        myDB.clearIncidentTable();
        double lat = 47.7136844;
        double lng = -122.2074087;
        String loc = "Kirkland";
        for (int i = 0; i < 300; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.5;
            double y = (rand.nextDouble() * 2 - 1)*0.5;
            System.out.println(x);
            System.out.println(y);
            incident_ = new crime_incident(2, rand.nextInt(5), lat + x , lng + y, 1001, loc, 2);
            myDB.insertIncident(incident_);
        }
//        incident_ = new crime_incident(3,2,-122,41,1001,"Redmond",2);
//        myDB.insertIncident(incident_);
//        incident_ = new crime_incident(1,1,47.7136844,-122.2074087,1000,"Kirkland",1);
//        myDB.insertIncident(incident_);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        TextView txtLocation;

        // Add a marker in Sydney and move the camera
        LatLng latlng = null;

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //newLocationChange(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
//            return;
//        }
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Log.d("Network", "Network");
            if (locationManager != null) {
                Location loc = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(loc != null)
                    latlng = new LatLng(loc.getLatitude(), loc.getLongitude());
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,18));
        addIncidentsOnMap();
    }

    public void addIncidentsOnMap() {
        ArrayList<String> allIncidents = myDB.getAllIncidents();
        for (int i = 0; i < allIncidents.size(); i++) {
            String incident = allIncidents.get(i);
            System.out.println(incident);
            double lat = Double.parseDouble(incident.split(",")[1]);
            double lng = Double.parseDouble(incident.split(",")[2]);
            int type = Integer.parseInt(incident.split(",")[4]);
            String loc = incident.split(",")[3];
            LatLng latlng = new LatLng(lat,lng);
            mMap.addMarker(new MarkerOptions().position(latlng).title(loc)
            //        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_theft))
            );
        }
    }

    public void onSearch(String location) {
        List<Address> addressList = null;
        if (location != null || !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location,1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Address address = addressList.get(0);
        LatLng latlng = new LatLng(address.getLatitude(), address.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,18));
    }

    public void changeMapType() {
        if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            Toast.makeText(MapsActivity.this, "Satellite Map Mode", Toast.LENGTH_SHORT).show();
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Toast.makeText(MapsActivity.this, "Normal Map Mode", Toast.LENGTH_SHORT).show();
        }
    }

    // For Toolbar Menu Items.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.menuSearch);
        SearchView searchView = (SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //adapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings:
                Toast.makeText(MapsActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_mapType:
                changeMapType();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}