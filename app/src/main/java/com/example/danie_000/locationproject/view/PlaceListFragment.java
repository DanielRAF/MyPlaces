package com.example.danie_000.locationproject.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.example.danie_000.locationproject.R;
import com.example.danie_000.locationproject.controller.HelpMethods;
import com.example.danie_000.locationproject.controller.PlacesCursorAdapter;
import com.example.danie_000.locationproject.controller.PlacesDB;
import com.example.danie_000.locationproject.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class PlaceListFragment extends Fragment {
    final int EXECUTE_FROM_NAME_SEARCH = 0;
    final int EXECUTE_FROM_FULL_SEARCH = 2;
    final int EXECUTE_FROM_FIRST = 1;
    final int EXECUTE_FROM_NO_LOCATION = 3;
    int executeRequst;

    private ProgressBar pb;

    private PlacesDB dbManager;

    private SharedPreferences settings;
    private OnItemClicked listenr;

    private double lat , lon;

    private ListView placeList;
    private boolean isFirstTime = true, isLocationUpdate = false;
    private PlacesCursorAdapter placeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_place_list, container, false);
        //Initialize views
        pb = (ProgressBar) rootView.findViewById(R.id.progressBar);
        placeList = (ListView) rootView.findViewById(R.id.placesList);
        settings = PreferenceManager.getDefaultSharedPreferences(getActivity());

        //If no internet connection, show last search (without distance[location is not available yet]).
        if (HelpMethods.isNetworkAvailable(getActivity()) == false) {
            Toast.makeText(getActivity(), getString(R.string.connection_failed_massage), Toast.LENGTH_LONG).show();
            Cursor cursor = dbManager.getTableData(dbManager.SEARCH_TABLE_REQUEST);
            String[] from = {dbManager.PLACE_NAME, dbManager.PLACE_ADDRESS, dbManager.PLACE_LAT, dbManager.PLACE_LON, dbManager.PLACE_PHONE};
            int[] to = {R.id.txtName, R.id.txtAddress, R.id.txtDistance, R.id.layout};
            placeAdapter = new PlacesCursorAdapter(getActivity(), R.layout.list_view_place_list, cursor, from, to);
            //set placeList
            placeList.setAdapter(placeAdapter);
            pb.setVisibility(View.INVISIBLE);
        }

        //On search button clicked
        rootView.findViewById(R.id.btnSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeRequst = Integer.parseInt(settings.getString("type", "2"));
                //If location is ready, search is available
                if (!isFirstTime || executeRequst == 3) {
                    String searchString = ((EditText) rootView.findViewById(R.id.etSearch)).getText().toString();
                    //Preparer the string for url
                    try {
                        searchString = URLEncoder.encode(searchString, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //Set request and execute
                    GetInfoAsyncTask dataTask = new GetInfoAsyncTask();
                    dataTask.execute(searchString);
                }
                else {
                    Toast.makeText(getActivity(), getString(R.string.wating_for_location), Toast.LENGTH_LONG).show();
                }
            }
        });

        //On near by button clicked
        rootView.findViewById(R.id.btnNearBy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //If location is ready, btnNearBy button is available
                if (!isFirstTime) {
                    //Set request and execute
                    executeRequst = EXECUTE_FROM_FIRST;
                    GetInfoAsyncTask dataTask = new GetInfoAsyncTask();
                    dataTask.execute();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.wating_for_location), Toast.LENGTH_LONG).show();
                }
            }
        });

        //On place (item) click
        placeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                //if Connection available, set place on map (in PlaceMapFragment)
                if (HelpMethods.isNetworkAvailable(getActivity())) {
                    Place place = dbManager.getPlace(id, dbManager.SEARCH_TABLE_REQUEST);
                    listenr.getOnMap(place);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.cannot_open_map), Toast.LENGTH_LONG).show();
                }
            }
        });

        //On place (item) long click
        placeList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                //Set&show dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(getString(R.string.faviorites_dialog));
                dialog.setMessage(getString(R.string.favorites_dialog_message));

                //On negative button clicked - save place on places table (in DB)
                dialog.setNegativeButton(getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Place place = dbManager.getPlace(id, dbManager.SEARCH_TABLE_REQUEST);
                        dbManager.insertPlace(place, dbManager.PLACE_TABLE_REQUEST);
                    }
                });

                //On positive button clicked - share the place
                dialog.setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Place place = dbManager.getPlace(id, dbManager.SEARCH_TABLE_REQUEST);
                        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                        shareIntent.setType("text/plain");
                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share_user_message) + " \n" + place.getName() + "\n" + place.getAddress());
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_massage)));
                    }
                });
                dialog.show();
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Set activity listener and DB
        listenr = (OnItemClicked)getActivity();
        dbManager = new PlacesDB(getActivity(), 1);
    }

    //Task to get information from internet/data base
    public class GetInfoAsyncTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Set pb and placeList
            pb.setVisibility(View.VISIBLE);
            placeList.setAdapter(null);
        }

        @Override
        protected String doInBackground(String... params) {
            //Checks user settings
            String urlString = "";
            String r = settings.getString("radius", "5000");
            int radius = Integer.parseInt(r);
            switch(executeRequst) {
                case EXECUTE_FROM_FIRST:
                    urlString = "http://api.v3.factual.com/t/places?geo={%22$circle%22:{%22$center%22:[" + lat + "," + lon + "],%22$meters%22:%20" + radius + "}}&KEY=sBtxldAPWm7O3NxqiskndLxtScwtLNUXZr72FN5l";
                    break;
                case EXECUTE_FROM_FULL_SEARCH:
                    urlString ="http://api.v3.factual.com/t/places?geo={%22$circle%22:{%22$center%22:[" + lat + "," + lon + "],%22$meters%22:%20" + radius + "}}&KEY=sBtxldAPWm7O3NxqiskndLxtScwtLNUXZr72FN5l&q="+ params[0];
                    break;
                case EXECUTE_FROM_NAME_SEARCH:
                    urlString = "http://api.v3.factual.com/t/places?geo={%22$circle%22:{%22$center%22:[" + lat + "," + lon + "],%22$meters%22:%20" + radius + "}}&KEY=sBtxldAPWm7O3NxqiskndLxtScwtLNUXZr72FN5l&filters={\"name\":{\"$search\":\"" + params[0] + "\"}}";
                    break;
                case EXECUTE_FROM_NO_LOCATION:
                    urlString ="http://api.v3.factual.com/t/places?KEY=sBtxldAPWm7O3NxqiskndLxtScwtLNUXZr72FN5l&q="+ params[0];
                    break;
            }
            //connect to factual api, get the data as a string
           return HelpMethods.getStringFromUrl(urlString);
        }

        @Override
        protected void onPostExecute(String urlString) {

            pb.setVisibility(View.INVISIBLE);

            //If data was given, handle it.
            if (urlString != null && urlString != "") {
                dbManager.deleteTable(dbManager.SEARCH_TABLE_REQUEST);
                try {
                    JSONObject json = new JSONObject(urlString);
                    JSONObject response = json.getJSONObject("response");
                    //If no results
                    if (response.getInt("included_rows") == 0) {
                        Toast.makeText(getActivity(), getString(R.string.no_results), Toast.LENGTH_LONG).show();
                        return;
                    }
                    //Get data from JSON and put it in the DB
                    JSONArray data = response.getJSONArray("data");
                    Place place;
                    for (int i = 0; i < data.length(); i++) {
                        String web, phone;
                        if (((JSONObject) data.get(i)).isNull("website")) {
                            web = null;
                        }
                        else {
                            web = ((JSONObject) data.get(i)).getString("website");
                        }
                        if (((JSONObject) data.get(i)).isNull("tel")) {
                            phone = null;
                        }
                        else {
                            phone = ((JSONObject) data.get(i)).getString("tel");
                        }
                        place = new Place(((JSONObject) data.get(i)).getString("name"), ((JSONObject) data.get(i)).getString("address"), ((JSONObject) data.get(i)).getDouble("latitude"),
                                ((JSONObject) data.get(i)).getDouble("longitude"), web, phone);
                        int a = 0;
                        dbManager.insertPlace(place, dbManager.SEARCH_TABLE_REQUEST);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                }

                //set placeAdapter - if no new data has given(from api), ListView will show the last search (with location), if new data was given ListView will show the new search
                Cursor cursor = dbManager.getTableData(dbManager.SEARCH_TABLE_REQUEST);
                String[] from = {dbManager.PLACE_NAME, dbManager.PLACE_ADDRESS, dbManager.PLACE_LAT, dbManager.PLACE_LON, dbManager.PLACE_PHONE};
                int[] to = {R.id.txtName, R.id.txtAddress, R.id.txtDistance, R.id.layout};
                placeAdapter = new PlacesCursorAdapter(getActivity(), R.layout.list_view_place_list, cursor, from, to, lat, lon);
                //set placeList
                placeList.setAdapter(placeAdapter);
                if (HelpMethods.isNetworkAvailable(getActivity()) == false && isLocationUpdate == true) {
                    Toast.makeText(getActivity(), getString(R.string.connection_failed_massage), Toast.LENGTH_LONG).show();
                }
                if (urlString == null && HelpMethods.isNetworkAvailable(getActivity()) == true) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle(getString(R.string.key_over_dialog_title));
                    dialog.setMessage(getString(R.string.key_over_dialog_massage));
                    dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialog.show();
                }
                isLocationUpdate = true;
        }
    }


    //Gets the location from MainFragment and handles it
    public void onLocationAvailable(double lat, double lon,boolean isFirstTime) {
        this.isFirstTime = isFirstTime;
        this.lat = lat;
        this.lon = lon;
        //If it's the first time to get location, start first search
        if (isFirstTime) {
            executeRequst = EXECUTE_FROM_FIRST;
            this.isFirstTime = false;
            GetInfoAsyncTask dataTask = new GetInfoAsyncTask();
            dataTask.execute();
        }

    }

    //Get data from activity interface
    interface OnItemClicked {
        public void getOnMap(Place place);
    }

}
