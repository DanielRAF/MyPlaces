package com.example.danie_000.locationproject.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.example.danie_000.locationproject.R;
import com.example.danie_000.locationproject.model.Place;


/**
 * An activity representing a single Place detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link MainActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link PlaceMapFragment}.
 */
public class PlaceMapActivity extends ActionBarActivity {

    //This activity is only for phone, not tablet
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_map);
        //Get data from MainActivity by extras and set up map (in PlaceMapFragment)
        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("lat", -1);
        double lon = intent.getDoubleExtra("lon", -1);
        String name = intent.getStringExtra("name");
        double dis = intent.getDoubleExtra("dis", -1);
        String web = intent.getStringExtra("web");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");
        PlaceMapFragment placeMapFragment = (PlaceMapFragment) getSupportFragmentManager().findFragmentById(R.id.place_detail);
        placeMapFragment.setUpMap(new Place(name, address, lat, lon, web, phone));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            //Go to SettingsActivity
            case R.id.action_settings:
                Intent intent = new Intent(PlaceMapActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
