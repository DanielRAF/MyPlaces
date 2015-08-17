package com.example.danie_000.locationproject.view;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.danie_000.locationproject.R;
import com.example.danie_000.locationproject.model.Place;


public class MainActivity extends ActionBarActivity implements PlaceListFragment.OnItemClicked {
// factual key: sBtxldAPWm7O3NxqiskndLxtScwtLNUXZr72FN5l
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private PlaceMapFragment placeMapFragment;

    private MyBatteryReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Get place map fragment if exist
        placeMapFragment = (PlaceMapFragment) getSupportFragmentManager().findFragmentById(R.id.place_detail);

        //Register receiver for battery (to check if connected or disconnected)
        receiver = new MyBatteryReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        filter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        registerReceiver(receiver, filter);
    }

    //Set data on map fragment method
    @Override
    public void getOnMap(Place place) {
        //At phone mode, sending extras (data) to map activity
        if (placeMapFragment == null) {
            Intent intent = new Intent(MainActivity.this, PlaceMapActivity.class);
            intent.putExtra("lat", place.getLat());
            intent.putExtra("lon", place.getLon());
            intent.putExtra("name", place.getName());
            intent.putExtra("web", place.getWeb());
            intent.putExtra("phone", place.getPhone());
            intent.putExtra("address", place.getAddress());
            startActivity(intent);
        }
        //At tablet mode, set up map
        else {
            placeMapFragment.setUpMap(place);
        }

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
            //Move to settings activity
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyBatteryReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        //Get battery info
        String action = intent.getAction();

            //Check if battery connected or disconnected
            if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
                Toast.makeText(MainActivity.this, getString(R.string.connected), Toast.LENGTH_LONG).show();
            } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
                Toast.makeText(MainActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
            }

    }
}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
