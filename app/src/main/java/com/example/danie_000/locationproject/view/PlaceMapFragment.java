package com.example.danie_000.locationproject.view;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.danie_000.locationproject.R;
import com.example.danie_000.locationproject.controller.HelpMethods;
import com.example.danie_000.locationproject.model.Place;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * A fragment representing a single Place detail screen.
 * This fragment is either contained in a {@link MainActivity}
 * in two-pane mode (on tablets) or a {@link PlaceMapActivity}
 * on handsets.
 */
public class PlaceMapFragment extends Fragment {


    private GoogleMap mMap;
    private Intent intent;
    private TextView txtPlaceName, txtPlaceAddress, txtPlaceWeb, txtPlacePhone;
    private Button btnWaze;
    private Place place;

    public PlaceMapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate fragment_place_map view
        View rootView = inflater.inflate(R.layout.fragment_place_map, container, false);

        //Set views
        txtPlaceName = (TextView) rootView.findViewById(R.id.txtPlaceName);
        txtPlaceAddress = (TextView) rootView.findViewById(R.id.txtPlaceAddress);
        txtPlacePhone = (TextView) rootView.findViewById(R.id.txtPlacePhone);
        txtPlaceWeb = (TextView) rootView.findViewById(R.id.txtPlaceWeb);
        btnWaze = (Button) rootView.findViewById(R.id.btnWaze);

        //Get Intent and get place data from intent
        intent = getActivity().getIntent();
        double lat = intent.getDoubleExtra("lat",-1);
        double lon = intent.getDoubleExtra("lon", -1);
        String name = intent.getStringExtra("name");
        String web = intent.getStringExtra("web");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");
        place = new Place(name, address, lat, lon, web, phone);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Set up map
        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                .getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //If it's phone user, set data
        if (intent.getStringExtra("name") != null) {
            setData(place);
        }
    }

    //method for tablet user
    public void setUpMap(Place place) {
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        setData(place);

    }

    //Set place data on views and set place location on the map
    public void setData(final Place place){
        txtPlaceName.setText(place.getName());
        txtPlaceName.setSelected(true);
        txtPlaceAddress.setText(place.getAddress());
        if (place.getPhone() == null) {
            txtPlacePhone.setText("Phone not available");
        }
        else {
            txtPlacePhone.setText(place.getPhone());

            //On txtPlacePhone clicked, call the place
            txtPlacePhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent dialIntent = new Intent();
                        String phone = txtPlacePhone.getText().toString();
                        phone = phone.replace("-", "");
                        dialIntent.setAction(Intent.ACTION_CALL);
                        dialIntent.setData(Uri.parse("tel:" + phone));
                        startActivity(dialIntent);
                }
            });

            HelpMethods.makeTextViewHyperlink(txtPlacePhone);
        }

        if (place.getWeb() == null)
            txtPlaceWeb.setText("Website not available");
        else {
            txtPlaceWeb.setText(place.getWeb());
        }

        //On btnWaze clicked, set address on waze
        btnWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "waze://?q=" + place.getAddress();
                   Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent =
                            new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.waze"));
                    startActivity(intent);
                }
            }
        });

        //Set place location on map
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLat(), place.getLon()), 17));
        mMap.addMarker(new MarkerOptions().position(new LatLng(place.getLat(), place.getLon())).title("Target"));
    }

}
