package com.example.naval.crime;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "report_incidents.db";
    public static final String TABLE_PRODUCTS = "reports";
//    public static final String _id = "_id";
    public static final String caseNum = "caseNum";
    public  static final String incidentType = "incidentType";
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    public static final String incidentTime = "incidentTime";
    public static final String entryTime = "entryTime";
    public static final String location = "location";
    public static final String description = "description";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + caseNum + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + caseNum + " TEXT,"
                + incidentType + " TEXT, "
                + latitude + " TEXT, "
                + longitude + " TEXT, "
                + incidentTime + " TEXT, "
//                + entryTime + " DOUBLE, "
//                + location + " TEXT, "
                + description + " TEXT "
                + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ondVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Add new row in DB
    public boolean insertIncident(crime_incident incident_) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        values.put(incidentType, incident_.getIncidentType());
        values.put(latitude,Double.toString(incident_.getLatitude()));
        values.put(longitude,Double.toString(incident_.getLongitude()));
        values.put(incidentTime,incident_.getIncidentTime());
        values.put(description, "Dummy_Value");
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return true;
    }

    public boolean insertIncidentList(new_crime_incident[] incidentsList) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i< incidentsList.length; i++) {
            values.put(incidentTime,incidentsList[i].getAt_scene_time());
            values.put(caseNum, Long.valueOf(incidentsList[i].getCad_event_number()));
            values.put(description, String.valueOf(incidentsList[i].getInitial_type_description()));
            values.put(incidentType, incidentsList[i].getInitial_type_group());
            values.put(latitude,incidentsList[i].getLatitude());
            values.put(longitude,incidentsList[i].getLongitude());
            System.out.println(values);
            db.insertWithOnConflict(TABLE_PRODUCTS, null, values,SQLiteDatabase.CONFLICT_REPLACE);
//            db.insert(TABLE_PRODUCTS, null, values);
        }
        db.close();
        return true;
    }
    // Delete a row from DB
    public boolean deleteIncident(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, "_id = ? ",new String[] {Integer.toString(id)});
        db.close();
        return true;
    }

    // Delete a row from DB
    public boolean deleteAllIncident() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_PRODUCTS);
        onCreate(db);
        db.close();
        return true;
    }

    public boolean clearIncidentTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS,null,null);
        db.close();
        return true;
    }

    public int getCount() {
        int count = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        count = db.rawQuery( "SELECT * FROM " + TABLE_PRODUCTS, null ).getCount();
        db.close();
        return count;
    }

    public ArrayList<String> getAllIncidents() {
        ArrayList<String> array_list = new ArrayList<String>();
        Cursor res;
        String incident_ = null;
        SQLiteDatabase db = this.getReadableDatabase();
        res =  db.rawQuery( "SELECT * FROM " + TABLE_PRODUCTS, null );
        if(res.getCount() == 0)
            return array_list;
        res.moveToFirst();
        System.out.println("getAllIncidents - res.moveToFirst "+ res.getCount() + " " +
        res.getString(res.getColumnIndex(caseNum)));
        while(res.isAfterLast() == false){
            incident_ = res.getString(res.getColumnIndex(caseNum))
                    + ";" + res.getString(res.getColumnIndex(latitude))
                    + ";" + res.getString(res.getColumnIndex(longitude))
                    + ";" + res.getString(res.getColumnIndex(description))
                    + ";" + getIncidentType(res.getString(res.getColumnIndex(incidentType)));
            System.out.println(incident_);
            array_list.add(incident_);
            res.moveToNext();
        }
        db.close();
        return array_list;
    }

    public int getIncidentType(String incident) {
        if(incident.contains("THEFT")
                || incident.contains("ROBBERY")
                || incident.contains("BURG")
                )
            return 1;
        else if(incident.contains("MOTOR"))
            return 2;
        else if(incident.contains("NARCOTICS")
                || incident.contains("DETOX")
                )
            return 3;
        else if(incident.contains("DISTURBANCE")
                || incident.contains("HAZ")
                || incident.contains("LEWD")
                || incident.contains("BIAS")
                )
            return 4;
        else if(incident.contains("WEAPN")
                || incident.contains("FIGHT")
                || incident.contains("ASLT")
                || incident.contains("HARAS")
                )
            return 5;
        else if(incident.contains("TRAFFIC"))
            return 6;
        else if(incident.contains("ALARM"))
            return 7;
        else if(incident.contains("NOISE")
                || incident.contains("DEMONSTRATIONS")
                || incident.contains("PANHANDLING")
                )
            return 8;
        else if(incident.contains("PARKING"))
            return 9;
        else if(incident.contains("SHOPLIFT")
                || incident.contains("FRAUD")
                )
            return 10;
        else if(incident.contains("PROPERTY")
                || incident.contains("TRESPASS")
                )
            return 11;
        else if(incident.contains("SUSPICIOUS")
                || incident.contains("CRISIS")
                )
            return 12;
        else  if(incident.contains("UNKNOWN"))
            return 13;



        return -1;
    }

    public ArrayList<String> getAllLocations() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_PRODUCTS, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(location)));
            res.moveToNext();
        }
        return array_list;
    }

    public void createDummyDB() {
        crime_incident incident_;
        ArrayList<String> allIncidents = getAllIncidents();
        if(allIncidents.size() != 0)
            return;
        clearIncidentTable();
        double lat = 47.7136844;
        double lng = -122.2074087;
        String loc = "Kirkland";
        for (int i = 0; i < 50; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.5;
            double y = (rand.nextDouble() * 2 - 1)*0.5;
            incident_ = new crime_incident(2, rand.nextInt(12), lat + x , lng + y, 1001, loc, 2);
//            myDB.insertIncident(incident_);
        }
        loc = "Bothell";
        lat = 47.759497;
        lng = -122.190601;
        for (int i = 0; i < 50; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.03;
            double y = (rand.nextDouble() * 2 - 1)*0.03;
            incident_ = new crime_incident(2, rand.nextInt(12), lat + x , lng + y, 1001, loc, 2);
//            myDB.insertIncident(incident_);
        }

        loc = "Seattle";
        lat = 47.720323;
        lng = -122.329173;
        for (int i = 0; i < 50; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.03;
            double y = (rand.nextDouble() * 2 - 1)*0.03;
            incident_ = new crime_incident(2, rand.nextInt(12), lat + x , lng + y, 1001, loc, 2);
//            myDB.insertIncident(incident_);
        }

        loc = "Redmond";
        lat = 47.677348;
        lng = -122.123764;
        for (int i = 0; i < 20; i ++) {
            Random rand = new Random();
            double x = (rand.nextDouble() * 2 - 1)*0.03;
            double y = (rand.nextDouble() * 2 - 1)*0.03;
            incident_ = new crime_incident(2, rand.nextInt(12), lat + x , lng + y, 1001, loc, 2);
//            myDB.insertIncident(incident_);
        }
    }

}
