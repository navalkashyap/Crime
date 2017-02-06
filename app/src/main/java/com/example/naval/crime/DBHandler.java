package com.example.naval.crime;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "report_incidents.db";
    public static final String TABLE_PRODUCTS = "reports";
    public static final String _id = "_id";
    public  static final String incidentType = "incidentType";
    public static final String latitude = "latitude";
    public static final String longitude = "longitude";
    public static final String incidentTime = "incidentTime";
    public static final String entryTime = "entryTime";
    public static final String location = "location";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_PRODUCTS + "("
                + _id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + incidentType + " INTEGER, "
                + latitude + " TEXT, "
                + longitude + " TEXT, "
                + incidentTime + " DOUBLE, "
                + entryTime + " DOUBLE, "
                + location + " TEXT"
                + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ondVersion, int newVersion) {
        db.execSQL("DROP_TABLE_IF_EXISTS" + TABLE_PRODUCTS);
        onCreate(db);
    }

    // Add new row in DB
    public boolean insertIncident(crime_incident incident_) {
        ContentValues values = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();
       // values.put(_id, incident_.getId());
        values.put(incidentType, incident_.getIncidentType());
        values.put(latitude,Double.toString(incident_.getLatitude()));
        values.put(longitude,Double.toString(incident_.getLongitude()));
        values.put(incidentTime,incident_.getIncidentTime());
        values.put(entryTime,incident_.getEntryTime());
        values.put(location,incident_.getLocation());
        db.insert(TABLE_PRODUCTS, null, values);
        db.close();
        return true;
    }

    // Delete a row from DB
    public boolean deleteIncident(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS, "_id = ? ",new String[] {Integer.toString(id)});
        return true;
    }

    public boolean clearIncidentTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRODUCTS,null,null);
//        db.delete(TABLE_PRODUCTS, "_id = ? ",new String[] {Integer.toString(id)});
        return true;
    }

    public ArrayList<String> getAllIncidents() {
        ArrayList<String> array_list = new ArrayList<String>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "SELECT * FROM " + TABLE_PRODUCTS, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(_id))
                + "," + res.getString(res.getColumnIndex(latitude))
                + "," + res.getString(res.getColumnIndex(longitude))
                + "," + res.getString(res.getColumnIndex(location))
                    + "," + res.getString(res.getColumnIndex(incidentType))
            );
            res.moveToNext();
        }
        return array_list;
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

}
