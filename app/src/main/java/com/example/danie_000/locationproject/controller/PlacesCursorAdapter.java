package com.example.danie_000.locationproject.controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.danie_000.locationproject.R;

/**
 * Created by danie_000 on 7/23/2015.
 * Aa$4DZ358W
 */
public class PlacesCursorAdapter extends SimpleCursorAdapter {
    double latC = -1, lonC = -1;

    public PlacesCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, double lat, double lon) {
        super(context, layout, c, from, to);
        latC = lat;
        lonC = lon;
    }

    public PlacesCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
        latC = -1;
        lonC = -1;
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.list_view_place_list, parent, false);
            final String phone = cursor.getString(cursor.getColumnIndex(PlacesDB.PLACE_PHONE));
            view.findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tel;
                        Intent dialIntent = new Intent();
                        tel = phone.replace("-", "");
                        dialIntent.setAction(Intent.ACTION_CALL);
                        dialIntent.setData(Uri.parse("tel:" + tel));
                        context.startActivity(dialIntent);
                    }
                });
                return view;
    }

    @Override
    public void bindView(final View view,final Context context,final Cursor cursor) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

        String name = cursor.getString(cursor.getColumnIndex(PlacesDB.PLACE_NAME));
        String address = cursor.getString(cursor.getColumnIndex(PlacesDB.PLACE_ADDRESS));
        double lat = cursor.getDouble(cursor.getColumnIndex(PlacesDB.PLACE_LAT));
        double lon = cursor.getDouble(cursor.getColumnIndex(PlacesDB.PLACE_LON));
        final String phone = cursor.getString(cursor.getColumnIndex(PlacesDB.PLACE_PHONE));

        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        txtName.setSelected(true);
        TextView txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtAddress.setSelected(true);
        TextView txtDistance = (TextView) view.findViewById(R.id.txtDistance);

        txtName.setText(name);
        txtAddress.setText(address);

        Button btnCall = (Button) view.findViewById(R.id.btnCall);
        if (latC != -1) {
            double dis = HelpMethods.getDistanceFromLatLonInKm(latC, lonC, lat, lon);
            if (settings.getString("mk", "km").equals("miles")) {
                dis = dis / 1.621371;
                txtDistance.setText(String.format("%.3f", dis) + " Miles");
            }
            else
                txtDistance.setText(String.format("%.3f", dis) + " Km");
        } else {
            txtDistance.setText("Distance not available");
        }
        if (phone == null){
            btnCall.setVisibility(View.INVISIBLE);
        }
        else {
            btnCall.setVisibility(View.VISIBLE);
        }
    }
}
