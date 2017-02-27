package com.example.naval.crime;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.os.SystemClock.sleep;

/**
 * Created by navalkashyap on 2/26/2017.
 */

public class CrimeAlertService extends Service {

    final class thethread implements  Runnable{
        int serviceId;
        thethread(int serviceId) {
            this.serviceId = serviceId;
            notification_count = 0;
        }
        int notification_count;
        @Override
        public void run() {
            while(true) {
                System.out.println("Naval" + notification_count);
                sleep(10000);
                notification_count++;
                if(notification_count < 0)
                    break;
            }

        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new thethread(startId));
//        thread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

class RetrieveFeeds extends AsyncTask<Object, Void, String> {

    DBHandler mapDB;
    int minThreshold = 10;
    boolean addInicdentsOnMap = false;
    boolean sendNotifcation = true;

    @Override
    protected String doInBackground(Object... params) {
        LatLng latlngs = (LatLng) params[0];
//        mapContext = (Context) params[1];
//        addInicdentsOnMap = (boolean) params[2];
//        mMap = (GoogleMap) params[3];
//        sendNotifcation = (boolean) params[4];
//        mapDB = new DBHandler(mapContext);
        return seattle_api(latlngs);
    }

    String seattle_api(LatLng latlngs) {
        String latitude = Double.toString(latlngs.latitude);
        String longitude = Double.toString(latlngs.longitude);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String dateTo = dateFormat.format(cal.getTime());
        cal.add(Calendar.HOUR, -6);
        String dateFrom = dateFormat.format(cal.getTime());
        String circle = "50000";
        try {
            URL url_seattle = new URL("https://data.seattle.gov/resource/pu5n-trf4.json?" +
                    "$select=cad_event_number,initial_type_group,at_scene_time,latitude,longitude,initial_type_description&" +
                    "$where=at_scene_time%20between%20%27"+dateFrom+"%27%20and%20%27"+dateTo+"%27%20AND%20" +
                    "within_circle(incident_location,%20" +
                    latitude + ",%20"+longitude+",%20"+circle+")");
            System.out.println(url_seattle);
            HttpURLConnection urlConnection = (HttpURLConnection) url_seattle.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                    Log.i("INFO", line);

                }
                bufferedReader.close();
                return stringBuilder.toString();
            }
            finally{
                urlConnection.disconnect();
            }
        }
        catch(Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return null;
        }
    }

    protected void onPostExecute(String response) {
        if(response == null) {
            response = "THERE WAS AN ERROR";
        }
//        if (minThreshold < incidentsResponse.length & sendNotifcation)
//            new MapsActivity().show_notification(mapContext);
    }
}
