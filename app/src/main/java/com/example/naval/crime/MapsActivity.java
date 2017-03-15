package com.example.naval.crime;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import static java.lang.Thread.sleep;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Toolbar toolbar;
    DBHandler myDB = new DBHandler(this);
    public LatLng currentLatLng = new LatLng(47.7136844,-122.2074087);

    // TAG for maps activity
    private final static String TAG = "Maps Activity";

    // previous location
    private Location previousLocation = null;

    // Service reference
    private LocationService locationService;

    // check whether the service is bound
    private boolean isServiceBound;

    // Set up Service Connection
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            locationService = ((LocationService.LocationBinder) service).getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            isServiceBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        // Start Location service
//        startService(new Intent(this, LocationService.class));

        // bind to Location service
        Log.i(TAG, "Call to enable binding with Location Service.");
        if(!isServiceBound){
            bindService(new Intent(this, LocationService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }

        setContentView(R.layout.activity_maps);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Crime Alert!");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        startCrimeAlertService();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        Log.i(TAG, "Unbind from Location Service");
        if(isServiceBound){
            unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    public void startCrimeAlertService() {
        Intent intent = new Intent(this,CrimeAlertService.class);
        startService(intent);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(isServiceBound && locationService.isBetterLocation(location, previousLocation))
                {
                    currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                    previousLocation = location;
                    new RetrieveFeedTask().execute(currentLatLng, MapsActivity.this, true, mMap,true);
                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        int permissionCheck = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 50000, 0, locationListener);
            Log.d("Network", "Network");
            if (locationManager != null) {
                Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(loc != null)
                    currentLatLng = new LatLng(loc.getLatitude(), loc.getLongitude());
            }
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        }
        // Need to add a wait for user's response
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,15));
        new RetrieveFeedTask().execute(currentLatLng, MapsActivity.this,true,mMap,true);
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
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.weapon)));
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
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carprowl)));
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
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.fireinactive)));
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
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng,15));
        myDB.deleteAllIncident();
     //   new RetrieveFeedTask().execute(latlng,this,true,mMap,false);

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
//        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
//        long[] pattern = { 0, 100, 600, 100, 700};
//        vibrator.vibrate(pattern, -1);
        Notification noti = new Notification.Builder(context)
                .setTicker("TickerTitle")
                .setContentTitle("Warning!")
                .setContentText("You have reached a crime-prone area.Please be cautious!")
                .setSmallIcon(R.drawable.crimealert)
                .setSound(alarmSound)

                .setContentIntent(pIntent).getNotification();
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        // Get the notification manager system service
        NotificationManager nm = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0,noti);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
//            case R.id.action_settings:
//                Toast.makeText(MapsActivity.this, "Settings clicked", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.action_mapType:
                changeMapType();
                break;
            case R.id.show_notification:
                show_notification(MapsActivity.this);
                break;
//            case R.id.show_DBinfo:
//                myDB.deleteAllIncident();
//                break;
//            case R.id.getCount:
//                Toast.makeText(MapsActivity.this, "DB count " + myDB.getCount(), Toast.LENGTH_SHORT).show();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

}