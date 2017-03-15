package com.example.naval.crime;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;


class RetrieveFeedTask extends AsyncTask<Object, Void, String> {

    DBHandler mapDB;
    GoogleMap mMap;
    Context mapContext;
    int minThreshold = 10;
    boolean addInicdentsOnMap = false;
    boolean sendNotifcation = true;

    @Override
    protected String doInBackground(Object... params) {
        LatLng latlngs = (LatLng) params[0];
        mapContext = (Context) params[1];
        addInicdentsOnMap = (boolean) params[2];
        mMap = (GoogleMap) params[3];
        sendNotifcation = (boolean) params[4];
        mapDB = new DBHandler(mapContext);
//        LatLng Seattle_latlng = new LatLng(47.766790, -122.202505);
//        System.out.println("Distance between "+getDistance(latlngs,Seattle_latlng));
//        System.out.println();
        bothell_api(latlngs);
        return seattle_api(latlngs);
    }

    double getDistance(LatLng x, LatLng y) {
        double sq_dist = Math.pow((x.latitude - y.latitude),2) + Math.pow((x.longitude - y.longitude),2);
        double dist = Math.pow(sq_dist, 0.5);
        return dist;
    }

    String bothell_api(LatLng latlngs) {
        String latitude = Double.toString(latlngs.latitude);
        String longitude = Double.toString(latlngs.longitude);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String dateTo = dateFormat.format(cal.getTime());
        cal.add(Calendar.DATE, -15);
        String dateFrom = dateFormat.format(cal.getTime());
        String circle = "50000";
        try {
            URL url_bothell = new URL("https://moto.data.socrata.com/resource/4h35-4mtu.json?" +
                    "$select=case_number,incident_type_primary,incident_datetime,latitude,longitude,incident_description&" +
                    "$where=within_circle(location,"+
                    latitude +"," + longitude + ","+ circle+ ")%20and%20" +
                    "updated_at%20between%20%27" + dateFrom + "%27%20and%20%27" + dateTo + "%27");
            System.out.println(url_bothell);
            HttpURLConnection urlConnection = (HttpURLConnection) url_bothell.openConnection();
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                    Log.i("INFO", line);

                }
                bufferedReader.close();
                Gson gson = new Gson();
                Type collectionType = new TypeToken<Collection<WoodRed_incident>>() {}.getType();
                Collection<WoodRed_incident> enums = gson.fromJson(stringBuilder.toString(),collectionType);
                WoodRed_incident[] incidentsResponse = enums.toArray(new WoodRed_incident[enums.size()]);
                mapDB.insertIncidentList_bothell(incidentsResponse);
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
//        Log.i("INFO", response);
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<Seattle_incident>>() {}.getType();
        Collection<Seattle_incident> enums = gson.fromJson(response,collectionType);
        Seattle_incident[] incidentsResponse = enums.toArray(new Seattle_incident[enums.size()]);
        mapDB.insertIncidentList(incidentsResponse);
        if(addInicdentsOnMap)
            new MapsActivity().addIncidentsOnMap(mapDB,mMap);
//        if (minThreshold < incidentsResponse.length && sendNotifcation)
//            new MapsActivity().show_notification(mapContext);
    }
}