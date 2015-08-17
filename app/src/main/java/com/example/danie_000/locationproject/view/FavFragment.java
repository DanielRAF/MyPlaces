package com.example.danie_000.locationproject.view;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.danie_000.locationproject.R;
import com.example.danie_000.locationproject.controller.HelpMethods;
import com.example.danie_000.locationproject.controller.PlacesCursorAdapter;
import com.example.danie_000.locationproject.controller.PlacesDB;
import com.example.danie_000.locationproject.model.Place;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavFragment extends Fragment {

    private PlacesDB dbManager;

    private PlaceListFragment.OnItemClicked listener;
    private ListView favList;
    private PlacesCursorAdapter placeAdapter;

    public FavFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (PlaceListFragment.OnItemClicked) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View rootView = inflater.inflate(R.layout.fragment_fav, container, false);

        // Initialize values
        dbManager = new PlacesDB(getActivity(), 1);

        favList = (ListView) rootView.findViewById(R.id.favList);
        Cursor cursor = dbManager.getTableData(dbManager.PLACE_TABLE_REQUEST);
        String[] from = {dbManager.PLACE_NAME, dbManager.PLACE_ADDRESS, dbManager.PLACE_LAT, dbManager.PLACE_LON, dbManager.PLACE_PHONE};
        int[] to = {R.id.txtName, R.id.txtAddress, R.id.txtDistance, R.id.layout};
        placeAdapter = new PlacesCursorAdapter(getActivity(), R.layout.list_view_place_list, cursor, from, to);

        // Set favorites list
        favList.setAdapter(placeAdapter);

        favList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //if Connection available, set place on map (in PlaceMapFragment)
                if (HelpMethods.isNetworkAvailable(getActivity())) {
                    Place place = dbManager.getPlace(id, dbManager.PLACE_TABLE_REQUEST);
                    listener.getOnMap(place);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.cannot_open_map), Toast.LENGTH_LONG).show();
                }
            }
        });

        //Dialog for delete or for share
        favList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle(getString(R.string.faviorites_dialog));
                dialog.setMessage(getString(R.string.fav_dialog));

                //If delete was clicked
                dialog.setNegativeButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbManager.deletePlace(dbManager.PLACE_TABLE_REQUEST, id);
                        placeAdapter.swapCursor(dbManager.getTableData(dbManager.PLACE_TABLE_REQUEST));
                    }
                });

                //If share was clicked
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
    public void onResume() {
        super.onResume();
        //Refresh favorites ListView
          placeAdapter.swapCursor(dbManager.getTableData(dbManager.PLACE_TABLE_REQUEST));
    }

    //Refresh ListView method (fot tabs)
    public void swapCursor() {
//        cursorAdapter.swapCursor(dbManager.getTableData(dbManager.PLACE_TABLE_REQUEST));
          placeAdapter.swapCursor(dbManager.getTableData(dbManager.PLACE_TABLE_REQUEST));

    }

    public void onLocationAvailable (double lat, double lon) {
        //Get data again (with distance)
        String[] from = {dbManager.PLACE_NAME, dbManager.PLACE_ADDRESS, dbManager.PLACE_LAT, dbManager.PLACE_LON, dbManager.PLACE_PHONE};
        int[] to = {R.id.txtName, R.id.txtAddress, R.id.txtDistance, R.id.layout};
        Cursor cursor = dbManager.getTableData(dbManager.PLACE_TABLE_REQUEST);
        placeAdapter = new PlacesCursorAdapter(getActivity(), R.layout.list_view_place_list, cursor, from, to, lat, lon);
        favList.setAdapter(placeAdapter);
    }
}
