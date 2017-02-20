package com.example.naval.crime;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


class RetrieveFeedTask extends AsyncTask<LatLng, Void, String> {

    private Exception exception;
    TextView responseView;

    @Override
    protected String doInBackground(LatLng... latLngs) {
        String lattitude = Double.toString(latLngs[0].latitude);
        String longitude = Double.toString(latLngs[0].longitude);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String dateTo = dateFormat.format(cal.getTime());
        cal.add(Calendar.DATE, -30);
        String dateFrom = dateFormat.format(cal.getTime());
        try {
            URL url = new URL("https://moto.data.socrata.com/resource/4h35-4mtu.json?$where=within_circle(location,"+
                    lattitude +"," + longitude + ",6000)%20and%20" +
                    "updated_at%20between%20%27" + dateFrom + "%27%20and%20%27" + dateTo + "%27");
            System.out.println(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
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
        Log.i("INFO", response);
        Gson gson = new Gson();
        Type collectionType = new TypeToken<Collection<crime_incident>>() {}.getType();
        Collection<crime_incident> enums = gson.fromJson(response,collectionType);
        crime_incident[] incidentsresponse = enums.toArray(new crime_incident[enums.size()]);
        Log.i("INFO", incidentsresponse[0].getCase_number());
//        addIncidents(incidentsresponse);
    }
}