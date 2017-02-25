package com.example.naval.crime;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Toolbar toolbar;
    DBHandler myDB = new DBHandler(this);
    public LatLng currentLatLng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crime Alert!");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {}
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            Log.d("Network", "Network");
            if (locationManager != null) {
                Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(loc != null)
                    currentLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},200);
//            while (true) {
//                permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
//                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
//
//                    Log.i("INFO", "Got permissions, exiting block loop");
//                    break;
//                }
//                Log.i("INFO", "Sleeping, waiting for permissions");
//                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
//            }
        }
        // Need to add a wait for user's response
        mMap.setMyLocationEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,16));
        new RetrieveFeedTask().execute(currentLatLng,this,true,mMap);
    }

    public void addIncidentsOnMap(DBHandler myDB, GoogleMap mMap) {
        ArrayList<String> allIncidents = myDB.getAllIncidents();
        if(allIncidents.size() == 0)
            return;
        for (int i = 0; i < allIncidents.size(); i++) {
            String incident = allIncidents.get(i);
            System.out.println(incident);
            double lat = Double.parseDouble(incident.split(";")[1]);
            double lng = Double.parseDouble(incident.split(";")[2]);
            int type = Integer.parseInt(incident.split(";")[4]);
            String description = incident.split(";")[3];
            LatLng latlng = new LatLng(lat,lng);
            switch (type) {
                case -1:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.crisis)));
                    break;
                case 0:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.burglary)));
                    break;
                case 1:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carprowl)));
                    break;
                case 2:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.collision)));
                    break;
                case 3:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.crisis)));
                    break;
                case 4:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.disturbance)));
                    break;
                case 5:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.falsealarm)));
                    break;
                case 6:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fireinactive)));
                    break;
                case 7:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.liquor)));
                    break;
                case 8:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.narcotics)));
                    break;
                case 9:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.suspicious)));
                    break;
                case 10:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.threats)));
                    break;
                case 11:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.traffic)));
                    break;
                case 12:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.unsafe)));
                    break;
                case 13:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.weapon)));
                    break;
                default:
                    mMap.addMarker(new MarkerOptions().position(latlng).title(description));
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
        myDB.deleteAllIncident();
        new RetrieveFeedTask().execute(latlng,this,true,mMap);
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

    public void show_notification(Context context) {
        Intent intent = new Intent();
        PendingIntent pIntent = PendingIntent.getActivity(context,0,intent,0);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if(alarmSound == null){
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }
        Notification noti = new Notification.Builder(MapsActivity.this)
                .setTicker("TickerTitle")
                .setContentTitle("Warning!")
                .setContentText("You have reached a crime-prone area.Please be cautious!")
                .setSmallIcon(R.drawable.crimealert)
                .setSound(alarmSound)
                .setContentIntent(pIntent).getNotification();
        noti.flags = Notification.FLAG_AUTO_CANCEL;
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0,noti);
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
            case R.id.show_notification:
                show_notification(MapsActivity.this);
                break;
            case R.id.show_DBinfo:
                myDB.deleteAllIncident();
                break;
            case R.id.getCount:
                Toast.makeText(MapsActivity.this, "DB count " + myDB.getCount(), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}