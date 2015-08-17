package com.example.danie_000.locationproject.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.danie_000.locationproject.model.Place;

import java.util.ArrayList;

/**
 * Created by danie_000 on 7/15/2015.
 */
public class PlacesDB extends SQLiteOpenHelper {
    public final int PLACE_TABLE_REQUEST = 0;
    public final int SEARCH_TABLE_REQUEST = 1;

    static public final String PLACES_TABLE = "places";
    static public final String SEARCH_TABLE = "search";
    static public final String ID = "_id";
    static public final String PLACE_NAME = "name";
    static public final String PLACE_ADDRESS = "address";
    static public final String PLACE_LAT = "lat";
    static public final String PLACE_LON = "lon";
    static public final String PLACE_WEB = "web";
    static public final String PLACE_PHONE = "phone";


    public PlacesDB(Context context, int version) {
        super(context, "PlacesDB.db", null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
                db.execSQL("CREATE TABLE " + PLACES_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PLACE_NAME +
                        " TEXT NOT NULL, " + PLACE_ADDRESS + " TEXT, " + PLACE_LAT + " REAL, " + PLACE_LON + " REAL, " + PLACE_WEB + " TEXT, "
                        + PLACE_PHONE + " TEXT)");

                db.execSQL("CREATE TABLE " + SEARCH_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PLACE_NAME +
                        " TEXT NOT NULL, " + PLACE_ADDRESS + " TEXT, " + PLACE_LAT + " REAL, " + PLACE_LON + " REAL, " + PLACE_WEB + " TEXT, "
                        + PLACE_PHONE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void insertPlace(Place place, int table) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues valuesToAdd = new ContentValues();
        valuesToAdd.put(PLACE_NAME, place.getName());
        valuesToAdd.put(PLACE_ADDRESS, place.getAddress());
        valuesToAdd.put(PLACE_LAT, place.getLat());
        valuesToAdd.put(PLACE_LON, place.getLon());
        valuesToAdd.put(PLACE_WEB, place.getWeb());
        valuesToAdd.put(PLACE_PHONE, place.getPhone());

        switch (table) {
            case PLACE_TABLE_REQUEST:
                db.insert(PLACES_TABLE, null, valuesToAdd);
                break;
            case SEARCH_TABLE_REQUEST:
                db.insert(SEARCH_TABLE, null, valuesToAdd);
                break;
        }
        db.close();
    }


    public void deleteTable(int request){
        SQLiteDatabase db = getWritableDatabase();
        String table = null;
        switch (request) {
            case PLACE_TABLE_REQUEST:
                table = PLACES_TABLE;
                break;
            case SEARCH_TABLE_REQUEST:
                table = SEARCH_TABLE;
                break;
        }
        db.execSQL("delete from " + table);
        db.close();
    }

    public Cursor getTableData(int table) {
        SQLiteDatabase db = getReadableDatabase();
        switch (table) {
            case PLACE_TABLE_REQUEST:
                return db.rawQuery("Select * from " + PLACES_TABLE, null);
            case SEARCH_TABLE_REQUEST:
                return db.rawQuery("Select * from " + SEARCH_TABLE, null);
            default:
                return null;
        }
    }

    public Place getPlace(long id, int request){
        SQLiteDatabase db = getReadableDatabase();
        String table = null;
        switch (request) {
            case PLACE_TABLE_REQUEST:
                table = PLACES_TABLE;
                break;
            case SEARCH_TABLE_REQUEST:
                table  = SEARCH_TABLE;
                break;
        }
        Cursor c = db.rawQuery("SELECT * FROM " + table + " where " + ID + " = " + id, null);
        String name = null, address = null, web = null, phone = null;
        double lat = 0, lon = 0, dis = 0;
        while (c.moveToNext() == true) {
            if (id == c.getLong(c.getColumnIndex(ID))) {
                name = c.getString(c.getColumnIndex(PLACE_NAME));
                address = c.getString(c.getColumnIndex(PLACE_ADDRESS));
                lat = c.getDouble(c.getColumnIndex(PLACE_LAT));
                lon = c.getDouble(c.getColumnIndex(PLACE_LON));
                web = c.getString(c.getColumnIndex(PLACE_WEB));
                phone = c.getString(c.getColumnIndex(PLACE_PHONE));
            }
        }
        db.close();

        return new Place(name, address, lat, lon, web, phone);
    }

    public void deletePlace(int table, long id) {
        SQLiteDatabase db =getWritableDatabase();
        switch (table) {
            case PLACE_TABLE_REQUEST:
                db.delete(PLACES_TABLE, ID + " = " + id, null);
                break;
            case SEARCH_TABLE_REQUEST:
                db.delete(SEARCH_TABLE, ID + " = " + id, null);
                break;
        }
        db.close();
    }
}
