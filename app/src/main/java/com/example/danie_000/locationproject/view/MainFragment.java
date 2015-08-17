package com.example.danie_000.locationproject.view;


import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.danie_000.locationproject.R;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment implements ActionBar.TabListener, LocationListener {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ActionBar actionBar;
    private FavFragment favFragment;
    private boolean isFirstTime = true, isUptadtesRemoved = false, isGps = false, isNetwork = false;
    private PlaceListFragment placeListFragment;
    private LocationManager locationManager;
    private String providerNetwork, providerGPS;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        //Get action bar from activity (only action bar can handle tabs, fragment can handle view pager)
        actionBar = ((ActionBarActivity)getActivity()).getSupportActionBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Initialize values
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        providerNetwork = LocationManager.NETWORK_PROVIDER;
        providerGPS = LocationManager.GPS_PROVIDER;
        if (locationManager.isProviderEnabled(providerGPS)) {
            isGps = true;
            locationManager.requestLocationUpdates(providerGPS, 0, 0, this);
        }
        if (locationManager.isProviderEnabled(providerNetwork)) {
            isNetwork = true;
            locationManager.requestLocationUpdates(providerNetwork, 0, 0, this);
        }

        //If location providers are disable or not exists make this Toast:
        if (isGps == false && isNetwork == false) {
            Toast.makeText(getActivity(), getString(R.string.no_providers_message), Toast.LENGTH_LONG).show();
        }

        // Set up the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        return rootView;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        //If favorites tab selected, refresh cursor
        if (tab.getPosition() == 1 && favFragment != null) {
            favFragment.swapCursor();
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
               case 0:
                   placeListFragment = new PlaceListFragment();
                   return placeListFragment;

               //Save fav fragment for changes
                case 1:
                    favFragment = new FavFragment();
                    return favFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.search);
                case 1:
                    return getString(R.string.favorites);
            }
            return null;
        }
    }


    //Location manager methods**************************************************************************
    @Override
    public void onLocationChanged(Location location) {
        if (favFragment != null && placeListFragment != null)
        {
            double lat = location.getLatitude();
            double lon = location.getLongitude();

            //If GPS location,  get location and remove updates
            if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
                locationManager.removeUpdates(this);
                if (isFirstTime) {
                    placeListFragment.onLocationAvailable(lat, lon, isFirstTime);
                    favFragment.onLocationAvailable(lat, lon);
                    isFirstTime = false;
                } else {
                    Toast.makeText(getActivity(), getString(R.string.gps_avalilable_message), Toast.LENGTH_LONG).show();
                    placeListFragment.onLocationAvailable(lat, lon, isFirstTime);
                }
                isUptadtesRemoved = true;
            }
            //If network location and first time to get into onLocationChanged get location
            else if (isFirstTime && location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
                placeListFragment.onLocationAvailable(lat, lon, isFirstTime);
                favFragment.onLocationAvailable(lat, lon);
                isFirstTime = false;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    //*************************************************************************************************

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Remove updates when Activity destroyed
        if (!isUptadtesRemoved) {
            locationManager.removeUpdates(this);
        }
    }
}
