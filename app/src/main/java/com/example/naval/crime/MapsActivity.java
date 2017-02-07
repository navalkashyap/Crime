package com.example.naval.crime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.SearchView;
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
    ArrayAdapter<String> adapter;

    DBHandler myDB;
    crime_incident incident_;
    LatLng currentLatlng = null;
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
        for (int i = 0; i < 50; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.5;
            double y = (rand.nextDouble() * 2 - 1)*0.5;
            incident_ = new crime_incident(2, rand.nextInt(12), lat + x , lng + y, 1001, loc, 2);
            myDB.insertIncident(incident_);
        }
        loc = "Bothell";
        lat = 47.759497;
        lng = -122.190601;
        for (int i = 0; i < 50; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.03;
            double y = (rand.nextDouble() * 2 - 1)*0.03;
            incident_ = new crime_incident(2, rand.nextInt(12), lat + x , lng + y, 1001, loc, 2);
            myDB.insertIncident(incident_);
        }

        loc = "Seattle";
        lat = 47.720323;
        lng = -122.329173;
        for (int i = 0; i < 50; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.03;
            double y = (rand.nextDouble() * 2 - 1)*0.03;
            incident_ = new crime_incident(2, rand.nextInt(12), lat + x , lng + y, 1001, loc, 2);
            myDB.insertIncident(incident_);
        }

        loc = "Redmond";
        lat = 47.677348;
        lng = -122.123764;
        for (int i = 0; i < 20; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.03;
            double y = (rand.nextDouble() * 2 - 1)*0.03;
            incident_ = new crime_incident(2, rand.nextInt(12), lat + x , lng + y, 1001, loc, 2);
            myDB.insertIncident(incident_);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Log.d("Network", "Network");
            if (locationManager != null) {
                Location loc = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(loc != null)
                    currentLatlng = new LatLng(loc.getLatitude(), loc.getLongitude());
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatlng,16));
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
            switch (type) {
                case 0:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Burglary @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.burglary)));
                    break;
                case 1:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Car Prowl @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carprowl)));
                    break;
                case 2:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Collision @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.collision)));
                    break;
                case 3:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Crisis @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.crisis)));
                    break;
                case 4:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Disturbance @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.disturbance)));
                    break;
                case 5:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("False Alarm @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.falsealarm)));
                    break;
                case 6:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Fire Inactive @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fireinactive)));
                    break;
                case 7:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Liquor @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.liquor)));
                    break;
                case 8:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Narcotics @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.narcotics)));
                    break;
                case 9:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Suspicious @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.suspicious)));
                    break;
                case 10:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Threats @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.threats)));
                    break;
                case 11:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Traffic @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.traffic)));
                    break;
                case 12:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Unsafe @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.unsafe)));
                    break;
                case 13:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Weapon @"+loc)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.weapon)));
                    break;
                default:
                    mMap.addMarker(new MarkerOptions().position(latlng).title("Incident @"+loc));
                    break;
            }
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